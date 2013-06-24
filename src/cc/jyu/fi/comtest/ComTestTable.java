// ComTest - Comments for testing
package cc.jyu.fi.comtest;

import cc.jyu.fi.comtest.utils.Strings;

/**
 * Class for storing ComTest tables
 * @author tojukarp
 */
public class ComTestTable {
    private int startingLine;
    private Strings columnNames;
    private Strings[] columnValues;

    /**
     * Creates a new table, taking the column names from a string list.
     * @param columns
     */
    public ComTestTable(Strings columns) {
        columnNames = columns;
        columnValues = new Strings[columns.size()];

        for ( int i = 0; i < columns.size(); i++ )
            columnValues[i] = new Strings();
    }

    public int numColumns() {
        return columnNames.size();
    }

    public int numRows() {
        return columnValues[0].size();
    }

    public int getStartingLine() {
        return startingLine;
    }

    public void setStartingLine(int line) {
        startingLine = line;
    }
    
    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    public Strings getColumnValues(int index) {
        return columnValues[index];
    }

    public String get(int column, int item) {
        return columnValues[column].get(item);
    }

    public String get(String column, int item) {
        return columnValues[columnNames.indexOf(column)].get(item);
    }

    public void set(int column, int item, String value) {
        columnValues[columnNames.indexOf(column)].set(item, value);
    }

    public void addRow(Strings items) {
        if ( items.size() != columnNames.size() )
            throw new IllegalArgumentException(String.format("Expected %d items, got %d", items.size(), columnNames.size()));

        for ( int i = 0; i < columnNames.size(); i++ )
            columnValues[i].add(items.get(i));
    }
}
