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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class ItemGroup {

    //The format for groups.txt is id/parent/name/desc/child groups/child items, tab separated
    
    public int parent = -1;
    public int id = -1;
    public int superparent = -1;
    public boolean issuper = false;
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
        return this.id == other.id;
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
        return id + "\t" + parent + "\t" + name.trim() + "\t" + desc.trim() + "\t" + children.toString() + "\t" + items.toString();
    }
}
