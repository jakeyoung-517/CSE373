package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;


/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private int size;
    private IDictionary<T, Integer> items;
    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        pointers = new int[10];
        items = new ChainedHashDictionary<>();
        size = 0;
    }

    @Override
    public void makeSet(T item) {
        if (items.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        if (pointers.length <= size){
            int[] temp = new int[2*size];
            for (int i = 0; i < pointers.length; i++) {
                temp[i] = pointers[i];
            }
            pointers = temp;
        }
        items.put(item, size);
        pointers[size] = -1;
        size++;
    }


    @Override
    public int findSet(T item) {
        if (!items.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int index = items.get(item);
        return findRoot(index);
    }

    private int findRoot(int item) {
        if (pointers[item] < 0) {
            return item;
        } else {
            int next = pointers[item];
            pointers[item] = findRoot(next);
        }
        return pointers[item];
    }

    @Override
    public void union(T item1, T item2) {
        if (!items.containsKey(item1) || !items.containsKey(item2)) {
            throw new IllegalArgumentException();
        }
        int rep1 = findSet(item1);
        int rep2 = findSet(item2);
        int rank1 = pointers[rep1];
        int rank2 = pointers[rep2];
        if (rep1 == rep2) {
            return;
        }
        if (rank1 < rank2) {
            pointers[rep1] = rank1 + rank2;
            pointers[rep2] = rep1;
        } else {
            pointers[rep2] = rank1 + rank2;
            pointers[rep1] = rep2;
        }

    }
}
