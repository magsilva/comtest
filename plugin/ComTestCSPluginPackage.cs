using System;
using System.Diagnostics;
using System.Globalization;
using System.Runtime.InteropServices;
using System.ComponentModel.Design;
using Microsoft.Win32;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.OLE.Interop;
using Microsoft.VisualStudio.Shell;
using EnvDTE;
using System.ComponentModel;
using EnvDTE80;

namespace JYU.ComTestCSPlugin
{
    /// <summary>
    /// This is the class that implements the package exposed by this assembly.
    ///
    /// The minimum requirement for a class to be considered a valid package for Visual Studio
    /// is to implement the IVsPackage interface and register itself with the shell.
    /// This package uses the helper classes defined inside the Managed Package Framework (MPF)
    /// to do it: it derives from the Package class that provides the implementation of the 
    /// IVsPackage interface and uses the registration attributes defined in the framework to 
    /// register itself and its components with the shell.
    /// </summary>
    // This attribute tells the PkgDef creation utility (CreatePkgDef.exe) that this class is
    // a package.
    [PackageRegistration(UseManagedResourcesOnly = true)]
    // This attribute is used to register the informations needed to show the this package
    // in the Help/About dialog of Visual Studio.
    [InstalledProductRegistration("#110", "#112", "1.0", IconResourceID = 400)]
    // This attribute is needed to let the shell know that this package exposes some menus.
    [ProvideMenuResource("Menus.ctmenu", 1)]
    [Guid(GuidList.guidComTestCSPluginPkgString)]
    public sealed class ComTestCSPluginPackage : Package
    {
        const string GUID_TEST_PROJECT = "{3AC096D0-A1C2-E12C-1390-A8335801FDAB}";

        DTE dte;

        IVsOutputWindowPane outPane = null;
        ComTestSettings settings;

        /// <summary>
        /// Default constructor of the package.
        /// Inside this method you can place any initialization code that does not require 
        /// any Visual Studio service because at this point the package object is created but 
        /// not sited yet inside Visual Studio environment. The place to do all the other 
        /// initialization is the Initialize method.
        /// </summary>
        public ComTestCSPluginPackage()
        {
            Trace.WriteLine(string.Format(CultureInfo.CurrentCulture, "Entering constructor for: {0}", this.ToString()));

            dte = EnvironmentDTE.FindCurrent();
            settings = ComTestSettings.Load( (DTE2)dte );
        }

        IVsOutputWindowPane CreatePane( Guid paneGuid, string title, bool visible, bool clearWithSolution )
        {
            IVsOutputWindow output =
                (IVsOutputWindow)GetService( typeof( SVsOutputWindow ) );
            IVsOutputWindowPane pane;

            // Create a new pane.
            output.CreatePane(
                ref paneGuid,
                title,
                Convert.ToInt32( visible ),
                Convert.ToInt32( clearWithSolution ) );

            // Retrieve the new pane.
            output.GetPane( ref paneGuid, out pane );

            return pane;
        }


        /////////////////////////////////////////////////////////////////////////////
        // Overriden Package Implementation
        #region Package Members

