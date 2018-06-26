/*
 * The MIT License
 *
 * Copyright 2018 memcallen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package evemarginfinder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class QueryTranslator {

    public static class getTypes extends ZeroArgFunction {

        public int[] types;

        public getTypes(int... types) {
            this.types = types == null ? new int[0] : types;
        }

        @Override
        public LuaValue call() {

            LuaTable l = LuaTable.listOf(
                    IntStream.of(types).mapToObj(i -> LuaValue.valueOf(i))
                            .toArray(LuaValue[]::new));

            return l.toLuaValue();

        }

    }

    private static class FilterWrapper{
        
        private FilterFrame filter;
        
        public FilterWrapper(FilterFrame filters){
            filter = filters;
        }
        
        public String get(String key){
            return filter.get(key);
        }
        
        public boolean set(String key, String value){
            return filter.set(key, value);
        }
        
    }
    
    public static class XMLLuaConfig {

        public final String name, file;

        public XMLLuaConfig(String name, String file) {
            this.name = name;
            this.file = file;
        }
    }

    public static LuaValue root = null;
    public static getTypes typeFunc = new getTypes();
    public static String decoder_dir = "decoders/";
    private static FilterFrame filters;
   
    public final static JsonParser PARSER = new JsonParser();

    public final static HashMap<String, Color> COLORS = new HashMap<>();
    private final static HashMap<String, Color> ROW_COLORS = new HashMap<>();

    public static final int
            AREA_STATION = 0,
            AREA_SYSTEM = 1,
            AREA_REGION = 2;
    
    //<editor-fold defaultstate="collapsed" desc="File Loaders">
    public static List<XMLLuaConfig> query_parsers = new ArrayList<>();
    public static List<XMLLuaConfig> table_generators = new ArrayList<>();

    public static XMLLuaConfig active_parser;
    public static XMLLuaConfig active_table;
    
    public static Class[] table_classes;

    public static List<String> table_headers = new ArrayList<>();

    private static DocumentBuilder xml_builder;

    public static List<XMLLuaConfig>[] loadParser(File xml_file) throws SAXException, IOException {

        if (xml_builder == null) {
            try {
                xml_builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException ex) { //this shouldn't happen
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

        Document doc = xml_builder.parse(xml_file);

        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();

        List<XMLLuaConfig> queries = new ArrayList(),
                tables = new ArrayList();

        for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {

            if (!(node instanceof Element)) {
                continue;
            }

            Element el = (Element) node;

            String tag = el.getNodeName();

            String name = "";
            String file = "";
            boolean do_color = false;
            try {
                name = el.getElementsByTagName("name").item(0).getTextContent();
                file = decoder_dir + el.getElementsByTagName("lua-file").item(0).getTextContent();
                NodeList list = el.getElementsByTagName("do-table-color");
                if (list.getLength() > 0) {
                    do_color = Boolean.valueOf(list.item(0).getTextContent());
                }
            } catch (NullPointerException e) {
                throw new SAXException(
                        String.format("Error: file %s has malformed tag %s - missing name or lua-file", xml_file.getName(), tag));
            }

            switch (tag) {
                case "query-parser":
                    queries.add(new XMLLuaConfig(name, file));
                    break;
                case "table-generator":
                    tables.add(new XMLLuaConfig(name, file));
                    break;
                default:
                    System.err.println("Found an incorrect tag in parser file " + file + " (" + tag + ")");
                    break;
            }

        }

        List<XMLLuaConfig>[] out = new ArrayList[2];
        
        out[0] = queries;
        out[1] = tables;
        
        return out;

    }

    public static void loadParsers() {

        File decoders = new File(decoder_dir);

        if (!decoders.exists()) {
            System.err.println("Warning: Decoder dir (" + decoder_dir + ") doesn't exist, recreating");
            decoders.mkdir();
            return;
        }

        if (!decoders.isDirectory()) {
            System.err.println("Error: Decoder dir isn't a dir (" + decoder_dir + ")");
            throw new Error("Decoder directory isn't a directory, user will need to delete or move whatever file is there");
        }

        query_parsers.clear();
        table_generators.clear();

        for (File child : decoders.listFiles()) {

            if (child.getName().endsWith("xml")) {
                try {
                    List[] parsers = loadParser(child);
                    query_parsers.addAll(parsers[0]);
                    table_generators.addAll(parsers[1]);
                } catch (SAXException ex) {
                    JOptionPane.showMessageDialog(null, "Error: Could not load decoder config file " + child.getName() + " " + ex.getMessage(), "Error loading XML File", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        if (query_parsers.size() > 0) {
            active_parser = query_parsers.get(0);
        } else {
            JOptionPane.showMessageDialog(null, "Error: No query parsers found", "Error", JOptionPane.ERROR_MESSAGE);
            throw new Error("Error: no query parsers found");
        }

        if (table_generators.size() > 0) {
            active_table = table_generators.get(0);
        } else {
            JOptionPane.showMessageDialog(null, "Error: No table generators found", "Error", JOptionPane.ERROR_MESSAGE);
            throw new Error("Error: no table generators found");
        }

    }

    public static void setActiveTable(String name) {

        XMLLuaConfig xml = table_generators.stream().filter(x -> x.name.equals(name))
                .findAny().orElse(null);

        Objects.requireNonNull(xml);

        active_table = xml;
        
        QueryTranslator.reset_lua();
    }

    public static void setActiveParser(String name) {

        XMLLuaConfig xml = query_parsers.stream().filter(x -> x.name.equals(name))
                .findAny().orElse(null);

        Objects.requireNonNull(xml);

        active_parser = xml;
        
        QueryTranslator.reset_lua();
    }

    public static String getPName(String file){
        for(XMLLuaConfig xml : query_parsers){
            if(xml.file.equals(file)){
                return xml.name;
            }
        }
        return null;
    }
    
    public static String getTName(String file){
        for(XMLLuaConfig xml : table_generators){
            if(xml.file.equals(file)){
                return xml.name;
            }
        }
        return null;
    }
    
    public static void loadHeaders() {

        LuaValue ret_class = QueryTranslator.root.get("getColumnTypes").call();

        if (!ret_class.istable()) {
            throw new LuaError("Error: getColumnTypes returned non table");
        }

        LuaTable classes = ret_class.checktable();

        table_classes = new Class[classes.length()];

        for (int i = 0; i < classes.length(); i++) {
            try {
                table_classes[i] = Class.forName(classes.get(i + 1).tojstring());
            } catch (ClassNotFoundException ex) {
                throw new LuaError(String.format("Error: getColumnTypes returned invalid java class %s for type table", classes.get(i).tojstring()));
            }
        }

        LuaValue ret_headers = QueryTranslator.root.get("getColumnNames").call();

        if (!ret_headers.istable()) {
            throw new LuaError("Error: getColumnNames returned non table");
        }

        LuaTable headers = ret_headers.checktable();

        table_headers.clear();

        for (int i = 0; i < headers.length(); i++) {
            table_headers.add(i, headers.get(i + 1).tojstring());
        }

    }
    
    public static void initialize(){
        
        loadParsers();
        
        if(Configuration.has("query-parser")){
            setActiveParser(Configuration.get("query-parser"));
        }
        
        if(Configuration.has("query-table")){
            setActiveTable(Configuration.get("query-table"));
        }
        
        reset_lua();
        
        loadHeaders();
        
    }
    
    public static void save(){
        Configuration.set("query-parser", active_parser.name);
        Configuration.set("query-table", active_table.name);
    }
    
    public static void terminate(){
        save();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Translation Bits">
    
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

    public static Color getCellColors(int row, int column, List<List> data) {

        if (Boolean.parseBoolean(Configuration.get("do-table-color"))) {

            return ROW_COLORS.containsKey(data.get(row).get(0)) ? ROW_COLORS.get(data.get(row).get(0)) : null;

        }

        return null;
    }

    public static void onNewQuery(){
        ROW_COLORS.clear();
    }
    
    public static List<List> getTableData(LuaValue queries_ret) {

        List<List> out = new ArrayList<>();

        LuaValue buy = queries_ret.get(1);
        LuaValue sell = queries_ret.get(2);

        for (int i = 1; i <= buy.length(); i++) {

            LuaValue ret;
            HashMap<String, String> props = new HashMap<>();

            if (Boolean.parseBoolean(Configuration.get("do-table-color"))) {

                ret = root.get("translateTableCol")
                        .call(buy.get(i), sell.get(i), CoerceJavaToLua.coerce(props));

            } else {
                ret = root.get("translateTable").call(buy.get(i), sell.get(i));
            }

            if (!ret.istable()) {
                throw new LuaError("Error: Table Translator returned a non-table");
            }

            LuaTable vals = ret.checktable();

            if (vals.length() != QueryTranslator.table_classes.length) {
                throw new LuaError("Error: Table Translator returned different length from getColumnTypes");
            }

            if (Configuration.get("do-table-color").equals("true")) {
                ROW_COLORS.put(props.get("name"), COLORS.get(props.get("color")));
            }

            List v = new ArrayList();

            for (int index = 1; index <= vals.length(); index++) {

                Object obj = CoerceLuaToJava.coerce(vals.get(index), QueryTranslator.table_classes[index - 1]);

                v.add(obj);

            }

            out.add(v);

        }

        return out;

    }

    public static LuaValue translate(int[] itemids, JsonElement response) {

        typeFunc.types = itemids;

        LuaValue ret = root.get("translate").call(CoerceJavaToLua.coerce(response));

        return ret;
    }

    public static String getURL(int[] itemids, int systemid, int area_type) {
        
        typeFunc.types = itemids;
        
        LuaValue ret = root.get("getURL").call(CoerceJavaToLua.coerce(systemid), CoerceJavaToLua.coerce(area_type));
        
        if(!ret.isstring()) {
            throw new LuaError("getURL returned non-string");
        }
        
        return ret.checkjstring();
    }
    
    public static void setFilter(FilterFrame filter){
        filters = filter; 
    }
    
    /**
     * Ran after Configs are loaded & whenever the query files are changed
     */
    public static void reset_lua() {

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

        root.set(LuaValue.valueOf("getItemId"), CoerceJavaToLua.coerce(new OneArgFunction(){
            @Override
            public LuaValue call(LuaValue arg) {
                if (!arg.isstring()) {
                    throw new LuaError("Error: invalid parameter type for getItemId, should be string");
                }
                return LuaValue.valueOf(DatabaseManager.queryItemId(arg.checkjstring()));
            }
        }));
        
        root.set(LuaValue.valueOf("log"), CoerceJavaToLua.coerce(new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                ConsoleFrame.log(arg.tojstring());
                return LuaValue.NIL;
            }
        }));

        root.set(LuaValue.valueOf("filter"), CoerceJavaToLua.coerce(new FilterWrapper(filters)));
        
        try {
            root.get("dofile").call(LuaValue.valueOf(active_parser.file));
        } catch (LuaError e) {
            JOptionPane.showMessageDialog(null, active_parser.name + ": " + e.getMessage(), "Error loading query parser", JOptionPane.ERROR_MESSAGE);
            throw e;
        }

        try {
            root.get("dofile").call(LuaValue.valueOf(active_table.file));
        } catch (LuaError e) {
            JOptionPane.showMessageDialog(null, active_table.name + ": " + e.getMessage(), "Error loading table generator", JOptionPane.ERROR_MESSAGE);
            throw e;
        }

    }

    //</editor-fold>
}
