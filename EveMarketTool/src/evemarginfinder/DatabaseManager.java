package evemarginfinder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Vector;
import java.util.stream.IntStream;
import javax.swing.JOptionPane;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class DatabaseManager extends Thread{

    //LOG FILE STUFF
    private static final File LOGFILE = new File("LOG-" + System.currentTimeMillis());
    private static OutputStream log_stream = null;

    private static final OutputStream CONSOLE_STREAM = new OutputStream() {

        public Console out = System.console();

        @Override
        public void write(int b) throws IOException {

            out.format(Character.toString((char) b));

            log_stream.write(b);
        }

    };
    //END LOG FILE STUFF

    public ItemGroup[] groups;
    public MainFrame gui = null;
    public FilterFrame filterf = new FilterFrame();

    public static Entry<Integer, String>[] items; //I should've used a map but oh well
    public static HashMap<String, Integer> systems = new HashMap<>();
    public static String itemgroupFile = "groups.txt";
    public static String itemFile = "typeid.txt";
    public static String systemFile = "systems.txt";

    public static JsonParser parser = new JsonParser();

    public static String getQueryURL(int[] itemids, int loc, boolean stat) {
        String out = Configuration.get("uformat");

        out = out.replace("{0}", Configuration.get("url"));

        String typef = Configuration.get("type");

        String types = Configuration.get("typeroot").replace("{0}",
                IntStream.of(itemids).mapToObj(i -> typef.replace("{0}", Integer.toString(i))).reduce("", String::concat)
        );

        String location = Configuration.get(stat ? "station" : "region").replace("{0}", Integer.toString(loc));

        return out.replace("{1}", types).replace("{2}", location);
    }

    public static List<Vector> getMarketInfoBulk(int[] itemid, int sysid) {

        String url = getQueryURL(itemid, sysid, false);

        ConsoleFrame.log("Querying " + url);

        long pre = System.currentTimeMillis();
        
        JsonElement response;

        try {
            response = read(url);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage().length() > 100 ? e.getMessage().substring(0, 100) + "..." : e.getMessage(),
                    "Error Connecting to API", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        ConsoleFrame.log("Received response in " + (System.currentTimeMillis() - pre) + "ms");
        ConsoleFrame.log("Translating response");
        
        pre = System.currentTimeMillis();
        
        List<Vector> out = QueryTranslator.getTableData(QueryTranslator.translate(itemid, response));
        
        ConsoleFrame.log("Translated in " + (System.currentTimeMillis() - pre) + "ms");
        
        return out;
    }

    public static JsonElement read(String string) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(string).openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/4.0");
        JsonElement ret = parser.parse(new BufferedReader(new InputStreamReader(conn.getInputStream())));
        conn.disconnect();
        return ret;
    }

    public static int queryItemId(String name) {

        for (Entry<Integer, String> item : items) {
            if (item.getValue().equals(name)) {
                return item.getKey();
            }
        }

        return -1;
    }

    public static String queryItemName(int typeid) {

        for (Entry<Integer, String> item : items) {
            if (item.getKey() == typeid) {
                return item.getValue();
            }
        }

        return null;

    }

    public static int querySystemId(String name) {
        return systems.containsKey(name) ? systems.get(name) : -1;
    }

    public static String querySystemName(int id) {
        for (Entry<String, Integer> e : systems.entrySet()) {
            if (e.getValue() == id) {
                return e.getKey();
            }
        }
        return null;
    }

    public void loadSystems() throws FileNotFoundException {

        long pre = System.currentTimeMillis();

        File file = new File(systemFile);

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\t");

                systems.put(line[2], Integer.valueOf(line[0]));

            }
        }

        ConsoleFrame.log("Loaded Systems in " + (System.currentTimeMillis() - pre) + " millis");

    }

    public void loadItems() throws FileNotFoundException {

        long pre = System.currentTimeMillis();

        List<Entry<Integer, String>> temp = new ArrayList<>();

        File file = new File(itemFile);

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().trim().split("\t");

                temp.add(new AbstractMap.SimpleEntry<>(Integer.valueOf(line[0]), line[1]));
            }
        }

        items = temp.toArray(new Entry[temp.size()]);

        ConsoleFrame.log("Loaded Items in " + (System.currentTimeMillis() - pre) + " millis");
    }

    public void initialize() throws FileNotFoundException {

        long pre = System.currentTimeMillis();

        List<ItemGroup> temp = new ArrayList<>();

        File file = new File(itemgroupFile);

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            while (scanner.hasNextLine()) {
                temp.add(ItemGroup.decode(scanner.nextLine()));
            }
        }

        groups = temp.toArray(new ItemGroup[temp.size()]);

        ConsoleFrame.log("Loaded Groups in " + (System.currentTimeMillis() - pre) + " millis");

        //initilize front-end
        loadItems();
        loadSystems();

        ConsoleFrame.log("Loading CheckBoxHandler");
        
        CheckBoxHandler.setData(groups, items);
        
        Entry<Integer, String>[] entries_groups = new Entry[temp.size()];

        for (int i = 0; i < groups.length; i++) {
            ItemGroup group = groups[i];
            entries_groups[i] = new AbstractMap.SimpleEntry<>(group.id, group.name);
        }

        ConsoleFrame.log("Loading Main Interface");
        
        gui = new MainFrame(entries_groups, items, filterf);

        gui.setVisible(true);

    }

    @Override
    public void run(){
        
            Configuration.initialize();
            
            QueryTranslator.initialize();

            CheckBoxHandler.initialize();
            
            filterf = new FilterFrame();
            filterf.loadCfg();
            QueryTranslator.setFilter(filterf);
            
            QueryTranslator.reset_lua();

            ConsoleFrame.log("Initing stuff");

            try {
                initialize();
            } catch (FileNotFoundException ex) {
                ConsoleFrame.log("Error initializing database manager: " + ex.getMessage());
                return;
            }

            ConsoleFrame.log("Finished loading data, showing window");

            ConsoleFrame.log("Starting selector loop");

            while(true){
                try {
                    Thread.sleep(1000);//infinite sleep because I'm not sure what to do here
                } catch (InterruptedException ex) {
                }
            }

    }
    
    public static void main(String[] args) {

        if (!LOGFILE.exists()) {
            try {
                LOGFILE.createNewFile();
            } catch (IOException ex) {
                ConsoleFrame.log("Could not create logfile, maybe program is in read only folder?");
                ConsoleFrame.log(ex.getMessage());
                return;
            }
        }

        try {
            log_stream = new FileOutputStream(LOGFILE);

            System.setErr(new PrintStream(log_stream));
        } catch (FileNotFoundException ex) {
            ConsoleFrame.log("Something went very wrong, could not open file that was just created");
            ConsoleFrame.log(ex.getMessage());
            return;
        }

        ConsoleFrame frame = new ConsoleFrame();

        frame.init(new PrintStream(log_stream));

        frame.setVisible(true);

        Thread t = new DatabaseManager();

        t.setName("main-thread");

        t.start();
    }

}
