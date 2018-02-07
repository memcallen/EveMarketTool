function isnan(num)
	return tostring(num) == tostring(0/0)
end

function isinfinite(num)
	return tostring(num) == tostring(1/0)
end

function getColumnTypes()
	--Name, Buy, Sell, %Margin Volume
	return {"java.lang.String", "java.lang.Double", "java.lang.Double", "java.lang.Double", "java.lang.Integer"}
end

function getColumnNames()
	
	return {"Name", "Buy Price", "Sell Price", "% Margin", "Volume"}
end

function translateTableCol(buy, sell, props)
	
	r = translateTable(buy, sell)
	props:put("name", r[1])
	if isnan(r[4]) or isinfinite(r[4]) then
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
		(sell.topFive - buy.topFive) / buy.topFive,
		buy.volume
	}
	
end

