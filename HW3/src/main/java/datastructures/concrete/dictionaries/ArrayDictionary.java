package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see datastructures.interfaces.IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field.
    // We will be inspecting it in our private tests.
    private Pair<K, V>[] pairs;

    // You may add extra fields or helper methods though!
    private int size;
    private int space;


    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictIterator<>(size, pairs);
    }


    private static class ArrayDictIterator<K, V> implements Iterator<KVPair<K, V>> {
        private int current;
        private int size;
        private Pair<K, V>[] pairs;

        public ArrayDictIterator(int size, Pair<K, V>[] pairs) {
            this.current = 0;
            this.size = size;
            this.pairs = pairs;
        }

        public boolean hasNext() {
            return (current < size);
        }

        @SuppressWarnings("unchecked")
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Pair curr = pairs[current++];
            return new KVPair(curr.key, curr.value);
        }
    }


    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(3);
        size = 0;
        space = 3;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        if ((!this.containsKey(key))) {
            throw new NoSuchKeyException();
        }
        for (int i = 0; i < this.size; i++) {
            if (pairs[i].compareTo(key) == 0) {
                return pairs[i].value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void put(K key, V value) {
        for (int i = 0; i < this.size; i++) {
            if (pairs[i].compareTo(key) == 0) {
                pairs[i].value = value;
                return;
            }
        }
        while (size >= space) {
            createBiggerArray();
        }
        pairs[size] = new Pair(key, value);
        size++;
    }

    private void createBiggerArray() {
        Pair<K, V>[] temp = makeArrayOfPairs(this.space * 2);
        for (int i = 0; i < this.size; i++) {
            temp[i] = pairs[i];
        }
        this.pairs = temp;
        space = (space * 2);
    }

    @Override
    public V remove(K key) {
        for (int i = 0; i < this.size; i++) {
            if (pairs[i].compareTo(key) == 0) {
                V val = pairs[i].value;
                pairs[i] = pairs[size - 1];
                size--;
                return val;
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        if (size > 0) {
            for (int i = 0; i < this.size; i++) {
                if (pairs[i].compareTo(key) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    private static class Pair<K, V> implements Comparable<K> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }

        @Override
        public int compareTo(K other) {
            if (other != null && other.equals(this.key)) {
                return 0;
            } else if (other == this.key) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
