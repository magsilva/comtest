// ComTest - Comments for testing
package comtest.csharp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import comtest.utils.Strings;
import java.util.EnumMap;
import static comtest.CommentHandler.*;
import static comtest.utils.StringUtilities.*;

/**
 * Contents of a C# source file.
 * @author tojukarp
 */
public class CSSourceFile {
    public static final String[] AccessModifiers = {"public", "protected", "internal", "private"};
    public static final String IndentStr = "\t";

    //public Strings debug;
    public Strings usings;
    public Map<String, Namespace> namespaces;

    public CSSourceFile() {
        //debug = new Strings();
        usings = new Strings();
        namespaces = new HashMap<String, Namespace>();
        namespaces.put("", new Namespace(""));
    }

    public static class Block {
        public Block parent = null;
        public int codeLine;
    }

    public static interface CodeBlock {
        public void add(String codeLine);
        public void add(Strings codeLines);
        public void add(CodeBlock block);

        public Strings getCode();
        public int size();
        public boolean isEmpty();

        public String get(int i);
        public void set(int i, String value);

        public String removeLastLine();
        public void indent(int tabs);
    }

    public static class SimpleCodeBlock extends Block implements CodeBlock {
        private Strings code = new Strings();

        public SimpleCodeBlock() {}
        public SimpleCodeBlock(Strings lines) {
            add(lines);
        }

        public String get(int i) { return code.get(i); }
        public void set(int i, String value) { code.set(i, value); }

        public Strings getCode() { return code; }
        public int size() { return code.size(); }

        public boolean isEmpty() {
            if ( code.size() == 0 ) return true;
            
            for ( int i = 0; i < code.size(); i++ ) {
                if ( code.get(i).length() > 0 ) return false;
            }
            
            return true;
        }

        public void add(String codeLine) { code.add(codeLine); }
        public void add(Strings codeLines) { code.add(codeLines); }
        public void add(CodeBlock block) { code.add( block.getCode() ); }

        public String removeLastLine() {
            if ( code.size() == 0 )
                return null;
            return code.remove(code.size() - 1);
        }
        
        public void indent(int tabs) {
            code.prepend(repeat("\t", tabs));
        }
    }
    
    public static class NestedCodeBlock extends Block implements CodeBlock {
        private int selectedBlock = 1;

        public CodeBlock blockBefore = new SimpleCodeBlock();
        public CodeBlock blockInside = new SimpleCodeBlock();
        public CodeBlock blockAfter = new SimpleCodeBlock();

        public Strings getCode() {
            Strings allCode = new Strings();
            allCode.add(blockBefore.getCode());
            allCode.add(blockInside.getCode());
            allCode.add(blockAfter.getCode());
            return allCode;
        }
        
        public int size() {
            return blockBefore.size() + blockInside.size() + blockAfter.size();
        }

        public boolean isEmpty() {
            return blockBefore.isEmpty() && blockInside.isEmpty() && blockAfter.isEmpty();
        }

        public String get(int i) {
            if ( i < blockBefore.size() )
                return blockBefore.get(i);
            else if ( i - blockBefore.size() < blockInside.size() )
                return blockInside.get(i - blockBefore.size());

            return blockAfter.get(i - blockBefore.size() - blockInside.size());
        }

        public void set(int i, String value) {
            if ( i < blockBefore.size() )
                blockBefore.set(i, value);
            else if ( i - blockBefore.size() < blockInside.size() )
                blockInside.set(i - blockBefore.size(), value);
            else
                blockAfter.set(i - blockBefore.size() - blockInside.size(), value);
        }

        public void SelectBlockBefore() { selectedBlock = 0; }
        public void SelectBlockInside() { selectedBlock = 1; }
        public void SelectBlockAfter() { selectedBlock = 2; }

        public CodeBlock getSelectedBlock() {
            switch ( selectedBlock ) {
                case 0: return blockBefore;
                case 1: return blockInside;
                case 2: return blockAfter;
            }
            return null;
        }

        public void add(String codeLine) {
            getSelectedBlock().add(codeLine);
        }

        public void add(Strings codeLines) {
            getSelectedBlock().add(codeLines);
        }

        public void add(CodeBlock block) {
            getSelectedBlock().add(block);
        }

        public String removeLastLine() {
            return getSelectedBlock().removeLastLine();
        }
        
        public void indent(int tabs) {
            blockBefore.indent(tabs);
            blockInside.indent(tabs);
            blockAfter.indent(tabs);
        }
    }

    public static class NamedBlock extends Block {
        public String name;
        public List<CommentBlock> commentBlocks;

        public NamedBlock(String name) {
            this.name = name;
            commentBlocks = new ArrayList<CommentBlock>();
        }
    }

    public static class Namespace extends NamedBlock {        
        public Map<String, Class> classes;

        public Namespace(String name) {
            super(name);
            classes = new HashMap<String, Class>();
        }

        public Namespace getParentNamespace() {
            Block p = this;

            do {
                p = this.parent;
            } while (p != null && !(p instanceof Namespace));

            return (Namespace)p;
        }
    }

    public static class ClassOrMethod extends Namespace {
        public String access;
        public String modifiers;
        public Strings decoratorAttributes;

        public ClassOrMethod(String name) {
            super(name);
            modifiers = "";
            access = "";
            decoratorAttributes = new Strings();
        }

        public Class getParentClass() {
            Block p = this;

            do {
                p = this.parent;
            } while (p != null && !(p instanceof Class));

            return (Class)p;
        }
    }

    public static class Class extends ClassOrMethod {        
        public Map<String, Field> fields;
        public Map<String, Property> properties;
        public List<Method> methods;
        public boolean isStruct = false;

        public Class(String name) {
            super(name);
            fields = new HashMap<String, Field>();
            properties = new HashMap<String, Property>();
            methods = new ArrayList<Method>();
        }

        public String getSignature() {
            return access + " " + modifiers + " class " + name;
        }
    }

    public static class Method extends ClassOrMethod {
        public static final String[] ImplementedModifiers = {"static", "virtual", "override"};

        public boolean isStatic;
        public boolean isVirtual;
        public boolean isOverride;

        public String returnType;
        public ArrayList<Variable> params;
        public CodeBlock body;

        public Method(String name) {
            super(name);
            params = new ArrayList<Variable>();
            body = new SimpleCodeBlock();
            returnType = "void";
        }

        public String getSignature() {
            String sig = access + " ";
            if ( isStatic ) access += "static ";
            if ( isVirtual ) access += "virtual ";
            if ( isOverride ) access += "override ";
            sig += modifiers + " " + returnType + " " + name + "(";

            for (int i = 0; i < params.size(); i++) {
                sig += params.get(i).type + " " + params.get(i).name;
            }

            sig += ")";
            return sig;
        }
    }

    public static class Variable {
        public String name;
        public String type;

        public Variable(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }
    
    public static class Field extends Variable {
        public String accessor;

        public Field(String type, String name) {
            super(type, name);
        }
    }
    
    public static class Property extends Field {
        public Method getter;
        public Method setter;

        public Property(String type, String name) {
            super(type, name);
        }
    }
}
