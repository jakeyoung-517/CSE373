package datastructures.sorting;

import misc.BaseTest;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.Sorter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.fail;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestSorterFunctionality extends BaseTest {
    @Test(timeout=SECOND)
    public void testSimpleUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Sorter.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }


    @Test(timeout=SECOND)
    public void testRandomSort() {
        Random rng = new Random();
        IList<Integer> list = new DoubleLinkedList<>();
        List<Integer> javlist = new ArrayList<>();
        int rand;
        for (int i = 0; i < 100; i++) {
            rand = rng.nextInt(100);
            list.add(rand);
            javlist.add(rand);
        }

        Collections.sort(javlist);
        IList<Integer> top = Sorter.topKSort(5, list);
        assertEquals(5, top.size());

        for (int i = 0; i < top.size(); i++) {
            assertEquals(javlist.get(javlist.size()-top.size()+i), top.get(i));
        }
    }


    @Test(timeout=SECOND)
    public void testExceptions() {
        IList<Integer> list = null;
        IList<Integer> top;
        try {
            list = Sorter.topKSort(5, list);
            fail();
        } catch (IllegalArgumentException e) {
            // intentionally left blank
        }

        list = new DoubleLinkedList<>();
        list.add(1);
        try {
            list = Sorter.topKSort(-1, list);
            fail();
        } catch (IllegalArgumentException e) {
            // intentionally left blank
        }
    }

    @Test(timeout=SECOND)
    public void testZeroK() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        assertEquals(0, Sorter.topKSort(0, list).size());
    }

    @Test(timeout=SECOND)
    public void testKGreaterThanN() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Sorter.topKSort(23, list);
        assertEquals(20, top.size());

    }
}
