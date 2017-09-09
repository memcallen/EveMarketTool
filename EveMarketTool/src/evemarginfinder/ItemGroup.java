package evemarginfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class ItemGroup {

    public int parent = -1;
    public int id = -1;
    public List<Integer> children = new ArrayList<>();
    public String name = "";
    public String desc = "";
    public List<Integer> items = new ArrayList<>();

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemGroup other = (ItemGroup) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public static ItemGroup decode(String line) {
        String[] temp = line.trim().split("\t");
        ItemGroup group = new ItemGroup();
        group.id = Integer.valueOf(temp[0]);
        group.parent = Integer.valueOf(temp[1]);
        group.name = temp[2];
        group.desc = temp[3];
        if (!temp[4].equals("[]")) {
            Stream.of(temp[4].substring(1, temp[4].length() - 1).split(",")).map(String::trim).mapToInt(Integer::valueOf).forEach(group.children::add);
        }
        if (!temp[5].equals("[]")) {
            Stream.of(temp[5].substring(1, temp[5].length() - 1).split(",")).map(String::trim).mapToInt(Integer::valueOf).forEach(group.items::add);
        }
        return group;
    }

    public String encode() {
        return id + "\t" + parent + "\t" + name + "\t" + desc + "\t" + children.toString() + "\t" + items.toString();
    }
}
