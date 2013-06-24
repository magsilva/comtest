// ComTest - Comments for testing
package comtest.legacy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * A class for making testing easy.
 * Reads the files given in parameters and generates JUNIT tests
 * from comments.<p />
 * 
 *   The idea is to simplify writing unit tests.  And to get
 *   unit test as examples to comments and JavaDoc.  
 *   That's where the name ComTest comes (Comments for Testing)
 *       
 * <pre>
 * Example: Just simple initialization section:
 * 
 * Counter cnt = new Counter(3);                            // IS
 *              cnt.getCount() === 0;  cnt.getSum() === 0;  // IS
 * cnt.add(1);  cnt.getCount() === 1;  cnt.getSum() === 1;  // IS
 * cnt.add(2);  cnt.getCount() === 2;  cnt.getSum() === 3;  // IS
 * 
 * Syntax:
 *   The ComTest comment must be inside block comments 
 *   and start with
 *     (at)example
 *     [pre name="testModuleName"]  // change [] to HTML <> 
 *   and ends with 
 *     [/pre]
 *     
 *   Before handling the ComTest line, comments after //
 *   is removed.
 *     
 *   The test itself contains following sections
 *    0) The test case name is taken from pre-tag.
 *    
 *       Or if there is only name="test" then name is
 *       found from the next starting class or method
 *       and the the test case names comes like
 *         testAdd34
 *       where 34 is the line of the add-methods heading.     
 *   
 *    1) Initialization section.                           (IS)
 *       This is Java code until first template section
 *       
 *    2) Template section                                  (TS)
 *       This is Java-code where at least one $ exists
 *       on the line outside of quotes. 
 *       Template code is output from
 *       each table value line by replacing template
 *       values by table values.  After first template
 *       line all lines are kept as template line until
 *       the table starts.  Template "variables" are
 *       just text replacements.
 *       If the line is form: "$a" == "$b";
 *       it is not template line, but
 *       it can be forced template line be extra $ at the
 *       end of it, so: "$a" == "$b"; $ 
 *       
 *    3) Normal code                                       (NC)
 *       This is normal Java code after first table ends.
 *       Normal code is output immediately when it is hit.
 *       Normal code ends to first template line.
 *       
 *    4) Table heading                                     (TH)
 *       This is the first line after template lines
 *       where there is at least one column separator 
 *         " | "
 *       or | as a first or last char in the line 
 *       (after removing * and trim)  
 *       There could be more columns
 *       than there is templates variables.  All template
 *       variable does not need to begin with $ (actually none), 
 *       but there is a big risk to change some unwanted 
 *       on that case.
 *       
 *    5) Table values.                                     (TV)
 *       These are lines with at least one column separator
 *         " | "
 *       or | as a first or last char in the line 
 *       (after removing * and trim)  
 *       If the cell value is ignore value
 *         "---"
 *       then all template sentences with that column header
 *       will be ignored.  
 *       
 *       Table values are substituted to corresponding template
 *       variables for each table line.  If there is sentences
 *       with $ after substitution then those sentences are 
 *       removed.
 *       
 *    6) Table separator (line)                            (LI)
 *         "-----"  (at least 5 "-" -signs)
 *       Table separator lines are comments. Actually it 
 *       can be anywhere in ComTest.  The whole line is 
 *       treated as a comment even ----- is in 
 *       the end of line
 *       
 *    7) Initialization separator                           (IZ)
 *         "====="  (at least 5 "=" -signs)
 *       After initialization separator the next table
 *       line or normal section causes start of new test case 
 *       and that starts with initialization section.
 *       The test cases are numbered by adding _n after
 *       original test case name.
 *       
 *    8) If Initialization separator is before first       (IZE)
 *       table heading, the whole table will be handled
 *       so that every line comes a new test case with
 *       initialization section.                  
 *       
 *    9) If there is normal lines or template lines
 *       after table, then that resets previous templates.
 *       
 *   10) All sentences with === is divided to leftSide and
 *       rightSide.  And then generated code
 *       assertEquals("explanation",rightSide,leftSide);    
 *       Note that for more complicated assert's one can write
 *       f.ex assertTrue("Should be less: #LINE#" , a < 3);
 *       
 *       Sentences with ~~~ or ~~ is handled as double
 *       comparison so they produce 
 *       assertEquals("explanation",rightSide,leftSide,#PRECISION);
 *       
 *       If the line has only one === or ~~ sentence
 *       then the ending ; may left out.
 *       
 *       Sentences with =R= or =~ is handled as regular expression
 *       comparison.    
 *           
 *       
 *   11) To write directly to Java class, introduce the
 *       test with name="testJAVA".  This turns Raw-mode on
 *       and on raw mode the lines are not written inside any 
 *       test case method.  In raw-mode there is no
 *       initialization section (IS), so on case of re-init
 *       no code is repeated.  
 *       To write JUnit annotations line .@Before the
 *       at sign  must be prefixed by any char to prevent
 *       JavaDoc going wrong.  The prefix char is removed.
 *   
 *   12) Lines that ends with                           
 *         #THROWS exceptionName
 *       is surrounded by "try ... fail() .. catch exceptionName e {}"
 *       so that line fails if the exception is not thrown.
 *       Note that the #THROWS works only line based.
 *       But if table line contains #THROWS then the
 *       template may generate more lines inside try catch.
 *       
 *       If #THROWS exeptionName is before any other line after pre-tag, 
 *       then the test is started by
 *         public void testN() throws exeptionName {
 *       
 *   13) Import is handled by "commands"
 *         #STATICIMPORT    - creates a line import static package.ClassName.*;
 *         #DYNAMICIMPORT   - creates a line import package.ClassName.*;
 *         #CLASSIMPORT     - creates a line import package.ClassName;
 *         #PACKAGEIMPORT   - creates a line import package.*;
 *         #NOIMPORT        - clears the automatic importing for JUnit
 *                          - if #NOIMPORT=all then not even JUnit-importing is done
 *         #import impSentence; - adds the selected import sentence
 *       Import commands can be either in ComTest comments or in
 *       normal comments if there is only spaces before the command.
 *       If there is no import commands on the file or ComTest.ini files, then
 *         import package.*;  or/and
 *         import static package.ClassName.* ; is inserted automatically
 *       if static method or ordinary method is tested.
 *       
 *       jUnit imports:
 *         #JUNITIMPORTS    - what imports is added to begining of file
 *                            lines separated by :
 *         Default:
 *         #JUNITIMPORTS=import static org.junit.Assert.*;:import org.junit.*;            
 *       
 *   14) The result directory and package can be changed be variables
 *         #DIRECTORY=dir   
 *         #PACKAGE=packageName
 *       in normal comments if there is only spaces before.
 *       If dir is relative, then it is appended to end of files directory.
 *       If packageName is .subPackageName then it is added to files
 *       packageName. 
 *       
 *   15) Variable sentences can be also in file
 *       ComTest.ini in same directory than the file to scan.
 *       In ini file the variables do not need to be in comments.
 *       Before # there is allowed only spaces.
 *       // is treated as a comment like in Java-file.  
 *       The ComTest.ini file is read first and the variables
 *       in the Java-file may override the values.                
 *          
 *   16) The symbols $, | , ---  and other "variables"
 *       can be changed by variable substitution lines     
 *          #COLUMNSEPARATOR=" | ";
 *          #COLUMNCHAR="|";
 *          #TEMPLATELINEMARKER="$";
 *          #DELETESENTENCEMARKER="---";
 *          #INITLINE="=====";
 *          #EVERYLINEINITS1="=====";
 *          #EVERYLINEINITS2="=== Every line inits ===";
 *          #EQUALSMARKER="===";
 *          #EQUALSMARKER2="=>";
 *          #ALMOSTMARKER1="~~~";
 *          #ALMOSTMARKER2="~~";
 *          #REGEXPMARKER1="=R=";
 *          #REGEXPMARKER2="=~";
 *          #TOLERANCE="0.000001";
 *          #THROWSMARKER="#THROWS";
 *          #IMPORTMARKER="#import";
 *          #PACKAGEMARKER="#PACKAGE=";
 *          #DIRECTORYMARKER="#DIRECTORY=";
 *          #BEFORETESTCLASS=@SuppressWarnings({ "PMD" })
 *       Each substitution must be in own line and
 *       = must be the next char after variable name.
 *       Variables are valid after substitution point until
 *       end of file or new change.  Every file starts with defaults.
 *       COLUMNCHAR variable is needed only if it is
 *       the first or last char on the line.  So mostly
 *       in one column tables.        
 *       Variable substitution can be in ComTest-comments or
 *       in normal comments if there is only spaces before the command.
 *   
 *     
 * Example: Initialization section, template section and table 
 *          with two new initialization                           
 *   
 * Counter cnt = new Counter(3);                            // IS 
 * cnt.add($add);  cnt.getCount() === $count;               // TS  
 * cnt.getSum() === $sum;                                   // TS
 * cnt.getMax() === $max; cnt.getMin() === $min;            // TS
 * 
 *  ----------------------------------------------          // LI
 *     $add   |   $count   | $sum   | $max | $min           // TH
 *  ----------------------------------------------          // LI
 *     ---    |     0      |  0     |   0  |   0            // TV
 *      1     |     1      |  1     |   1  |   1            // TV
 *      2     |     2      |  1+2   |   2  |   1            // TV
 *      3     |     3      |  1+2+3 |   3  |   1            // TV
 *      4     |     3      |  6     |   3  |   1            // TV
 *  ==============================================          // IZ
 *      5     |     1      |  5     |   5  |   5            // TV 
 *      2     |     2      |  5+2   |   5  |   2            // TV
 *      3     |     3      |  5+2+3 |   5  |   2            // TV 
 *  ==============================================          // IZ
 *     -1     |     1      |  -1    |  -1  |  -1            // TV
 *      2     |     2      | -1+2   |   2  |  -1            // TV  
 *      9     |     3      | -1+2+9 |   9  |  -1            // TV
 * </pre>                                              
 * 
 * Below is a real life example from test with all sections
 * and with many tables. For code generated
 * see ComTestTest.testStringAdd()
 * 
 * @example
 * <pre name="testStringAdd">             
 * // Example of many tables
 *   Strings list = new Strings();               // IS
 *   
 *   list.size() === 0;                          // IS
 *   list.add("$value"); list.size() === $size;  // TS
 *   list.get($i) === "$value";                  // TS
 *   
 *    $i   |  $value | $size                     // TH
 *   -------------------------                   // LI
 *     0   |  one    |  1                        // TV
 *     1   |  two    |  2                        // TV
 *     2   |  three  |  3                        // TV
 *     3   |  ---    |  3                        // TV
 *
 *   list.get(3) === "three"; #THROWS IndexOutOfBoundsException // NC
 *        
 *   list.add("$value"); list.size() === $size;  // TS2
 *   list.add($b); // no output no variable $b   // --       
 *   
 *   === Every line inits ===
 *    $i   |  $value | $size                     // TH2
 *   -------------------------                   // LI
 *     0   |  one    |  1                        // TV
 *     1   |  two    |  1                        // TV 
 *     2   |  three  |  1                        // TV
 *     
 * #COLUMNSEPARATOR=" , "; 
 * #TEMPLATELINEMARKER="%";
 *   list.add("four"); list.size() === 1;        // NC 
 *   assertTrue("Strings: #LINE#",list.size()>0);// NC
 *   list.add("%value"); list.size() === %size;  // TS3
 *   assertTrue("Strings: #LINE#",list.size()>0);// TS3
 *   
 *    %i   ,  %value , %size                     // TH3
 *   -------------------------                   // LI
 *     0   ,  one    ,  2                        // TV
 *     1   ,  two    ,  3                        // TV
 *     2   ,  three  ,  4                        // TV
 *     
 *   if ( %i > 0 ) { list.get(%i) === "%value"; }// TS4
 *   
 *    %i   ,  %value                             // TH3
 *   -----------------                           // LI
 *     1   ,  one                                // TV
 *     2   ,  two                                // TV
 *     3   ,  three                              // TV
 *     4   ,  four   #THROWS IndexOutOfBoundsException 
 *     
 * #COLUMNSEPARATOR=" | "; 
 * #TEMPLATELINEMARKER="$";
 * </pre>
 *      
 * @example 
 * <pre name="testDouble">
 *  // Test double comparison and tolerance   
 *  double d1 = 0.1;                             // IS
 *  d1 ~~ 1.0/10;                                // IS
 *  #TOLERANCE=0.2;
 *  d1 ~~ 0;                                     // IS
 *  </pre>
 *
 * @example
 * <pre name="testRegExp">
 *  // Test regExp comparison
 *  "cat" =R= "c.*t"
 *  "cat" =~  "c.*"
 *  </pre>
 *
 * @example
 * <pre name="testException">
 *  // Test case when Exception must be thrown
 * int []a = {0,1};
 * a[2] === 2; #THROWS (IndexOutOfBoundsException ex) { ex.getMessage() =R= ".*2.*"; } // can be tested by regexp
 * a[2] === 2; #THROWS (IndexOutOfBoundsException ex) { ex.getMessage() === "2"; }
 * a[3] === 3; #THROWS IndexOutOfBoundsException
 * 
 * @example 
 * <pre name="testJAVA">
 * // Test raw mode and introduce an attribute and initializer 
 * 
 * private Strings globalList;
 * 
 * /.** *./    // This is the way to make comments inside comments 
 * .@Before public void init() {// Run before ALL tests, not only those below
 *   globalList = new Strings();
 *   String st[] = {"one", "two", "three" };
 *   for (String s:st) globalList.add(s);
 * }
 * </pre>
 *
 * @example
 * <pre name="testGlobalList">             
 *   globalList.size() === 3;
 *   globalList.add("one");
 * </pre>               
 *
 * @example
 * <pre name="testGlobalList2">             
 *   globalList.size() === 3;
 *   globalList.add("two");
 * </pre>               
 *
 * 
 * <h2>Tips</h2>
 * <ul>
 * <li>More difficult testing directly to JUNit test file.
 * ComTest prevents all hand made things in test unit.</li>
 *
 * <li>Things written to test unit is usable in ComTest</li>
 *
 * <li>All JUnit commands are available</li>
 *
 * <li>To write attributes or helper methods to the TestUnit
 *  use name="testJAVA"</li>
 *
 * <li>Do not use variable names that includes the other one
 * like $a and $aa.  If $a=6 and the replace is done, then
 * $aa may come 6a.</li> 
 *
 * <li> To use logical or test: |, do it without spaces around it
 * like c = a|b; not like c = a | b; Or if you need to write " | "
 * then change the separator char by substituting to variables, see 12)</li>
 * 
 * <li>Write the code inside comments first as normal code
 * and test the code and then move inside comments.</li>
 *  
 * <li>To change the output directory and package write
 * ComTest.ini to the package file.</li>
 *    
 * <li>It is possible to make templates without naming
 * the template variables (column names) by $.  
 * Then it is enough to have one $
 * f.ex at the end of the first line after the ending ;.
 * The only danger is to have column names that are included
 * in the Java-sentences for some other meaning.
 * </li>   
 * 
 * <li>If using if sentences with === then you must use
 * brackets.  So instead of
 * <pre> 
 *   if ( a < 5 ) a === 3;
 * </pre>
 * use
 * <pre> 
 *   if ( a < 5 ) { a === 3; }
 * </pre>
 * The else sentence can be used with or without brackets.
 * There is same problem also for for and while and do sentences.
 * </li>
 * 
 * </ul>
 * 
 * @author vesal
 * @version 14.3.2010
 *
 */
