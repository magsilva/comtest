// ComTest - Comments for testing
package cc.jyu.fi.comtest;

import cc.jyu.fi.comtest.utils.Strings;

import java.util.ArrayList;

import static cc.jyu.fi.comtest.utils.StringUtilities.*;

/**
 * Different styles for commenting code.
 * @author tojukarp
 */
public abstract class CommentStyles {
    //#import comtest.CommentStyles.*;

    /**
     * /*
     *  * C comment
     *  * /
     */
    public static final BlockCommentStyle C = new BlockCommentStyle("/*", "*", "*/");

    /**
     * /**
     *  * Javadoc comment
     *  * /
     */
    public static final BlockCommentStyle JavaDoc = new BlockCommentStyle("/**", "*", "*/");

    /**
     * // C++ comment
     */
    public static final LineCommentStyle Cpp = new LineCommentStyle("//");

    /**
     * /// XML comment
     */
    public static final LineCommentStyle Xml = new LineCommentStyle("///");

    /**
     * All known commmenting styles
     */
    public static final CommentStyle[] All = {C, Cpp, JavaDoc, Xml};
    
    /**
     * Base class for all commenting styles
     */
    public abstract static class CommentStyle {
        public String startSequence;

        /**
         * Gets the index of the comment starting marker.
         * @param s Line to search in
         * @return Index of the starting marker
         */
        public int getStartIndex(String s) {
            return indexOfNotInQuotes(s, startSequence);
        }

        /**
         * Gets the index of the comment starting marker.
         * @param s Line to search in
         * @return Index of the starting marker
         */
        public int getStartIndex(StringBuilder s) {
            return indexOfNotInQuotes(s, startSequence);
        }

        /**
         * Removes inner comments from a code line.
         * @param s Line
         * @return Line without inner comments
         * @example
         * <pre name="test">
         * BlockCommentStyle testStyle = new BlockCommentStyle("(", "", ")");
         * testStyle.removeInnerComments("no comments") === "no comments"
         * testStyle.removeInnerComments("(comment)") === ""
         * testStyle.removeInnerComments("((comment)") === ""
         * testStyle.removeInnerComments("un(der)com((part)ment((al(iz)ed") === "uncommented"
         * </pre>
         */
        @SuppressWarnings("empty-statement")
        public String removeInnerComments(String s) {
            StringBuilder sb = new StringBuilder(s);
            while ( removeInnerComment(sb) != null ) ;
            return sb.toString();
        }

        public abstract String removeInnerComment( StringBuilder sb );

        /**
         * Comments the given lines.
         * @param source Uncommented lines
         * @return Commented lines
         */
        public abstract Strings comment(Strings source);
    }

    /**
     * Block (multiline) comment with starting and ending markers
     */
    public static class BlockCommentStyle extends CommentStyle {
        public String midSequence;
        public String endSequence;

        public BlockCommentStyle(String start, String middle, String end) {
            startSequence = start;
            midSequence = middle;
            endSequence = end;
        }

        /**
         * Checks if a block comment is closed.
         * @param s String to test
         * @return
         * @example
         * <pre name="test">
         * BlockCommentStyle testStyle = new BlockCommentStyle("(", "", ")");
         * testStyle.IsClosed("") === true
         * testStyle.IsClosed("no comments") === true
         * testStyle.IsClosed("(unclosed") === false
         * testStyle.IsClosed("un(closed") === false
         * testStyle.IsClosed("(unclosed(") === false
         * testStyle.IsClosed("(closed)") === true
         * testStyle.IsClosed("(cl(osed)") === true
         * </pre>
         */
        public boolean IsClosed(String s) {
            String s2 = removeInnerComments(s);
            return s2.indexOf(startSequence) < 0;
        }

        /**
         * Gets the index of the comment ending marker.
         * @param s Line to search in
         * @param startIndex Index to start from
         * @return Index of the ending marker
         * @example
         * <pre name="test">
         * BlockCommentStyle testStyle = new BlockCommentStyle("[", "", "]");
         * BlockCommentStyle testStyle2 = new BlockCommentStyle("[[", "", "]]");
         * testStyle.getEndIndex("commented ] uncommented", 0) === 10
         * testStyle2.getEndIndex("commented ] uncommented", 0) === -1
         * testStyle.getEndIndex("]]", 0) === 0
         * testStyle2.getEndIndex("]]", 0) === 0
         * testStyle.getEndIndex("]]", 1) === 1
         * testStyle2.getEndIndex("]]", 1) === -1
         * testStyle.getEndIndex("", 0) === -1
         * testStyle2.getEndIndex("", 0) === -1
         * </pre>
         */
        public int getEndIndex(String s, int startIndex) {
            if ( startIndex <= 0 )
                // In-comment quotes do not matter, hence indexOf
                return s.indexOf(endSequence);
            
            String s2 = s.substring(startIndex);
            int endIndex2 = s2.indexOf(endSequence);
            return endIndex2 < 0 ? endIndex2 : startIndex + endIndex2;
        }

