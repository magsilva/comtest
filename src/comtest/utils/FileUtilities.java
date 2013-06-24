package comtest.utils;

import java.util.Scanner;
import java.io.*;
import static comtest.utils.StringUtilities.*;

/**
 * A collection of static file and directory handling
 * functions.
 * @author tojukarp
 */
public abstract class FileUtilities {
    // #import comtest.utils.Strings;

    private static class SingleFilenameFilter implements FilenameFilter {
        private String acceptFileName;
        
        public SingleFilenameFilter(String fileName) {
            acceptFileName = fileName;
        }
        public boolean accept(File dir, String name) {
            return name.equalsIgnoreCase(acceptFileName);
        }
    }
    
    /**
     * Finds the directory of a given file name.
     * The file path can be either absolute or relative.
     * @param fileName The file name
     * @return Absolute directory path, or null if exception
     * @example
     * <pre name="test">
     * findDirectory("C:\\a\\b\\c.txt") === "C:\\a\\b"
     * findDirectory("C:\\") === "C:"
     * </pre>
     */
    public static String findDirectory(String fileName) {
        try {
            File file = new File(fileName);
            String path = file.getCanonicalPath();
            int lastSlash = path.lastIndexOf("\\");
            return path.substring(0, lastSlash);
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * Makes an absolute path from relative one.
     * Returns the path itself, if already absolute.
     * @param relativePath Input path
     * @param rootPath Path the relative path is relative to
     * @return Absolute path
     * @example
     * <pre name="test">
     * absolutePath("t1.txt", "C:\\tiedostot") === "C:\\tiedostot\\t1.txt";
     * absolutePath("t1.txt", "C:\\tiedostot\\") === "C:\\tiedostot\\t1.txt";
     * absolutePath("alfa\\beta.txt", "C:\\gamma") === "C:\\gamma\\alfa\\beta.txt";
     * //absolutePath("..\\beta.txt", "C:\\gamma\\delta") === "C:\\gamma\\beta.txt";
     * //absolutePath("\\beta.txt", "C:\\gamma\\delta") === "C:\\beta.txt";
     * </pre>
     */
    public static String absolutePath(String relativePath, String rootPath) {
        File file = new File(relativePath);
        if ( file.isAbsolute() )
            return relativePath;

        return addPath(rootPath, relativePath);
    }

    /**
     * Makes a relative path from absolute one.
     * @param absolutePath Absolute path
     * @param rootPath Path the relative path should be relative to
     * @return Relative path
     * @example
     * <pre name="test">
     * String abs = "E:\\anlakane\\testit\\EkaComtesti\\Program.cs";
     * String root1 = "E:\\anlakane\\testit\\EkaComtesti";
     * String root2 = "E:\\anlakane\\testit\\TokaComtesti";
     * relativePath(abs, root1) === "Program.cs";
     * relativePath(abs, root2) === "..\\EkaComtesti\\Program.cs";
     * relativePath(abs, "D:\\") === abs;
     * relativePath("D:\\a\\b\\cd.ef", "D:\\a\\b\\cdef") === "..\\cd.ef";
     * relativePath("D:\\a\\b\\cdef.g", "D:\\a\\b\\cdef") === "..\\cdef.g";
     * </pre>
     */
    public static String relativePath(String absolutePath, String rootPath) {
        if ( isEmpty(rootPath) )
            return absolutePath;
        
        String[] pathElements = absolutePath.split("\\\\");
        String[] rootElements = rootPath.split("\\\\");
        int sameElements = 0;

        for ( int i = 0; i < Math.min( pathElements.length, rootElements.length ); i++ ) {
            if ( rootElements[i].equalsIgnoreCase(pathElements[i]) )
                sameElements++;
            else
                break;
        }

        if ( sameElements == 0 )
            // Different drive
            return absolutePath;

        if ( sameElements == pathElements.length )
            // Same as root path
            return ".";

        StringBuilder relPath = new StringBuilder();

        for ( int i = 0; i < rootElements.length - sameElements; i++ ) {
            // Go back
            relPath.append("..\\");
        }

        for ( int i = 0; i < pathElements.length - sameElements; i++ ) {
            // Go forward
            relPath.append(pathElements[sameElements + i]).append("\\");
        }

        // Remove the last backslash
        if ( relPath.length() > 0 )
            relPath.deleteCharAt(relPath.length() - 1);

        return relPath.toString();
    }

    /**
     * Finds the directory of a given file name, starting from startingDir
     * and then climbing up until the file is found.
     * @param startingDir Directory to start climbing from
     * @param fileName File name to find
     * @return Absolute directory path, or null if not found.
     * @example
     * <pre name="test">
     * findFirstDirectory("C:\\thisis\\nota\\real\\directory", "fnord") === null;
     * </pre>
     */
    public static String findFirstDirectory(String startingDir, String fileName) {

        File dir = new File(startingDir);
        FilenameFilter filter = new SingleFilenameFilter(fileName);

        while ( dir != null ) {
            File[] files = dir.listFiles();

            if ( files == null )
                // No such directory
                return null;
            
            if ( files.length > 0 ) {
                try {
                    return dir.getCanonicalPath();
                } catch (IOException ioe) {
                    return null;
                }
            }

            dir = dir.getParentFile();
        }

        return null;
    }

    /**
     * Adds a path to file name. Paths with or without the
     * trailing slash are supported.
     * @param path Path
     * @param file File name without path, can also be a directory name
     * @return File (or directory) name with path
     * @example
     * <pre name="test">
     * addPath("D:\\1ab\\so", "lute") === "D:\\1ab\\so\\lute";
     * addPath("D:\\2ab\\so", "") === "D:\\2ab\\so";
     * addPath("D:\\3ab\\so", ".") === "D:\\3ab\\so";
     * addPath("D:\\4ab\\so", "D:\\ab\\so") === "D:\\4ab\\so\\D:\\ab\\so";
     * addPath("5rel\\at", "iv.e") === "5rel\\at\\iv.e";
     * addPath("6rel\\at\\", "iv.e") === "6rel\\at\\iv.e";
     * </pre>
     */
    public static String addPath(String path, String file) {
        if ( path.equals(".") || isEmpty(path) )
            return file;

        if ( file.equals(".") || isEmpty(file) )
            return path;

        if ( file.startsWith("\\") )
            return file;

        if ( path.endsWith("\\") )
            return path + file;
        
        return path + "\\" + file;
    }

    /**
     * Makes one or more directories until the given
     * directory path exists.
     * @param dir Directory path to make
     * @return True if success
     */
    public static boolean makeDirs(String dir) {
        return new File(dir).mkdirs();
    }

    /**
     * Makes the necessary directories for the given
     * file to exist in.
     * @param file File name with path
     * @return True if success
     */
    public static boolean makeDirsForFile(String file) {
        return makeDirs(findDirectory(file));
    }

    /**
     * Gets the current working directory, null if I/O error
     * @return Current directory or null
     */
    public static String getCurrentDir() {
        try {
            return new File(".").getCanonicalPath();
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * Reads a file resource from the .jar file
     * @param resourceName Resource file name to read
     * @param charset Character set to use
     * @return Strings
     * @example
     * <pre name="test">
     * Strings s = readResourceFile("csharp/testproject.template", "utf-8");
     * s.size() === 60;
     * </pre>
     */
    public static Strings readResourceFile(String resourceName, String charset) {
        Strings lines = new Strings();
        Scanner in;

        ClassLoader loader = FileUtilities.class.getClassLoader();
        InputStream resStream = loader.getResourceAsStream(resourceName);
        in = new Scanner(resStream, charset);

        try {
            while ( in.hasNextLine() ) lines.add( in.nextLine() );
        } finally {
            in.close();
        }
        
        return lines;
    }
}
