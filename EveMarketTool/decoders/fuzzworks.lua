
function convert(id, root)
	
	return {
		["type"]=id,
		["volume"]=root:get("volume"):getAsString(),
		["min"]=root:get("min"):getAsDouble(),
		["max"]=root:get("max"):getAsDouble(),
		["topFive"]=root:get("percentile"):getAsDouble()
	}
	
end

function translate(json)
	
	buy = {}
	sell = {}
	
	for k, id in pairs(getTypes())
	do
		
		el = json:get(tostring(id)):getAsJsonObject()
		
		table.insert(buy, convert(id, el:get("buy"):getAsJsonObject()))
		table.insert(sell, convert(id, el:get("sell"):getAsJsonObject()))
		
	end
	
	return {buy, sell}
	
end
