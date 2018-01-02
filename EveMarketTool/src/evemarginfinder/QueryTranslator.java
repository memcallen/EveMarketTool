/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evemarginfinder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.swing.JOptionPane;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
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

    public static List<Vector> getTableData(TranslatedQuery[][] queries) {

        List<Vector> out = new ArrayList<>();

        for (int i = 0; i < queries[0].length; i++) {
            LuaValue ret = root.get("translateTable").call(
                    CoerceJavaToLua.coerce(queries[0][i]),
                    CoerceJavaToLua.coerce(queries[1][i]));

            if (!ret.istable()) {
                throw new LuaError("Error: Table Translator returned two types other than tables");
            }

            LuaTable vals = ret.checktable();

            System.out.println(vals.length());
            System.out.println(ConfigManager.table_classes.length);
            
            if (vals.length() != ConfigManager.table_classes.length) {
                throw new LuaError("Error: Table Translator returned different length from getColumnTypes");
            }

            Vector v = new Vector();

            for (int index = 0; index < vals.length(); index++) {

                Object obj = CoerceLuaToJava.coerce(vals.get(index + 1), ConfigManager.table_classes[index]);

                v.add(obj);

            }

            out.add(v);

        }

        return out;

    }

    public static TranslatedQuery[][] translate(int[] itemids, JsonElement response) {

        typeFunc.types = itemids;

        LuaValue ret = root.get("translate").call(
                CoerceJavaToLua.coerce(response.getAsJsonObject()));

        if (!ret.istable()) {
            throw new LuaError("Error: Query Translation "
                    + (ret == LuaValue.NIL ? "returned nil/no return" : "has incorrect return type"));
        }

        if (!ret.get(0).istable() || !ret.get(1).istable()) {
            throw new LuaError("Error: Query Translator returned non-table for " + (ret.get(0).istable() ? "sell" : "buy"));
        }

        List<TranslatedQuery> buy = new ArrayList<>();
        List<TranslatedQuery> sell = new ArrayList<>();

        for (int i = 1; i <= ((LuaTable) ret.get(0)).length(); i++) {
            LuaValue val = ((LuaTable) ret.get(0)).get(i);
            buy.add(new TranslatedQuery(
                    val.get("type").checkint(),
                    val.get("volume").checkint(),
                    val.get("min").checkdouble(),
                    val.get("max").checkdouble(),
                    val.get("topFive").checkdouble()
            ));
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

    /**
     * Ran after Configs are loaded & whenever the query files are changed
     */
    public static void init() {

        root = JsePlatform.standardGlobals();

        root.set(LuaValue.valueOf("getTypes"), CoerceJavaToLua.coerce(typeFunc));

        root.set(LuaValue.valueOf("getItemName"), CoerceJavaToLua.coerce(new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return LuaValue.valueOf(DatabaseManager.queryItemName(arg.checkint()));
            }
        }));

        try {
            root.get("dofile").call(LuaValue.valueOf(ConfigManager.get("query-file")));
        } catch (LuaError e) {
            JOptionPane.showMessageDialog(null, ConfigManager.get("query-file") + ": " + e.getMessage(), "Error loading query parser", JOptionPane.ERROR_MESSAGE);
            throw e;
        }

        try {
            root.get("dofile").call(LuaValue.valueOf(ConfigManager.get("query-table")));
        } catch (LuaError e) {
            JOptionPane.showMessageDialog(null, ConfigManager.get("query-table") + ": " + e.getMessage(), "Error loading table generator", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
        
    }

}
