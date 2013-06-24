// ComTest - Comments for testing
package comtest.csharp;

import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.*;
import comtest.utils.Strings;
import static comtest.utils.StringUtilities.*;
import static comtest.utils.UuidUtilities.*;
import static comtest.utils.XmlUtilities.*;

//#import comtest.utils.Strings;

/**
 * C# project file for Visual Studio 2010
 * @author tojukarp
 */
public class CSProjectFile {
    // #import org.w3c.dom.*;
    // #import static comtest.utils.XmlUtilities.*;
    
    public static final UUID ProjectType_WindowsCS = UUID.fromString("FAE04EC0-301F-11D3-BF4B-00C04F79EFBC");
    public static final UUID ProjectType_Test = UUID.fromString("3AC096D0-A1C2-E12C-1390-A8335801FDAB");
    public static final UUID ProjectType_SilverlightWP7 = UUID.fromString("C089C8C0-30E0-4E22-80C0-CE093F111A43");

    public static class Reference {
        public String assemblyName;
        public boolean isPrivate;

        public Reference(String assemblyName) {
            this.assemblyName = assemblyName;
        }
    }

    public static class ExternalReference extends Reference {
        public String hintPath;

        public ExternalReference(String assemblyName, String hintPath) {
            super(assemblyName);
            this.hintPath = hintPath;
        }
    }

    public static class ProjectReference extends ExternalReference {
        public UUID projectGuid;

        public ProjectReference(String assemblyName, String hintPath, UUID guid) {
            super(assemblyName, hintPath);
            projectGuid = guid;
        }

        public ProjectReference(CSProjectFile project, String projectPath) {
            this( project.getAssemblyName(), projectPath, project.getGuid() );
        }
    }

    private Document doc;
    private Element mainPropertiesNode;
    private Element referenceNode;
    private Element projReferenceNode;
    private Element sourceNode;
    private Strings straySourceFiles = null;

    public CSProjectFile(Document documentData) {
        doc = documentData;
        Node node = doc.getFirstChild().getFirstChild();

        while ( node != null ) {
            String nodeName = node.getNodeName();

            if ( nodeName.equalsIgnoreCase("PropertyGroup") ) {
                Element propertyGroup = (Element)node;

                if ( propertyGroup.getElementsByTagName("AssemblyName").getLength() == 1 )
                    mainPropertiesNode = (Element)node;
            }
            else if ( nodeName.equalsIgnoreCase("ItemGroup") ) {
                Element itemGroup = (Element)node;

                if ( itemGroup.getElementsByTagName("Reference").getLength() > 0 )
                    referenceNode = (Element)node;
                else if(itemGroup.getElementsByTagName("ProjectReference").getLength() > 0)
                    projReferenceNode = (Element)node;
                else if ( itemGroup.getElementsByTagName("Compile").getLength() > 0 )
                {
                    if ( sourceNode != null && straySourceFiles == null )
                    {
                        // Multiple Compile tags. Create a new one if adding more files.
                        straySourceFiles = new Strings();
                        straySourceFiles.addAll( this.getSourceFiles() );
                        Element compileElement = (Element)node;
                        straySourceFiles.add( compileElement.getAttribute("Include") );
                        sourceNode = null;
                    }
                    else
                        sourceNode = (Element)node;
                }
            }

            node = node.getNextSibling();
        }

        //mainProperties = mainPropertiesNode.getChildNodes();
    }

    /**
     * Gets the project data as a DOM document
     * @return DOM document
     */
    public Document getDocument() {
        return doc;
    }

