# EveMarketTool

EveMarketTool is an application to bulk-price check items, which can be specified individually or by group, for the game Eve Online.

# Installation
EveMarketTool does not need to be installed. The only setup required is downloading the zip file from [releases](https://github.com/memcallen/EveMarketTool/releases) and unzipping the file.

# Hardware requirements
EveMarketTool will run on almost all machines. Recommended minimum ram is 4 Gigabytes. EveMarketTool should run on all CPUs that can run Java 1.8.

# Usage

## Tab One

![First Tab Image](https://raw.githubusercontent.com/memcallen/EveMarketTool/master/docs/images/firsttabscreenshot.png)

 - Indicates the items you would like to query
 - Select item groups with the left panel
 - Select individual items with the right panel
 - Both panels support searching, via the text input above both panels (Press enter to loop through all that match)


## Tab Two

![Second Tab Image](https://raw.githubusercontent.com/memcallen/EveMarketTool/master/docs/images/secondtabscreenshot.png)

 - Where the queries are displayed
 - The left panel is where the items to be queried are displayed
   - Currently, the panel is display only and you cannot modify the items through it
 - The filters button opens a window which allows you to set filters
   - Press 'Add' to add a new filter
   - The filter name goes in the left text box
   - The filter value goes in the right text box
   - Check your decoders' documentation if they use the filters, not all decoders use the filters
 - Press load to query the items
   - This takes a few seconds, don't press the button multiple times
   - For queries less than 50 items, it will take approximately 5 seconds, but increases rapidly after 100 items
 - The table can be sorted by clicking on the column headers


### Tab Three
 - For configuring the API
 - Currently, you can only edit the config files manually, but this is on the to-do list
 - Config File Section:
   - Add Cfg
     - Adds a new config
   - Open Cfg
     - Opens a new cfg without needing to restart the application
   - Reload From File
     - Reloads the current config from file
   - Write To File
     - Writes the current config to file
   - Currently, remove config is disabled, but is also on the to-do list
 - Response Parsing Section:
   - API Decoder
     - Provides a formatted URL
     - Converts the response into an object, which is then sent to a table generator
   - Table Generator
     - Takes the output from an API Decoder and populates the main table
     - Also does table colouring
   - Reload Parser Configs
     - Reloads the parsers without needing to restart the application

## Generic Table Filters

 - Generic Table implements a generic table with coloring
 - Currently implemented filters:
   - Maximum_Price
     - In ISK
   - Minimum_Margin
     - In decimal percent (0.5, 0.15, etc)
   - Invalid_Color
     - By default, Generic Table uses red highlight as the invalid color
     - Allows you to change the highlight color
       - Can be any lowercase plaintext color (red, blue, green, etc)
         - Uses Java reflection to get colors from Java's Color class (if it's a constant, you can use it)

# LUA API

This section specifies the defined methods and default APIs available to the Lua script files.
EveMarketTool uses LuaJ to parse and run the lua scripts. Currently, the default APIs are based on LuaJ's JsePlatform.standardGlobals, but this will most likely change due to security reasons.

#### Default APIs:
 - JseBase
 - Package
 - Bit32
 - Table
 - String
 - Coroutine
 - JseMath 
 - JseIo
 - JseOs
 - JseJava
 - LuaJava

## Java Methods

### Table<Integer> getTypes()
 - Returns the current query's typeids as a table

### String getItemName(Integer typeid)
 - Returns the Item Name from its typeid

### String filter:get(String)
 - Gets a property from the filter window
 - Returns nil if none found

### void filter:set(String, String)
 - Sets a property for the filter window
 - Properties are global unless EveMarketTool restarted

## Required Lua Methods

### String getURL(int systemid, boolean system)
 - The systemid parameter is the provided system or region id, from the second tab
 - The system parameter is true if it's a system/region, false if it's a station
 - Returns a string, which is directly queried

### Table translate(Userdata json)
 - The json parameter is the root element from the actual response, see [Gson @ JsonElement](https://github.com/google/gson/blob/master/gson/src/main/java/com/google/gson/JsonElement.java) for its methods
  - Returns a zero-indexed array with buy at 0 and sell at 1
    - Each item entry is a table of the format \[ID(int),Volume(int), Minimum Price(Double), Maximum Price(Double), TopFiveAvg(Double)\]
    - TopFiveAvg is the average of the 'best' five percent of orders (ie lowest for buy, highest for sell)
    - If your web api doesn't have a top five percent field, use the appropriate value or write a table generator

### Table<String> getColumnTypes()
  - Specifies the java classes for each of the columns, java.lang.Object should work for odd object types (Do not leave any empty)

### Table<String> getColumnNames()
  - Specifies the column header names

### Table translateTable(buy object, sell object)
 - Translates a set of buy and sell objects into a row for the table
 - buy and sell are the same format that was returned from translate
 - Returns a table which contains the data for each of the table headers

## Optional Lua Methods

### Table translateTableCol(buy object, sell object, HashMap<String, String> properties)
 - Translates a set of buy and sell objects into a row for the table
 - Only enabled if the config's do-table-color == true
 - buy and sell are the same as translateTable
 - properties is a normal Java HashMap, wrapped into a userdata
 - Implemented Properties:
    - name: the name of the item (required for colors)
    - color: the cell color (required for colors)
       - Is the plaintext color (ie red, blue, green, etc)
 - Returns same as translateTable
