package datastructures.sorting;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import misc.BaseTest;
import misc.Sorter;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapAndSorterStress extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }


    @Test(timeout=10*SECOND)
    public void insertAndRemoveMany() {
        Random rng = new Random();
        IPriorityQueue<Integer> heap = new ArrayHeap<>();
        PriorityQueue<Integer> javheap = new PriorityQueue<>();

        int rand;
        for (int i = 0; i < 1000000; i++) {
            rand = rng.nextInt(1000000);
            heap.insert(rand);
            javheap.add(rand);
            assertEquals(javheap.peek(), heap.peekMin());
        }

        for (int i = 0; i < 1000000; i++) {
            assertEquals(javheap.poll(), heap.removeMin());
        }

    }


    @Test(timeout=10*SECOND)
    public void sortBigList() {
        Random rng = new Random();
        IList<Integer> list = new DoubleLinkedList<>();
        List<Integer> javlist = new ArrayList<>();
        int rand;
        for (int i = 0; i < 100000; i++) {
            rand = rng.nextInt(100000);
            list.add(rand);
            javlist.add(rand);
        }
        list = Sorter.topKSort(100000, list);
        Collections.sort(javlist);

        for (int i = 0; i < list.size(); i++) {
            assertEquals(javlist.remove(javlist.size()-1), list.remove());
        }

        assertTrue(true);
    }
}