    /**
     * Gets a property from the main property group
     * @param name Property name
     * @return Property value
     * @example
     * <pre name="test">
     * Document doc = createDomDocument();
     * Element root = doc.createElement("Project");
     * Element group = doc.createElement("PropertyGroup");
     * Element asname = doc.createElement("AssemblyName");
     * Element prop1 = doc.createElement("FirstProperty");
     * prop1.setTextContent("FirstValue");
     * Element prop2 = doc.createElement("SecondProperty");
     * prop2.setTextContent("SecondValue");
     * Element prop3 = doc.createElement("ThirdProperty");
     * group.appendChild(asname);
     * group.appendChild(prop1);
     * group.appendChild(prop2);
     * group.appendChild(prop3);
     * root.appendChild(group);
     * doc.appendChild(root);
     * CSProjectFile projfile = new CSProjectFile(doc);
     * projfile.getMainProperty("FirstProperty") === prop1;
     * projfile.getMainProperty("SecondProperty") === prop2;
     * projfile.getMainProperty("ThirdProperty") === prop3;
     * projfile.getMainProperty("InvalidProperty") === null;
     * </pre>
     */
    public Element getMainProperty(String name) {
        NodeList matches = mainPropertiesNode.getElementsByTagName(name);
        if ( matches == null || matches.getLength() == 0 ) return null;
        return (Element)matches.item(0);
    }

    /**
     * Gets the first matching property from the main property group
     * @param name Property name
     * @return Property value
     * @example
     * <pre name="test">
     * Document doc = createDomDocument();
     * Element root = doc.createElement("Project");
     * Element group = doc.createElement("PropertyGroup");
     * Element asname = doc.createElement("AssemblyName");
     * Element prop1 = doc.createElement("FirstProperty");
     * prop1.setTextContent("FirstValue");
     * Element prop2 = doc.createElement("SecondProperty");
     * prop2.setTextContent("SecondValue");
     * Element prop3 = doc.createElement("ThirdProperty");
     * group.appendChild(asname);
     * group.appendChild(prop1);
     * group.appendChild(prop2);
     * group.appendChild(prop3);
     * root.appendChild(group);
     * doc.appendChild(root);
     * CSProjectFile projfile = new CSProjectFile(doc);
     * projfile.getMainPropertyValue("FirstProperty") === prop1.getTextContent();
     * projfile.getMainPropertyValue("SecondProperty") === prop2.getTextContent();
     * projfile.getMainPropertyValue("ThirdProperty") === "";
     * projfile.getMainPropertyValue("InvalidProperty") === null;
     * </pre>
     */
    public String getMainPropertyValue(String name) {
        Element propNode = getMainProperty(name);
        if ( propNode == null ) return null;
        return propNode.getTextContent();
    }

    /**
     * Gets all matching properties from the main property group
     * @param name Property name
     * @return Property value
     * @example
     * <pre name="test">
     * Document doc = createDomDocument();
     * Element root = doc.createElement("Project");
     * Element group = doc.createElement("PropertyGroup");
     * Element asname = doc.createElement("AssemblyName");
     * Element prop1 = doc.createElement("FirstProperty");
     * prop1.setTextContent("FirstValue");
     * Element prop2 = doc.createElement("FirstProperty");
     * prop2.setTextContent("SecondValue");
     * Element prop3 = doc.createElement("ThirdProperty");
     * group.appendChild(asname);
     * group.appendChild(prop1);
     * group.appendChild(prop2);
     * group.appendChild(prop3);
     * root.appendChild(group);
     * doc.appendChild(root);
     * CSProjectFile projfile = new CSProjectFile(doc);
     * Strings match1 = projfile.getMainPropertyValues("FirstProperty");
     * match1.get(0) === prop1.getTextContent();
     * match1.get(1) === prop2.getTextContent();
     * projfile.getMainPropertyValues("ThirdProperty").get(0) === "";
     * projfile.getMainPropertyValues("InvalidProperty").size() === 0;
     * </pre>
     */
    public Strings getMainPropertyValues(String name) {
        Strings values = new Strings();
        NodeList matches = mainPropertiesNode.getElementsByTagName(name);
        if ( matches == null ) return values;

        for ( int i = 0; i < matches.getLength(); i++ ) {
            Node propNode = matches.item(i);
            values.add( propNode.getTextContent() );
        }

        return values;
    }

