package comtest.utils;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

/**
 * A collection of static string handling functions
 * to make the string handling in ComTest more easy.
 * 
 * @author vesal
 * @version 22.9.2007
 */
public class StringUtilities { // NOPMD by vesal on 13.1.2008 15:27
    
    /**
     * Chars that are handled as quote chars 
     */
    public static final String QUOTES = "\"'";
    /*
      #PACKAGEIMPORT
     */    
    
    /**
     * Splits s to pieces from where.  Quotes is taken account
     * so where inside quotes, then it is ignored.
     * @param s string to split
     * @param where from where to split
     * @param jump how much delete from each part
     * @param trim to trim or not the parts
     * @return list of parts
     * @example
     * <pre name="testSplitBy">
     * String s = "first; second;\"third;3\";4;";
     * Strings p = splitBy(s,";",1);
     * p.size() === 5
     * p.get(0) === "first"; p.get(1) === " second"; p.get(2) === "\"third;3\""
     * p.get(3) === "4";     p.get(4) === ""
     * 
     * s = "first; second;\"third;3\";4";
     * p = splitBy(s,";",0,true);
     * p.size() === 4
     * p.get(0) === "first;"
     * p.get(1) === "second;"
     * p.get(2) === "\"third;3\";"
     * p.get(3) === "4"
     *
     * Strings elem = splitBy("( 1,  2, 3, \"1 2 3\")", ",", 1, false, " ()");
     * elem.size() === 4;
     * elem.get(0) === "1";
     * elem.get(1) === "2";
     * elem.get(2) === "3";
     * elem.get(3) === "\"1 2 3\"";
     * </pre>
     * 
     */
    public static Strings splitBy(String s, String where, int jump, boolean trim, String remove) {
        Strings parts = new Strings();
        int p = 0, p2; 
        while ( true ) {
          p2 = indexOfNotInQuotes(s, where, QUOTES, p);
          if ( p2 < 0 ) break;
          String part = s.substring(p, p2 + where.length() - jump);
          part = removeChars(part, remove);
          if ( trim ) part = part.trim();
          p = p2+where.length();
          parts.add(part);
        }

        String lastPart = s.substring(p);
        lastPart = removeChars(lastPart, remove);
        if ( trim ) lastPart = lastPart.trim();
        parts.add( lastPart );
        return parts;
    }

    public static Strings splitBy(String s, String where, int jump, boolean trim) {
        return splitBy(s, where, jump, trim, null);
    }
    
    /**
     * Splits s to pieces from where.  Quotes is taken account
     * so where inside quotes, then it is ignored.
     * @param s string to split
     * @param where from where to split
     * @param jump how much delete from each part
     * @return list of parts
     */ 
    public static Strings splitBy(String s, String where, int jump) {
       return splitBy(s, where, jump, false); 
    }

    
    /**
     * Splits s to pieces from where.  Quotes is taken account
     * so where inside quotes, then it is ignored.
     * @param s string to split
     * @param where from where to split
     * @return list of parts
     * 
     * @example
     * <pre name="test">
     * Strings p;
     * p = splitBy("one|two|three","|");
     * p.size() === 3;
     * p.get(0) === "one";
     * p.get(1) === "two";
     * p.get(2) === "three";
     * 
     * p = splitBy("one|","|");
     * p.size() === 2;
     * p.get(0) === "one";
     * p.get(1) === "";
     * 
     * p = splitBy("one","|");
     * p.size() === 1;
     * p.get(0) === "one";
     *
     * p = splitBy("","|");
     * p.size() === 1;
     * p.get(0) === "";
     *
     * </pre>
     */ 
    public static Strings splitBy(String s, String where) {
       return splitBy(s, where, where.length() , false); 
    }

