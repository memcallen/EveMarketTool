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
package treepanel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.ToIntFunction;
import treepanel.TreeNode.NodeIteratorInfo;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class TreeNode<T> implements Iterable<NodeIteratorInfo<T>> {

    private T value;
    //siblings
    public TreeNode<T> prev, next;
    //children
    public TreeNode<T> head, tail;
    //parent
    public TreeNode<T> parent;

    public TreeNode(T value) {
        this.value = value;
    }

    @Override
    public Iterator<NodeIteratorInfo<T>> iterator() {
        return new DepthFirstTNIterator(this);
    }

    public TreeNode<T> Child(T value) {
        TreeNode<T> node = new TreeNode(value);

        if (parent != null) {
            AddChild(parent, node);
        }

        return node;
    }

    public T GetValue() {
        return value;
    }

    public boolean isLeaf() {
        return head == null && tail == null;
    }

    public boolean isBranch() {
        return !isLeaf();
    }

    public int NumChildren() {
        TreeNode<T> curr = head;
        int count = 0;

        while (curr != null) {
            count++;
            curr = curr.next;
        }

        return count;
    }

    public void UpdateState() {
        // does nothing, used in HideableTreeNode
    }

    /**
     * @param child The child node
     * @param compare The comparator function
     */
    public void AddChildSorted(TreeNode<T> child, Comparator<TreeNode<T>> compare) {

        child.parent = this;

        // if the head is null, list is empty
        if (head == null) {
            PushFront(this, child);
            return;
        }

        if (compare.compare(child, head) < 0) {
            PushFront(this, child);
            return;
        }

        TreeNode<T> curr = head;

        while (curr.next != null) {
            if (compare.compare(child, curr) < 0) {
                break;
            }
            curr = curr.next;
        }

        if (compare.compare(child, curr) < 0) {
            if (curr.prev == null) {
                PushFront(this, child);
            } else {
                PushAfter(this, curr.prev, child);
            }
        } else {
            PushAfter(this, curr, child);
        }

    }

    public static <T> void PushFront(TreeNode<T> parent, TreeNode<T> child) {

        if (parent.head != null) {
            parent.head.prev = child;
        }

        child.next = parent.head;

        parent.head = child;

        if (parent.tail == null) {
            parent.tail = child;
        }

        child.parent = parent;

    }

    public static <T> void PushBack(TreeNode<T> parent, TreeNode<T> child) {

        if (parent.tail != null) {
            parent.tail.next = child;
        }

        child.prev = parent.tail;

        parent.tail = child;

        if (parent.head == null) {
            parent.head = child;
        }

        child.parent = parent;

    }

    public static <T> void PushAfter(TreeNode<T> parent, TreeNode<T> after, TreeNode<T> child) {
        
        if (after.next != null) {
            after.next.prev = child;
            child.next = after.next;
        }
        
        child.prev = after;
        after.next = child;
        
        child.parent = parent;
    }

    public static <T> void AddChild(TreeNode<T> parent, TreeNode<T> child) {
        PushBack(parent, child);
    }

    public static <T> void GroupChildren(TreeNode<T> parent, ToIntFunction<TreeNode<T>> grouper) {
        
        TreeNode<T>[] children = new TreeNode[parent.NumChildren()];
        TreeNode<T> curr = parent.head;
        int n = children.length;
        
        // Cache all children to array
        
        for(int i = 0; i < n; i++) {
            children[i] = curr;
            curr = curr.next;
        }
        
        // Sort array via grouper function (& mangle links)
        Arrays.sort(children, (a, b)->Integer.compare(grouper.applyAsInt(a), grouper.applyAsInt(b)));
        
        // Repair links
        
        for(int i = 0; i < n; i++) {
            children[i].prev = i == 0 ? null : children[i - 1];
            children[i].next = i == n - 1 ? null : children[i + 1];
        }
        
        parent.head = children[0];
        parent.tail = children[n - 1];
        
    }
    
    public static class TreeNodeIterator implements Iterator<TreeNode> {

        private TreeNode current;
        private final TreeNode parent;

        public TreeNodeIterator(TreeNode start) {
            current = start;
            parent = start.parent;
        }

        @Override
        public boolean hasNext() {
            return current != parent;
        }

        @Override
        public TreeNode next() {
            TreeNode ret = current;

            if (current.head != null) {
                current = current.head;
            } else {
                if (current.next != null) {
                    current = current.next;
                } else {
                    current = current.parent.next;
                }
            }

            return ret;
        }

    }

    public static class DepthFirstTNIterator<T> implements Iterator<NodeIteratorInfo<T>> {

        private TreeNode<T> current;
        private int depth = 0;
        private final TreeNode<T> parent;

        public DepthFirstTNIterator(TreeNode<T> start) {
            current = start;
            parent = start.parent;
        }

        @Override
        public boolean hasNext() {
            return current != parent && current != null;
        }

        @Override
        public NodeIteratorInfo<T> next() {
            NodeIteratorInfo info = new NodeIteratorInfo(current, depth);

            // check children first
            if (current.head != null) {
                current = current.head;
                depth++;
            } else {

                // find next available sibling
                while (current.next == null) {

                    depth--;

                    current = current.parent;

                    if (current == null || current == parent) {
                        break;
                    }

                }

                if (current != null) {
                    // go to sibling
                    current = current.next;
                }
            }

            return info;
        }

    }

    public static class NodeIteratorInfo<T> {

        public TreeNode<T> node;
        public int node_depth;

        public NodeIteratorInfo(TreeNode<T> node, int depth) {
            this.node = node;
            this.node_depth = depth;
        }

        public T value() {
            return node.GetValue();
        }
    }

}
