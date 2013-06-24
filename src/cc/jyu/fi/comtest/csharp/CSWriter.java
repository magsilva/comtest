// ComTest - Comments for testing
package cc.jyu.fi.comtest.csharp;

import cc.jyu.fi.comtest.csharp.CSSourceFile.*;
import cc.jyu.fi.comtest.utils.Strings;

/**
 * Generates C# code from a CSSourceFile object.
 * @author tojukarp
 */
public abstract class CSWriter {
    public static Strings write(CSSourceFile source) {
        Strings dest = new Strings();

        dest.add(writeUsings(source));
        dest.add(writeNamespaces(source));
        //dest.add("// Debug data after this //");
        //dest.add(source.debug);

        return dest;
    }

    public static Strings writeUsings(CSSourceFile source) {
        Strings dest = new Strings();

        for ( String s : source.usings )
            dest.add("using " + s + ";");

        dest.add("");
        return dest;
    }

    public static Strings writeNamespaces(CSSourceFile source) {
        Strings dest = new Strings();

        for ( Namespace ns : source.namespaces.values() ) {
            boolean defaultns = (ns.name.length() == 0);

            if (!defaultns) {
                dest.add("namespace " + ns.name);
                dest.add("{");
            }

            dest.add("\t", writeClasses(ns), "");

            if (!defaultns)
                dest.add("}");
        }

        dest.add("");
        return dest;
    }

    public static Strings writeClasses(Namespace ns) {
        Strings dest = new Strings();

        for ( CSSourceFile.Class cl : ns.classes.values() ) {
            boolean defaultcl = (cl.name.length() == 0);

            for ( int i = 0; i < cl.commentBlocks.size(); i++ )
                dest.add( cl.commentBlocks.get(i).toStrings() );

            dest.add("[", cl.decoratorAttributes, "]");

            if (!defaultcl) {                                
                dest.add(cl.getSignature());
                dest.add("{");
            }

            // ...
            dest.add("\t", writeMethods(cl), "");

            if (!defaultcl)
                dest.add("}");
        }

        return dest;
    }

    public static Strings writeMethods(CSSourceFile.Class cl) {
        Strings dest = new Strings();

        for ( Method m : cl.methods ) {
            for ( int i = 0; i < m.commentBlocks.size(); i++ )
                dest.add( m.commentBlocks.get(i).toStrings() );
            dest.add("[", m.decoratorAttributes, "]");
            dest.add(m.getSignature());
            dest.add("{");
            dest.add("\t", m.body.getCode(), "");
            dest.add("}");
        }

        return dest;
    }
}
