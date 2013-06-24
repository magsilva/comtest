// ComTest - Comments for testing
package comtest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.io.File;

import comtest.legacy.ComTestScanner;
import comtest.utils.Strings;
import comtest.csharp.*;
import java.util.ArrayList;

import static comtest.utils.StringUtilities.*;
import static comtest.utils.FileUtilities.*;

/**
 * ComTest main class
 * @author tojukarp
 */
public class ComTest {
    private static class FileEntry
    {
        String fileName;
        Language language = Language.Unknown;
        Strings subentries = null;

        public FileEntry( String fileName )
        {
            this.fileName = fileName;
        }

        public FileEntry( String fileName, Language language )
        {
            this.fileName = fileName;
            this.language = language;
        }
    }
    
    private static class ArgumentInfo {
        //boolean forceLanguage = false;
        //Language forcedLanguage = Language.Unknown;
        List<FileEntry> files = new ArrayList<FileEntry>();

        public FileEntry addFile(String fileName, Language language)
        {
            FileEntry newEntry;
            int separator = fileName.indexOf('*');

            if ( separator > 0 )
            {
                String namePart = fileName.substring(0, separator);
                String[] subentries = fileName.substring(separator + 1).split(",");
                newEntry = new FileEntry( namePart );
                newEntry.subentries = new Strings();
                newEntry.subentries.addAll(Arrays.asList(subentries));
            }
            else
            {
                newEntry = new FileEntry( fileName );
            }

            newEntry.language = ( language != Language.Unknown ) ? language : detectLanguage(newEntry.fileName);
            files.add(newEntry);
            return newEntry;
        }
    }

    public enum Language { Unknown, Java, CPP, Csharp, CsharpProject, CsharpSolution }

    private static Map<String, ComTestIni> iniFiles = new HashMap<String, ComTestIni>();
    //private static CSProjectFile lastTestProjectFile;
    private static int globalIndent = 0;
    private static int errors = 0;

    /**
     * @param args name of files to process
     */
    public static void main(String[] args) {
        ArgumentInfo argInfo = parseArguments(args);
        if ( argInfo == null ) {
            usage();
            return;
        }

        for ( FileEntry entry : argInfo.files )
            printAndProcessFile(entry, getOutFileName(entry) , null);

        if ( errors > 0 ) {
            System.out.println();
            System.out.println(String.format("!!! There were errors in %d file(s) !!!", errors));
        }
    }

    private static ArgumentInfo parseArguments(String[] args) {
        if ( args.length == 0 )
            return null;

        ArgumentInfo argInfo = new ArgumentInfo();
        int firstFileIndex = 1;
        Language forcedLanguage = Language.Unknown;

        if ( args[firstFileIndex-1].equalsIgnoreCase("java") ) forcedLanguage = Language.Java;
        else if ( args[firstFileIndex-1].equalsIgnoreCase("c++") ) forcedLanguage = Language.CPP;
        else if ( args[firstFileIndex-1].equalsIgnoreCase("c#") ) forcedLanguage = Language.Csharp;
        else if ( args[firstFileIndex-1].equalsIgnoreCase("csproj") ) forcedLanguage = Language.CsharpProject;
        else if ( args[firstFileIndex-1].equalsIgnoreCase("sln") ) forcedLanguage = Language.CsharpSolution;
        else firstFileIndex = 0;

        StringBuilder continuousName = null;

        for ( int i = firstFileIndex; i < args.length; i++ ) {
            if ( args[i].length() == 0 )
            {
                if ( continuousName != null )
                    continuousName.append(" ");
                continue;
            }

            int lastch = args[i].length() - 1;

            if( continuousName != null && args[i].charAt(lastch) == '"' ) {
                continuousName.append(args[i]);
                continuousName.setLength(continuousName.length() - 1);
                argInfo.addFile(continuousName.toString(), forcedLanguage);
                continuousName = null;
            }
            else if(args[i].charAt(0) == '"' && args[i].charAt(lastch) != '"') {
                continuousName = new StringBuilder(args[i]);
                continuousName.delete(0, 1);
            }
            else if ( continuousName != null )
                continuousName.append(args[i]);
            else
                argInfo.addFile(args[i], forcedLanguage);
        }

        return argInfo;
    }