    /**
     * Sets a single-property value in the main group.
     * If one exists, its value will be overwritten.
     * @param name Name of the property
     * @param value New value
     * @example
     * <pre name="test">
     * Document doc = createDomDocument();
     * Element root = doc.createElement("Project");
     * Element group = doc.createElement("PropertyGroup");
     * Element asname = doc.createElement("AssemblyName");
     * group.appendChild(asname);
     * root.appendChild(group);
     * doc.appendChild(root);
     *
     * CSProjectFile projfile = new CSProjectFile(doc);
     * projfile.setMainPropertyValue("test", "value");
     * projfile.getMainPropertyValue("test") === "value";
     * projfile.setMainPropertyValue("test", "newvalue");
     * projfile.getMainPropertyValue("test") === "newvalue";
     * projfile.getMainPropertyValue("invalid") === null;
     * </pre>
     */
    public void setMainPropertyValue(String name, String value) {
        Element existingElement = getMainProperty(name);
        
        if ( existingElement == null ) {
            if ( value == null ) return;
            existingElement = doc.createElement(name);
            mainPropertiesNode.appendChild(existingElement);
        }
        else if ( value == null ) {
            mainPropertiesNode.removeChild(existingElement);
            return;
        }
        
        existingElement.setTextContent(value);
    }
    
    /**
     * Adds multiple values for a main property.
     * @param name Name of the property
     * @param values Values
     * <pre name="test">
     * Document doc = createDomDocument();
     * Element root = doc.createElement("Project");
     * Element group = doc.createElement("PropertyGroup");
     * Element asname = doc.createElement("AssemblyName");
     * group.appendChild(asname);
     * root.appendChild(group);
     * doc.appendChild(root);
     *
     * CSProjectFile projfile = new CSProjectFile(doc);
     * Strings vals = new Strings();
     * vals.add("first");
     * vals.add("second");
     * vals.add("third");
     *
     * projfile.addMainPropertyValues("test", vals);
     * Strings vals2 = projfile.getMainPropertyValues("test");
     * vals2.size() === vals.size();
     * vals2.get(0) === vals.get(0);
     * vals2.get(1) === vals.get(1);
     * vals2.get(2) === vals.get(2);
     * </pre>
     */
    public void addMainPropertyValues(String name, Strings values) {
        for ( int i = 0; i < values.size(); i++ ) {
            Element newElement = doc.createElement(name);
            newElement.setTextContent(values.get(i));
            mainPropertiesNode.appendChild(newElement);
        }
    }

    public UUID getGuid() { return strToUuid( getMainPropertyValue("ProjectGuid") ); }
    public String getDesignerFolder() { return getMainPropertyValue("AppDesignerFolder"); }
    public String getRootNamespace() { return getMainPropertyValue("RootNamespace"); }
    public String getAssemblyName() { return getMainPropertyValue("AssemblyName"); }

    public void setGuid(UUID newGuid) { setMainPropertyValue("ProjectGuid", uuidToStr(newGuid, true)); }
    public void setDesignerFolder(String newFolder) { setMainPropertyValue("AppDesignerFolder", newFolder); }
    public void setRootNamespace(String rootNs) { setMainPropertyValue("RootNamespace", rootNs); }
    public void setAssemblyName(String asName) { setMainPropertyValue("AssemblyName", asName); }

    /**
     * Gets the project types
     * @return List of project type GUIDs
     */
    public List<UUID> getProjectTypes() {
        ArrayList<UUID> projTypes = new ArrayList<UUID>();
        String typeStr = getMainPropertyValue("ProjectTypeGuids");

        if ( !isEmpty(typeStr) ) {
            List<String> ptypeList = Arrays.asList(typeStr.split(";"));
            addUuids(projTypes, ptypeList);
        }

        return projTypes;
    }

