/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evemarginfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class ConfigManager {

    public static class XMLLuaConfig {

        public final String name, file;

        public XMLLuaConfig(String name, String file) {
            this.name = name;
            this.file = file;
        }
    }

    public static String cfg_file = "markettool.cfg";
    public static String decoder_dir = "decoders/";

    private static final HashMap<String, String> VALUES = new HashMap<>();

    public static Vector<XMLLuaConfig> query_parsers = new Vector();
    public static Vector<XMLLuaConfig> table_generators = new Vector();

    public static Class[] table_classes;

    public static Vector<String> table_headers = new Vector();

    private static Entry<String, String> stoe(String in) {
        int i_eq = in.indexOf("=");
        if(i_eq > 0){
            if(in.charAt(i_eq - 1) != '\\'){
                return new SimpleEntry(in.substring(0, i_eq), in.substring(i_eq + 1));
            }
        }
        return null;
    }

    private static void checkFiles() throws IOException {

        File in = new File(cfg_file);
        if (!in.exists()) {
            System.err.println("Warning: config file (" + cfg_file + ") doesn't exist, recreating");
            in.createNewFile();
        }

        File d_dir = new File(decoder_dir);
        if (!d_dir.exists()) {
            System.err.println("Warning: Decoder dir (" + decoder_dir + ") doesn't exist, recreating");
            d_dir.mkdir();
        }

    }

    public static void setDefaults() {

        ConfigManager.set("uformat", "{0}?{1}&{2}");
        ConfigManager.set("url", "https://market.fuzzwork.co.uk/aggregates/");
        ConfigManager.set("typeroot", "types={0}");
        ConfigManager.set("type", "{0},");
        ConfigManager.set("region", "region={0}");
        ConfigManager.set("station", "station={0}");

    }

    private static DocumentBuilder xml_builder;

    public static Vector[] loadParser(File xml_file) throws SAXException, IOException {

        if (xml_builder == null) {
            try {
                xml_builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException ex) { //this shouldn't happen
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

        Document doc = xml_builder.parse(xml_file);

        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();

        Vector<XMLLuaConfig> queries = new Vector(),
                tables = new Vector();

        for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {

            if (!(node instanceof Element)) {
                continue;
            }

            Element el = (Element) node;

            String tag = el.getNodeName();

            String name = "";
            String file = "";

            try {
                name = el.getElementsByTagName("name").item(0).getTextContent();
                file = el.getElementsByTagName("lua-file").item(0).getTextContent();
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

        return new Vector[]{queries, tables};

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
                    Vector[] parsers = loadParser(child);
                    query_parsers.addAll(parsers[0]);
                    table_generators.addAll(parsers[1]);
                } catch (SAXException ex) {
                    JOptionPane.showMessageDialog(null, "Error: Could not load Parser file " + child.getName() + " " + ex.getMessage(), "Error loading XML File", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        if (query_parsers.size() > 0) {
            set("query-file", decoder_dir + query_parsers.get(0).file);
        } else {
            JOptionPane.showMessageDialog(null, "Error: No query parsers found", "Error", JOptionPane.ERROR_MESSAGE);
            throw new Error("Error: no query parsers found");
        }

        if (table_generators.size() > 0) {
            set("query-table", decoder_dir + table_generators.get(0).file);
        } else {
            JOptionPane.showMessageDialog(null, "Error: No table generators found", "Error", JOptionPane.ERROR_MESSAGE);
            throw new Error("Error: no table generators found");
        }

    }

    public static void setActiveTable(String name){
        
        XMLLuaConfig xml = table_generators.stream().filter(x -> x.name.equals(name))
                        .findAny().orElse(null);
        
        Objects.requireNonNull(xml);
        
        set("query-table", decoder_dir + xml.file);
        
        QueryTranslator.init();
    }
    
    public static void setActiveParser(String name){
        
        XMLLuaConfig xml = query_parsers.stream().filter(x -> x.name.equals(name))
                        .findAny().orElse(null);
        
        Objects.requireNonNull(xml);
        
        set("query-parser", decoder_dir + xml.file);
        
        QueryTranslator.init();
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
                ex.printStackTrace();
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

    private static void loadFile() throws IOException {

        ConsoleFrame.log("Loading " + cfg_file);
        try (BufferedReader reader = new BufferedReader(new FileReader(cfg_file))) {

            System.out.println(reader);
            
            reader.lines().peek(System.out::println).map(ConfigManager::stoe)
                    .forEach(e -> VALUES.put(e.getKey(), e.getValue()));

        }catch(Exception e){
            ConsoleFrame.log_error("Invalid " + cfg_file + ", skipping config reading (" + e.toString() + ")");
            e.printStackTrace();
            if(e instanceof IOException){
                throw e;
            }
        }
    }

    public static void reset() {
        try {
            
            checkFiles();
            
            clear();
            
            setDefaults();
            
            loadParsers();
            
        } catch (IOException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void load() {
        try {

            reset();
            
            loadFile();

        } catch (IOException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void save() {
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(cfg_file))) {
                VALUES.entrySet().forEach((e) -> {
                    out.println(e.getKey() + "=" + e.getValue());
                });
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void clear() {
        VALUES.clear();
    }

    public static boolean has(String key) {
        return VALUES.containsKey(key);
    }

    public static String get(String key) {
        return VALUES.containsKey(key) ? VALUES.get(key) : "";
    }

    public static void set(String key, String value) {
        VALUES.put(key, value);
    }

}
