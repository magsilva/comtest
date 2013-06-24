// ComTest - Comments for testing
package cc.jyu.fi.comtest.csharp;

import java.util.UUID;

import cc.jyu.fi.comtest.ComTestException;
import cc.jyu.fi.comtest.utils.Strings;
import static cc.jyu.fi.comtest.csharp.CSProjectFile.*;
import static cc.jyu.fi.comtest.utils.FileUtilities.*;

/**
 * Makes a test project for a C# project
 * @author tojukarp
 */
public class TestProjectProcessor {
    private String relSourceFile;
    private String relDestFile;
    private String sourceDir;
    private String destDir;

    /**
     * Creates a new test processor
     * @param srcFile Absolute path to original project file
     * @param dstFile Absolute path to generated test file
     */
    public TestProjectProcessor(String srcFile, String dstFile) {
        sourceDir = findDirectory(srcFile);
        destDir = findDirectory(dstFile);
        relSourceFile = relativePath(srcFile, destDir);
        relDestFile = relativePath(dstFile, destDir);
    }

    /**
     * Parses a project and creates a test project for it.
     * @param project Original project
     * @param file Path to project file (for reference)
     * @return Test project
     * @throws ComTestException on error
     */
    public CSProjectFile process(CSProjectFile project) throws ComTestException {
        if ( project.projectTypeIs(CSProjectFile.ProjectType_SilverlightWP7) )
            throw new ComTestException("Silverlight projects are not supported yet.", true);

        Strings testFileContents = readResourceFile("csharp/testproject.template", "UTF-8");
        CSProjectFile testfile = CSProjectFileReader.read(testFileContents);
        
        testfile.setAssemblyName(project.getAssemblyName() + "Test");
        testfile.setGuid(UUID.randomUUID());
        testfile.setMainPropertyValue("TargetFrameworkVersion", project.getMainPropertyValue("TargetFrameworkVersion"));

        Strings refPaths = project.getMainPropertyValues("ReferencePath");
        testfile.addMainPropertyValues("ReferencePath", refPaths);

        testfile.addReference( new ProjectReference(project, relSourceFile) );
        for ( Reference ref : project.getReferences() ) {
            if ( ref.assemblyName.contains(project.getAssemblyName() + "Content") )
                continue;

            if ( ref instanceof ExternalReference || ref instanceof ProjectReference ) {
                ExternalReference eref = (ExternalReference)ref;
                eref.hintPath = relativePath( absolutePath(eref.hintPath, sourceDir), destDir );
            }

            testfile.addReference(ref);
        }

        return testfile;
    }
}