    /**
     * Find first instance of what that is not inside of quotes 
     * @param s     from where to find
     * @param what  string to find
     * @param quote what chars are used as a quote
     * @param pos position where to start
     * @return index of first occurrence or -1 if not found
     */
    public static int indexOfNotInQuotes(String s, String what, String quote, int pos) { // NOPMD by vesal on 13.1.2008 15:27
        if ( quote.equals("") ) return s.indexOf(what); // NOPMD by vesal on 13.1.2008 15:27
        if ( quote.indexOf(what) >= 0 ) return s.indexOf(what);

        boolean inQuote = false;
        char quoteChar = 0;
        
        for (int i=pos; i<s.length(); i++) {
            if ( inQuote && s.charAt(i) == '\\'  ) { i += 1; continue; }
            int p = quote.indexOf(s.charAt(i));
            if ( !inQuote && p >= 0 )  {
                inQuote = true;
                quoteChar = quote.charAt(p);
                continue;
            } 
            if ( inQuote ) {
                if ( s.charAt(i) == quoteChar ) inQuote = false;
                continue;
            }
            if ( s.startsWith(what,i) ) return i; 
        }
        return -1;
    }

    
    /**
     * Find first instance of what that is not inside of quotes 
     * @param s     from where to find
     * @param what  string to find
     * @return index of first occurrence or -1 if not found
     */
    public static int indexOfNotInQuotes(String s, String what) {
      return indexOfNotInQuotes(s,what,QUOTES);  
    }

        
    /**
     * Find first instance of what that is not inside of quotes.
     * @param s     from where to find
     * @param what  string to find
     * @param quote what chars are used as a quote
     * @param pos position where to start
     * @return index of first occurrence or -1 if not found
     * TODO: faster version without copy of s 
     */ 
    public static int indexOfNotInQuotes(StringBuilder s, String what, String quote, int pos) {
        return indexOfNotInQuotes(s.toString(), what, quote, pos);
    }

    
    /**
     * Find first instance of what that is not inside of quotes 
     * @param s     from where to find
     * @param what  string to find
     * @param quote what chars are used as a quote
     * @return index of first occurrence or -1 if not found
     */ 
    public static int indexOfNotInQuotes(StringBuilder s, String what, String quote) {
        return indexOfNotInQuotes(s, what, quote, 0);
    }

    
    /**
     * Find first instance of what that is not inside of quotes 
     * @param s     from where to find
     * @param what  string to find
     * @return index of first occurrence or -1 if not found
     */
    public static int indexOfNotInQuotes(StringBuilder s, String what) {
      return indexOfNotInQuotes(s,what,QUOTES);  
    }

        
     /**
     * Find first instance of what that is not inside of quotes 
     * @param s     from where to find
     * @param what  string to find
     * @param quote what chars are used as a quote
     * @return index of first occurrence or -1 if not found
     * @example
     * <pre name="testIndexOfNotInQuotes">
     * indexOfNotInQuotes("a = a+1; // add one ", "//","\"")        === 9  
     * indexOfNotInQuotes("a = a+1; ", "//","\"")                   === -1  
     * indexOfNotInQuotes("s = \"//\"; // s is comment", "//","\"") === 10  
     * indexOfNotInQuotes("s = \"//\"; ", "//","\"")                === -1  
     * indexOfNotInQuotes("s = \"a\";  // \"//\" ", "//","\"")      === 10  
     * indexOfNotInQuotes("s = \"a\";  // \"//\" ", "\"","\"")      === 4  
     * indexOfNotInQuotes("s = \"a\";  // \"//\" ", "a","")         === 5  
     * indexOfNotInQuotes("s = \"a\"; c='a'; d=a; ", "a","\"'")     === 18  
     * </pre>
     */
    public static int indexOfNotInQuotes(String s, String what, String quote) {
        return indexOfNotInQuotes(s, what, quote,0);
    }

    
    /**
     * Removes n instances of what from the beginning of s
     * @param s where to remove
     * @param what string what to remove
     * @param n how many times to remove
     * @return string without what in the beginning
     * @example
     * <pre name="testRemoveFromBeginingN">
     * removeFromBegining("* a=a+1;","*",2)      === " a=a+1;"  
     * removeFromBegining("*** a=a+1;","*",2)    === "* a=a+1;"  
     * removeFromBegining("* ** a=a+1;","*",2)   === " ** a=a+1;"  
     * </pre>
     */
    public static String removeFromBegining(String s, String what, int n) {
        String result = s;
        int i = 0;
        while ( i < n && result.startsWith(what)) {
            i++;
            result = result.substring(what.length());
        }    
        return result;
    }

    
    /**
     * Removes all instances of what from the beginning of s
     * @param s where to remove
     * @param what string what to remove
     * @return string without what in the beginning
     * @example
     * <pre name="testRemoveFromBegining">
     * removeFromBegining("* a=a+1;","*")      === " a=a+1;"  
     * removeFromBegining("*** a=a+1;","*")    === " a=a+1;"  
     * removeFromBegining("* ** a=a+1;","*")   === " ** a=a+1;"  
     * </pre>
     */
    public static String removeFromBegining(String s, String what) {
        String result = s;
        while ( result.startsWith(what)) 
            result = result.substring(what.length()); 
        return result;
    }

    
    /**
     * Removes all instances of what from the beginning of s
     * If there is only spaces before s, then remove them also.
     * @param s where to remove
     * @param what string what to remove
     * @return string without what in the beginning
     * @example
     * <pre name="testRemoveFromBeginingTrim">
     * removeFromBeginingTrim("  * a=a+1;","*")      === " a=a+1;"  
     * removeFromBeginingTrim("  *** a=a+1;","*")    === " a=a+1;"  
     * removeFromBeginingTrim("  * ** a=a+1;","*")   === " ** a=a+1;"  
     * removeFromBeginingTrim("  a=a+1;","*")        === "  a=a+1;"  
     * </pre>
     */
    public static String removeFromBeginingTrim(String s, String what) {
        int p = indexOfNotInQuotes(s, what);
        if ( p < 0 ) return s;
        for ( int i=0; i<p; i++ ) {
            if ( !Character.isWhitespace(s.charAt(i)) ) return s;
        }
        String result = s.trim();
        while ( result.startsWith(what) ) 
            result = result.substring(what.length()); 
        return result;
    }

    
    /**
     * Returns a string where all after afterWhat string is removed
     * Also the afterWhat is removed.  If afterWhat is inside
     * quotes, then it is not affected. 
     * @param s string to handle
     * @param afterWhat string to look for
     * @return the handled string
     * @example
     * <pre name="test">
     * removeAllAfter("a = a+1; // add one ", "//")         === "a = a+1; "  
     * removeAllAfter("a = a+1; ","//")                     === "a = a+1; "  
     * removeAllAfter("s = \"//\"; // s is comment", "//" ) === "s = \"//\"; "  
     * removeAllAfter("s = \"//\"; " ,"//")                 === "s = \"//\"; "  
     * removeAllAfter("s = \"a\";  // \"//\" " ,"//")       === "s = \"a\";  "  
     * </pre>
     */
    public static String removeAllAfter(String s, String afterWhat) {
        int i = indexOfNotInQuotes(s,afterWhat,QUOTES);
        if ( i < 0 ) return s;
        return s.substring(0,i);
    }

    
    /**
     * Returns a string where all after afterWhat string is removed
     * Also the afterWhat is removed.  If afterWhat is inside
     * quotes, then it is not affected. 
     * @param s string to handle
     * @param afterWhat string to look for
     * @return true if something is removed
     * @example
     * <pre name="test">
     * StringBuilder sb;
     * sb = new StringBuilder($str); removeAllAfter(sb,$what) === $out; $sb.toString() === $result;
     * 
     *  $str                         | $what  | $result        | $out
     * --------------------------------------------------------------
     * "a = a+1; // add one "        | "//"   | "a = a+1; "    | true
     * "a = a+1; "                   | "//"   | "a = a+1; "    | false
     * "s = \"//\"; // s is comment" | "//"   | "s = \"//\"; " | true
     * "s = \"//\"; "                | "//"   | "s = \"//\"; " | false
     * "s = \"a\";  // \"//\" "      | "//"   | "s = \"a\";  " | true
     * </pre>
     */
    public static boolean removeAllAfter(StringBuilder s, String afterWhat) {
        int i = indexOfNotInQuotes(s,afterWhat,QUOTES);
        if ( i < 0 ) return false;
        s.delete(i,s.length());
        return true;
    }

    
    /**
     * Adds a new part to filename
     * @param fileName  filename to change
     * @param newPart part to add
     * @return filename + newPart
     * @example
     * <pre name="testAddToName">
     * addToName("my.java","Test") === "myTest.java"
     * addToName("my","Test") === "myTest"
     * </pre>
     */
    public static String addToName(String fileName, String newPart) {
        int i = fileName.lastIndexOf('.');
        if ( i < 0 ) return fileName + newPart;
        return fileName.substring(0,i) + newPart + fileName.substring(i);
    }


