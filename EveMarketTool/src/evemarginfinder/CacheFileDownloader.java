package evemarginfinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import evemarginfinder.MultiThreadPool.MultiThreadPoolBuilder;
import evemarginfinder.MultiThreadPool.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * CacheFileUpdater v3 Uses multithreading to speed up processing
 * {@link http://eve-files.com/chribba/typeid.txt} - no longer updated?
 * {@link www.fuzzwork.co.uk/resources/typeids.csv}
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class CacheFileDownloader {

    public static String read(String url) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(new URL(url).openStream())) {
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
        }
        return sb.toString();
    }

    public static int[] getItemGroups() throws Exception {

        String in = read("https://esi.tech.ccp.is/latest/markets/groups/");

        String[] sints = in.substring(1, in.length() - 1).split(",");

        return Stream.of(sints).mapToInt(Integer::valueOf).toArray();

    }

    public static ItemGroup getItemGroup(Integer group) {

        System.out.println("Getting " + group);

        String data = "";

        try {
            data = read("https://esi.tech.ccp.is/latest/markets/groups/" + group);
        } catch (Exception ex) {
            Logger.getLogger(CacheFileUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }

        JsonObject root = new JsonParser().parse(data)
                .getAsJsonObject();

        ItemGroup g = new ItemGroup();

        g.desc = root.get("description").getAsString();
        g.name = root.get("name").getAsString();
        g.id = root.get("market_group_id").getAsInt();
        if (root.has("parent_group_id")) {
            g.parent = root.get("parent_group_id").getAsInt();
        }

        JsonArray array = root.get("types").getAsJsonArray();

        for (int i = 0; i < array.size(); i++) {
            g.items.add(array.get(i).getAsInt());
        }

        return g;

    }

    public static ArrayList<Pair<Integer, ItemGroup>> populateGroups(int[] group_ids, int threads) throws InterruptedException {

        //pool setup
        MultiThreadPoolBuilder<Integer, ItemGroup> builder = new MultiThreadPoolBuilder();

        builder.setStaticSize(threads).setRetryOnException(true)
                .setThreadFactory(MultiThreadPool.poolthread(CacheFileDownloader::getItemGroup));

        MultiThreadPool<Integer, ItemGroup> ggetter_pool = builder.Create();

        ArrayList<Pair<Integer, ItemGroup>> igroups = new ArrayList<>(group_ids.length);

        //queue groups
        for (int i : group_ids) {
            ggetter_pool.queue(i);
        }

        ggetter_pool.start_and_wait();

        //get groups
        for (Iterator<Pair<Integer, ItemGroup>> i = ggetter_pool.output_iterator(); i.hasNext();) {
            igroups.add(i.next());
        }

        ggetter_pool.interrupt();

        igroups.sort((a, b) -> a.first.compareTo(b.first));

        return igroups;
    }

    public static void resolveParents(ArrayList<Pair<Integer, ItemGroup>> igroups, int threads) throws InterruptedException {

        //parent resolver
        Function<Pair<ItemGroup, ArrayList<Pair<Integer, ItemGroup>>>, Void> resolver = (Pair<ItemGroup, ArrayList<Pair<Integer, ItemGroup>>> p) -> {
            System.out.println("Resolving " + p.first.id);
            Integer parentid = p.first.parent;
            if (parentid != -1) {
                for (Pair<Integer, ItemGroup> curr : p.second) {
                    if (curr.second.id == parentid) {
                        curr.second.children.add(p.first.id);
                        break;
                    }
                }
            }
            return null;
        };

        //setup pool
        MultiThreadPoolBuilder<Pair<ItemGroup, ArrayList<ItemGroup>>, Void> builder2 = new MultiThreadPoolBuilder();

        builder2.setStaticSize(threads).setRetryOnException(true)
                .setThreadFactory(MultiThreadPool.poolthread(resolver));

        MultiThreadPool<Pair<ItemGroup, ArrayList<ItemGroup>>, Void> parent_resolver = builder2.Create();

        //queue and wait
        igroups.forEach((group) -> {
            parent_resolver.queue(new Pair(group.second, igroups));
        });

        parent_resolver.start_and_wait();

    }

    public static void outputGroups(ArrayList<Pair<Integer, ItemGroup>> igroups, String filename) throws IOException {
        File group_file = new File(filename);

        if (!group_file.exists()) {
            group_file.createNewFile();
        }

        try (PrintStream output = new PrintStream(new FileOutputStream(group_file))) {
            igroups.forEach((p) -> {
                output.println(p.second.encode());
            });
        }
    }

    public static ArrayList<Pair<Integer, ItemGroup>> readGroups(String filename) throws FileNotFoundException {
        File input = new File(filename);
        ArrayList<Pair<Integer, ItemGroup>> typeids = new ArrayList<>();

        try (Scanner scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                ItemGroup ig = ItemGroup.decode(scanner.nextLine().trim());
                typeids.add(new Pair<>(ig.id, ig));
            }
        }

        return typeids;
    }

    public static ArrayList<Pair<Integer, String>> readTypeIDs(String filename) throws FileNotFoundException {
        File input = new File(filename);
        ArrayList<Pair<Integer, String>> typeids = new ArrayList<>();

        try (Scanner scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                String uline = scanner.nextLine();
                String[] line = uline.replace("\"", "").split("\\,");
                typeids.add(new Pair<>(Integer.valueOf(line[0].trim()), line[1].trim()));
            }
        }

        return typeids;
    }

    public static TreeSet<Integer> getChildren(ArrayList<Pair<Integer, ItemGroup>> igroups) {
        TreeSet<Integer> items = new TreeSet<>(Integer::compare);

        igroups.stream().flatMap(p -> p.second.items.stream()).forEach(items::add);

        return items;
    }

    public static ArrayList<Pair<Integer, String>> getReferencedTypeIDs(ArrayList<Pair<Integer, ItemGroup>> igroups, String filename) throws FileNotFoundException {
        TreeSet<Integer> items = getChildren(igroups);
        ArrayList<Pair<Integer, String>> typeids = readTypeIDs(filename);
        ArrayList<Pair<Integer, String>> references = new ArrayList<>();

        for (int i = 0; i < typeids.size(); i++) {
            if (items.contains(typeids.get(i).first)) {
                references.add(typeids.get(i));
            }
        }

        return references;
    }

    public static void writeTypeIDs(ArrayList<Pair<Integer, String>> items, String filename) throws IOException {
        File item_file = new File(filename);

        if (!item_file.exists()) {
            item_file.createNewFile();
        }

        try (PrintStream output = new PrintStream(new FileOutputStream(item_file))) {
            items.forEach((p) -> {
                output.println(p.first + "\t" + p.second);
            });
        }
    }

    public static void main(String[] args) throws Exception {
        //This is 100% overengineered but idc
        
        //available threads - current count - the pool thread
        int threads = Runtime.getRuntime().availableProcessors() - Thread.activeCount() - 1;
        boolean update_groups = true;

        System.out.println("Getting Groups");

        ArrayList<Pair<Integer, ItemGroup>> igroups;

        if (update_groups) {
            // get groups
            int[] group_ids = getItemGroups();

            //populate groups
            igroups = populateGroups(group_ids, threads);

            System.out.println("Resolving Parents");

            //resolve parents
            resolveParents(igroups, threads);

            System.out.println("Writing Groups");

            //save groups
            outputGroups(igroups, "group_output.txt");
        } else {
            igroups = readGroups("groups.txt");
        }

        System.out.println("Filtering TypeIDs");

        //find referenced typeids
        ArrayList<Pair<Integer, String>> typeids = getReferencedTypeIDs(igroups, "typeid_fuzzwork.txt");

        //sort them
        typeids.sort((a, b) -> a.first.compareTo(b.first));

        System.out.println("Writing TypeIDs");

        //write found items
        writeTypeIDs(typeids, "typeid.txt");

    }

}
