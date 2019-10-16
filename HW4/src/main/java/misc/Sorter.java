package misc;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;


public class Sorter {
    /**
     * This method takes the input list and returns the top k elements
     * in sorted order.
     * <p>
     * So, the first element in the output list should be the "smallest"
     * element; the last element should be the "largest".
     * <p>
     * If the input list contains fewer than 'k' elements, return
     * a list containing all input.length elements in sorted order.
     * <p>
     * This method must not modify the input list.
     *
     * @throws IllegalArgumentException if k < 0
     * @throws IllegalArgumentException if input is null
     */
    public static <T extends Comparable<T>> IList<T> topKSort(int k, IList<T> input) {
        // Implementation notes:
        //
        // - This static method is a _generic method_. A generic method is similar to
        //   the generic methods we covered in class, except that the generic parameter
        //   is used only within this method.
        //
        //   You can implement a generic method in basically the same way you implement
        //   generic classes: just use the 'T' generic type as if it were a regular type.
        //
        // - You should implement this method by using your ArrayHeap for the sake of
        //   efficiency.

        if (k < 0 || input == null) {
            throw new IllegalArgumentException();
        }
        if (k > input.size()) {
            k = input.size();
        }
        if (k == 0) {
            return new DoubleLinkedList<>();
        }
        IPriorityQueue<T> heap = new ArrayHeap<>();
        for (T item: input) {
            if (heap.size() < k) {
                heap.insert(item);
            }
            else {
                if (item.compareTo(heap.peekMin()) > 0) {
                    heap.removeMin();
                    heap.insert(item);
                }
            }
        }
        IList<T> sorted = new DoubleLinkedList<>();

        for (int i = heap.size(); i > 0; i--) {
            sorted.add(heap.removeMin());
        }
        return sorted;
    }
}
