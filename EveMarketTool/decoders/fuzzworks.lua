
function getURL(sysid, system)
	
	prefixes = {"station", "system", "region"}
		
	systext = prefixes[system + 1] .. "=" .. sysid
	
	url = "https://market.fuzzwork.co.uk/aggregates/?" .. systext .. "&types="
	
	types = ""
	
	t = getTypes()
	size = 0
	
	for _ in pairs(t) do
		size = size + 1
	end
	
	for i, v in pairs(t) do
		
        if i < size then
           c = ','
        end
        
		types = types .. string.format('%d%s', t[i], c)
		
	end
	
	return url .. types
end

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
	
	json = json:getAsJsonObject()
	
	for k, id in pairs(getTypes())
	do
		
		el = json:get(tostring(id)):getAsJsonObject()
		
		table.insert(buy, convert(id, el:get("buy"):getAsJsonObject()))
		table.insert(sell, convert(id, el:get("sell"):getAsJsonObject()))
		
	end
	
	return {buy, sell}
	
end
