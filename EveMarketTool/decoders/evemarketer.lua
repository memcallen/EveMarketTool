
function convert(root)
	
	return {
		["type"]=root:get("forQuery"):getAsJsonObject():get("types"):getAsJsonArray():get(0):getAsInt(),
		["volume"]=root:get("volume"):getAsString(),
		["min"]=root:get("min"):getAsDouble(),
		["max"]=root:get("max"):getAsDouble(),
		["topFive"]=root:get("fivePercent"):getAsDouble()
	}
	
end

function translate(json)
	
	buy = {}
	sell = {}
	
	root = json:getAsJsonArray()
	
	for k=0, root:size() - 1, 1 do
		
		el = root:get(k):getAsJsonObject()
		
		table.insert(buy, convert(el:get("buy"):getAsJsonObject()))
		table.insert(sell, convert(el:get("sell"):getAsJsonObject()))
		
	end
	
	return {buy, sell}
	
end
