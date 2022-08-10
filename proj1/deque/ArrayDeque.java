package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private final int INITIAL_CAPACITY = 8;
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[INITIAL_CAPACITY];
        size = nextFirst = 0;
        nextLast = 1;
    }

    /**
     * Adjusts the length of this array to match the size, approving efficiency.
     */
    private void resize() {
        if (size >= items.length) {
            // expands the length of this array
            resizeHelper(items.length * 2);
        } else if (items.length >= 16 && size < items.length * 0.25) {
            // reduces the length of this array
            resizeHelper(items.length / 2);
        }
    }

    /**
     * Helps resize() to adjust the size of array
     */
    private void resizeHelper(int capacity) {
        T[] a = items;
        int begin = plusOne(nextFirst);
        int end = minusOne(nextLast);
        items = (T[]) new Object[capacity];
        nextFirst = 0;
        nextLast = 1;
        for (int i = begin; i != end; i = Math.floorMod(i + 1, a.length)) {
            items[nextLast] = a[i];
            nextLast = plusOne(nextLast);
        }
        items[nextLast] = a[end];
        nextLast = plusOne(nextLast);
    }

    /**
     * Helps pointers to move
     */
    private int minusOne(int index) {
        return Math.floorMod(index - 1, items.length);
    }

    /**
     * Helps pointers to move
     */
    private int plusOne(int index) {
        return Math.floorMod(index + 1, items.length);
    }

    public void addFirst(T item) {
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size++;
        resize();
    }

    public void addLast(T item) {
        items[nextLast] = item;
        nextLast = plusOne(nextLast);
        size++;
        resize();
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int i;
        for (i = plusOne(nextFirst); i != minusOne(nextLast); i = plusOne(i)) {
            System.out.print(items[i] + " ");
        }
        System.out.println(items[i]);
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        nextFirst = plusOne(nextFirst);
        T firstItem = items[nextFirst];
        items[nextFirst] = null;
        size--;
        resize();
        return firstItem;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        nextLast = minusOne(nextLast);
        T lastItem = items[nextLast];
        items[nextLast] = null;
        size--;
        resize();
        return lastItem;
    }

    public T get(int index) {
        if (index >= size || index < 0 || isEmpty()) {
            return null;
        }
        index = Math.floorMod(plusOne(nextFirst) + index, items.length);
        return items[index];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> temp = (Deque<T>) o;
        if (temp.size() != size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!(temp.get(i).equals(this.get(i)))) {
                return false;
            }
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int flag;
        ArrayDequeIterator() {
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
