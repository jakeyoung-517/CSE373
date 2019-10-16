package datastructures;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Random;


/**
 * This file should contain any tests that check and make sure your
 * delete method is efficient.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDeleteStress extends TestDoubleLinkedList {
    @Test(timeout=5*SECOND)
    public void testDeleteFromEndIsEfficient() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        int s = list.size();
        for (int i = 0; i < 10000; i++) {
            list.add(-1);
            list.delete(s);
        }
    }

    @Test(timeout=5*SECOND)
    public void testDeleteFromFrontIsEfficient() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        for (int i = 0; i < 10000; i++) {
            list.add(-1);
            list.delete(0);
        }
    }


    @Test (timeout = 5 * SECOND)
    public void testDeleteAtRandomIsEfficient() {
        Random r = new Random();
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 5000; i++) {
            list.add(i);
        }
        assertEquals(5000, list.size());
        for (int i = 5000; i > 0; i--) {
            list.delete(r.nextInt(i));
        }
    }

}
