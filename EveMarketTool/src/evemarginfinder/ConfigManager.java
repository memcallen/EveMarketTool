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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class ConfigManager {

    public static String cfg_file = "markettool.cfg";
    public static String decoder_dir = "./decoders";
    
    private static HashMap<String, String> values = new HashMap<>();

    private static List<String> cfg_dec = new ArrayList<>();
    
    private static Entry<String, String> stoe(String in) {
        if (in.matches("\\w+\\=\\w+")) {
            String[] split = in.split("=");
            return new SimpleEntry(split[0], split[1]);
        } else {
            return null;
        }
    }

    private static void checkFiles() throws IOException {

        File in = new File(cfg_file);
        if (!in.exists()) {
            in.createNewFile();
        }

        File d_dir = new File(decoder_dir);
        if (!d_dir.exists()) {
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

        ConfigManager.set("query-file", "./decoders/fuzzworks.lua");

    }

    public static void load() {
        try {

            checkFiles();

            values.clear();

            setDefaults();

            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new FileReader(cfg_file));

                reader.lines().map(ConfigManager::stoe)
                        .forEach(e -> values.put(e.getKey(), e.getValue()));

            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void save() {
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(cfg_file))) {
                values.entrySet().forEach((e) -> {
                    out.print(e.getKey() + "=" + e.getValue());
                });
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean has(String key) {
        return values.containsKey(key);
    }

    public static String get(String key) {
        return values.containsKey(key) ? values.get(key) : "";
    }

    public static void set(String key, String value) {
        values.put(key, value);
    }

}
