
function getColumnTypes()
	--Name, Buy, Sell, %Margin Volume
	return {"java.lang.String", "java.lang.Double", "java.lang.Double", "java.lang.Double", "java.lang.Integer"}
end

function getColumnNames()
	
	return {"Name", "Buy Price", "Sell Price", "% Margin", "Volume"}
end

function translateTable(buy, sell)
	
	return {
		getItemName(buy["type"]),
		buy.topFive,
		sell.topFive,
		(buy.topFive - sell.topFive) / buy.topFive,
		buy.volume
	}
	
end

