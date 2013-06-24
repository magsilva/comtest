// ComTest - Comments for testing
package cc.jyu.fi.comtest.csharp;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXParseException;

import cc.jyu.fi.comtest.ComTestException;
import cc.jyu.fi.comtest.utils.Strings;
import cc.jyu.fi.comtest.utils.StringsInputStream;

/**
 * Reads csproj files
 * @author tojukarp
 */
public class CSProjectFileReader {
    public static CSProjectFile read(Strings source) throws cc.jyu.fi.comtest.ComTestException {
        try {
            InputStream srcStream = new StringsInputStream(source);
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( srcStream );
            srcStream.close();

            // Normalize text representation and get root element
            doc.getDocumentElement().normalize();
            Element rootElement = doc.getDocumentElement();

            if ( !doc.getDocumentElement().getNodeName().equalsIgnoreCase("project") )
                throw new ComTestException("Project XML element missing", true);

            return new CSProjectFile(doc);

        } catch (SAXParseException err) {
            String msg = "Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId() +
                    ", message " + err.getMessage();
            throw new ComTestException(msg, true);

        }catch (Throwable t) {
            String msg = t.getClass() + " with message: " + t.getMessage();
            throw new ComTestException(msg, true);
        }
    }

    private static String getAttributeValue(Node xmlNode, String attribName) {
        if ( !xmlNode.hasAttributes() )
            return null;

        NamedNodeMap attribs = xmlNode.getAttributes();
        Node attribNode = attribs.getNamedItem(attribName);

        if ( attribNode == null )
            return null;

        return attribNode.getNodeValue();
    }
}
