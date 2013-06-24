// ComTest - Comments for testing
package comtest.csharp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import comtest.utils.Strings;
import static comtest.utils.UuidUtilities.*;

/**
 * C# solution file for Visual Studio 2010
 * @author tojukarp
 */
public class CSSolutionFile {
    public static class ProjectEntry {
        public String fileName;
        public UUID typeGuid;
        public Strings otherData;

        // These are also found in the project
        public UUID guid;
        public String assemblyName;

        public ProjectEntry() {
            otherData = new Strings();
        }
    }

    public static class GlobalSection {
        protected String name;
        protected String value;
        protected Strings content;

        public GlobalSection(String name, String value) {
            content = new Strings();
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public Strings getContent() {
            return content;
        }

        /*public void setContent(Strings newContent) {
            content = newContent;
        }*/

        public void addContent(String newLine) {
            content.add(newLine);
        }
    }

    public static class SolutionConfigPlatforms extends GlobalSection {        
        public static class Item {
            public String configuration;
            public String platform;
            public String configuration2;
            public String platform2;

            public Item(String s) {
                String[] elements = s.split("( = )|\\|");
                configuration = elements[0];
                platform = elements[1];
                configuration2 = elements[2];
                platform2 = elements[3];
            }

            @Override
            public String toString() {
                return String.format("  %s|%s = %s|%s", configuration, platform, configuration2, platform2);
            }
        }       

        public static String getSectionName() { return "SolutionConfigurationPlatforms"; }
        public static String getSectionValue() { return "preSolution"; }

        public Set<Item> items;

        public SolutionConfigPlatforms() {
            super(getSectionName(), getSectionValue());
            items = new HashSet<Item>();
        }

        @Override public String getName() { return getSectionName(); }
        @Override public String getValue() { return getSectionValue(); }

        @Override
        public Strings getContent() {
            Strings cnt = new Strings();

            for ( Item item : items )
                cnt.add( item.toString() );

            return cnt;
        }

        @Override
        public void addContent(String newLine) {
            items.add( new Item(newLine.trim()) );
        }
    }

    public static class ProjectConfigPlatforms extends GlobalSection {
        public static class Item {
            public UUID guid;
            public String slnConfiguration;
            public String slnPlatform;
            public String projConfiguration;
            public String projPlatform;

            public Item() {
            }

            public Item(String s) {
                guid = strToUuid( s.substring(0, 38) );
                String s2 = s.substring(39);
                String[] elements = s2.split("( = )|\\|");
                slnConfiguration = elements[0];
                slnPlatform = elements[1];
                projConfiguration = elements[2];
                projPlatform = elements[3];
            }

            @Override
            public String toString() {
                return String.format("  %s.%s|%s = %s|%s", uuidToStr(guid, true),
                        slnConfiguration, slnPlatform,
                        projConfiguration, projPlatform);
            }
        }

        public static String getSectionName() { return "ProjectConfigurationPlatforms"; }
        public static String getSectionValue() { return "postSolution"; }

        public Set<Item> items;

        public ProjectConfigPlatforms() {
            super(getSectionName(), getSectionValue());
            items = new HashSet<Item>();
        }

        @Override public String getName() { return getSectionName(); }
        @Override public String getValue() { return getSectionValue(); }

        @Override
        public Strings getContent() {
            Strings cnt = new Strings();

            for ( Item item : items )
                cnt.add( item.toString() );

            return cnt;
        }

        @Override
        public void addContent(String newLine) {
            items.add( new Item(newLine.trim()) );
        }

        public void removeByGuid(UUID guid) {
            Collection<Item> removeItems = new HashSet<Item>();

            for ( Item item : items ) {
                if ( item.guid == guid )
                    removeItems.add(item);
            }
            
            items.removeAll(items);
        }
    }

    public Set<ProjectEntry> projects;
    public Map<String, GlobalSection> globalSections;

    public CSSolutionFile() {
        projects = new HashSet<ProjectEntry>();
        globalSections = new HashMap<String, GlobalSection>();
    }
    
    public SolutionConfigPlatforms getSolutionConfigPlatforms() {
        return (SolutionConfigPlatforms)globalSections.get(SolutionConfigPlatforms.getSectionName());
    }

    public ProjectConfigPlatforms getProjectConfigPlatforms() {
        return (ProjectConfigPlatforms)globalSections.get(ProjectConfigPlatforms.getSectionName());
    }

    public ProjectEntry getProjectByName(String name) {
        for ( ProjectEntry project : projects ) {
            if ( project.assemblyName.equals(name) )
                return project;
        }

        return null;
    }

    /**
     * Add project to solution.
     * @param project Project details (reference not copied, only some details)
     * @param projectFile Project file name. No file access is performed.
     */
    public void addProject(CSProjectFile project, String projectFile) {
        ProjectEntry newEntry = new ProjectEntry();
        newEntry.guid = project.getGuid();
        newEntry.typeGuid = CSProjectFile.ProjectType_WindowsCS;
        newEntry.assemblyName = project.getAssemblyName();
        newEntry.fileName = projectFile;
        projects.add(newEntry);

        final String[] configs = {"ActiveCfg", "Build.0"};
        ProjectConfigPlatforms projPlatforms = getProjectConfigPlatforms();

        // Add default build targets
        for ( SolutionConfigPlatforms.Item slnTarget : getSolutionConfigPlatforms().items ) {
            for ( String config : configs ) {
                ProjectConfigPlatforms.Item newTarget = new ProjectConfigPlatforms.Item();
                newTarget.guid = project.getGuid();
                newTarget.projConfiguration = slnTarget.configuration;
                newTarget.projPlatform = slnTarget.platform;
                newTarget.slnConfiguration = slnTarget.configuration;
                newTarget.slnPlatform = String.format("%s.%s", slnTarget.platform, config);
                projPlatforms.items.add(newTarget);
            }
        }
    }

    /**
     * Gets a project entry in the solution, or null if not in it.
     * @param project Project details
     * @return Project entry in solution
     */
    public ProjectEntry getProjectEntry(CSProjectFile project) {
        for ( ProjectEntry entry : projects ) {
            if ( entry.guid == project.getGuid() )
                return entry;
        }

        return null;
    }

    /**
     * Gets a project entry in the solution, or null if not in it.
     * @param fileName Project file name
     * @return Project entry in solution
     */
    public ProjectEntry getProjectEntry(String fileName) {
        for ( ProjectEntry entry : projects ) {
            if ( entry.fileName.equalsIgnoreCase(fileName) )
                return entry;
        }

        return null;
    }

    /**
     * Remove project from solution.
     * @param fileName Project file name
     */
    public void removeProject(String fileName) {
        ProjectEntry entryToRemove = getProjectEntry(fileName);
        if ( entryToRemove == null )
            return;

        projects.remove(entryToRemove);
        getProjectConfigPlatforms().removeByGuid(entryToRemove.guid);
    }
}