    private static void usage() {
        System.out.println("Run: java comtest.ComTest [language] filename(s)");
        System.out.println("or: java -jar comtest.jar [language] filename(s)");
        System.out.println();
        System.out.println("Supported languages are: Java, C++, C#, C# projects and solutions");
        System.out.println("The language is detected automatically for every file if unspecified.");
        System.out.println();
        System.out.println("If you want to specify which projects to process in a C# solution, you can use");
        System.out.println("java -jar comtest.jar Solution.sln:proj1,proj2");
    }

    public static Language detectLanguage(String filename) {
        int fnlen = filename.length();

        if (fnlen > 6 && filename.substring(fnlen - 6).equalsIgnoreCase("csproj"))
            return Language.CsharpProject;
        else if ( fnlen > 5 && filename.substring(fnlen - 4).equalsIgnoreCase("java") )
            return Language.Java;
        else if ( fnlen > 4 && filename.substring(fnlen - 3).equalsIgnoreCase("cpp") )
            return Language.CPP;
        else if ( fnlen > 4 && filename.substring(fnlen - 3).equalsIgnoreCase("sln") )
            return Language.CsharpSolution;
        else if ( fnlen > 3 && filename.substring(fnlen - 2).equalsIgnoreCase("cs") )
            return Language.Csharp;

        return Language.Unknown;
    }

    /**
     * Prints the status information and processes a file.
     * @param fileEntry File to process
     * @param outName File to create
     * @param rootPath Path the file name is relative to (null if none)
     * @return Result of the operation
     */
    private static ComTestResult printAndProcessFile(
            FileEntry entry, String outName, String rootPath)
    {
        String relIn = relativePath(entry.fileName, rootPath);
        String relOut = relativePath(outName, rootPath);

        System.out.print(repeat("  ", globalIndent));
        System.out.print(relIn);
        System.out.print(" (");
        System.out.print(entry.language.name());
        System.out.print(")");

        try {
            ComTestResult result = processFile(entry, outName);

            if ( result instanceof ComTestResult.Success ) {
                System.out.print(" => ");
                System.out.print(relOut);
                System.out.println(" ok");
            }
            else if ( result instanceof ComTestResult.SkippedAsGood ||
                        result instanceof ComTestResult.SkippedAsBad )
            {
                System.out.println(" || skipped: " + result.getMessage());
            }
            else if ( result instanceof ComTestResult.Fail ) {
                System.out.println(" !! error: " + result.getMessage());
            }
            else if ( result instanceof ComTestResult.InternalError ) {
                System.out.println(" !* INTERNAL ERROR: " + result.getMessage());
            }
            
            return result;

        } catch ( Throwable t ) {
            System.out.println( " !* INTERNAL ERROR: " + t.getClass() );
            for ( StackTraceElement el : t.getStackTrace() ) {
                if ( el.isNativeMethod() ) continue;
                System.out.println( String.format("      at %s, line %d, method %s",
                    el.getFileName(), el.getLineNumber(), el.getMethodName()) );
            }
            return new ComTestResult.SkippedAsBad( "internal error" );
        }
    }

    private static void startIndentedBlock() {
        globalIndent++;
        System.out.println(" {");
    }

    private static void endIndentedBlock() {
        globalIndent--;
        System.out.print(repeat("  ", globalIndent));
        System.out.print("}");
    }

    private static String getOutFileName(FileEntry entry) {
        return getOutFileName(entry.fileName, entry.language);
    }

    private static String getOutFileName(String fileName, Language language) {
        String outname;

        if ( language == Language.CsharpSolution ) return fileName;
        if ( language == Language.CsharpProject )
            outname = getIniFile(findDirectory(fileName)).getOutProjectName(fileName);
        else
            outname = getIniFile(findDirectory(fileName)).getOutFileName(fileName);

        return outname;
    }

