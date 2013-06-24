// ComTest - Comments for testing
package comtest;

import comtest.utils.Strings;
import java.util.ArrayList;
import java.util.List;
import comtest.CommentStyles.*;

/**
 * Reads and organizes code comments.
 * @author tojukarp
 */
public class CommentHandler {
    public static class CommentBlock {
        public CommentStyle style;
        public Strings content;
        public int codeLine = -1;

        public CommentBlock() {
            style = null;
            content = new Strings();
        }

        public Strings toStrings() {
            return style.comment(content);
        }
    }

    public List<CommentBlock> blocks;
    public CommentBlock currentBlock;

    /**
     * Constructs a new comment handler
     */
    public CommentHandler() {
        blocks = new ArrayList<CommentBlock>();
        currentBlock = null;
    }

    /**
     * Reads a line and returns it stripped of comments.
     * Comments are added to currentBlock, and to blocks after end of comment.
     * @param line Line to process
     * @param lineNum Line number in code starting from 0 (use negative if irrelevant)
     * @return Line without comments
     * @example
     * <pre name="test"
     * CommentHandler c = new CommentHandler();
     * c.readLine("// Line comment", 1) === ""
     * c.readLine("code /// XML comment", 2) === "code "
     * c.readLine("more code /* block starts", 3) === "more code "
     * c.readLine("commented code*" + "/", 4) === ""
     * c.readLine("/*commented*" + "/uncommented", 5) === "uncommented"
     * c.readLine("commented*" + "/uncommented", 6) === "commented*" + "/uncommented"
     * c.readLine("/**Javadoc", 7) === ""
     * c.readLine("Continues*" + "/code", 8) === "code"
     * c.currentBlock === null
     * c.blocks.size() === 5
     * c.blocks.get(0).content.get(0) === " Line comment"
     * c.blocks.get(1).content.get(0) === " XML comment"
     * c.blocks.get(2).content.get(0) === " block starts"
     * c.blocks.get(2).content.get(1) === "commented code"
     * c.blocks.get(3).content.get(0) === "commented"
     * c.blocks.get(4).content.get(0) === "Javadoc"
     * c.blocks.get(4).content.get(1) === "Continues"
     * </pre>
     */
    @SuppressWarnings("empty-statement")
    public String readLine(String line, int lineNum) {
        if ( currentBlock != null && currentBlock.style instanceof BlockCommentStyle ) {
            // Commented out
            BlockCommentStyle blockStyle = (BlockCommentStyle)currentBlock.style;
            int endComment = blockStyle.getEndIndex(line, 0);
            int midLen = blockStyle.midSequence.length();

            if ( midLen > 0 ) {
                // Remove mid-comment marker and everything before it
                int midComment = line.indexOf(blockStyle.midSequence);

                if ( midComment >= 0 && ( endComment < 0 || midComment + midLen < endComment ) )
                    line = line.substring(midComment + midLen);
            }

            if ( endComment < 0 ) {
                currentBlock.content.add(line);
                return "";
            }
            else {
                line = blockStyle.startSequence + line;
            }
        }

        StringBuilder codeBuilder = new StringBuilder(line);
        CommentStyle style;

        while ( true ) {
            style = CommentStyles.guess(codeBuilder.toString(), true);
            if ( style == null ) return codeBuilder.toString();

            String comment = style.removeInnerComment(codeBuilder);
            if ( comment == null ) break;

            addComment(comment, style, lineNum);
            terminateBlock();
        }

        int startc = style.getStartIndex(codeBuilder);
        String code = codeBuilder.substring(0, startc);
        String comment = codeBuilder.substring(startc + style.startSequence.length());
        addComment(comment, style, lineNum);
        return code;
    }

    private void addComment(String comment, CommentStyle style, int lineNum) {
        if ( currentBlock != null && style != currentBlock.style ) {
            // Terminate current block before entering a new one
            terminateBlock();
        }

        if ( currentBlock == null ) {
            // Start a new block
            currentBlock = new CommentBlock();
            currentBlock.codeLine = lineNum;
            currentBlock.style = style;
        }

        currentBlock.content.add(comment);
    }

    /**
     * Terminates the current comment block and adds its contents to
     * blocks list.
     */
    public void terminateBlock() {
        if ( currentBlock == null )
            return;

        blocks.add(currentBlock);
        currentBlock = null;
    }

    /**
     * Clears all read blocks. Does not affect the current block.
     */
    public void clear() {
        blocks = new ArrayList<CommentHandler.CommentBlock>();
    }
}
