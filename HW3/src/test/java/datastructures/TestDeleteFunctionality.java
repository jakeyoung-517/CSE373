package datastructures;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

/**
 * This class should contain all the tests you implement to verify that
 * your 'delete' method behaves as specified.
 *
 * This test _extends_ your TestDoubleLinkedList class. This means that when
 * you run this test, not only will your tests run, all of the ones in
 * TestDoubleLinkedList will also run.
 *
 * This also means that you can use any helper methods defined within
 * TestDoubleLinkedList here. In particular, you may find using the
 * 'assertListMatches' and 'makeBasicList' helper methods to be useful.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDeleteFunctionality extends TestDoubleLinkedList {
    @Test(timeout=SECOND)
    public void testExample() {
        // Feel free to modify or delete this dummy test.
        assertTrue(true);
        assertEquals(3, 3);
    }

    @Test(timeout=SECOND)
    public void basicTestDelete() {
        IList<String> list = makeBasicList();
        String r = list.delete(1);
        this.assertListMatches(new String[] {"a", "c"}, list);
        assertEquals(r, "b");
    }
    @Test(timeout=SECOND)
    public void testDeleteFromFront() {
        IList<String> list = makeBasicList();
        list.add("d");
        list.add("e");
        String r = list.delete(0);
        this.assertListMatches(new String[] {"b", "c", "d", "e"}, list);
        assertEquals(r, "a");
    }

    @Test(timeout=SECOND)
    public void testDeleteFromBack() {
        IList<String> list = makeBasicList();
        list.add("d");
        list.add("e");
        String r = list.delete(4);
        this.assertListMatches(new String[] {"a", "b", "c", "d"}, list);
        assertEquals(r, "e");
    }

    @Test (timeout = SECOND)
    public void testDeleteCorrectlyThrowsIndexOutOfBoundsException() {
        IList<String> list = makeBasicList();
        try {
            list.delete(-1);
        }
        catch (IndexOutOfBoundsException ex) {
            // Do nothing if throws this exception
        }
        try {
            list.delete(list.size());
        }
        catch (IndexOutOfBoundsException ex) {
            // Do nothing if throws this exception
        }
    }

    @Test (timeout = SECOND)
    public void testDeleteEntireList() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 50; i++) {
            list.add(i);
        }
        for (int i = 0; i < 50; i++) {
            list.delete(0);
        }
        assertEquals(0, list.size());
    }
}