    /**
     * Returns just the file part from fileName. No path, no extension
     * @param fileName name where file parts is searched
     * @return only file name part from fileName
     * @example testJustFilePart
     * <pre name="testJustFilePart">
     * justFilePart("myTest.java")                     === "myTest"
     * justFilePart("myTest")                          === "myTest"
     * justFilePart("/users/my/java/myTest.java")      === "myTest"
     * justFilePart("/users/my.prg/java/myTest.java")  === "myTest"
     * justFilePart("/users/my.prg/java/myTest")       === "myTest"
     * justFilePart("/users/my.prg/java/myTest/")      === ""
     * justFilePart("e:\\users\\my.prg\\java\\myTest") === "myTest"
     * justFilePart("\\users\\my.prg\\java\\myTest")   === "myTest"
     * justFilePart("e:myTest.java")                   === "myTest"
     * </pre>
     */
    public static String justFilePart(String fileName) {
        int id = fileName.lastIndexOf('.');
        int is = fileName.lastIndexOf('/');
        int ib = fileName.lastIndexOf('\\');
        int idd = fileName.lastIndexOf(':');
        if ( idd > is ) is = idd;
        if ( ib > is ) is = ib;
        if ( id < is ) id = fileName.length();
        if ( id < 0 )  id = fileName.length();
        return fileName.substring(is+1,id);
    }

