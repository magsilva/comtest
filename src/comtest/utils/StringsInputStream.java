package comtest.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream for Strings class
 * @author tojukarp
 */
public class StringsInputStream extends InputStream {
    // #import comtest.utils.Strings;
    // #import java.io.IOException;
    // #import java.util.Scanner;

    private static final byte[] CRLFB = {0x0D, 0x0A};
    private static final String CRLF = new String(CRLFB);

    Strings src;
    String curLine = null;
    int nextLineNum = 0;
    int nextCharNum = 0;
    
    /**
     * Creates a new stream
     * @param source String list to read from
     */
    public StringsInputStream(Strings source) {
        src = source;
    }
    
    /**
     * Reads one byte from the stream.
     * Negative index indicates end of the file.
     * @return Byte as integer
     * @throws IOException
     * @example
     * <pre name="test">
     * Strings s = new Strings();
     * StringsInputStream stream = new StringsInputStream(s);
     * Scanner sc = new Scanner(stream);
     * int i = 0;
     * s.add("testi1");
     * s.add("kissa2");
     * s.add("hattu3");
     * while ( sc.hasNext() )
     *   sc.nextLine() === s.get(i++);
     * sc.close();
     * try {
     *   stream.close();
     * } catch (IOException ioe) {
     *   fail( ioe.toString() );
     * }
     * </pre>
     */
    @Override
    public int read() throws IOException {
        try {
            do {
                if ( curLine == null ) {
                    // Read the next line, if any
                    if ( nextLineNum >= src.size() ) return -1; // EOF
                    curLine = src.get(nextLineNum).concat(CRLF);
                    nextLineNum++;
                    nextCharNum = 0;
                }

                if ( nextCharNum >= curLine.length() ) {
                    // End of line
                    curLine = null;
                }
            } while ( curLine == null );

            return curLine.charAt(nextCharNum++);

        } catch ( Exception e ) {
            throw new IOException(e.getClass() + " : " + e.getMessage());
        }
    }
}
