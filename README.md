# EveMarketTool

This is a tool to find items to sell, by system.

How to run and download:

1. Go to 'Clone/Download'
2. Download the zip
3. Unzip the files
4. Run 'EveMarketTool(.jar)' in the folder 'Eve Market Tool Compiled' OR compile from source (It's a netbeans project)
5. If you compiled from source, be sure to include 'groups.txt', 'systems.txt', and 'typeid.txt' in the same folder as the jar as well as the library folder

Usage:

1. Once the program has loaded, select item groups in the left side (same names as in the market) and/or select individual items on the right. (Hovering over the items or item groups will display the ID)
2. After you've selected the items you want, go to the 'Info' tab
3. All the selected items should appear on the left side, if they don't press the Refresh button. (If it still doesn't send me an eve-mail @ Memcallen Kahoudi)
4. Enter the system name OR id in the 'system' box (defaults to Jita)
5. Press load. Requests with more than 50 items will take a while, don't keep pressing the button

IMPORTANT: The 'Profit' column does not indicate actual profits, it's better used as an 'item popularity' column as the volume is the number of items on the market currently, not the amount sold.

The Filter System:
- You can sort the columns in the table by pressing the labels at the top. You can also resize the columns incase the names/numbers are blocked
- The Min Margin and Maximum Cost will only work when 'Use Filter' is selected

- Min Margin: Filters the minimum margin for an item. Example: item x costs 100 isk in buy orders, and 110 isk in sell orders, the margin is 0.1 (or 10%)

- Maximum Cost: Filters by the maximum cost for an item, goes by the sell orders in the market

- Remove Invalids: When checked, this item will remove items with invalid entries. An invalid entry is one that usually doesn't have enough items being sold to have proper buy and sell numbers.
