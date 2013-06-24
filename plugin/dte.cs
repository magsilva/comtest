using System;
using System.Diagnostics;
using System.Runtime.InteropServices;
using EnvDTE;
using Microsoft.VisualStudio.OLE.Interop;

namespace JYU.ComTestCSPlugin
{
    /// <summary>
    ///   Derived from http://forums.microsoft.com/MSDN/ShowPost.aspx?PostID=243030&SiteID=1
    /// </summary>
    public static class EnvironmentDTE
    {
        [DllImport( "ole32.dll", ExactSpelling = true )]
        private static extern int CreateBindCtx( int reserved, out IBindCtx ppbc );
        [DllImport( "ole32.dll", ExactSpelling = true )]
        private static extern int GetRunningObjectTable( int reserved, out IRunningObjectTable pprot );

        /// <summary>
        ///  Gets the currently running Visual Studio IDE that our control is contained in.
        /// </summary>
        /// <returns></returns>
        public static DTE FindCurrent()
        {
            IBindCtx ppbc = null;
            IEnumMoniker ppenumMoniker = null;
            IRunningObjectTable pprot = null;
            DTE dteObject = null;

            try
            {
                int pID = System.Diagnostics.Process.GetCurrentProcess().Id;
                int hr = GetRunningObjectTable( 0, out pprot );
                Marshal.ThrowExceptionForHR( hr );
                if ( hr == Microsoft.VisualStudio.VSConstants.S_OK )
                {
                    hr = CreateBindCtx( 0, out ppbc );
                    Marshal.ThrowExceptionForHR( hr );
                    pprot.EnumRunning( out ppenumMoniker );
                    while ( ppenumMoniker != null )
                    {
                        IMoniker[] rgelt = new IMoniker[1];
                        uint pceltFetched = 0;
                        hr = ppenumMoniker.Next( 1, rgelt, out pceltFetched );
                        Marshal.ThrowExceptionForHR( hr );
                        for ( uint i = 0; i < pceltFetched; i++ )
                        {
                            string Name = string.Empty;
                            rgelt[i].GetDisplayName( ppbc, null, out Name );
                            if ( !string.IsNullOrEmpty( Name )
                                && Name.IndexOf( "!VisualStudio.DTE" ) == 0
                                && Name.LastIndexOf( pID.ToString() ) > 0 )
                            {
                                object ppunk = null;
                                pprot.GetObject( rgelt[i], out ppunk );
                                if ( ppunk != null && ppunk is DTE ) dteObject = (DTE)ppunk;
                            }
                            Marshal.ReleaseComObject( rgelt[i] );
                        }
                        if ( pceltFetched < 1 || dteObject != null ) break;
                    }
                }
            }
            catch ( Exception ex )
            {
                Debug.Assert( false, ex.Message );
            }
            finally
            {
                if ( ppenumMoniker != null ) Marshal.ReleaseComObject( ppenumMoniker );
                if ( ppbc != null ) Marshal.ReleaseComObject( ppbc );
                if ( pprot != null ) Marshal.ReleaseComObject( pprot );
            }

            return dteObject;
        }

        public static string GetProjectTypeGuids( EnvDTE.Project proj )
        {

            string projectTypeGuids = "";
            object service = null;
            Microsoft.VisualStudio.Shell.Interop.IVsSolution solution = null;
            Microsoft.VisualStudio.Shell.Interop.IVsHierarchy hierarchy = null;
            Microsoft.VisualStudio.Shell.Interop.IVsAggregatableProject aggregatableProject = null;
            int result = 0;

            service = GetService( proj.DTE, typeof( Microsoft.VisualStudio.Shell.Interop.IVsSolution ) );
            solution = (Microsoft.VisualStudio.Shell.Interop.IVsSolution)service;

            result = solution.GetProjectOfUniqueName( proj.UniqueName, out hierarchy );

            if ( result == 0 )
            {
                aggregatableProject = (Microsoft.VisualStudio.Shell.Interop.IVsAggregatableProject)hierarchy;
                result = aggregatableProject.GetAggregateProjectTypeGuids( out projectTypeGuids );
            }

            return projectTypeGuids;
        }

        public static object GetService( object serviceProvider, System.Type type )
        {
            return GetService( serviceProvider, type.GUID );
        }

        public static object GetService( object serviceProviderObject, System.Guid guid )
        {

            object service = null;
            Microsoft.VisualStudio.OLE.Interop.IServiceProvider serviceProvider = null;
            IntPtr serviceIntPtr;
            int hr = 0;
            Guid SIDGuid;
            Guid IIDGuid;

            SIDGuid = guid;
            IIDGuid = SIDGuid;
            serviceProvider = (Microsoft.VisualStudio.OLE.Interop.IServiceProvider)serviceProviderObject;
            hr = serviceProvider.QueryService( ref SIDGuid, ref IIDGuid, out serviceIntPtr );

            if ( hr != 0 )
            {
                System.Runtime.InteropServices.Marshal.ThrowExceptionForHR( hr );
            }
            else if ( !serviceIntPtr.Equals( IntPtr.Zero ) )
            {
                service = System.Runtime.InteropServices.Marshal.GetObjectForIUnknown( serviceIntPtr );
                System.Runtime.InteropServices.Marshal.Release( serviceIntPtr );
            }

            return service;
        }
    }
}
