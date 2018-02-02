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

function translateTableCol(buy, sell, opts)
	
	r = translateTable(buy, sell)
	opts[2]:Set(r[1])
	if isnan(r[4]) or isinfinite(r[4]) then
		opts[1]:Set("red")
	else
		opts[1]:Set("none")
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

