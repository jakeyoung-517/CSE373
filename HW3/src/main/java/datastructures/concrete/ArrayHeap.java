package datastructures.concrete;

import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;


//import misc.exceptions.NotYetImplementedException;


/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;


    private int size;
    private int height; //default height 2 (0,1,2 levels)
    private int maxSize; //default maxSize is 21 (based on default height of 2)
    // Feel free to add more fields and constants.


    public ArrayHeap() {
        size = 0;
        height = 2; //default tree size
        maxSize = 21; //default 21 (based on default height of two)
        heap = makeArrayOfT(maxSize);
    }


    public ArrayHeap(IList<T> list) {
        size = list.size();
        height = 0;
        maxSize = 1;
        while (size > maxSize) {
            height++;
            maxSize += (int) Math.pow(NUM_CHILDREN, height);
        }
        heap = makeArrayOfT(maxSize);
        buildHeap(list);
    }


    private void buildHeap(IList<T> list) {
        for (int i = size - 1; i >= 0; i--) {
            heap[i] = list.get(i);
            percolateDown(i);
        }
    }

    /*
    this private helper method finds the max size for the current height of the heap.
    :return: int, max size of current height tree

    private int heightMaxSize(){
        maxSize = 0;
        for (int i = 0; i <= height; i++) {
            maxSize += (int) Math.pow(NUM_CHILDREN, i);
        }
        return maxSize;
    }
    //when resizing array, height++ and maxSize = heightMaxSize();
    */


    /**
    This private helper method will resize the heap array when the size == maxSize for the current height.
    pre: size == maxSize
    post: height++, maxSize = heightMaxSize(), size = size
     */
    private void resize(){
        if (size == maxSize){
            height++;
            maxSize += (int) Math.pow(NUM_CHILDREN, height);
            T[] newHeap = makeArrayOfT(maxSize);
            System.arraycopy(heap, 0, newHeap, 0, size);
            heap = newHeap;
        }
    }


    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    private T[] getChildren(int index) {
        T[] children = makeArrayOfT(4);
        for (int i = 0; i < 4; i++){
            if (index*4 + 1 + i < size) {
                children[i] = heap[(index) * 4 + 1 + i];
            } else {
                children[i] = null;
            }
        }
        return children;
    }


    private int min(T[] vals) {
        if (vals == null || vals.length == 0) {
            throw new RuntimeException();
        }
        if (vals[0] == null) {
            return 0;
        }
        T min = vals[0];
        int index = 0;
        for (int i = 0; i < 4; i++){
            if (vals[i] != null && min.compareTo(vals[i]) > 0) {
                min = vals[i];
                index = i;
            }
        }
        return index;
    }


    private void percolateDown(int index) { //fixes heap
        T[] children = getChildren(index);
        int mindex = min(children);
        if (children[mindex] == null || children[mindex].compareTo(heap[index]) >= 0) {
            return; // base case
        } else {
            T temp = children[mindex];
            heap[index*4 + 1 + mindex] = heap[index];
            heap[index] = temp;
            percolateDown(index*4 + 1 + mindex);
        }
    }


    @Override
    public T removeMin() {
        if (heap == null || size == 0) {
            throw new EmptyContainerException();
        }
        T data = heap[0];
        heap[0] = heap[size-1];
        heap[size-1] = null;
        size--;
        percolateDown(0);
        return data;
    }


    @Override
    public T peekMin() {
        if (heap == null || heap[0] == null) {
            throw new EmptyContainerException();
        }
        return heap[0];
    }


    private void percolateUp(int index){
        if (index == 0 || heap[index].compareTo(heap[(index - 1)/4]) >= 0){
            return;
        } else {
            T temp = heap[(index-1)/4];
            heap[(index-1)/4] = heap[index];
            heap[index] = temp;
            percolateUp((index-1)/4);
        }
    }


    @Override
    public void insert(T item) {
        if (item == null){
            throw new IllegalArgumentException();
        }
        if (size == maxSize){
            resize();
        }
        heap[size] = item;
        percolateUp(size);
        size++;
    }


    @Override
    public int size() {
        return size;
    }
}
