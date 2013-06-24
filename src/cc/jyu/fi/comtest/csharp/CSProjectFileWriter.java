// ComTest - Comments for testing
package cc.jyu.fi.comtest.csharp;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import cc.jyu.fi.comtest.ComTestException;
import cc.jyu.fi.comtest.utils.Strings;
import cc.jyu.fi.comtest.utils.StringsOutputStream;

/**
 * Writes csproj files
 * @author tojukarp
 */
public class CSProjectFileWriter {
    private static Transformer xformer;
    
    public static Strings write(CSProjectFile source) throws ComTestException {
        Strings dest = new Strings();
        OutputStream destStream = new StringsOutputStream(dest);

        try {
            Source domSource = new DOMSource(source.getDocument());
            Result result = new StreamResult(destStream);
            if ( xformer == null )
                xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(domSource, result);

        } catch (TransformerConfigurationException tce) {
            throw new ComTestException(tce.toString(), true);
        } catch (TransformerException te) {
            throw new ComTestException(te.toString(), true);
        }

        try {
            destStream.close();
        } catch (IOException ioe) {
        }

        return dest;
    }
}