    /**
     * Returns just the file part from fileName. No path, but the
     * extension is kept.
     * @param fileName name where file parts is searched
     * @return only file name part from fileName
     * @example testJustFileWithExtension
     * <pre name="testJustFileWithExtension">
     * justFileWithExtension("myTest.java")                     === "myTest.java"
     * justFileWithExtension("myTest")                          === "myTest"
     * justFileWithExtension("/users/my/java/myTest.java")      === "myTest.java"
     * justFileWithExtension("/users/my.prg/java/myTest.java")  === "myTest.java"
     * justFileWithExtension("/users/my.prg/java/myTest")       === "myTest"
     * justFileWithExtension("/users/my.prg/java/myTest/")      === ""
     * justFileWithExtension("e:\\users\\my.prg\\java\\myTest") === "myTest"
     * justFileWithExtension("\\users\\my.prg\\java\\myTest")   === "myTest"
     * justFileWithExtension("e:myTest.java")                   === "myTest.java"
     * </pre>
     */
    public static String justFileWithExtension(String fileName) {
        int is = fileName.lastIndexOf('/');
        int ib = fileName.lastIndexOf('\\');
        int idd = fileName.lastIndexOf(':');
        if ( idd > is ) is = idd;
        if ( ib > is ) is = ib;
        return fileName.substring(is+1, fileName.length());
    }

    
    /**
     * Removes from s the first instance of what
     * @param s where to remove
     * @param what what string to remove 
     * @return s without first instance on what
     * @example
     * <pre name="test">
     * remove($s,$what) === $result;
     * 
     *   $s        |  $what |  $result 
     * ----------------------------------
     * "aab"       |  "a"   |  "ab"
     * "bab"       |  "a"   |  "bb"
     * "bba"       |  "a"   |  "bb"
     * </pre>
     * 
     * @example
     * <pre name="test">
     * remove($s,$what) === $result;
     * 
     *   $s        |  $what |  $result 
     * ----------------------------------
     * "\"aa\"b"   |  "a"   |  "\"aa\"b"
     * "\"a\"ab"   |  "a"   |  "\"a\"b"
     * </pre>
     */
    public static String remove(String s, String what) {
        int p = indexOfNotInQuotes(s, what);
        if ( p < 0 ) return s;
        return s.substring(0,p)+s.substring(p+what.length());
    }
    

    /**
     * Removes from s the first instance of what
     * @param s where to remove
     * @param what what string to remove 
     * @return true if removed, else false
     * @example
     * <pre name="test">
     * StringBuilder sb;
     * sb = new StringBuilder($s); remove(sb,$what) === $out; sb.toString() === $result;
     * 
     *   $s        |  $what |  $result | $out
     * -----------------------------------------
     * "aab"       |  "a"   |  "ab"    | true
     * "bab"       |  "a"   |  "bb"    | true
     * "bba"       |  "a"   |  "bb"    | true
     * </pre>
     * 
     * @example
     * <pre name="test">
     * StringBuilder sb;
     * sb = new StringBuilder($s); remove(sb,$what) === $out; sb.toString() === $result;
     * 
     *   $s        |  $what |  $result    | $out
     * -----------------------------------------
     * "\"aa\"b"   |  "a"   |  "\"aa\"b"  | false
     * "\"a\"ab"   |  "a"   |  "\"a\"b"   | true
     * </pre>
     */
    public static boolean remove(StringBuilder s, String what) {
        int p = indexOfNotInQuotes(s, what);
        if ( p < 0 ) return false;
        s.delete(p,p+what.length());
        return true;
    }

