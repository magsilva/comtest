// ComTest - Comments for testing
package comtest.csharp;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import comtest.ComTestException;
import comtest.ComTestIni;
import comtest.ComTestSyntax;
import comtest.ComTestTable;
import comtest.CommentHandler;
import comtest.CommentHandler.CommentBlock;
import comtest.CommentStyles;
import comtest.csharp.CSSourceFile.*;
import comtest.utils.Strings;
import java.text.ParseException;

import static comtest.utils.StringUtilities.*;
import static comtest.ComTestSyntax.*;

/**
 * Reads CSSourceFile class and writes another based on ComTest
 * comments in the source methods.
 * @author tojukarp
 */
public class ComTestProcessor {
    private static class ComTestBlock {
        public int codeLine;
        public Method origMethod;
        public Strings content;

        public ComTestBlock(int startLine, Method m) {
            codeLine = startLine;
            origMethod = m;
            content = new Strings();
        }
    }

    ComTestIni ini;

    public ComTestProcessor() {
    }

    public ComTestProcessor(ComTestIni ini) {
        this.ini = ini;
    }

    /**
     * Processes ComTest comments from a C# source file and generates a
     * test source file.
     * @param source Source file
     * @return Test source file, or null if file contains no tests
     * @throws comtest.ComTestException if syntax or other errors
     */
    public CSSourceFile process(CSSourceFile source) throws comtest.ComTestException {
        CSSourceFile dest = makeTestFile(source);
        Namespace testNamespace = null;
        CSSourceFile.Class testClass = null;
        int methods = 0;

        for ( Namespace ns : source.namespaces.values() ) {            
            testNamespace = null;

            for ( CSSourceFile.Class cl : ns.classes.values() ) {
                for ( Method m : cl.methods ) {
                    for ( ComTestBlock ctBlock : getComtestBlocks(m) ) {
                        if ( ctBlock.content.size() == 0 )
                            continue;

                        if ( testNamespace == null ) {
                            String testnsname = "";
                            // Import the namespace if not already
                            if ( ns.name.length() > 0 ) {
                                dest.usings.add(ns.name);
                                testnsname = firstNotNull(ini.packageName, "Test" + ns.name);
                            }
                            
                            // Add a new test namespace
                            testNamespace = new Namespace(testnsname);
                            dest.namespaces.put(testnsname, testNamespace);

                            // Create a test class
                            testClass = makeTestClass(cl.name);
                            testNamespace.classes.put("UnitTest", testClass);
                        }

                        try {
                            // Generate a test method
                            Method mTest = makeTestMethod(ctBlock);
                            testClass.methods.add(mTest);
                            methods++;
                        } catch (Exception e) {
                            String msg = String.format("ComTest internal error: %s in method %s", e.toString(), m.name );
                            throw new comtest.ComTestException(msg, true);
                        }
                    }
                }
            }
        }

        if ( methods == 0 )
            return null;

        return dest;
    }

    /**
     * Processes the ComTest comment block into C# test code.
     * @param block Comment block
     * @return Code block
     */
    private static CodeBlock processBody(ComTestBlock block) {
        CommentHandler ch = new CommentHandler();
        CodeBlock codeBlock = new SimpleCodeBlock();
        CodeBlock templateBlock = null;
        ComTestTable templateTable = null;
        boolean startTemplate = false;

        for ( int i = 0; i < block.content.size(); i++ ) {
            String s = ch.readLine(block.content.get(i), i).trim();
            if ( s.isEmpty() ) continue;

            NestedCodeBlock sb = applyMacros(s, block.origMethod.name, block.codeLine + i);
            String s2 = sb.blockInside.getCode().get(0);

            if ( templateTable != null ) {
                // Try to process the line as a template line
                ComTestTable newTemplateTable
                    = processTemplateLine(templateTable, s2, block.origMethod.name, block.codeLine + i);

                if ( startTemplate ) {
                    templateTable.setStartingLine(block.codeLine + i);
                    startTemplate = false;
                }

                if ( newTemplateTable == null ) {
                    // Template ends
                    // Replace all prior template variables
                    templateBlock = applyTemplate(templateBlock, templateTable);
                    codeBlock.add(templateBlock);
                    templateBlock = null;
                }
                
                templateTable = newTemplateTable;
            }

            if ( templateTable == null ) {
                CodeBlock subBlock = processNormalLine(s2, block.origMethod.name, block.codeLine + i);

                if ( subBlock == null ) {
                    if ( templateBlock == null ) {
                        // Heading (----) for table, skip this
                        continue;
                    }

                    // Template begins
                    String cols = templateBlock.removeLastLine();
                    String[] elemArray = cols.split( Pattern.quote(COLUMNSEPARATOR) );
                    Strings elements = new Strings();
                    elements.addAll(Arrays.asList(elemArray));
                    elements.trim();
                    templateTable = new ComTestTable(elements);
                    startTemplate = true;
                }
                else if ( !subBlock.isEmpty() ) {
                    // Normal code line
                    sb.blockInside = subBlock;

                    if ( templateBlock != null ) {
                        // In a template block
                        templateBlock.add(sb);
                    }

                    else if ( s2.contains(ComTestSyntax.TEMPLATELINEMARKER) ) {
                        // First line of a template block
                        templateBlock = new SimpleCodeBlock();
                        templateBlock.add(sb);
                    }

                    else {
                        // Outside a template block
                        codeBlock.add(sb);
                    }
                }
            }
        }

        if ( templateTable != null ) {
            // End template
            // Replace all prior template variables
            templateBlock = applyTemplate(templateBlock, templateTable);
            codeBlock.add(templateBlock);
        }

        return codeBlock;
    }

