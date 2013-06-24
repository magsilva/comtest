package comtest.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream for Strings class
 * @author tojukarp
 */
public class StringsOutputStream extends OutputStream {
    // #import comtest.utils.Strings;
    // #import java.io.IOException;
    // #import java.io.OutputStream;
    // #import java.io.OutputStreamWriter;

    Strings dest;
    StringBuilder curLine = new StringBuilder();
    
    /**
     * Creates a new stream
     * @param dst String list to write to
     */
    public StringsOutputStream(Strings dst) {
        dest = dst;
    }
    
    /**
     * Writes one byte to the stream.
     * @param b
     * @throws IOException
     * @example
     * <pre name="test">
     * Strings dest = new Strings();
     * OutputStream stream = new StringsOutputStream(dest);
     * OutputStreamWriter writer = new OutputStreamWriter(stream);
     * try {
     *   writer.append("testi1").append((char)0x0D).append((char)0x0A);
     *   writer.append("kissa2").append((char)0x0D).append((char)0x0A);
     *   writer.append("hattu3").append((char)0x0D).append((char)0x0A);
     *   writer.close();
     *   stream.close();
     * } catch (IOException ioe) {
     *   fail( ioe.toString() );
     * }
     * dest.size() === 3;
     * dest.get(0) === "testi1";
     * dest.get(1) === "kissa2";
     * dest.get(2) === "hattu3";
     * </pre>
     */
    @Override
    public void write(int b) throws IOException {
        char c = (char)b;
        
        if ( b < 0 || b == 0x0A ) {
            // End of file or line feed -> change line
            dest.add(curLine.toString());
            curLine.setLength(0);
            return;
        }

        if ( b == 0x0D )
            // Carriage return, do nothing
            return;

        curLine.append( (char)b );
    }

    @Override
    public void close() throws IOException {
        super.close();
        
        if ( curLine.length() > 0 )
            dest.add(curLine.toString());
    }
}
