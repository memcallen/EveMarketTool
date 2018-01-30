package evemarginfinder;

import com.google.gson.JsonElement;
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
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.JOptionPane;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class DatabaseManager {

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
    public List<Integer> selected_items = new ArrayList<>();
    public Consumer<Integer> visual_selector_groups = System.out::println;
    public Consumer<Integer> visual_selector_items = System.out::println;
    public MainFrame gui = null;

    public static Entry<Integer, String>[] items; //I should've used a map but oh well
    public static HashMap<Integer, String> systems = new HashMap<>();
    public static String itemgroupFile = "groups.txt";
    public static String itemFile = "typeid.txt";
    public static String systemFile = "systems.txt";

    public static Queue<Integer> groups_q = new LinkedList<>();
    public static Queue<Integer> items_q = new LinkedList<>();

    public static JsonParser parser = new JsonParser();

    public static String getQueryURL(int[] itemids, int loc, boolean stat) {
        String out = ConfigManager.get("uformat");

        out = out.replace("{0}", ConfigManager.get("url"));

        String typef = ConfigManager.get("type");

        String types = ConfigManager.get("typeroot").replace("{0}",
                IntStream.of(itemids).mapToObj(i -> typef.replace("{0}", Integer.toString(i))).reduce("", String::concat)
        );

        String location = ConfigManager.get(stat ? "station" : "region").replace("{0}", Integer.toString(loc));

        return out.replace("{1}", types).replace("{2}", location);
    }

    public static List<Vector> getMarketInfoBulk(int[] itemid, int sysid) {

        String url = getQueryURL(itemid, sysid, true);

        System.out.println("Querying " + url);

        JsonElement response;

        try{
            response = read(url);
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Connecting to API", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        return QueryTranslator.getTableData(QueryTranslator.translate(itemid, response));
    }

    public static JsonElement read(String string) throws IOException {
        return parser.parse(new BufferedReader(new InputStreamReader(new URL(string).openStream())));
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

    public void loadSystems() throws FileNotFoundException {

        long pre = System.currentTimeMillis();

        File file = new File(systemFile);

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            while (scanner.hasNextLine()) {
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

        if (!LOGFILE.exists()) {
            try {
                LOGFILE.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (Arrays.asList(args).contains("--debug")) {
            try {
                log_stream = new FileOutputStream(LOGFILE);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.setOut(new PrintStream(log_stream));
        }

        Thread t = new Thread(() -> {

            ConfigManager.load();

            QueryTranslator.init();

            ConfigManager.loadHeaders();

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
            }
        });

        t.setName("main-thread");

        t.start();
    }

}