    /**
     * Processes a ComTest template table line.
     * If the line is not recognized as such, null is returned.
     * @param templateTable Template table to add to
     * @param s Line to process
     * @param methodName Method name
     * @param codeLine Line number in original source
     * @return New template table, or null if not processed.
     */
    private static ComTestTable processTemplateLine(ComTestTable templateTable, String s, String methodName, int codeLine) {
        String[] elemArray = s.split( Pattern.quote(COLUMNSEPARATOR) );
        Strings elements = new Strings();
        elements.addAll(Arrays.asList(elemArray));
        int numElements = elements.size();

        if ( numElements != templateTable.numColumns() )
            return null;

        elements.trim();
        templateTable.addRow(elements);
        return templateTable;
    }

    /**
     * Processes a normal (non-template) ComTest code line.
     * @param s Line to process
     * @param methodName Method name from which the code line was taken
     * @param codeLine Line number in original source
     * @return Processed code block, or null if template line
     */
    private static CodeBlock processNormalLine(String s, String methodName, int codeLine) {
        CodeBlock block = new SimpleCodeBlock();
        int separIndex = indexOfNotInQuotes(s, SEPARATORLINE);

        if ( separIndex >= 0 )
            // Template begins
            return null;

        AssertErrorMessage message = new AssertErrorMessage(methodName, codeLine + 1);
        Strings newLines;

        // Operators
        newLines = EQUALS.applyAll(s, message);
        newLines = ALMOST.applyAll(newLines, message, TOLERANCE);
        newLines = REGEX.applyAll(newLines, message);

        // Macros
        block.add( newLines );
        
        return block;
    }

    /**
     * Applies any ComTest macros (NOT operators) on the line.
     * @param s Code line with or without macros
     * @param methodName Method name from which the code line was taken
     * @param codeLine Line number in original source
     * @return Code block with all applicable macros applied
     */
    public static NestedCodeBlock applyMacros(String s, String methodName, int codeLine) {
        NestedCodeBlock result = new NestedCodeBlock();
        int macroIndex = indexOfNotInQuotes(s, MACROMARKER);
        if ( macroIndex < 0 ) {
            result.add(s);
            return result;
        }
        
        String codePart = s.substring(0, macroIndex).trim();
        String macroPart = s.substring(macroIndex + MACROMARKER.length());
        String macroLower = macroPart.toLowerCase();

        if ( macroLower.startsWith(THROWSMACRO) && macroPart.length() > THROWSMACRO.length() + 1 ) {
            String afterMacro = macroPart.substring( THROWSMACRO.length() ).trim();
            CodeBlock catchBlock = new SimpleCodeBlock();
            String exception;
            
            if (afterMacro.charAt(0) == '(') {
                // Contains a catch block
                int cPar = findClosingParenthesis(afterMacro, 0, ')');
                // Error handling: cPar < 0
                exception = afterMacro.substring(1, cPar);
                int oBrace = afterMacro.indexOf('{');
                if (oBrace > cPar) {
                    int cBrace = findClosingParenthesis(afterMacro, oBrace, '}');
                    if (cBrace > oBrace + 1) {
                        String catchCode = afterMacro.substring(oBrace + 1, cBrace - 1);
                        catchBlock = processNormalLine(catchCode, methodName, codeLine);
                        catchBlock.indent(1);
                    }
                }
            }
            else {
                // No catch block
                exception = afterMacro;                
            }
            
            result.blockBefore.add("try");
            result.blockBefore.add("{");
            result.blockInside.add("\t" + codePart);
            result.blockAfter.add( String.format(
                "\tAssert.Fail(\"Did not throw %s in method %s on line %d\");",
                    exception, methodName, codeLine ) );
            result.blockAfter.add("}");
            //result.blockAfter.add("catch ( " + afterMacro +  " ) { " + catchBlock + " }");
            result.blockAfter.add("catch (" + exception + ")");
            result.blockAfter.add("{");
            result.blockAfter.add(catchBlock);
            result.blockAfter.add("}");
        }
        else {
            // Not a recognizable macro
            result.add(s);
        }

        return result;
    }