    /**
     * Process the file and make a unit test
     * @param entry file to process
     * @param outName the name of the file to generate
     * @return Result of the process
     */
    public static ComTestResult processFile(FileEntry entry, String outName)
    {
        String fileName = entry.fileName;
        Language language = entry.language;
        Strings contents = getFileContents(fileName);

        if ( contents == null )
            return new ComTestResult.SkippedAsBad("file not found");
        if ( contents.size() < 1)
            return new ComTestResult.SkippedAsBad("file is empty");

        String inDir = findDirectory(fileName);
        ComTestIni ini = getIniFile(inDir);

        if ( language == Language.Java ) {
            ComTestScanner scanner = new comtest.java.ComTestScanner(ini, contents);
            scanner.scan(fileName);
            return new ComTestResult.Success("OK");
        }
        if ( language == Language.CPP ) {
            ComTestScanner scanner = new comtest.cpp.ComTestScanner(ini, contents);
            scanner.scan(fileName);
            return new ComTestResult.Success("OK");
        }
        if ( language == Language.Csharp ) {
            return processCsFile(contents, outName, ini);
        }
        if ( language == Language.CsharpProject ) {
            return processCsProject(fileName, contents, outName);
        }
        if ( language == Language.CsharpSolution ) {
            return processCsSolution(fileName, entry.subentries, contents);
        }

        return new ComTestResult.InternalError("unknown language in processFile!");
    }

    private static ComTestResult processCsFile(Strings contents, String outName, ComTestIni ini) {
        CSSourceFile source;

        try {
            source = CSReader.read(contents);
        } catch ( ComTestException cte ) {
            return new ComTestResult.Fail("could not read file - " + cte.getMessage());
        }

        ComTestProcessor comtestProc = new ComTestProcessor(ini);
        CSSourceFile tests;

        try {
            tests = comtestProc.process(source);
            if ( tests == null )
                return new ComTestResult.SkippedAsBad("no tests");

        } catch ( ComTestException cte ) {
            return new ComTestResult.Fail("could not process file - " + cte.getMessage());
        }

        Strings dest = CSWriter.write(tests);
        makeDirsForFile(outName);
        writeToFile(dest, outName);

        return new ComTestResult.Success("OK", tests);
    }

    private static ComTestResult processCsProject(String fileName, Strings contents, String outName) {
        String inDir = findDirectory(fileName);
        String outDir = findDirectory(outName);
        CSProjectFile projfile;

        try {
            projfile = CSProjectFileReader.read(contents);
        } catch (ComTestException cte) {
            return new ComTestResult.Fail("could not read project - " + cte.getMessage());
        }

        if ( projfile.projectTypeIs(CSProjectFile.ProjectType_Test) ) {
            // Test project, skip this
            return new ComTestResult.SkippedAsBad("test project");
        }

        CSProjectFile testProject;

        try {
            TestProjectProcessor processor = new TestProjectProcessor(fileName, outName);
            testProject = processor.process(projfile);
        } catch (ComTestException cte) {
            return new ComTestResult.Fail("could not process project - " + cte.getMessage());
        }

        // Copy the project GUID and assembly name from existing test file
        if ( new File(outName).exists() ) {
            Strings oldContents = getFileContents(outName);
            try {
                CSProjectFile oldTestProject = CSProjectFileReader.read(oldContents);
                testProject.setGuid( oldTestProject.getGuid() );
                testProject.setAssemblyName( oldTestProject.getAssemblyName() );

                // Add all source files
                for ( String oldSourceFile : oldTestProject.getSourceFiles() )
                    testProject.addSourceFile(oldSourceFile);

            } catch (ComTestException cte) {}
        }

        List<String> sourceFiles = projfile.getSourceFiles();
        List<String> testFiles = testProject.getSourceFiles();
        boolean modified = false;
        int goodFiles = 0;

        if ( sourceFiles.size() > 0 ) {
            // Create the test project directory
            makeDirs(outDir);

            startIndentedBlock();

            for ( String relSourceFile : sourceFiles ) {
                // Process all source files in project
                String sourceFile = addPath( inDir, relSourceFile );
                String relTestFile = justFileWithExtension(getOutFileName(relSourceFile, detectLanguage(relSourceFile)));
                String testFile = addPath( outDir, relTestFile );

                FileEntry sourceEntry = new FileEntry(sourceFile, detectLanguage(sourceFile));
                ComTestResult testRes = printAndProcessFile(sourceEntry, testFile, outDir);

                boolean goodFile = (
                    testRes instanceof ComTestResult.Success ||
                    testRes instanceof ComTestResult.SkippedAsGood
                );
                boolean inProject = testFiles.contains(relTestFile);

                if ( goodFile ) goodFiles++;

                if ( goodFile && !inProject ) {
                    testProject.addSourceFile(relTestFile);
                    modified = true;
                }
                else if ( !goodFile && inProject ) {
                    testProject.removeSourceFile(relTestFile);
                    modified = true;
                }
            }

            endIndentedBlock();
        }

        if ( goodFiles == 0 ) {
            return new ComTestResult.SkippedAsBad("no tests");
        }

        if ( !modified ) {
            // No files to add or remove, do not create the project
            return new ComTestResult.SkippedAsGood("no changes", testProject);
        }

        // Write the test project file
        try {
            Strings dest = CSProjectFileWriter.write(testProject);
            writeToFile(dest, outName);
        } catch (ComTestException cte) {
            return new ComTestResult.Fail("could not write test project - " + cte.getMessage());
        }

        // Create the properties (assembly info) file
        String propDir = addPath( outDir, testProject.getDesignerFolder() );
        String propFile = addPath( propDir, "AssemblyInfo.cs" );

        if ( !new File(propFile).exists() ) {
            CSSourceFile propfile = testProject.GeneratePropertiesFile();
            makeDirs( propDir );
            Strings proplines = CSWriter.write(propfile);
            writeToFile(proplines, propFile);
        }

        return new ComTestResult.Success("OK", testProject);
    }

