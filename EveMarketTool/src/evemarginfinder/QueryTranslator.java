/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evemarginfinder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.swing.JOptionPane;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
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

    public final static JsonParser PARSER = new JsonParser();

    public final static HashMap<String, Color> COLORS = new HashMap<>();

    private final static HashMap<String, Color> ROW_COLORS = new HashMap<>();

    static {

        COLORS.put("none", null);
        COLORS.put(null, null);

        Field[] colors = Color.class.getFields();

        for (Field color : colors) {
            if (color.getType() == Color.class) {
                try {
                    COLORS.put(color.getName(), (Color) color.get(null));
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(QueryTranslator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public static Color getCellColors(int row, int column, List<Vector> data) {

        if (ConfigManager.get("do-table-color").equals("true")) {

            return ROW_COLORS.containsKey(data.get(row).get(0)) ? ROW_COLORS.get(data.get(row).get(0)) : null;

        }

        return null;
    }

    public static void onNewQuery(){
        ROW_COLORS.clear();
    }
    
    public static List<Vector> getTableData(LuaValue queries_ret) {

        List<Vector> out = new ArrayList<>();

        LuaValue buy = queries_ret.get(1);
        LuaValue sell = queries_ret.get(2);

        for (int i = 1; i <= buy.length(); i++) {

            LuaValue ret;
            HashMap<String, String> props = new HashMap<>();

            if (ConfigManager.get("do-table-color").equals("true")) {

                ret = root.get("translateTableCol")
                        .call(buy.get(i), sell.get(i), CoerceJavaToLua.coerce(props));

            } else {
                ret = root.get("translateTable").call(buy.get(i), sell.get(i));
            }

            if (!ret.istable()) {
                throw new LuaError("Error: Table Translator returned a non-table");
            }

            LuaTable vals = ret.checktable();

            if (vals.length() != ConfigManager.table_classes.length) {
                throw new LuaError("Error: Table Translator returned different length from getColumnTypes");
            }

            if (ConfigManager.get("do-table-color").equals("true")) {
                ROW_COLORS.put(props.get("name"), COLORS.get(props.get("color")));
            }

            Vector v = new Vector();

            for (int index = 1; index <= vals.length(); index++) {

                Object obj = CoerceLuaToJava.coerce(vals.get(index), ConfigManager.table_classes[index - 1]);

                v.add(obj);

            }

            out.add(v);

        }

        return out;

    }

    public static LuaValue translate(int[] itemids, JsonElement response) {

        typeFunc.types = itemids;

        LuaValue ret = root.get("translate").call(
                CoerceJavaToLua.coerce(response.getAsJsonObject()));

        return ret;
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
                if (!arg.isint()) {
                    throw new LuaError("Error: invalid parameter type for getItemName, should be int");
                }
                return LuaValue.valueOf(DatabaseManager.queryItemName(arg.checkint()));
            }
        }));

        root.set(LuaValue.valueOf("log"), CoerceJavaToLua.coerce(new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                ConsoleFrame.log(arg.tojstring());
                return LuaValue.NIL;
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
