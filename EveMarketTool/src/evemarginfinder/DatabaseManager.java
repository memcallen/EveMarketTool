package evemarginfinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.swing.JCheckBox;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class DatabaseManager {

    //LOG FILE STUFF
    
    private static File logfile = new File("LOG-" + System.currentTimeMillis());//30001198
    private static OutputStream log_stream = null;

    private static OutputStream console_stream = new OutputStream() {

        public Console out = System.console();

        @Override
        public void write(int b) throws IOException {

            out.format(Character.toString((char) b));

            log_stream.write(b);
        }

    };

    static {//create log file
        
        if (!logfile.exists()) {
            try {
                logfile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            log_stream = new FileOutputStream(logfile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.setOut(new PrintStream(System.console() == null ? log_stream : console_stream));

    }
    
    //END LOG FILE STUFF
    
    public static class CheckBoxListener implements ItemListener {

        private boolean isgroup = true;

        /**
         * @param isgroup true=is a group, false=is an item
         */
        public CheckBoxListener(boolean isgroup) {
            this.isgroup = isgroup;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {

            //when the checkbox state gets changed through setSelected, this listener gets called
            //this line prevents unnecessary recursion
            if (!((JCheckBox) e.getItem()).hasFocus()) {
                return;
            }

            if (e.getStateChange() == ItemEvent.DESELECTED) {
                return;
            }

            int id = Integer.valueOf(((JCheckBox) e.getItem()).getToolTipText());

            if (isgroup) {
                DatabaseManager.selectGroup_Queue(id);
            } else {
                DatabaseManager.selectItem_Queue(id);
            }

        }

    }

    public ItemGroup[] groups;
    public static Entry<Integer, String>[] items; //I should've used a map but oh well
    public static HashMap<Integer, String> systems = new HashMap<>();
    public List<Integer> selected_items = new ArrayList<>();
    public Consumer<Integer> visual_selector_groups = System.out::println;
    public Consumer<Integer> visual_selector_items = System.out::println;
    public static String itemgroupFile = "groups.txt";
    public static String itemFile = "typeid.txt";
    public static String systemFile = "systems.txt";
    public MainFrame gui = null;

    public static Queue<Integer> groups_q = new LinkedList<>();
    public static Queue<Integer> items_q = new LinkedList<>();

    public static JsonParser parser = new JsonParser();

    public static List<Vector> getMarketInfoBulk(int[] itemid, int sysid) {

        String types = IntStream.of(itemid).mapToObj(id -> "&typeid=" + id).reduce("", (a, b) -> a + b);

        JsonArray root = read("http://api.eve-central.com/api/marketstat/json?" + types.substring(1) + "&usesystem=" + sysid)
                .getAsJsonArray();

        List<Vector> vectors = new ArrayList<>();

        for (int i = 0; i < root.size(); i++) {

            JsonObject item = root.get(i).getAsJsonObject();

            JsonObject _buy = item.get("buy").getAsJsonObject();
            JsonObject _sell = item.get("sell").getAsJsonObject();

            double sell = _buy.get("fivePercent").getAsDouble();
            double buy = _sell.get("fivePercent").getAsDouble();
            long volume = _buy.get("volume").getAsLong();

            double difference = buy - sell;
            double margin = difference / buy;
            double cost = sell;
            double profit = volume / 24d * difference;

            Vector out = new Vector();

            int id = _buy.get("forQuery").getAsJsonObject().get("types").getAsJsonArray().get(0).getAsInt();

            out.add(queryItemName(id));
            out.add(margin);
            out.add(cost);
            out.add(profit);
            out.add(volume);

            vectors.add(out);

        }

        return vectors;

    }

    @Deprecated
    public static Vector getMarketInfo(int itemid, int sysid) {

        JsonObject eve_central = read("http://api.eve-central.com/api/marketstat/json?typeid=" + itemid + "&usesystem=" + sysid)
                .getAsJsonArray().get(0).getAsJsonObject();

        JsonObject _buy = eve_central.get("buy").getAsJsonObject();
        JsonObject _sell = eve_central.get("sell").getAsJsonObject();

        double sell = _buy.get("fivePercent").getAsDouble();
        double buy = _sell.get("fivePercent").getAsDouble();
        int volume = _buy.get("volume").getAsInt();

        double difference = buy - sell;
        double margin = difference / buy;
        double cost = sell;
        double profit = volume / 24d * difference;

        Vector out = new Vector();

        out.add(queryItemName(itemid));
        out.add(margin);
        out.add(cost);
        out.add(profit);
        out.add(volume);

        return out;
    }

    public static JsonElement read(String string) {
        try {
            return parser.parse(new BufferedReader(new InputStreamReader(new URL(string).openStream())));
        } catch (IOException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String read(URL url) {
        String data = "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            data = reader.lines().reduce("", String::join);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CacheFileRefresher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CacheFileRefresher.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    public String readS(String url) throws Exception {
        String data = "";
        Scanner scanner = new Scanner(new URL(url).openStream());

        while (scanner.hasNextLine()) {
            data += scanner.nextLine();
        }

        scanner.close();
        return data;
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

    public void loadSystems() throws FileNotFoundException{
        
        long pre = System.currentTimeMillis();
        
        File file = new File(systemFile);
        
        try (Scanner scanner = new Scanner(new FileInputStream(file))){
            while(scanner.hasNextLine()){
                String[] line = scanner.nextLine().split("\t");
                
                systems.put(Integer.valueOf(line[0]), line[2]);
                
            }
        }
        
        System.out.println("Loaded Systems in " + (System.currentTimeMillis() - pre) + " millis");
        
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

        System.out.println("Loaded Items in " + (System.currentTimeMillis() - pre) + " millis");
    }

    public void init() throws FileNotFoundException {

        long pre = System.currentTimeMillis();

        List<ItemGroup> temp = new ArrayList<>();

        File file = new File(itemgroupFile);

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            while (scanner.hasNextLine()) {
                temp.add(ItemGroup.decode(scanner.nextLine()));
            }
        }

        groups = temp.toArray(new ItemGroup[temp.size()]);

        System.out.println("Loaded Groups in " + (System.currentTimeMillis() - pre) + " millis");

        //initilize front-end
        
        loadItems();
        loadSystems();

        Entry<Integer, String>[] entries_groups = new Entry[temp.size()];

        for (int i = 0; i < groups.length; i++) {
            ItemGroup group = groups[i];
            entries_groups[i] = new AbstractMap.SimpleEntry<>(group.id, group.name);
        }

        EventQueue.invokeLater(() -> {
            gui = new MainFrame(entries_groups, items);

            visual_selector_groups = i -> gui.setSelectedGroup(i, true);
            visual_selector_items = i -> gui.setSelectedItem(i, true);

            gui.setVisible(true);
        });

    }

    public static void selectGroup_Queue(int id) {
        groups_q.add(id);
    }

    public static void selectItem_Queue(int id) {
        items_q.add(id);
    }

    public void checkSelections() {

        while (groups_q.size() > 0) {
            selectGroup(groups_q.poll());
        }

        while (items_q.size() > 0) {
            selected_items.add(items_q.poll());
        }

    }

    public void selectGroup(int id) {
        selectGroup(id, 0);
    }

    private void selectGroup(int id, int n) {
        if (groups == null) {
            throw new Error("Initialize groups first (loadGroups error/not called)");
        }

        ItemGroup group = null;
        int group_index = 0;

        for (; group_index < groups.length; group_index++) {
            if (groups[group_index].id == id) {
                group = groups[group_index];
                break;
            }
        }

        if (group == null) {
            throw new Error("Error, no group with that id, id=" + id + ", iter#=" + n);
        }

        visual_selector_groups.accept(group.id);

        if (group.children.size() > 0) {

            group.children.forEach(child -> selectGroup(child, n + 1));

        }

        if (group.items.size() > 0) {

            for (int i = 0; i < group.items.size(); i++) {
                visual_selector_items.accept(group.items.get(i));
            }

        }

    }

    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            DatabaseManager man = new DatabaseManager();

            System.out.println("Initing stuff");

            try {
                man.init();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }

            System.out.println("Starting selector loop");

            while (true) {
                man.checkSelections();

                try {
                    Thread.sleep(16);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        t.setName("main-thread");
        
        t.start();
    }

}