    private static ComTestResult processCsSolution(String fileName, Strings projects, Strings contents) {
        String slnDir = findDirectory(fileName);
        boolean modified = false;

        // Backup the solution first
        String backupName = fileName + ".ctbackup";
        File backupFile = new File(backupName);
        if ( !backupFile.exists() )
            writeToFile(contents, backupName);

        // Read the solution
        CSSolutionFile slnFile = CSSolutionReader.read(contents);
        if ( slnFile == null ) {
            return new ComTestResult.Fail("Could not open the solution file.");
        }
        
        startIndentedBlock();

        // Take a snapshot of projects to process
        HashSet<CSSolutionFile.ProjectEntry> oldProjects;
        oldProjects = new HashSet<CSSolutionFile.ProjectEntry>( slnFile.projects );

        for ( CSSolutionFile.ProjectEntry projEntry : oldProjects ) {
            String absFileName = addPath(slnDir, projEntry.fileName);

            // Process all projects
            if ( detectLanguage(absFileName) == Language.CsharpProject ) {
                if (projects != null && !projects.contains(justFilePart(absFileName)))
                    continue;

                FileEntry projectEntry = new FileEntry( absFileName, Language.CsharpProject );
                String testProjName = getOutFileName(projectEntry);

                ComTestResult testRes = printAndProcessFile(projectEntry, testProjName, slnDir);

                boolean goodProject = (
                    testRes instanceof ComTestResult.Success ||
                    testRes instanceof ComTestResult.SkippedAsGood
                );
                boolean inSolution =
                    slnFile.getProjectByName(projEntry.assemblyName + "Test") != null;
                    //slnFile.getProjectByName(lastTestProjectFile.getAssemblyName()) != null;

                String relProjName = relativePath(testProjName, slnDir);

                if ( goodProject && !inSolution ) {
                    CSProjectFile testProject = (CSProjectFile)testRes.getObject();
                    slnFile.addProject(testProject, relProjName);
                    modified = true;
                }
                else if ( !goodProject && inSolution ) {
                    slnFile.removeProject(relProjName);
                    modified = true;
                }
            }
        }

        endIndentedBlock();

        if ( !modified ) {
            // No files to add or remove, do not rewrite the solution
            return new ComTestResult.SkippedAsGood("no changes", slnFile);
        }

        // Write back the modified solution
        Strings dest = CSSolutionWriter.write(slnFile);
        writeToFile(dest, fileName);
        return new ComTestResult.Success("OK", slnFile);
    }

    private static ComTestIni getIniFile(String dir) {
        if ( iniFiles.containsKey(dir) )
            return iniFiles.get(dir);

        if ( iniFiles.size() >= 10 ) {
            // This is to limit how many ini files are cached simultaneously
            String keyToRemove = null;
            for ( String key : iniFiles.keySet() ) {
                keyToRemove = key;
                break;
            }
            iniFiles.remove(keyToRemove);
        }

        ComTestIni newIni = new ComTestIni(dir);
        iniFiles.put(dir, newIni);
        return newIni;
    }
}