        /// <summary>
        /// Initialization of the package; this method is called right after the package is sited, so this is the place
        /// where you can put all the initilaization code that rely on services provided by VisualStudio.
        /// </summary>
        protected override void Initialize()
        {
            Trace.WriteLine (string.Format(CultureInfo.CurrentCulture, "Entering Initialize() of: {0}", this.ToString()));
            base.Initialize();

            // Add our command handlers for menu (commands must exist in the .vsct file)
            OleMenuCommandService mcs = GetService(typeof(IMenuCommandService)) as OleMenuCommandService;
            if ( null != mcs )
            {
                // ComTest -> Test Solution
                CommandID cmdTestSolution = new CommandID( GuidList.guidComTestCSPluginCmdSet, (int)PkgCmdIDList.cmdidTestSolution );
                MenuCommand itemTestSolution = new MenuCommand( TestSolutionCallback, cmdTestSolution );
                mcs.AddCommand( itemTestSolution );

                // ComTest -> Test Solution (Debug)
                CommandID cmdDebugSolution = new CommandID( GuidList.guidComTestCSPluginCmdSet, (int)PkgCmdIDList.cmdidDebugSolution );
                MenuCommand itemDebugSolution = new MenuCommand( TestSolutionCallback, cmdDebugSolution );
                mcs.AddCommand( itemDebugSolution );

                // ComTest -> Test Project (code)
                CommandID cmdTestProjectCode = new CommandID( GuidList.guidComTestCSPluginCmdSet, (int)PkgCmdIDList.cmdidTestProjectCode );
                MenuCommand itemTestProjectCode = new MenuCommand( TestProjectCodeCallback, cmdTestProjectCode );
                mcs.AddCommand( itemTestProjectCode );

                // ComTest -> Test Project (Debug)
                CommandID cmdDebugProject = new CommandID( GuidList.guidComTestCSPluginCmdSet, (int)PkgCmdIDList.cmdidDebugProject );
                MenuCommand itemDebugProject = new MenuCommand( TestProjectCodeCallback, cmdDebugProject );
                mcs.AddCommand( itemDebugProject );

                // ComTest -> Options
                CommandID cmdOptions = new CommandID( GuidList.guidComTestCSPluginCmdSet, (int)PkgCmdIDList.cmdidOptions );
                MenuCommand itemOptions = new MenuCommand( OptionsItemCallback, cmdOptions );
                mcs.AddCommand( itemOptions );

                // ComTest -> Test Project (solution explorer)
                CommandID cmdTestProjectExp = new CommandID( GuidList.guidComTestCSPluginCmdSet, (int)PkgCmdIDList.cmdidTestProjectExp );
                MenuCommand itemTestProjectExp = new MenuCommand( TestProjectExpCallback, cmdTestProjectExp );
                mcs.AddCommand( itemTestProjectExp );
            }
        }
        #endregion

        /// <summary>
        /// This function is the callback used to execute a command when the a menu item is clicked.
        /// See the Initialize method to see how the menu item is associated to this function using
        /// the OleMenuCommandService service and the MenuCommand class.
        /// </summary>
        private void TestSolutionCallback( object sender, EventArgs e )
        {
            MenuCommand cmd = sender as MenuCommand;
            bool debug = cmd.CommandID.ID == PkgCmdIDList.cmdidDebugSolution;

            try
            {
                string slnFile = GetSolutionFile();

                // Save and close the solution before proceeding
                dte.Solution.Close( true );

                // Run ComTest
                RunComTest( slnFile );

                // Reload solution and run tests
                dte.Solution.Open( slnFile );
                RunVSTests( debug );
            }
            catch ( Exception ex )
            {
                ShowMessage( "ERROR: " + ex.Message );
            }
        }

        private void TestProjectCodeCallback( object sender, EventArgs e )
        {
            MenuCommand cmd = sender as MenuCommand;
            bool debug = cmd.CommandID.ID == PkgCmdIDList.cmdidDebugProject;

            try
            {
                string slnFile = GetSolutionFile();
                string projName = GetCodeProjectName();

                // Save and close the solution before proceeding
                dte.Solution.Close( true );

                // Run ComTest
                RunComTest( String.Format( "{0}*{1}", slnFile, projName ) );

                // Reload solution and run tests
                dte.Solution.Open( slnFile );
                RunVSTests( debug );
            }
            catch ( Exception ex )
            {
                ShowMessage( "ERROR: " + ex.Message );
            }
        }

        private void TestProjectExpCallback( object sender, EventArgs e )
        {
            try
            {
                string slnFile = GetSolutionFile();
                string projName = GetExpProjectName();

                // Save and close the solution before proceeding
                dte.Solution.Close( true );

                // Run ComTest
                RunComTest( String.Format( "{0}*{1}", slnFile, projName ) );

                // Reload solution and run tests
                dte.Solution.Open( slnFile );
                RunVSTests( false );
            }
            catch ( Exception ex )
            {
                ShowMessage( "ERROR: " + ex.Message );
            }
        }

        private void OptionsItemCallback(object sender, EventArgs e)
        {
            OptionsForm frmOptions = new OptionsForm( (DTE2)dte );
            frmOptions.Show();
            settings = frmOptions.Settings;
        }

        bool SolutionContainsTests()
        {
            foreach ( Project proj in dte.Solution.Projects )
            {
                try
                {
                    string[] guids = EnvironmentDTE.GetProjectTypeGuids( proj ).Split( ';' );

                    for ( int i = 0; i < guids.Length; i++ )
                    {
                        if ( String.Compare( guids[i], GUID_TEST_PROJECT, true ) == 0 )
                            return true;
                    }
                }
                catch ( Exception e )
                {
                }
            }

            return false;
        }

