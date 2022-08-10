package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        Node prev;
        T item;
        Node next;
        Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        Node p = new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = p;
        sentinel.next = p;
        size++;
    }

    public void addLast(T item) {
        Node p = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.next = p;
        sentinel.prev = p;
        size++;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node p = sentinel.next;
        for (int i = 0; i < size - 1; i++) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println(p);
    }

    public T removeFirst() {
        if (sentinel.next == sentinel) {
            return null;
        } else {
            T object = sentinel.next.item;
            sentinel.next.next.prev = sentinel;
            sentinel.next = sentinel.next.next;
            size--;
            return object;
        }
    }

    public T removeLast() {
        if (sentinel.prev == sentinel) {
            return null;
        } else {
            T object = sentinel.prev.item;
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
            size--;
            return object;
        }
    }

    public T get(int index) {
        if (index >= size || index < 0 || isEmpty()) {
            return null;
        } else {
            Node p = sentinel;
            for (int i = 0; i <= index; i++) {
                p = p.next;
            }
            return p.item;
        }
    }

    private T getRecursiveHelper(Node temp, int index) {
        if (index == 0) {
            return temp.item;
        }
        return getRecursiveHelper(temp.next, index - 1);
    }

    public T getRecursive(int index) {
        Node temp = sentinel;
        if (index >= size) {
            return null;
        }
        return getRecursiveHelper(temp.next, index);
    }

    public LinkedListDeque(LinkedListDeque other) {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
        for (int i = 0; i < other.size(); i++) {
            addLast((T) other.get(i));
        }
    }

    public Iterator<T> iterator() {
        return new LinkedListDeque.ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int flag;
        public ArrayDequeIterator() {
            flag = 0;
        }
        @Override
        public boolean hasNext() {
            return flag < size();
        }

        @Override
        public T next() {
            return get(flag++);
        }
    }
}