    /**
     * Removes all instances of characters in chars from string s.
     * Does not remove inside quotes.
     * @param s String to remove from
     * @param chars Characters to remove
     * @param quote Quote characters
     * @return New string
     * <pre name="test">
     * removeChars("kissa", null) === "kissa";
     * removeChars("kissa", "") === "kissa";
     * removeChars("a", "a") === "";
     * removeChars("ab", "a") === "b";
     * removeChars("ab", "b") === "a";
     * removeChars("ababc", "a") === "bbc";
     * removeChars("kissa istuu puussa", "su") === "kia it pa";
     * removeChars("a b 'c d e' f g", " ", "'") === "ab'c d e'fg";
     * </pre>
     */
    public static String removeChars(String s, String chars, String quote) {
        if ( chars == null || chars.length() == 0 ) return s;
        /*StringBuilder sb = new StringBuilder(s);
        return removeChars(sb, chars, quote) ? sb.toString() : s;*/

        StringBuilder sb = new StringBuilder();
        boolean quoting = false;

        for (int i = 0; i < s.length(); i++) {
            CharSequence c = s.subSequence(i, i+1);

            if ( quote.contains(c) && (i == 0 || s.charAt(i - 1) != '\\') )
                quoting = !quoting;

            if ( quoting || !chars.contains(c) )
                sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Removes all instances of characters in chars from string s.
     * Does not remove inside quotes.
     * @param s String to remove from
     * @param chars Characters to remove
     * @return New string
     */
    public static String removeChars(String s, String chars) {
        return removeChars(s, chars, QUOTES);
    }

    /**
     * Removes all instances of characters in chars from string s.
     * Does not remove inside quotes.
     * @param s String to remove from
     * @param chars Characters to remove
     * @param quote Quote characters
     * @return New string
     */
    /*public static boolean removeChars(StringBuilder sb, String chars, String quote) {
        if ( chars == null || chars.length() == 0 ) return false;

        int i = 0;
        boolean removed = false;
        boolean quoting = false;

        while ( i < sb.length() ) {
            CharSequence c = sb.subSequence(i, i+1);

            if ( quote.contains(c) && (i == 0 || sb.charAt(i - 1) != '\\') ) {
                quoting = !quoting;
                i++;
            }

            else if ( !quoting && chars.contains(c) ) {
                sb.deleteCharAt(i);
                removed = true;
            }

            else
                i++;
        }

        return removed;
    }*/

    /**
     * Read the file and return the contents as a ArrayList
     * @param fileName the file name to read
     * @return null if problems, otherwise the file contents
     * 
     */
    public static Strings getFileContents(String fileName) {    // deUnicode patch by tojukarp
        Scanner scanner = null;

        try {
            scanner = new Scanner(new FileInputStream(new File(fileName)));

            Strings lines = new Strings();

            if ( (scanner.hasNextLine()) ) {
                lines.add(deUnicode(scanner.nextLine()));
            }

            while ( (scanner.hasNextLine()) ) {
                lines.add(scanner.nextLine());
            }
            return lines;
        } catch (FileNotFoundException e) {
            return null;
        }
        finally {
            if ( scanner != null ) scanner.close();
        }
    }

    /**
     * Removes the Unicode byte order mark EF BB BF from a string.
     * @param s String with or without BOM
     * @return String without BOM
     * @example
     * <pre name="test">
     * String s1 = "normaali";
     * byte[] bytes = new byte[7];
     * bytes[0] = (byte)0xEF;
     * bytes[1] = (byte)0xBB;
     * bytes[2] = (byte)0xBF;
     * bytes[3] = (byte)'u';
     * bytes[4] = (byte)'n';
     * bytes[5] = (byte)'i';
     * bytes[6] = (byte)'c';
     * String s2 = new String(bytes);
     * deUnicode(s1) === s1;
     * deUnicode(s2) === "unic";
     * </pre>
     */
    public static String deUnicode(String s) {
        if ( s == null || s.length() == 0 )
            return s;
        
        byte[] bytes = s.getBytes();

        if ( bytes[0] == -17 ) {
            byte[] newbytes = new byte[bytes.length - 3];
            for (int i = 0; i < newbytes.length; i++)
                newbytes[i] = bytes[i + 3];
            return new String(newbytes);
        }

        return s;
    }

    /**
     * Write a string list to file.
     * @param strings String list.
     * @param fileName File name to write to.
     * @return true if successful
     */
    public static boolean writeToFile(Strings strings, String fileName) {
        FileWriter fw;
        PrintWriter pw;

        try {
            fw = new FileWriter(fileName);
        } catch (IOException ioe) {
            return false;
        }

        pw = new PrintWriter(fw);

        for (int i = 0; i < strings.size(); i++)
            pw.println(strings.get(i));

        pw.close();

        try {
            fw.close();
        } catch (IOException ioe) {}

        return true;
    }
    
    /**
     * Counts the index in ending word when
     * the builded string should contain
     * end-string as last characters. 
     * @param start The starting string.
     * @param end The required ending.
     * @return The starting index where end-string should be inserted after the start.
     * @example
     * <pre name="test">
     * countEndIndex("#STATIC","#STATICIMPORT") === 7;
     * countEndIndex("ABCD","1234") === 0;
     * countEndIndex("ABCD","ABCD") === 4;
     * countEndIndex("1234567890#STATIC","#STATICIMPORT") === 7;
     * 
     * </pre>
     */
    public static int countEndIndex(String start, String end) {
        int startLastIdx =start.length();
        int endLastIdx = end.length();
        int minLastIdx = Math.min(startLastIdx,endLastIdx);
        boolean matched = false;
        for(int i = 1; i<=minLastIdx;i++){
          boolean regmatch = end.regionMatches(0, start, startLastIdx-i, i);
          if (regmatch) matched = true;
          if (! regmatch && matched){
            return i-1;
          }
        }
        if (matched){
          return minLastIdx;
        } else {
          return 0;
        }
      }
    
 /*   
    public static int countEndIndex(String start, String end) {
    	if ( !end.startsWith(start) ) return 0; 
        return start.length();
    }
*/
    
    
    /**
     * Searches what backwards from pos and returns position found
     * @param s where to search
     * @param what to search
     * @param pos  where to start search
     * @return position found or -1 if not found
     * 
     * @example
     * <pre name="test">
     *   indexOfBack("0123456789","4",8)  === 4;
     *   indexOfBack("0123456789","9",8)  === -1;
     *   indexOfBack("0123456789","4",4)  === 4;
     *   indexOfBack("0123456789","4",3)  === -1;
     *   indexOfBack(null,"4",5)          === -1;
     *   indexOfBack(null,null,5)         === -1;
     *   indexOfBack("012345678",null,5)  === -1;
     *   indexOfBack("0123456789","4",18) === 4;
     *   indexOfBack("0123456789","",18)  === 10;
     *   indexOfBack("0123456789","",5)   === 5;
     *   indexOfBack("0123456789","abc",15) === -1;
     * </pre>
     */
    public static int indexOfBack(String s, String what, int pos) {
        if ( s == null )    return -1;
        if ( what == null ) return -1;
        if ( pos < 0 ) return -1;
        int p = pos;
        int len = what.length();
        if ( p >= s.length() ) p = s.length() - len;
        if ( p < 0 ) return -1;
        
        while ( p >= 0 && !s.substring(p,p+len).equals(what) ) p--;
        
        return p;
    }
    
    
    /**
     * Returns indent string copied from line.  Search for
     * line is started backwards from pos.
     * @param line line to look indent template
     * @param pos where to start looking
     * @return the same indent that current line has
     *  
     * @example
     * <pre name="test">
     *   getLineIndent("first\n    second\n",15)  === "    ";   
     *   getLineIndent("first\n    second\n",5)   === "    ";   
     *   getLineIndent("first\n    second\n",4)   === "";   
     *   getLineIndent("  first\n    second\n",6) === "  ";   
     * </pre>
     */
    public static String getLineIndent(String line, int pos) {
        int p = indexOfBack(line, "\n", pos);
        int e = p+1;
        while ( e < line.length() && Character.isWhitespace(line.charAt(e)) ) e++;
        return line.substring(p+1,e);
    }

    
    /**
     * Check if string is empty
     * @param s string to check
     * @return true if null or just spaces or tabs, false in other case
     * @example
     * <pre name="test">
     *   String s = null;
     *   isEmpty(s)      === true;
     *   isEmpty("")     === true;
     *   isEmpty(" ")    === true;
     *   isEmpty("a")    === false;
     *   isEmpty("\n")   === false;
     * </pre>
     */
    public static boolean isEmpty(String s) {
      if (s == null) return true;  
      for (int i=0; i<s.length(); i++ ) {
          char c = s.charAt(i);
          if ( c != ' ' && c != '\t' ) return false;
      }
      return true;
    }

    
    /**
     * Check if string is empty
     * @param s string to check
     * @return true if null or just spaces or tabs, false in other case
     * @example
     * <pre name="test">
     *   StringBuilder sb = null;
     *   isEmpty(sb)                        === true;
     *   isEmpty(new StringBuilder(""))     === true;
     *   isEmpty(new StringBuilder(" "))    === true;
     *   isEmpty(new StringBuilder("a"))    === false;
     *   isEmpty(new StringBuilder("\n"))   === false;
     * </pre>
     */
    public static boolean isEmpty(StringBuilder s) {
      if (s == null) return true;  
      for (int i=0; i<s.length(); i++ ) {
          char c = s.charAt(i);
          if ( c != ' ' && c != '\t' ) return false;
      }
      return true;
    }

    
    /**
     * Removes spaces from begin and end of s
     * @param s string where to remove
     * @return true if any removed
     * @example
     * <pre name="test">
     *   StringBuilder sb;
     *   sb = new StringBuilder($str); trim(sb) === $out; sb.toString() === $result;
     *   
     *    $str     | $result | $out
     *   ---------------------------- 
     *    "o  1"   | "o  1"  | false
     *    " o 1"   | "o 1"   | true
     *    "o 1 "   | "o 1"   | true
     *    " o  1 " | "o  1"  | true
     * </pre>
     */
    public static boolean trim(StringBuilder s) {
        if ( s.length() == 0 ) return false;
        boolean removed = false;
        while ( true ) {
            if ( s.length() == 0 ) break;
            if ( s.charAt(s.length()-1) != ' ' ) break;
            s.deleteCharAt(s.length()-1);
            removed = true;
        }
        while ( true ) {
            if ( s.length() == 0 ) break;
            if ( s.charAt(0) != ' ' ) break;
            s.deleteCharAt(0);
            removed = true;
        }
        return removed;     
    }

    /**
     * Returns whether a substring of a string equals another string.
     * @param s The string to take a substring from
     * @param beginIndex Index of first character, inclusive
     * @param endIndex Index of last character, exclusive
     * @param s2 The string to check equality against
     * @return True if equal, false if not or too short a string
     * @example
     *   <pre name="test">
     *     substrEquals("hamburger", 4, 8, "urge") === true;
     *   </pre>
     */
    public static boolean substrEquals(String s, int beginIndex, int endIndex, String s2) {
        if ( s.length() < endIndex || s2.length() != endIndex - beginIndex )
            return false;

        return s.substring(beginIndex, endIndex).equals(s2);
    }

    /**
     * Returns the first object if not null, otherwise the second one.
     * @param s1
     * @param s2
     * @return
     */
    public static <T> T firstNotNull(T s1, T s2) {
        return ( s1 != null ) ? s1 : s2;
    }

    /**
     * Counts the occurrences of character c in string s.
     * @param s String
     * @param c Character to count
     * @return Number of characters c in string s
     * @example
     * <pre name="test">
     * String s1 = "vesihiisi sihisi hississä";
     * countChars(s1, 'i') === 9;
     * countChars(s1, 's') === 8;
     * countChars(s1, 'ä') === 1;
     * countChars(s1, 'z') === 0;
     * </pre>
     */
    public static int countChars(String s, char c) {
        int occ = 0;

        for ( int i = 0; i < s.length(); i++ ) {
            if ( s.charAt(i) == c )
                occ++;
        }

        return occ;
    }

    /**
     * Returns a string concatenated multiple times.
     * @param s String to repeat
     * @param count Times to repeat
     * @return String s repeated count times
     * @example
     * <pre name="test">
     * repeat("a", 0) === "";
     * repeat("a", 1) === "a";
     * repeat("a", 2) === "aa";
     * repeat("ab", 2) === "abab";
     * repeat("abc", 4) === "abcabcabcabc";
     * </pre>
     */
    public static String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();

        for ( int i = 0; i < count; i++ ) {
            sb.append(s);
        }

        return sb.toString();
    }

    /**
     * Escapes a string.
     * @param s String
     * @param quote Quote to escape
     * @return Escaped string
     * <pre name="test">
     * escape("", '.') === "";
     * escape(".", '.') === "\\.";
     * escape("abc", '.') === "abc";
     * escape(".abc", '.') === "\\.abc";
     * escape(".abc.", '.') === "\\.abc\\.";
     * </pre>
     */
    public static String escape(String s, char quote) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ( c == quote )
                sb.append("\\");

            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Unescapes a string.
     * @param s String
     * @param quote Quote to unescape
     * @return Unescaped string
     * <pre name="test">
     * unescape("", '.') === "";
     * unescape("\\.", '.') === ".";
     * unescape("abc", '.') === "abc";
     * unescape("\\.abc", '.') === ".abc";
     * unescape("\\.abc\\.", '.') === ".abc.";
     * </pre>
     */
    public static String unescape(String s, char quote) {
        StringBuilder sb = new StringBuilder();
        int l = s.length();

        for (int i = 0; i < l; i++) {
            char c = s.charAt(i);
            if ( i >= l - 1 || s.charAt(i) != '\\' || s.charAt(i+1) != quote )
                sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Checks if a string is quoted.
     * @param s String
     * @param quote Quotation mark
     * @return Boolean
     * <pre name="test">
     * isQuoted("", '\'') === false;
     * isQuoted("'", '\'') === false;
     * isQuoted("''", '\'') === true;
     * isQuoted("'abc'", '\'') === true;
     * isQuoted("'ab'c", '\'') === false;
     * isQuoted("a'bc'", '\'') === false;
     * isQuoted("''abc", '\'') === false;
     * isQuoted("''abc''", '\'') === true;
     * isQuoted("-abcd-", '\'') === false;
     * isQuoted("-abcd-", '-') === true;
     * </pre>
     */
    public static boolean isQuoted(String s, char quote) {
        int l = s.length();
        return l > 1 && s.charAt(0) == quote && s.charAt(l - 1) == quote;
    }

    /**
     * Checks if a string is quoted.
     * @param s String
     * @return Boolean
     */
    public static boolean isQuoted(String s) {
        return isQuoted(s, '"');
    }

    /**
     * Quotes a string.
     * @param s String
     * @param quote Quotation mark
     * @return Quoted string
     * <pre name="test">
     * quote("", '\'') === "''";
     * quote("'", '\'') === "'\\''";
     * quote("a", '\'') === "'a'";
     * quote("ab", '\'') === "'ab'";
     * quote("'a'", '\'') === "'\\'a\\''";
     * quote("'ab'", '\'') === "'\\'ab\\''";
     * quote("kissa 'istuu' puussa", '\'') === "'kissa \\'istuu\\' puussa'";
     * quote("kissa istuu 'puussa'", '\'') === "'kissa istuu \\'puussa\\''";
     * </pre>
     */
    public static String quote(String s, char quote) {
        StringBuilder sb = new StringBuilder();
        sb.append(quote);
        sb.append( escape(s, quote) );
        sb.append(quote);
        return sb.toString();
    }

    /**
     * Quotes a string.
     * @param s String
     * @return Quoted string
     */
    public static String quote(String s) {
        return quote(s, '"');
    }

    /**
     * Unquotes a string.
     * @param s String
     * @param quote Set of quotation marks to search for
     * @return Unquoted string
     * <pre name="test">
     * unquote("", "'") === ""
     * unquote("'", "'") === "'"
     * unquote("''", "'") === ""
     * unquote("'''", "'") === "'"
     * unquote("a", "'") === "a"
     * unquote("ab", "'") === "ab"
     * unquote("'a'", "'") === "a"
     * unquote("'ab'", "'") === "ab"
     * unquote("'ab", "'") === "'ab"
     * unquote("ab'", "'") === "ab'"
     * unquote("'kissa \'istuu\' puussa'", "'") === "kissa 'istuu' puussa"
     * unquote("'kissa istuu \'puussa\''", "'") === "kissa istuu 'puussa'"
     * </pre>
     */
    public static String unquote(String s, String quote) {
        int l = s.length();
        if ( l < 2 ) return s;

        char c0 = s.charAt(0);
        String c0s = Character.toString(c0);
        if ( c0 != s.charAt(l - 1) || !quote.contains(c0s) ) return s;

        return unescape( s.substring(1, s.length() - 1), c0 );
    }

    /**
     * Unquotes a string.
     * @param s String
     * @return Unquoted string
     */
    public static String unquote(String s) {
        return unquote(s, QUOTES);
    }
    
    /**
     * Finds the closing counterpart for an opening parenthesis.
     * @param s String to look in
     * @param openingIndex Index of the opening parenthesis
     * @param closingPar Closing parenthesis character
     * @return Index of the closing parenthesis
     * <pre name="test">
     * findClosingParenthesis("(", 0, ')') === -1;
     * findClosingParenthesis("()", 0, ')') === 1;
     * findClosingParenthesis("[abc]", 0, ']') === 4;
     * findClosingParenthesis("(ab(c)de)", 0, ')') === 8;
     * findClosingParenthesis("(ab(c)de)", 3, ')') === 5;
     * findClosingParenthesis("(ab(c)de", 0, ')') === -1;
     * findClosingParenthesis("(abc')'de)", 0, ')') === 9;
     * findClosingParenthesis("(abc\")\"de)", 0, ')') === 9;
     * findClosingParenthesis("(", -1, ')') === -1;
     * </pre>
     */
    public static int findClosingParenthesis(String s, int openingIndex, char closingPar) {
        if (openingIndex < 0 || openingIndex >= s.length()) { return -1; }
        
        char openingPar = s.charAt(openingIndex);
        int level = 1;
        char openedQuote = '\0';
        
        for (int i = openingIndex + 1; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (openedQuote != '\0') {
                if (c == openedQuote) { openedQuote = '\0'; }
            }
            else {
                if (c == '"' || c == '\'') { openedQuote = c; }
                else if (c == openingPar) { level++; }
                else if (c == closingPar) {
                    if (--level == 0) { return i; }
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Splits a string by whitespaces.
     * @param s String
     * @return Strings
     * <pre name="test">
     * Strings p;
     * 
     * p = splitByWhitespace("one two");
     * p.size() === 2;
     * p.get(0) === "one";
     * p.get(1) === "two";
     * 
     * p = splitByWhitespace("one  two     three");
     * p.size() === 3;
     * p.get(0) === "one";
     * p.get(1) === "two";
     * p.get(2) === "three";
     * 
     * p = splitByWhitespace("one\n two \r\n \t\t three");
     * p.size() === 3;
     * p.get(0) === "one";
     * p.get(1) === "two";
     * p.get(2) === "three";
     * </pre>
     */
    public static Strings splitByWhitespace(String s) {
        return new Strings(s.split("[ \t\n\r]+"));
    }
}