        void RunComTest( string inputFile )
        {
            System.Diagnostics.Process ctProc = new System.Diagnostics.Process();
            ctProc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;
            ctProc.StartInfo.UseShellExecute = false;
            ctProc.StartInfo.RedirectStandardOutput = true;
            ctProc.StartInfo.FileName = settings.JavaPath;
            ctProc.StartInfo.Arguments = string.Format( "-jar {0} \"{1}\"", settings.ComtestPath, inputFile );

            if ( !ctProc.Start() )
            {
                ShowMessage( "ERROR: Could not launch ComTest." );
                return;
            }

            if ( outPane == null )
                outPane = CreatePane( Guid.NewGuid(), "ComTest", true, false );

            outPane.Clear();
            outPane.OutputString( "-- Begin running ComTest --\n" );
            ctProc.WaitForExit();
            string output = ctProc.StandardOutput.ReadToEnd();

            if ( output.Length == 0 )
            {
                outPane.OutputString( "No output from ComTest. Please check the ComTest settings for a correct path.\n" );
            }
            else
                outPane.OutputString( output );  // Capture output

            outPane.OutputString( "\n-- End running ComTest --\n" );
        }

        void RunVSTests( bool debug )
        {
            Window outputWindow = dte.Windows.Item( EnvDTE.Constants.vsWindowKindOutput );
            outputWindow.Activate();
            outPane.Activate();

            if ( SolutionContainsTests() )
            {
                if ( GetDTEVersion() >= 11 || GetDTEVersion() < 8 )
                {
                    // No macro support...
                    outPane.OutputString("Tests successfully generated. Use the Test / Run -menu to execute them.");
                    return;
                }

                if ( debug )
                    dte.ExecuteCommand( "Test.DebugAllTestsInSolution" );
                else
                    dte.ExecuteCommand( "Test.RunAllTestsInSolution" );
            }
        }

        private double GetDTEVersion()
        {
            double version = 0;
            double.TryParse( dte.Version, NumberStyles.Any, CultureInfo.GetCultureInfo("en-US"), out version );
            return version;
        }

        private string GetSolutionFile()
        {
            IVsSolution sService = (IVsSolution)GetService( typeof( SVsSolution ) );
            String slnDir, slnFile, optFile;

            if ( sService.GetSolutionInfo( out slnDir, out slnFile, out optFile ) != VSConstants.S_OK )
                throw new InvalidOperationException( "Could not get solution information." );

            if ( slnFile == null )
                throw new NullReferenceException( "Could not get solution file." );

            return slnFile;
        }

        private string GetCodeProjectName()
        {
            DTE dte = EnvironmentDTE.FindCurrent();

            if ( dte.ActiveDocument == null )
                throw new NullReferenceException( "Could not find an open code window." );

            return dte.ActiveDocument.ProjectItem.ContainingProject.Name;
        }

        private string GetExpProjectName()
        {
            Project proj = GetActiveProject();

            if ( proj == null )
                throw new NullReferenceException( "Could not get active project." );

            return proj.Name;
        }

        private static Project GetActiveProject()
        {
            DTE dte = EnvironmentDTE.FindCurrent();
            Project activeProject = null;

            Array activeSolutionProjects = dte.ActiveSolutionProjects as Array;
            if ( activeSolutionProjects != null && activeSolutionProjects.Length > 0 )
            {
                activeProject = activeSolutionProjects.GetValue( 0 ) as Project;
            }

            return activeProject;
        }

        public void ShowMessage( string msg )
        {
            IVsUIShell uiShell = (IVsUIShell)GetService( typeof( SVsUIShell ) );
            Guid clsid = Guid.Empty;
            int result;

            Microsoft.VisualStudio.ErrorHandler.ThrowOnFailure(
                uiShell.ShowMessageBox(
                  0,
                  ref clsid,
                  "ComTest",
                  string.Format( CultureInfo.CurrentCulture, msg,
                    this.ToString() ),
                  string.Empty,
                  0,
                  OLEMSGBUTTON.OLEMSGBUTTON_OK,
                  OLEMSGDEFBUTTON.OLEMSGDEFBUTTON_FIRST,
                  OLEMSGICON.OLEMSGICON_INFO,
                  0,        // false
                  out result )
                );
        }
    }
}