    /**
     * Gets the project references.
     * @return List of references
     */
    public List<Reference> getReferences() {
        List<Reference> refs = new ArrayList<Reference>();

        if ( referenceNode != null ) {
            NodeList refNodes = referenceNode.getElementsByTagName("Reference");

            for ( int i = 0; i < refNodes.getLength(); i++ ) {
                Element refNode = (Element)refNodes.item(i);
                String assemblyName = refNode.getAttribute("Include");

                if ( isEmpty(assemblyName) )
                    continue;

                Reference newRef = new Reference(assemblyName);
                NodeList refProperties = refNode.getChildNodes();
                String privateNode = getNodeContent(refProperties, "Private");
                String hintNode = getNodeContent(refProperties, "HintPath");

                if ( hintNode != null )
                    newRef = new ExternalReference(assemblyName, hintNode);

                newRef.isPrivate =
                        ( privateNode == null || !privateNode.equalsIgnoreCase("False") );

                refs.add(newRef);
            }
        }

        if ( projReferenceNode != null ) {
            NodeList refNodes = projReferenceNode.getElementsByTagName("ProjectReference");

            for ( int i = 0; i < refNodes.getLength(); i++ ) {
                Element refNode = (Element)refNodes.item(i);
                String hintPath = refNode.getAttribute("Include");

                if ( isEmpty(hintPath) )
                    continue;

                NodeList refProperties = refNode.getChildNodes();
                String assemblyName = getNodeContent(refProperties, "Name");
                String guidStr = getNodeContent(refProperties, "Project");
                ProjectReference newRef =
                    new ProjectReference(assemblyName, hintPath, strToUuid(guidStr));

                refs.add(newRef);
            }
        }

        return refs;
    }

    /**
     * Gets the list of source files in the project
     * @return List of file name strings
     */
    public List<String> getSourceFiles() {
        if ( sourceNode == null ) {
            if ( straySourceFiles == null ) return new ArrayList<String>();
            else return straySourceFiles;
        }

        ArrayList<String> srcFiles = new ArrayList<String>();
        if (straySourceFiles != null) srcFiles.addAll(straySourceFiles);
        NodeList entries = sourceNode.getElementsByTagName("Compile");

        for ( int i = 0; i < entries.getLength(); i++ ) {
            Element entry = (Element)entries.item(i);
            String fileName = entry.getAttribute("Include");
            srcFiles.add(fileName);
        }

        return srcFiles;
    }

    /**
     * Gets the number of source files in the project
     * @return Number of source files
     */
    public int getNumSourceFiles() {
        int sourceCount = sourceNode != null ? sourceNode.getElementsByTagName("Compile").getLength() : 0;
        int strayCount = straySourceFiles != null ? straySourceFiles.size() : sourceCount;

        return sourceCount + strayCount;
    }

    /**
     * Adds a new source file into the project.
     * @param fileName Source file to add
     */
    public void addSourceFile(String fileName) {
        if ( sourceNode == null ) {
            sourceNode = doc.createElement("ItemGroup");
            doc.getFirstChild().appendChild(sourceNode);
        }

        removeSourceFile(fileName);
        Element compileElement = doc.createElement("Compile");
        compileElement.setAttribute("Include", fileName);
        sourceNode.appendChild(compileElement);
    }

    /**
     * Removes a source file from the project.
     * @param fileName Source file to remove
     */
    public void removeSourceFile(String fileName) {
        NodeList compileNodes = sourceNode.getElementsByTagName("Compile");

        for ( int i = 0; i < compileNodes.getLength(); i++ ) {
            Element compileElement = (Element)compileNodes.item(i);
            if ( compileElement.getAttribute("Include").equalsIgnoreCase(fileName) )
                sourceNode.removeChild(compileElement);
        }
    }