    /**
     * Applies the current template table to a block of code.
     * @param srcBlock Block to apply template to
     * @param table Template table to apply
     * @return Block with template applied
     */
    private static CodeBlock applyTemplate(CodeBlock srcBlock, ComTestTable table) {
        CodeBlock appliedBlock = new SimpleCodeBlock();
        Pattern[] colPatterns = new Pattern[table.numColumns()];

        for ( int row = 0; row < table.numRows(); row++) {
            for ( int line = 0; line < srcBlock.size(); line++ ) {
                String s = srcBlock.get(line);

                if ( s.indexOf(TEMPLATELINEMARKER) < 0 ) {
                    appliedBlock.add(s);
                    continue;
                }

                for ( int col = 0; col < table.numColumns(); col++) {
                    String colName = table.getColumnName(col);
                    if ( s.indexOf(colName) < 0 )
                        continue;

                    String colValue = table.getColumnValues(col).get(row);
                    if ( colPatterns[col] == null )
                        colPatterns[col] = Pattern.compile(Pattern.quote(colName));

                    s = colPatterns[col].matcher(s).replaceAll(colValue);
                }

                //s = incrementLineNum(s, row);
                s = setLineNum(s, table.getStartingLine() + row + 1);
                appliedBlock.add(s);
            }
        }

        return appliedBlock;
    }

    /**
     * Sets the line number of an assertion statement message line.
     * @param line Assertion statement string
     * @param lineNum New line number
     * @return Modified statement
     * <pre name="test">
     * String s = setLineNum("Assert.AreEqual(exp, act, \"in method M, line 69\");", 101);
     * s === "Assert.AreEqual(exp, act, \"in method M, line 101\");"
     * </pre>
     */
    public static String setLineNum(String line, int lineNum) {
        try {
            AssertLine al = AssertLine.parse(line);
            AssertErrorMessage msg = al.getMessage();
            if (msg == null) return line;

            msg.codeLine = lineNum;
            al.setMessage(msg);
            return al.toString();
        } catch ( ParseException pe ) {
            return line;
        }
    }

    /**
     * Create an empty test source file
     * @param source Source file
     * @return Test file skeleton
     */
    private CSSourceFile makeTestFile(CSSourceFile source) {
        CSSourceFile testfile = new CSSourceFile();
        testfile.usings.add(source.usings);
        testfile.usings.add("using ", ini.imports, "");
        testfile.usings.add("Microsoft.VisualStudio.TestTools.UnitTesting");
        return testfile;
    }

    /**
     * Create a test class based on an existing class.
     * @param className Name of the original class
     * @return Test class
     */
    private static CSSourceFile.Class makeTestClass(String className) {
        CSSourceFile.Class testClass = new CSSourceFile.Class("Test" + removeGenerics( className) );
        testClass.access = "public";
        testClass.decoratorAttributes.add("TestClass()");
        CommentBlock aboutBlock = new CommentBlock();
        aboutBlock.style = CommentStyles.Xml;
        aboutBlock.content.add("Test class made by ComTest");
        aboutBlock.content.add("ComTestScanner.getComTestVersion()");
        return testClass;
    }

    /**
     * Create a test method based on the original method to test and the
     * ComTest block containing the test code for it.
     * @param ctBlock ComTest block
     * @return Test method
     */
    private Method makeTestMethod(ComTestBlock ctBlock) {
        String mTestName = "test" + removeGenerics( ctBlock.origMethod.name ) + ctBlock.codeLine;
        Method mTest = new Method(mTestName);
        mTest.access = "public";
        mTest.isStatic = true;
        mTest.decoratorAttributes.add("TestMethod()");
        mTest.body.add(processBody(ctBlock));
        return mTest;
    }

    private static String removeGenerics(String name) {
        return name.replaceAll("\\<.*\\>", "");
    }

    /**
     * Get all ComTest blocks from a method.
     * @param m Method from which to get comments
     * @return List of ComTest blocks
     */
    private static List<ComTestBlock> getComtestBlocks(Method m) {
        ArrayList<ComTestBlock> result = new ArrayList<ComTestBlock>();
        ComTestBlock curBlock = null;

        for ( CommentBlock cblock : m.commentBlocks ) {
            for ( int i = 0; i < cblock.content.size(); i++ ) {
                String line = cblock.content.get(i).trim();

                if( curBlock != null ) {
                    if( line.equals("</pre>") ) {
                        result.add(curBlock);
                        curBlock = null;
                    }
                    else
                        curBlock.content.add(line);
                }
                else if( line.equals("<pre name=\"test\">") ) {
                    curBlock = new ComTestBlock(cblock.codeLine + i + 1, m);
                }
            }
        }

        return result;
    }
}
