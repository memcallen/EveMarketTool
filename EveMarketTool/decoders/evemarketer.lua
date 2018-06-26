
function getURL(sysid, system)
	url = "https://api.evemarketer.com/ec/marketstat/json?typeid="
	
	types = ""
	
	t = getTypes()
	size = 0
	
	for _ in pairs(t) do
		size = size + 1
	end
	
	for i, v in pairs(t) do
		
        if i == size then
            c = '&'
        else
           c = ','
        end
        
		types = types .. string.format('%d%s', t[i], c)
		
	end
	
	url = url .. types
	
	if system == 0 then
		--station (no option for this api)
	elseif system == 1 then
		--system (usesystem=##)
		url = url .. "usesystem=" .. sysid
	else
		--region (regionlimit=##)
		url = url .. "regionlimit=" .. sysid
	end
	
	return url
end

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
	
	for k, id in pairs(getTypes()) do
		
		el = root:get(k - 1):getAsJsonObject()
		
		-- debug print
		--print(k, id, el)
		
		table.insert(buy, convert(el:get("buy"):getAsJsonObject()))
		table.insert(sell, convert(el:get("sell"):getAsJsonObject()))
		
	end
	
	return {buy, sell}
	
end
