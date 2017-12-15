/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evemarginfinder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class QueryTranslator {

    public static class TranslatedQuery {

        public TranslatedQuery(int type, int volume, double min, double max, double topFive) {
            this.type = type;
            this.volume = volume;
            this.min = min;
            this.max = max;
            this.topFive = topFive;
        }

        public int type = 0,
                volume = 0;
        public double max = 0,
                min = 0,
                topFive = 0;

        public void setType(int type) {
            this.type = type;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public void setTopFive(double topFive) {
            this.topFive = topFive;
        }

    }

    public static class getTypes extends ZeroArgFunction {

        public int[] types;

        public getTypes(int... types) {
            this.types = types;
        }

        @Override
        public LuaValue call() {

            LuaTable l = LuaTable.listOf(
                    IntStream.of(types).mapToObj(i -> LuaValue.valueOf(i))
                            .toArray(LuaValue[]::new));

            return l.toLuaValue();

        }

    }

    public static LuaValue root = null;
    public static getTypes typeFunc = new getTypes();

    public final static JsonParser parser = new JsonParser();
    
    public static TranslatedQuery[][] translate(int[] itemids, String response) {
        return translate(itemids, parser.parse(response));
    }
    
    public static TranslatedQuery[][] translate(int[] itemids, JsonElement response) {

        typeFunc.types = itemids;
        
        LuaValue ret = root.get("translate").call(
                CoerceJavaToLua.coerce(response.getAsJsonObject()));

        if (!ret.istable()) {
            throw new Error("Error in query translation script:"
                    + (ret == LuaValue.NIL ? "returned nil/no return" : "incorrect return type"));
        }

        List<TranslatedQuery> buy = new ArrayList<>();
        List<TranslatedQuery> sell = new ArrayList<>();

        Thread.yield();
        
        System.out.println(ret.tojstring());
        System.out.println(ret.get(0));
        System.out.println(ret.get(1));
        
        for (int i = 1; i <= ((LuaTable) ret.get(0)).length(); i++) {
            LuaValue val = ((LuaTable) ret.get(0)).get(i);
            buy.add(new TranslatedQuery(
                    val.get("type")
                            .checkint(), 
                    val.get("volume")
                            .checkint(),
                    val.get("min").checkdouble(), val.get("max").checkdouble(), val.get("topFive").checkdouble()));
        }

        for (int i = 1; i <= ((LuaTable) ret.get(1)).length(); i++) {
            LuaValue val = ((LuaTable) ret.get(1)).get(i);
            sell.add(new TranslatedQuery(val.get("type").checkint(), val.get("volume").checkint(),
                    val.get("min").checkdouble(), val.get("max").checkdouble(), val.get("topFive").checkdouble()));
        }

        return new TranslatedQuery[][]{
            buy.toArray(new TranslatedQuery[((LuaTable) ret).length()]),
            sell.toArray(new TranslatedQuery[((LuaTable) ret).length()])};
    }

    public static void init() {

        root = JsePlatform.standardGlobals();

        root.set(LuaValue.valueOf("getTypes"), CoerceJavaToLua.coerce(typeFunc));

        root.get("dofile").call(LuaValue.valueOf(ConfigManager.get("query-file")));

    }

}