        /**
         * Removes the first inner comment from a line and returns it.
         * @param sb Line as a string builder to be removed from
         * @return Removed string without comment markers
         * @example
         * <pre name="test">
         * BlockCommentStyle testStyle = new BlockCommentStyle("(", "", ")");
         * StringBuilder sb1 = new StringBuilder("(first)second");
         * StringBuilder sb2 = new StringBuilder("first((second)third)");
         * testStyle.removeInnerComment(sb1) === "first"
         * testStyle.removeInnerComment(sb2) === "(second"
         * sb1.toString() === "second"
         * sb2.toString() === "firstthird)"
         * </pre>
         */
        public String removeInnerComment( StringBuilder sb ) {
            int endc = sb.indexOf(endSequence);

            if ( endc >= 0 )
            {
                int startc = sb.substring(0, endc).indexOf(startSequence);

                if ( startc >= 0  )
                {
                    String comment = sb.substring(startc + startSequence.length(), endc);
                    sb.delete(startc, endc + endSequence.length());
                    return comment;
                }
            }

            return null;
        }

        @Override
        public Strings comment(Strings source) {
            Strings dest = new Strings();
            String midSpaced = midSequence + " ";
            dest.add(startSequence);
            dest.add(midSpaced, source, "");
            dest.add(endSequence);
            return dest;
        }
    }

    /**
     * Single line comment
     */
    public static class LineCommentStyle extends CommentStyle {
        public LineCommentStyle(String start) {
            startSequence = start;
        }

        /**
         * Removes the first inner comment from a line and returns it.
         * @param sb Line as a string builder to be removed from
         * @return Removed string without comment markers
         * @example
         * <pre name="test">
         * LineCommentStyle testStyle = new LineCommentStyle("//");
         * StringBuilder sb1 = new StringBuilder("test");
         * StringBuilder sb2 = new StringBuilder("//test");
         * StringBuilder sb3 = new StringBuilder("spam//eggs");
         * testStyle.removeInnerComment(sb1) === null
         * testStyle.removeInnerComment(sb2) === "test"
         * testStyle.removeInnerComment(sb3) === "eggs"
         * sb1.toString() === "test"
         * sb2.toString() === ""
         * sb3.toString() === "spam"
         * </pre>
         */
        public String removeInnerComment( StringBuilder sb ) {
            int start = sb.indexOf(startSequence);
            if ( start < 0 ) return null;

            String comment = sb.substring(start + startSequence.length());
            sb.setLength(start);
            return comment;
        }

        private String[] makeArray(String s1, String s2) {
            String[] result = new String[s2 == null ? 1 : 3];
            result[0] = s1;
            if ( s2 != null ) {
                result[1] = s2;
                result[2] = "";
            }
            return result;
        }

        @Override
        public Strings comment(Strings source) {
            Strings dest = new Strings();
            String startSpaced = startSequence + " ";
            return dest.add(startSpaced, source, "");
        }
    }

    /**
     * Tries to guess the comment style used in the line.
     * @param line Commented line
     * @param afterBeginning Check for comments starting after index 0
     * @return Comment style (null if unknown or not a comment)
     * @example
     * <pre name="test">
     * CommentStyles.guess("no comment", true) === null
     * CommentStyles.guess("//comment", true) === CommentStyles.Cpp
     * CommentStyles.guess("/*comment", true) === CommentStyles.C
     * CommentStyles.guess("/**comment", true) === CommentStyles.JavaDoc
     * CommentStyles.guess("///comment", true) === CommentStyles.Xml
     * CommentStyles.guess("///comment//c2/*c3", true) === CommentStyles.Xml
     * </pre>
     */
    public static CommentStyle guess(String line, boolean afterBeginning) {
        String s = line.trim();
        int sl = s.length();
        CommentStyle bestGuess = null;
        int bestIndex = Integer.MAX_VALUE;

        for ( CommentStyle style : All ) {
            if ( sl < style.startSequence.length() )
                continue;

            int startIndex = style.getStartIndex(s);

            if ( startIndex < 0 || (startIndex > 0 && !afterBeginning) )
                continue;

            if ( startIndex < bestIndex || style.startSequence.length() > bestGuess.startSequence.length() ) {
                if ( bestGuess instanceof BlockCommentStyle ) {
                    BlockCommentStyle bgStyle = (BlockCommentStyle)bestGuess;
                    if ( bgStyle.getEndIndex(s, startIndex) == bgStyle.endSequence.length() )
                        // Exception: /**/ should be C, not Javadoc comment
                        continue;
                }

                // The new best guess
                bestGuess = style;
                bestIndex = startIndex;
            }
        }

        return bestGuess;
    }
}
