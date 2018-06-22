
package evemarginfinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
@Deprecated
public class CacheFileRefresher {

    private String read(URL url) {
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

    private String read(String url) throws Exception {
        String data = "";
        Scanner scanner = new Scanner(new URL(url).openStream());

        while (scanner.hasNextLine()) {
            data += scanner.nextLine();
        }

        scanner.close();
        return data;
    }


    List<ItemGroup> groups = new ArrayList<>();

    private JsonParser parser = new JsonParser();

    public ItemGroup getItemGroup(int id) {

        String data = "";

        try {
            data = read("http://esi.tech.ccp.is/latest/markets/groups/" + id);
        } catch (Exception ex) {
            Logger.getLogger(CacheFileRefresher.class.getName()).log(Level.SEVERE, null, ex);
        }

        ConsoleFrame.log(data);

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

        if (!groups.contains(g)) {
            groups.add(g);
        }

        return g;

    }

    public void doParents() {

        for (ItemGroup g : groups) {
            int par = g.parent;

            if (par == -1) {
                continue;
            }

            ItemGroup parent = groups.stream().filter(i -> i.id == par).findFirst().orElse(null);

            if (parent != null) {
                parent.children.add(g.id);
            }
        }

    }

    public int[] getItemGroups() {
        String groups = "";
        try {
            groups = read("http://esi.tech.ccp.is/latest/markets/groups/");
        } catch (Exception ex) {
            Logger.getLogger(CacheFileRefresher.class.getName()).log(Level.SEVERE, null, ex);
        }

        groups = groups.substring(1, groups.length() - 1);

        return Stream.of(groups.split(",")).map(String::trim).mapToInt(Integer::valueOf).toArray();

    }

    public int getQuantity(int itemid) {
        //TODO this
        return 0;
    }

    public double getMargin(int itemid) throws IOException {

        //String data = read(null);
        //TODO gson stuff
        double sell_min = 0, sell_max = 0, buy_min = 0, buy_max = 0;// sell_max has to be less than buy_min w/ margin

        return (buy_min - sell_max) / buy_min;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        ConsoleFrame.log("Fix this first lol");
        System.exit(-1);

        CacheFileRefresher emf = new CacheFileRefresher();

        int[] groups = emf.getItemGroups();

        for (int group : groups) {
            emf.getItemGroup(group);
        }

        emf.doParents();

        File compiled = new File("compiled");

        compiled.createNewFile();

        PrintStream ps = new PrintStream(new FileOutputStream(compiled));

        emf.groups.forEach((evemarginfinder.ItemGroup ig) -> {
            ps.println(ig.encode());
        });

        ps.close();

    }

}
