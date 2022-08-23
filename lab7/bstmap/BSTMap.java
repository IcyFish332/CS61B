package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private int size = 0;
    private Node root = null;
    private class Node<K, V>{
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
        Node left;
        Node right;
    }

    private Node searchNode(Node subRoot, K key) {
        if (subRoot == null) return null;

        int cmp = key.compareTo((K)subRoot.key);
        if (cmp > 0) {
            return searchNode(subRoot.right, key);
        } else if (cmp < 0) {
            return searchNode(subRoot.left, key);
        }
        return subRoot;
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        if (searchNode(root, key) == null) return false;
        return true;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key){
        Node target = searchNode(root, key);
        if(target == null) return null;
        return (V) target.value;
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size(){
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
        size += 1;
    }
    private Node put(Node subRoot, K key, V value) {
        if (subRoot == null) {
            return new Node(key, value);
        }

        int cmp = key.compareTo((K) subRoot.key);
        if (cmp > 0) {
            subRoot.right = put(subRoot.right, key, value);
        } else if (cmp < 0) {
            subRoot.left = put(subRoot.left, key, value);
        } else {
            subRoot.value = value;
        }
        return subRoot;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet(){
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value){
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

//    public void printInOrder() {
//        int tempSize = size;
//        while(tempSize > 0) {
//
//        }
//    }
}
