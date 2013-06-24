package comtest.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;

/**
 * Utilities for manipulating XML documents
 * @author tojukarp
 */
public abstract class XmlUtilities {
    // #import org.w3c.dom.*;

    private static DocumentBuilder builder;

    /**
     * Creates an empty DOM document
     * @return Document
     */
    public static Document createDomDocument() {
        try {
            if ( builder == null )
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = builder.newDocument();
            return doc;

        } catch (ParserConfigurationException pce) { }

        return null;
    }

    /**
     * Returns an DOM node from node list with a specified name.
     * @param nodes Node list
     * @param name Node name
     * @return Node with specified name, or null if not found
     * @example
     * <pre name="test">
     * Document d = createDomDocument();
     * Node root = d.appendChild( d.createElement("Root") );
     * Node n1 = root.appendChild( d.createElement("Node1") );
     * Node n2 = root.appendChild( d.createElement("Node2") );
     * getNodeByName(root.getChildNodes(), "Node1") === n1;
     * getNodeByName(root.getChildNodes(), "Node2") === n2;
     * </pre>
     */
    public static Node getNodeByName(NodeList nodes, String name) {
        for ( int i = 0; i < nodes.getLength(); i++ ) {
            if ( nodes.item(i).getNodeName().equalsIgnoreCase(name) )
                return nodes.item(i);
        }

        return null;
    }

    /**
     * Finds the DOM node with specified name from a node list and returns
     * its content as a string.
     * @param nodes Node list
     * @param name Node name
     * @return Content of the node as a string, or null if no node/content
     * @example
     * <pre name="test">
     * Document d = createDomDocument();
     * Node root = d.appendChild( d.createElement("Root") );
     * Node n1 = root.appendChild( d.createElement("Node1") );
     * n1.appendChild( d.createTextNode("Value1") );
     * Element e2 = d.createElement("Node2");
     * e2.appendChild( d.createTextNode("Value2") );
     * Node n2 = root.appendChild( e2 );
     * getNodeContent(root.getChildNodes(), "Node1") === "Value1";
     * getNodeContent(root.getChildNodes(), "Node2") === "Value2";
     * getNodeContent(root.getChildNodes(), "NotFound") === null;
     * </pre>
     */
    public static String getNodeContent(NodeList nodes, String name) {
        Node node = getNodeByName(nodes, name);
        if ( node == null || node.getFirstChild() == null ) return null;
        return node.getTextContent();
    }

    /*public static void setNodeContent(Document doc, Node rootNode, String name, String value) {
        Node existingNode = getNodeByName(rootNode.getChildNodes(), name);

        if ( existingNode == null ) {
            Node newNode = doc.createElement(name);
            existingNode = rootNode.appendChild(newNode);
        }

        existingNode.setTextContent(value);
    }*/
}
