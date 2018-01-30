# EveMarketTool

EveMarketTool is an application to bulk-price check items, which can be specified individually or by group, for the game Eve Online.

# Installation
EveMarketTool does not need to be installed. The only setup required is downloading the zip file from [releases](https://github.com/memcallen/EveMarketTool/releases) and unzipping the file.

# Hardware requirements
EveMarketTool will run on almost all machines. Recommended minimum ram is 4 Gigabytes. EveMarketTool should run on all CPUs that can run Java 1.8.

# Usage

The first tab indicates the items you would like to query
 - Select item groups with the left panel
 - Select idividual items with the right panel
 - Both panels support searching, via the text input above both panels (Press enter to loop through all that match)

The second tab is where the queries are displayed
 - The left panel is where the items to be queried are displayed
   - Currently, the panel is display only and you cannot modify the items through it
 - The filters are currently not working, but I will most likely add a section to the table-generator lua file for it
 - Press load to query the items
   - This takes a few seconds, don't press the button multiple times
   - For queries less than 50 items, it will take approximately 5 seconds, but increases rapidly after 100 items
 - The table can be sorted by clicking on the column headers

The third tab is for configuring the api website and formatting for the url
 - If you are only using this tool to get item prices, this tab should be ignored
 - The current config uses market.fuzzworks.co.uk to query items
 - See the Configuration section for more details on how to configure the url

# API URL Configuration

This section is only relevant if fuzzworks has stopped its service, or if you want to configure EveMarketTool to use another api. It is based off of the Python method for string formatting

URL Format
 - {0} is the root url (Specified by the URL field)
 - {1} is the typeid section (Generated via the TypeID section)
 - {2} is the region or station format (Specified via the Region & Station fields)

TypeID Format
 - The first field is the root for the TypeID string
 - The second string specifies the format for each typeid - This is repeated for each typeid

Region & Station Format
 - These fields are used with the Station/Region field in the Info tab
   - Currently, this functionality is hardcoded to use the station field

# LUA API

This section specifies the defined methods and default APIs available to the lua script files
EveMarketTool uses LuaJ to parse and run the lua scripts. Currently, the default APIs are based off of LuaJ's JsePlatform.standardGlobals, but this will most likely change due to security reasons.

Default APIs:
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

Custom Methods:

Table<Integer> getTypes()
 - Returns the current query's typeids as a table

String getItemName(Integer typeid)
 - Returns the Item Name from its typeid

Table translate(Userdata json)
 - The json parameter is the root element from the actual response, see [Gson @ JsonElement](https://github.com/google/gson/blob/master/gson/src/main/java/com/google/gson/JsonElement.java) for its methods
  - Returns a zero-indexed array with buy at 0 and sell at 1
    - Each item entry is a table of the format \[ID(int),Volume(int), Minimum Price(Double), Maximum Price(Double), TopFiveAvg(Double)\]
    - TopFiveAvg is the average of the 'best' five percent of orders (ie lowest for buy, highest for sell)
    - If your web api doesn't have a top five percent field, use the appropriate value or write a table generator

Table<String> getColumnTypes()
  - Specifies the java classes for each of the columns, java.lang.Object should work for odd object types (Do not leave any empty)

Table<String> getColumnNames()
  - Specifies the column header names

Table translateTable(buy object, sell object)
 - Translates a set of buy and sell objects into a row for the table
 - buy and sell are the same format that was returned from translate
 - Returns a table which contains the data for each of the table headers