public class ComTestCpp { // NOPMD by vesal on 13.1.2008 15:29
    // TODO Comments from @example or pre line to test method
    // TODO Syntax:  $arg:REPEAT(one,two,three)
    // TODO Syntax:  $arg:REPEAT(0..7)
    // TODO Make a safe copy from old file in case of problems
    // TODO Should the table line be printed as a comment when every line produces a different test

	// #STATICIMPORT 
	// #import comtest.Strings;    
    
    /**
     * A class for capturing the process output
     * see: http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4
     */
    public static class StreamGobbler extends Thread  {
        InputStream is;
        String type;
        
        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }
        
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                while ( (line = br.readLine()) != null)
                    if ( "ok".equals(line )) // so that no error msg is printf from pure ok-line
                        System.out.println(line);
                    else
                        System.out.println(type + line);
                } catch (IOException e) {
                    System.out.println(e.getMessage());    
                }
        }
    }
    
    
    /**
     * Run the command and wait until end
     * @param command to run
     * @param type type of output
     * @return ret value
     */
    public static int run(String command, String type) {
        try {
            Process proc = Runtime.getRuntime().exec(command);
            // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(proc.getErrorStream(), type + "ERROR: ");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(proc.getInputStream(), type);
            errorGobbler.start();
            outputGobbler.start();
            return proc.waitFor();
            
        } catch ( IOException e ) {
            System.out.println(e.getMessage());
        } catch ( InterruptedException e ) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public static void CompileAndRun(String cppFile)
    {
        if ( run("g++ -o " + cppFile + ".exe " + cppFile, "Compile ") == 0 )
            run(cppFile + ".exe ", "Test error: ");
    }
}