    /**
     * Adds a new project reference.
     * @param ref Project reference
     */
    private void addProjectReference(ProjectReference ref) {
        if ( ref.projectGuid == null || isEmpty( ref.hintPath ) )
            return;

        if ( projReferenceNode == null ) {
            projReferenceNode = doc.createElement("ItemGroup");
            doc.getFirstChild().appendChild(projReferenceNode);
        }

        Element refElement = doc.createElement("ProjectReference");
        refElement.setAttribute("Include", ref.hintPath);
        Element guidElement = doc.createElement("Project");
        guidElement.setTextContent(uuidToStr(ref.projectGuid, true));
        Element nameElement = doc.createElement("Name");
        nameElement.setTextContent(ref.assemblyName);
        
        refElement.appendChild(guidElement);
        refElement.appendChild(nameElement);
        projReferenceNode.appendChild(refElement);
    }

    public boolean projectTypeIs(UUID projType) {
        List<UUID> typeList = getProjectTypes();
        if ( typeList == null || typeList.isEmpty() ) return false;
        return typeList.contains(projType);
    }

    /**
     * Adds a new reference into the project.
     * @param ref Reference to add
     */
    public void addReference(Reference ref) {
        if ( ref instanceof ProjectReference ) {
            addProjectReference( (ProjectReference)ref );
            return;
        }

        if ( referenceNode == null ) {
            // Create a reference group if none
            referenceNode = doc.createElement("ItemGroup");
            doc.getFirstChild().appendChild(referenceNode);
        }
        else {
            // Do not allow duplicate references
            NodeList refs = referenceNode.getElementsByTagName("Reference");
            for ( int i = 0; i < refs.getLength(); i++ ) {
                Element e = (Element)refs.item(i);

                if ( e.getAttribute("Include").equalsIgnoreCase(ref.assemblyName) )
                    return;
            }
        }

        Element refElement = doc.createElement("Reference");
        refElement.setAttribute("Include", ref.assemblyName);

        if ( ref instanceof ExternalReference ) {
            ExternalReference eref = (ExternalReference)ref;
            Element hintElement = doc.createElement("HintPath");
            hintElement.setTextContent(eref.hintPath);
            refElement.appendChild(hintElement);
        }
        if ( !ref.isPrivate ) {
            Element privElement = doc.createElement("Private");
            privElement.setTextContent("False");
            refElement.appendChild(privElement);
        }

        sourceNode.appendChild(refElement);
    }

    public CSSourceFile GeneratePropertiesFile() {
        CSSourceFile propertiesFile = new CSSourceFile();
        Strings metadata = new Strings();

        propertiesFile.usings.add("System.Reflection");
        propertiesFile.usings.add("System.Runtime.CompilerServices");
        propertiesFile.usings.add("System.Runtime.InteropServices");

        metadata.add("assembly: AssemblyTitle( \"" +  getAssemblyName() + "\" )");
        metadata.add("assembly: AssemblyDescription( \"\" )");
        metadata.add("assembly: AssemblyConfiguration( \"\" )");
        metadata.add("assembly: AssemblyCompany( \"Microsoft\" )");
        metadata.add("assembly: AssemblyProduct( \"" + getAssemblyName() + "\" )");
        metadata.add("assembly: AssemblyCopyright( \"Copyright Â© Microsoft 2011\" )");
        metadata.add("assembly: AssemblyTrademark( \"\" )");
        metadata.add("assembly: AssemblyCulture( \"\" )");

        metadata.add("assembly: ComVisible( false )");
        metadata.add("assembly: Guid( \"" + uuidToStr(getGuid(), false) + "\" )");

        metadata.add("assembly: AssemblyVersion( \"1.0.0.0\" )");
        metadata.add("assembly: AssemblyFileVersion( \"1.0.0.0\" )");

        CSSourceFile.Class defaultcl = new CSSourceFile.Class("");
        defaultcl.decoratorAttributes = metadata;
        propertiesFile.namespaces.get("").classes.put("", defaultcl);
        return propertiesFile;
    }
}
