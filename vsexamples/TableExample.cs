using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VSExamples
{
    /**
     * A class for showing how to test table-like objects.
     * This idea can be used to fill different kind of data
     * structures for test.
     * @author vesal
     *
     */
    public class TableExample
    {
        private Dictionary<String, String> items = new Dictionary<String, String>();

        /**
         * Add new value with key to the table.
         * Key is caseinsensitive.
         * @param key   key to use for value
         * @param value value to add
         * 
         * @example
         * <pre name="test">
         * 
         * TableExample table = new TableExample();
         * table.add("1","red");
         * table.add("2","blue");
         * table.add("b","blue");
         * table.add("B","BLUE");
         * 
         * table.get("1") === "red";
         * table.get("b") === "BLUE";
         * 
         * </pre>
         */
        public void add( String key, String value )
        {
            items[key.ToLower()] = value;
        }


        /**
         * Finds the value for key.  Key is caseinsensitive.
         * @param key for what the value is looked for
         * @return the value for key, null if no value for key.
         * 
         * @example
         * <pre name="test">
         * 
         * TableExample table = new TableExample();
         * table.add("$key","$value");
         * 
         *   $key  |  $value
         *  -------------------
         *    1    |   red
         *    2    |   blue
         *    3    |   green
         *    5    |   yellow
         *    Y    |   YELLOW
         *    y    |   yellow
         *    red  |   red
         *    RED  |   RED
         *    
         *  table.get($key) === $result;
         *  
         *   $key  |  $result
         *  -------------------
         *    "1"    |   "red"
         *    "2"    |   "blue"
         *    "4"    |   null  
         *    "5"    |   "yellow"
         *    "y"    |   "yellow"
         *    "Y"    |   "yellow"
         *    "red"  |   "RED"
         *    "RED"  |   "RED"
         *    ""     |   null  
         *    null   |   null  
         * 
         * </pre>
         * 
         */
        public String get( String key )
        {
            if ( key == null ) return null;
            try
            {
                return items[key.ToLower()];
            }
            catch ( KeyNotFoundException )
            {
                return null;
            }
        }
    }
}
