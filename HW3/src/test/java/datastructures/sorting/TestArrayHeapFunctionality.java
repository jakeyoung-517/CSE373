package datastructures.sorting;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import misc.BaseTest;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;
//import org.junit.Assert;
import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }


    protected IPriorityQueue<String> makeBasicHeap() {
        IPriorityQueue<String> list = new ArrayHeap<>();

        list.insert("a");
        list.insert("b");
        list.insert("c");
        list.insert("d");
        list.insert("e");

        return list;
    }


    @Test(timeout=SECOND)
    public void removeMinTest() {
        IPriorityQueue<String> heap = this.makeInstance();

        heap.insert("a");
        assertEquals("a", heap.removeMin());
        assertTrue(heap.isEmpty());

        heap = this.makeBasicHeap();
        assertEquals("a", heap.removeMin());
        assertEquals("b", heap.peekMin());
        assertEquals(4, heap.size());
    }


    @Test(timeout=SECOND)
    public void removeSeveralMinTest() {
        IPriorityQueue<String> heap = this.makeBasicHeap();
        assertEquals("a", heap.removeMin());
        assertEquals(4, heap.size());
        assertEquals("b", heap.removeMin());
        assertEquals(3, heap.size());
        assertEquals("c", heap.removeMin());
        assertEquals(2, heap.size());
        assertEquals("d", heap.removeMin());
        assertEquals(1, heap.size());
        assertEquals("e", heap.removeMin());
        assertEquals(0, heap.size());
    }


    @Test(timeout=SECOND)
    public void removeEmptyTest() {
        IPriorityQueue<String> heap = this.makeInstance();
        assertTrue(heap.isEmpty());
        try {
            heap.removeMin();
            fail();
        }catch (EmptyContainerException  e) {
            //intentionally left blank
        }
    }


    @Test(timeout=SECOND)
    public void peekMinTest() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.peekMin();
            fail();
        }catch (EmptyContainerException e) {
            // intentionally left blank
        }

        heap.insert(3);
        heap.insert(2);
        heap.insert(1);
        assertEquals(1, heap.peekMin());
    }


    @Test(timeout=SECOND)
    public void testBasicInsert() {
        IPriorityQueue<Integer> heap = this.makeInstance();

        heap.insert(3);
        assertEquals(1, heap.size());
        assertEquals(3, heap.peekMin());

        heap.insert(4);
        assertEquals(2, heap.size());
        assertEquals(3, heap.peekMin());

        heap.insert(2);
        assertEquals(3, heap.size());
        assertEquals(2, heap.peekMin());
    }


    @Test(timeout=SECOND)
    public void testInsertExceptions() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.insert(null);
            fail();
        } catch (IllegalArgumentException e) {
            // intentionally left blank
        }
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());
    }


    @Test(timeout=SECOND)
    public void insertManyTest() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 49; i >= 0; i--) {
            heap.insert(i);
            assertEquals(i, heap.peekMin());
        }
        assertEquals(50, heap.size());
    }


    @Test(timeout=SECOND)
    public void tripleTest() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(48);
        assertEquals(1, heap.size());
        assertEquals(48, heap.peekMin());

        heap.insert(47);
        assertEquals(2, heap.size());
        assertEquals(47, heap.peekMin());

        heap.insert(49);
        assertEquals(3, heap.size());
        assertEquals(47, heap.peekMin());

        assertEquals(47, heap.removeMin());
        heap.insert(47);

        for (int i = 100; i >= 50; i--) {
            heap.insert(i);
            assertEquals(47, heap.peekMin());
        }

        for (int i = 1; i < 47; i++) {
            heap.insert(i);
            assertEquals(1, heap.peekMin());
        }

        for (int i = 1; i < 101; i++) {
            assertEquals(i, heap.removeMin());
        }
    }


    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());
    }


    @Test(timeout=SECOND)
    public void isEmptyTest() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());

        heap.removeMin();
        assertEquals(0, heap.size());
        assertTrue(heap.isEmpty());
    }
}
