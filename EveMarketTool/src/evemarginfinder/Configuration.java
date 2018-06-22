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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.JOptionPane;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class Configuration {

    //<editor-fold desc="Object Stuff" defaultstate="collapsed">
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
            if (scanner.hasNextLine()) {
                name = scanner.nextLine();
            }

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
    //<editor-fold desc="Static Stuff" defaultstate="collapsed">
    public static List<Configuration> configs = new ArrayList();
    private static int current = 0;
    private static Path root = Paths.get("./");
    private static Configuration global;

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

        File _global = new File("./global.cfg");

        try {
            if (!_global.exists()) {
                _global.createNewFile();
            }

            global = new Configuration("Global", _global);

            try {
                global.load();
            } catch (FileNotFoundException ex) {
                ConsoleFrame.log_error("Error: Could not load global.cfg (" + ex.toString() + ")");
            }

            if (global._has("last")) {
                System.out.println("Found initial setting - " + global._get("last"));
                setActive(global._get("last"));
            }

        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (configs.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No Configurations found, try redownloading the program",
                     "No Configurations Found", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

    }

    public static void saveCurrent() {
        try {
            configs.get(current).save();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
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

        try {
            global._set("last", getCName());
            global.save();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    public static void removeCurrent(boolean save) throws IOException {
        Configuration c = configs.get(current);
        
        Path new_file = c.file.toPath().resolveSibling(c.file.getName() + ".disabled");
        Files.move(c.file.toPath(), new_file, StandardCopyOption.COPY_ATTRIBUTES);
        
        if (save) {
            try {
                c.file = new_file.toFile();
                c.save();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        configs.remove(current);
        
        if(current >= configs.size()) {
            current = configs.size() - 1;
        }
        
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

    public static int GetCurrent() {
        return current;
    }

    public static void forEach(Consumer<Configuration> cnsmr) {
        configs.forEach(cnsmr);
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Direct Manipulation">
    public static Stream<Entry<String, String>> stream() {
        return configs.get(current).cfg.entrySet().stream();
    }

    public static void clear() {
        configs.get(current)._clear();
    }

    public static boolean has(String key) {
        if (configs.isEmpty()) {
            return false;
        }
        return configs.get(current)._has(key);
    }

    public static String get(String key) {
        return configs.get(current)._get(key);
    }

    public static void set(String key, String value) {
        configs.get(current)._set(key, value);
        System.out.println("Setting " + key + " to " + value);
    }
    //</editor-fold>

}
