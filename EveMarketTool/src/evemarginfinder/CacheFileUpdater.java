/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evemarginfinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * I need to run this due to the moon mining update, but it requires an ethernet connection cause wifi is too slow
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class CacheFileUpdater {

    public static String read(String url) throws Exception {
        String data = "";
        Scanner scanner = new Scanner(new URL(url).openStream());

        while (scanner.hasNextLine()) {
            data += scanner.nextLine();
        }

        scanner.close();
        return data;
    }

    public static int[] getItemGroups() throws Exception {

        String in = read("https://esi.tech.ccp.is/latest/markets/groups/");

        String[] sints = in.substring(1, in.length() - 1).split(",");
        
        return Stream.of(sints).mapToInt(Integer::valueOf).toArray();

    }

    public static JsonParser parser = new JsonParser();

    public static ItemGroup getItemGroup(int group) {

        String data = "";

        try {
            data = read("https://esi.tech.ccp.is/latest/markets/groups/" + group);
        } catch (Exception ex) {
            Logger.getLogger(CacheFileUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }

        JsonObject root = parser.parse(data)
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

    public static void doParents(ItemGroup[] groups) {

        for (int i = 0; i < groups.length; i++) {
            ItemGroup g = groups[i];
            if (i % (groups.length / 10) == 0) {
                ConsoleFrame.log("Working on Groups: " + (i * 100 / groups.length) + "%");
            }
            Integer parentid = g.parent;
            if (parentid != -1) {
                ItemGroup parent = Stream.of(groups).filter(parentid::equals).findFirst().orElse(null);
                if (parent != null) {
                    parent.children.add(g.id);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {

        ConsoleFrame.log("Getting Group Ids");

        long pre = System.currentTimeMillis();

        int[] groupids = getItemGroups();

        ConsoleFrame.log("Finished Getting Group Ids (" + (System.currentTimeMillis() - pre) + ")\nGetting Groups");

        ItemGroup[] groups = new ItemGroup[groupids.length];

        pre = System.currentTimeMillis();

        for (int i = 0; i < groupids.length; i++) {
            if (i % (groupids.length / 10) == 0) {
                ConsoleFrame.log("Working on Groups: " + (i * 100d / groupids.length) + "%");
            }
            groups[i] = getItemGroup(groupids[i]);
        }

        ConsoleFrame.log("Finished Getting Groups (" + (System.currentTimeMillis() - pre) + ")\nResolving Children");

        pre = System.currentTimeMillis();

        doParents(groups);

        ConsoleFrame.log("Finished Resolving Parents (" + (System.currentTimeMillis() - pre) + ")\nSaving to File");

        File out = new File("compiled");

        out.createNewFile();

        PrintStream ps = new PrintStream(new FileOutputStream(out));

        pre = System.currentTimeMillis();

        for (ItemGroup group : groups) {
            ps.println(group.encode());
        }

        ConsoleFrame.log("Finished Writing to File (" + (System.currentTimeMillis() - pre) + ")\nFiltering Items");

        ps.close();

        List<Integer> found_items = new ArrayList<>();
        
        for(ItemGroup group : groups){
            found_items.addAll(group.items);
        }
        
        int[] items = found_items.stream().mapToInt(i -> i).toArray();
        Arrays.sort(items);
        
        File filtered = new File("typeids_filter.txt");
        
        filtered.createNewFile();
        
        PrintStream f_ps = new PrintStream(new FileOutputStream(filtered));
        
        Scanner scanner = new Scanner("typeid.txt");
        
        while(scanner.hasNextLine()){
            
            String line = scanner.nextLine();
            
            String id = line.split("\t")[0];
            
            if(Arrays.binarySearch(items, Integer.valueOf(id)) != -1){
                f_ps.println(line);
            }
            
        }
        
    }

}
