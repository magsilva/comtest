// ComTest - Comments for testing
package comtest.csharp;

import comtest.ComTestException;
import java.util.Stack;
import comtest.utils.Strings;
import comtest.CommentHandler;
import static comtest.utils.StringUtilities.*;

import comtest.csharp.CSSourceFile.*;
import java.util.ArrayList;

/**
 * Reads C# code and generates a CSSourceFile object.
 * @author tojukarp
 */
public class CSReader {
    private Strings source;
    private CSSourceFile dest = new CSSourceFile();
    private CommentHandler commentHandler = new CommentHandler();
    private Strings Decorators = new Strings();
    private String currentLine = "";
    private int currentLineNum = 0;
    private String currentStatement = "";    
    private Stack<Block> blocks = new Stack<Block>();
    private Namespace currentNamespace;
    private CSSourceFile.Class currentClass;
    private Method currentMethod = null;
    
    public static CSSourceFile read(Strings source) throws ComTestException {
        CSReader reader = new CSReader(source);
        reader.readProgram();
        return reader.dest;
    }

    private CSReader(Strings source) {
        this.source = source;
        currentNamespace = dest.namespaces.get("");
        currentClass = currentNamespace.classes.get("");
    }

    private void readProgram() throws ComTestException {
        for (int i = 0; i < source.size(); i++) {
            currentLine = source.get(i).trim();
            currentLineNum = i;

            try
            {
                if ( handlePreprocessor() ) continue;
                if ( handleComments() ) continue;
                if ( handleDecorators() ) continue;

                do {
                    int eos = indexOfNotInQuotes(currentLine, ";");
                    int sob = indexOfNotInQuotes(currentLine, "{");
                    int eob = indexOfNotInQuotes(currentLine, "}");
                    int min = minPositive(eos, sob, eob);

                    if ( min < 0 ) {
                        // Middle of a statement
                        currentStatement += currentLine;
                        break;
                    }
                    else if ( min == eos ) {
                        // Statement
                        currentStatement += currentLine.substring(0, eos);
                        handleStatement();
                        currentLine = currentLine.substring(eos + 1);
                    }
                    else if ( min == sob ) {
                        // Start of block
                        currentStatement += currentLine.substring(0, sob);
                        startBlock();
                        currentLine = currentLine.substring(sob + 1);
                    }
                    else if ( min == eob ) {
                        // End of block
                        endBlock();
                        currentLine = currentLine.substring(eob + 1);
                    }

                    currentStatement = "";
                } while (true);

            } catch ( Throwable t ) {
                if ( !(t instanceof ComTestException) ) {
                    String msg = String.format("exception of %s: %s on line %d", t.getClass(), t.getMessage(), i + 1);
                    throw new ComTestException(msg, true);
                }
            }
        }
    }

    /**
     * Returns the positive minimum of given values.
     * @param vals Values to test
     * @return Smallest positive value, or -1 if not found
     * @example
     * <pre name="test">
     * minPositive(1,2,3,4) === 1;
     * minPositive(4,3,2,1) === 1;
     * minPositive(7,-8,0,1,5) === 0;
     * minPositive(0,1,-2,3) === 0;
     * minPositive(-1,-2,-4) === -1;
     * </pre>
     */
    public static int minPositive(int... vals) {
        int minval = -1;

        for ( int i = 0; i < vals.length; i++ ) {
            if ( vals[i] >= 0 && ( minval < 0 || vals[i] < minval ) )
                minval = vals[i];
        }

        return minval;
    }

    private boolean handlePreprocessor() {
        return currentLine.startsWith("#");
    }

    private boolean handleComments() {
        currentLine = commentHandler.readLine(currentLine, currentLineNum);
        return currentLine.length() == 0;
    }

    private boolean handleDecorators() {
        if ( currentLine.length() < 2 )
            return false;

        int decStart = indexOfNotInQuotes(currentLine, "[");
        int decEnd = indexOfNotInQuotes(currentLine, "]");

        if ( decStart == 0 && decEnd > 0 ) {
            String decorator = currentLine.substring(decStart + 1, decEnd);
            Decorators.add(decorator);
            return true;
        }
        
        return false;
    }

    private void handleStatement() {
        String[] words = currentStatement.split(" ");

        if ( words.length == 0 )
            return;

        if (words[0].equals("using")) {
            dest.usings.add(words[1]);
        }
        else if ( currentMethod != null ) {
            currentMethod.body.add(currentStatement);
        }
        //else
        //    dest.debug.add(currentStatement);

        // Forget comments for statements
        commentHandler.clear();
    }

