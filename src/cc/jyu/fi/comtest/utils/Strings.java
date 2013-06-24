package cc.jyu.fi.comtest.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Class for handling String lists
 * @author vesal
 * @version 22.9.2007
 * @example
 * <pre name="testStrings">
 * Strings list = new Strings();
 * list.size() === 0;
 * list.add("$add"); list.size() === $size; list.get($index) === "$value";
 * 
 *    $add   | $size  |  $index | $value 
 *    -----------------------------------
 *     one   |   1    |   0     |  one   
 *     two   |   2    |   0     |  one   
 *     ---   |  ---   |   1     |  two   
 * </pre>
 * 
 */
public class Strings extends ArrayList<String> implements Cloneable {

    private static final long serialVersionUID = 1L;

    public Strings() {
        super();
    }

    public Strings(Collection<? extends String> c) {
        super(c);
    }

    public Strings(String... strings) {
        super(strings.length);
        add(strings);
    }

    /**
     * Add an other list to the end of this list 
     * @param list what list to add
     * @return this list reference
     * @example
     * <pre name="testAdd">
     * Strings l1 = new Strings(), l2 = new Strings();
     * l1.add("one"); l1.add("two");
     * l2.add("three"); l2.add("four");
     * l1.add(l2);
     * l1.get(2) === "three"; l1.get(3) === "four";
     * </pre>
     */
    public Strings add(Strings list) {
        addAll(list);
        return this;
    }

    public Strings add(String... strings) {
        addAll(Arrays.asList(strings));
        return this;
    }

    /**
     * Add an other list to the end of this list,
     * prepending and appending a string to each item.
     * @param list what list to add
     * @param start string to prepend
     * @param end string to append
     * @return this list reference
     */
    public Strings add(String start, Strings list, String end) {
        for (String s : list) {
            add(start + s + end);
        }
        return this;
    }

    /**
     * Add a string to list if not null.
     * @param s String to add
     * @return This list reference
     */
    public Strings addNotNull(String s) {
        if (s != null) {
            add(s);
        }

        return this;
    }

    /**
     * Add a string to list if not already there.
     * @param s String to add
     * @return This list reference
     */
    public Strings addIfUnique(String s) {
        if (!contains(s)) {
            add(s);
        }

        return this;
    }

    /**
     * Make a new instance from list with same strings inside
     * @return new list
     * @example
     * <pre name="testClone">
     * Strings l1 = new Strings(), l2;
     * l1.add("one"); l1.add("two");
     * l2 = l1.clone();
     * l1.set(1,"three");
     * l2.get(0) === "one"; l2.get(1) === "two"; l1.get(1) === "three";
     * </pre>
     */
    @Override
    public Strings clone() {
        Strings list = new Strings();
        list.add(this);
        return list;
    }

    /**
     * Replace from every line target by replacement.
     * Current list is changed, not cloned! 
     * @param target The sequence of char values to be replaced
     * @param replacement The replacement sequence of char values
     * @return reference to the same list
     * @example
     * <pre name="testReplace">
     * Strings l1 = new Strings(); 
     * l1.add("$value"); $
     * 
     *   | $value |
     *   ----------
     *   | one    |
     *   | two    |
     *   | three  |
     *   | four   |
     * 
     * l1.replace("o","p"); 
     * l1.get($i) === "$value";
     * 
     *    $i   |  $value
     *   ----------------
     *     0   |  pne
     *     1   |  twp
     *     2   |  three
     *     3   |  fpur 
     * </pre>
     */
    public Strings replace(String target, String replacement) {
        for (int i = 0; i < size(); i++) {
            set(i, get(i).replace(target, replacement));
        }
        return this;
    }

    /**
     * Remove a part from list
     * @param fromIndex index to start
     * @param toIndex index to stop 
     */
    @Override
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

    /**
     * Remove all empty strings from a list.
     * @example
     * <pre name="test">
     * Strings list = new Strings();
     * list.add("");
     * list.add("1");
     * list.add("test");
     * list.add("");
     * list.add("spam");
     * list.add("");
     * list.add("");
     * list.removeEmpty();
     * list.size() === 3
     * </pre>
     */
    public void removeEmpty() {
        for (int i = size() - 1; i >= 0; i--) {
            if (get(i).isEmpty()) {
                remove(i);
            }
        }
    }

    /**
     * Returns the list as separated by separator;
     * @param separator what to use as separator
     * @param endSeparator what to use as separator after last line
     *        if null or "" then no separator after last line
     * @return list separated by separator
     * 
     * @example
     * <pre name="test">
     * Strings list = new Strings();
     * list.toString("|","!") === "!";
     * list.add("one");
     * list.toString("|","!") === "one!";
     * list.add("two");
     * list.toString("|","!")  === "one|two!";
     * list.toString("|","")   === "one|two";
     * list.toString("|",null) === "one|two";
     * </pre>
     */
    public String toString(String separator, String endSeparator) {
        StringBuilder result = new StringBuilder();
        String sep = "";
        for (String s : this) {
            result.append(sep).append(s);
            sep = separator;
        }
        if (endSeparator != null) {
            result.append(endSeparator);
        }
        return result.toString();
    }

    /**
     * Returns the list as separated by separator;
     * @param separator what to use as separator
     * @return list separated by separator
     * 
     * @example
     * <pre name="test">
     * Strings list = new Strings();
     * list.toString("|") === "";
     * list.add("one");
     * list.toString("|") === "one";
     * list.add("two");
     * list.toString("|") === "one|two";
     * </pre>
     */
    public String toString(String separator) {
        return toString(separator, null);
    }

    /**
     * Trims all strings in the list.
     */
    public void trim() {
        for (int i = 0; i < size(); i++) {
            set(i, get(i).trim());
        }
    }

    /**
     * Appends a string after every list item.
     * @param suffix Suffix to append
     * @example
     * <pre name="test">
     * Strings list = new Strings();
     * list.add("a");
     * list.add("test");
     * list.add("");
     * list.append("spam");
     * list.get(0) === "aspam"
     * list.get(1) === "testspam"
     * list.get(2) === "spam"
     * </pre>
     */
    public void append(String suffix) {
        for (int i = 0; i < size(); i++) {
            set(i, get(i) + suffix);
        }
    }

    /**
     * Prepends a string before every list item.
     * @param suffix Suffix to append
     * @example
     * <pre name="test">
     * Strings list = new Strings();
     * list.add("a");
     * list.add("test");
     * list.add("");
     * list.prepend("spam");
     * list.get(0) === "spama"
     * list.get(1) === "spamtest"
     * list.get(2) === "spam"
     * </pre>
     */
    public void prepend(String prefix) {
        for (int i = 0; i < size(); i++) {
            set(i, prefix + get(i));
        }
    }
}
