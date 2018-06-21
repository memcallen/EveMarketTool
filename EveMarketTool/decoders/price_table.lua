function isnan(num)
	return tostring(num) == tostring(0/0)
end

function isinfinite(num)
	return tostring(num) == tostring(1/0)
end

function default(one, def)
	if one == nil then
		return def
	else
		return one
	end
end

function getColumnTypes()
	--Name, Buy, Sell, Delta $, Volume
	return {"java.lang.String", "java.lang.Double", "java.lang.Double", "java.lang.Double", "java.lang.Integer"}
end

function getColumnNames()
	
	return {"Name", "Buy Price", "Sell Price", "Delta $", "Volume"}
end

function translateTableCol(buy, sell, props)
	
	maxprice = tonumber(filter:get("Maximum_Price"))
	minprice = tonumber(filter:get("Minimum_Price")) or 0
	minmarg = tonumber(filter:get("Minimum_Margin"))
	outside_color = filter:get("Invalid_Color") or "red"
	
	r = translateTable(buy, sell)
	props:put("name", r[1])
	if minmarg ~= nil and r[4] < minmarg then
		props:put("color", outside_color)
	elseif maxprice ~= nil and r[2] > maxprice then
		props:put("color", outside_color)
	elseif isnan(r[4]) or isinfinite(r[4]) then
		props:put("color", "red")
	else
		props:put("color", "none")
	end
	
	return r
	
end

function translateTable(buy, sell)
	
	return {
		getItemName(buy["type"]),
		buy.topFive,
		sell.topFive,
		sell.topFive - buy.topFive,
		buy.volume
	}
	
end

