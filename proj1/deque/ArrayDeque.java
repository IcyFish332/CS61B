package deque;

public class ArrayDeque<T> {
    private final int INITIAL_CAPACITY  = 8;
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[INITIAL_CAPACITY];
        size = nextFirst = 0;
        nextLast = 1;
    }

    /** Adjusts the length of this array to match the size, approving efficiency. */
    private void resize() {
        if (size >= items.length) {
            // expands the length of this array
            resizeHelper(items.length * 2);
        } else if (items.length >= 16 && size < items.length * 0.25) {
            // reduces the length of this array
            resizeHelper(items.length / 2);
        }
    }

    /** Helps resize() to adjust the size of array */
    private void resizeHelper(int capacity) {
        T[] a = items;
        int aNextLast = nextLast;
        int aNextFirst = nextFirst;
        items = (T[]) new Object[capacity];
        nextFirst = 0;
        nextLast = 1;
        aNextLast = plusOne(aNextFirst);
        for (int i = aNextFirst; i != minusOne(aNextLast); i = Math.floorMod(i + 1, a.length)) {
            items[nextLast] = a[i];
            nextLast = plusOne(nextLast);
        }
        items[nextLast] = a[minusOne(aNextLast)];
        nextLast = plusOne(nextLast);
    }

    /** Helps pointers to move */
    private int minusOne(int index) {
        return Math.floorMod(index - 1, items.length);
    }

    /** Helps pointers to move */
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

    public boolean isEmpty() {
        return size == 0;
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
        if (size <= 0) {
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
        if (size <= 0) {
            return null;
        }
        nextLast = minusOne(nextLast);
        T firstItem = items[nextLast];
        items[nextLast] = null;
        size--;
        resize();
        return firstItem;
    }

    public T get(int index) {
        if (index >= size || index < 0 || isEmpty()) {
            return null;
        }
        int target = plusOne(nextFirst);
        for (int i = 0; i < index; i++) {
            target = plusOne(target);
        }
        return items[target];
    }

    public ArrayDeque(ArrayDeque other) {
        items = (T[]) new Object[other.items.length];
        size = other.size;
        nextFirst = other.nextFirst;
        nextLast = other.nextLast;
        for (int i = plusOne(nextFirst); i != minusOne(nextLast); i++) {
            items[i] = (T) other.items[i];
        }
        items[minusOne(nextLast)] = (T) other.items[minusOne(nextLast)];
    }
}
