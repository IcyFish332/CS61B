package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }
    private int size = 0;
    private static final int DEFAULT_INITIALSIZE = 16;
    private static final double DEFAULT_LOADFACTOR = 0.75;
    private final double MAX_LOADFACTOR;

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIALSIZE, DEFAULT_LOADFACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOADFACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        MAX_LOADFACTOR = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    private class MyHashMapNodeIterator implements Iterator {
        Queue<Node> queue;
        public MyHashMapNodeIterator() {
            queue = new LinkedList<>();
            for (Collection<Node> items : buckets) {
                for (Node node : items) {
                    queue.add(node);
                }
            }
        }
        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public Node next() {
            return queue.poll();
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapNodeIterator();
    }
    @Override
    /** Removes all of the mappings from this map. */
    public void clear() {
        size = 0;
        buckets = createTable(DEFAULT_INITIALSIZE);
    }

    private Node getNode(K key) {
        if (size == 0) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }
    @Override
    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return getNode(key) != null;
    }

    @Override
    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        Node n = getNode(key);
        if (n == null) {
            return null;
        }
        return n.value;
    }

    @Override
    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    @Override
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        Node node = getNode(key);
        if (node != null) {
            node.value = value;
        } else {
            int index = Math.floorMod(key.hashCode(), buckets.length);
            buckets[index].add(createNode(key, value));
            size += 1;

            if (size / buckets.length > MAX_LOADFACTOR) {
                resize(buckets.length * 2);
            }
        }
    }

    private void resize(int capacity) {
        Collection<Node>[] resized = new Collection[capacity];
        for (int i = 0; i < capacity; i++) {
            resized[i] = createBucket();
        }

        for (int i = 0; i < buckets.length; i++) {
            for (Node node : buckets[i]) {
                int index = Math.floorMod(node.key.hashCode(), capacity);
                resized[index].add(node);
            }
        }
        buckets = resized;
    }


    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Collection<Node> items : buckets) {
            for (Node node : items) {
                set.add(node.key);
            }
        }
        return set;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    // Your code won't compile until you do so!

}
