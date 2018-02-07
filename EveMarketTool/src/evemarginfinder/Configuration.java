/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evemarginfinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class Configuration {

    //<editor-fold desc="Object Stuff">
    @SuppressWarnings("FieldMayBeFinal")
    private HashMap<String, String> cfg = new HashMap<>();

    public String name;
    public File file;

    public Configuration(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public void load() throws FileNotFoundException {

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            name = scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                cfg.put(line.substring(0, line.indexOf("=")), line.substring(line.indexOf("=") + 1));
            }
        }

    }

    public void save() throws FileNotFoundException {

        try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
            out.println(name);

            cfg.entrySet().forEach((e) -> {
                out.println(e.getKey() + "=" + e.getValue());
            });
        }

    }

    public void defaults() {

        _set("uformat", "{0}?{1}&{2}");
        _set("url", "https://market.fuzzwork.co.uk/aggregates/");
        _set("typeroot", "types={0}");
        _set("type", "{0},");
        _set("region", "region={0}");
        _set("station", "station={0}");

    }

    public void _clear() {
        cfg.clear();
    }

    public boolean _has(String key) {
        return cfg.containsKey(key);
    }

    public String _get(String key) {
        return cfg.get(key);
    }

    public void _set(String key, String val) {
        cfg.put(key, val);
    }

    //</editor-fold>
    //<editor-fold desc="Static Stuff">
    static List<Configuration> configs = new ArrayList();
    static int current = 0;
    static Path root = Paths.get("./");

    public static void initialize() {

        File[] children = root.toFile().listFiles();

        ConsoleFrame.log("Looking for config files");
        
        if (children != null) {
            for (File child : children) {
                if (child.getName().endsWith(".emt.cfg")) {
                    ConsoleFrame.log("Found " + child.getName());

                    Configuration c = new Configuration(
                            child.getName().replace("\\.emt\\.cfg", ""), child);

                    try {
                        c.load();
                    } catch (FileNotFoundException ex) {
                        ConsoleFrame.log_error("Found " + child.getName() + " but could not parse it (" + ex.toString() + ")");
                    }

                    configs.add(c);

                }
            }
        }

    }

    public static void reload(String name) {
        configs.stream().filter((c) -> (c.name.equals(name))).forEachOrdered((c) -> {
            try {
                c.load();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public static void reloadCurrent() {
        try {
            configs.get(current).load();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void close() {

        configs.forEach((c) -> {
            try {
                c.save();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    public static void addNew(String name) {
        Configuration c = new Configuration(name, new File(name + ".emt.cfg"));
        c.defaults();
        configs.add(c);
    }

    public static void loadFromFile(File file) throws FileNotFoundException {
        Configuration c = new Configuration(file.getName().replace(".emt.cfg", ""), file);
        c.load();
        configs.add(c);
    }

    public static void remove(String name) {

    }

    public static void setActive(String name) {
        for (int i = 0; i < configs.size(); i++) {
            if (configs.get(i).name.equals(name)) {
                current = i;
                return;
            }
        }
    }

    public static String getCName() {
        return configs.get(current).name;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Direct Manipulation">
    public static void clear() {
        configs.get(current)._clear();
    }

    public static boolean has(String key) {
        return configs.get(current)._has(key);
    }

    public static String get(String key) {
        return configs.get(current)._get(key);
    }

    public static void set(String key, String value) {
        configs.get(current)._set(key, value);
    }
    //</editor-fold>

}