    private void startBlock() {
        String[] words = currentStatement.split(" ");
        int nwords = words.length;
        Block newBlock = new Block();

        if ( currentMethod != null ) {
            // Block inside a method
            //dest.debug.add(currentStatement);
            if ( currentStatement != null )
                currentMethod.body.add(currentStatement);
            currentMethod.body.add("{");
        }
        else if ( nwords > 0 && words[0].equals("namespace") ) {
            Namespace ns = new Namespace(words[1]);
            ns.name = words[1];
            ns.parent = currentNamespace;

            newBlock = ns;
            currentNamespace = ns;
        }
        else if ( nwords > 1 && ( arrayContains(words, "class" ) || arrayContains(words, "struct" ) ) ) {
            CSSourceFile.Class cl = new CSSourceFile.Class(words[nwords - 1]);

            for ( int i = 0 ; i < nwords - 1; i++ ) {
                if ( words[i].equals("class") ) {
                    cl.name = words[i+1];
                    break;
                }

                if ( words[i].equals("struct") ) {
                    cl.name = words[i+1];
                    cl.isStruct = true;
                    break;
                }
            }

            cl.access = returnMatching(words, CSSourceFile.AccessModifiers, " ");            
            cl.parent = currentClass;
            cl.modifiers = "";
            cl.decoratorAttributes = Decorators;

            for ( String word : words ) {
                if ( word.equals("class") || word.equals("struct") ||
                        word.equals(cl.name) ||
                        arrayContains( CSSourceFile.AccessModifiers, word ) )
                    continue;

                if ( cl.modifiers.length() == 0 )
                    cl.modifiers = word;
                else
                    cl.modifiers += " " + word;
            }

            newBlock = cl;
            currentClass = cl;
        }
        else if ( nwords > 0 && ( words[nwords-1].equals("get") || words[nwords-1].equals("set") || words[nwords-1].equals("delegate") ) ) {
            Method m = new Method("");
            m.access = returnMatching(words, CSSourceFile.AccessModifiers, " ");
            newBlock = m;
            currentMethod = m;
        }
        else {
            int openingParenthesis = indexOfNotInQuotes(currentStatement, "(");
            int closingParenthesis = indexOfNotInQuotes(currentStatement, ")");

            if ( openingParenthesis > 0 &&
                    closingParenthesis > openingParenthesis &&
                    currentMethod == null )
            {
                String sigNoParams = currentStatement.substring(0, openingParenthesis);
                words = sigNoParams.split(" ");
                nwords = words.length;
                Method m = new Method(words[nwords - 1]);

                m.isStatic = arrayContains(words, "static");
                m.isVirtual = arrayContains(words, "virtual");
                m.isOverride = arrayContains(words, "override");
                m.access = returnMatching(words, CSSourceFile.AccessModifiers, " ");
                m.returnType = words[nwords-2];
                m.modifiers = "";
                m.parent = currentClass;
                m.decoratorAttributes = Decorators;

                for ( int i = 0; i < words.length - 1; i++ ) {
                    if ( words[i].equals(m.name) || arrayContains( CSSourceFile.AccessModifiers, words[i] ) )
                        continue;
                    if ( words[i].equals(m.returnType) || arrayContains( Method.ImplementedModifiers, words[i] ) )
                        continue;

                    if ( m.modifiers.length() == 0 )
                        m.modifiers = words[i];
                    else
                        m.modifiers += " " + words[i];
                }

                if ( closingParenthesis - openingParenthesis > 1 ) {
                    String paramStr = currentStatement.substring(openingParenthesis + 1, closingParenthesis - 1).trim();
                    int plen = paramStr.length();
                    String paramType = null;
                    int lastSpace = -1;

                    for (int i = 0; i <= plen; i++) {
                        if ( i == plen || Character.isWhitespace(paramStr.charAt(i)) ) {
                            if ( lastSpace < i - 1 ) {
                                String lastWord = paramStr.substring(lastSpace + 1, i);
                                if ( paramType == null ) paramType = lastWord;
                                else {
                                    m.params.add(new Variable(paramType, lastWord));
                                    paramType = null;
                                }
                            }

                            lastSpace = i;
                        }
                    }
                }

                newBlock = m;
                currentMethod = m;
            }
            else {
                // Other block outside a method
                NestedCodeBlock nested = new NestedCodeBlock();
                nested.blockBefore.add(currentStatement);
                nested.blockBefore.add("{");
                nested.blockAfter.add("}");
                newBlock = nested;
            }
        }

        if ( commentHandler.blocks.size() > 0 ) {
            if ( newBlock instanceof NamedBlock ) {
                // Comment a named block
                ((NamedBlock)newBlock).commentBlocks = commentHandler.blocks;
            }

            // Forget comments for unnamed blocks
            // (named too, after copying)
            commentHandler.clear();
        }

        Decorators = new Strings();
        newBlock.codeLine = currentLineNum;
        blocks.push(newBlock);
    }

    /**
     * Returns whether the string array contains an element.
     * @param array
     * @param test
     * @return
     */
    private boolean arrayContains(String[] array, String test) {
        for ( String s : array ) {
            if ( s.equals(test) )
                return true;
        }
        return false;
    }

    /**
     * Returns all string elements found in an array concatenated and separated.
     * @param array Array in which to search
     * @param test Words to search for
     * @param sep Separator
     * @return All matches in test separated by sep
     */
    private String returnMatching(String[] array, String[] test, String sep) {
        String res = "";

        for ( String t : test ) {
            if ( arrayContains(array, t) ) {
                if (res.length() == 0)
                    res = t;
                else
                    res += sep + t;
            }
        }

        return res;
    }

    private String[] removeItems(String[] array, String[] items) {
        ArrayList<String> newArray = new ArrayList<String>();

        for ( String item : items ) {
            if ( !arrayContains(array, item) )
                newArray.add(item);
        }
        
        return (String[])newArray.toArray();
    }

    private void endBlock() {
        Block lastBlock = blocks.pop();
        
        if ( lastBlock instanceof CSSourceFile.Class ) {            
            currentNamespace.classes.put(currentClass.name, currentClass);
            currentClass = currentClass.getParentClass();
        }
        else if ( lastBlock instanceof Method ) {
            currentClass.methods.add(currentMethod);
            currentMethod = null;
        }
        else if ( lastBlock instanceof Namespace ) {
            dest.namespaces.put(currentNamespace.name, currentNamespace);
            currentNamespace = currentNamespace.getParentNamespace();
        }
        else if ( currentMethod != null ) {
            currentMethod.body.add("}");
        }
    }
}
