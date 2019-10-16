package datastructures.concrete;

// import com.sun.java.util.jar.pack.ConstantPool;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

// import misc.exceptions.NotYetImplementedException;

/**
 * Note: For more info on the expected behavior of your methods:
 * @see datastructures.interfaces.IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    /**
     * Adds item to the back of the list, or if list is empty creates list out of one item.
     */
    @Override
    public void add(T item) {
        if (size > 0) {
            back.next = new Node<>(back, item);
            back = back.next;
        } else {
            front = new Node<>(item);
            back = front;
        }
        size++;
    }


    /**
     * Removes and returns last item in the list, if it exists; otherwise throws EmptyContainerException.
     */
    @Override
    public T remove() {
        if (size > 1) {
            T data = back.data;
            back = back.prev;
            back.next = null;
            size--;
            return data;
        } else if (size == 1) {
            T data = back.data;
            back = null;
            front = null;
            size--;
            return data;
        } else {
            throw new EmptyContainerException();
        }
    }


    /**
     * Returns the item at the given index, if it exists; otherwise throws IndexOutOfBoundsException.
     */
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (index > (size - 1) / 2) {
            Node<T> temp = back;
            for (int i = 0; i < (size - 1) - index; i++) {
                temp = temp.prev;
            }
            return temp.data;
        } else {
            Node<T> temp = front;
            for (int i = 0; i < index; i++) {
                temp = temp.next;
            }
            return temp.data;
        }

    }

    /**
     * Replace the value at the given index with the given item. Throws IndexOutOfBoundsException if not possible.
     */
    @Override
    public void set(int index, T item) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 1 || index == 0) {
            if (front.next == null) {
                front = new Node<>(item);
                back = front;
            } else {
                front = new Node<>(item, front.next);
                front.next.prev = front;
            }
        } else if (index == (size - 1)) {
            back = new Node<>(back.prev, item);
            back.prev.next = back;
        } else if (index < size / 2) {
            Node<T> temp = front;
            for (int i = 1; i < index; i++) {
                temp = temp.next;
            }
            temp.next = new Node<>(temp, item, temp.next.next);
            temp.next.next.prev = temp.next;
        } else {
            Node<T> temp = back;
            for (int i = 1; i < (size - 1) - index; i++) {
                temp = temp.prev;
            }
            temp.prev = new Node<>(temp.prev.prev, item, temp);
            temp.prev.prev.next = temp.prev;
        }
    }

    /** Insert the given item at the given index, and shift the rest of the list to fit. Throws
     * IndexOutOfBoundsException if that isn't possible. */
    @Override
    public void insert(int index, T item) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 0 || index == size) {
            this.add(item);
        } else if (index == 0) {
            front.prev = new Node<>(item, front);
            front = front.prev;
            size++;
        } else if (index < (size - 1) / 2) {
            Node<T> temp = front;
            for (int i = 1; i < index; i++) {
                temp = temp.next;
            }
            temp.next = new Node<>(temp, item, temp.next);
            temp.next.next.prev = temp.next;
            size++;
        } else {
            Node<T> temp = back;
            for (int i = 1; i < size - index; i++) {
                temp = temp.prev;

            }
            temp.prev = new Node<>(temp.prev, item, temp);
            temp.prev.prev.next = temp.prev;
            size++;
        }
    }

    /**
     * Deletes the item at the given index. If there are any elements located at a higher
     * index, shift them all down by one.
     *
     * @throws IndexOutOfBoundsException if the index < 0 or index >= this.size()
     */
    @Override
    public T delete(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 1) {
            T item = front.data;
            front = null;
            back = null;
            size--;
            return item;
        } else if (index == 0) {
            T item = front.data;
            front = front.next;
            front.prev = null;
            size--;
            return item;
        }
        else if (index == size - 1) {
            T item = back.data;
            back = back.prev;
            back.next = null;
            size--;
            return item;
        } else if (index <= (size - 1) / 2) {
            Node<T> temp = front;
            for (int i = 1; i < index; i++) {
                temp = temp.next;
            }
            T item = temp.next.data;
            temp.next = temp.next.next;
            temp.next.prev = temp;
            size--;
            return item;
        } else {
            Node<T> temp = back;
            for (int i = 1; i < index; i++) {
                temp = temp.prev;
            }
            T item = temp.prev.data;
            temp.prev = temp.prev.prev;
            temp.prev.next = temp;
            size--;
            return item;
        }
    }

    /**
     * Returns the index corresponding to the first occurrence of the given item
     * in the list.
     *
     * If the item does not exist in the list, return -1.
     */
    @Override
    public int indexOf(T item) {
        Node<T> temp = front;
        for (int i = 0; i < size; i++) {
            if (temp.compareTo(item) == 0) {
                return i;
            }
            temp = temp.next;
        }
        return -1;
    }

    /**
     * Returns the number of elements in the container.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns 'true' if this container contains no elements, and 'false' otherwise.
     */
    @Override
    public boolean contains(T other) {
        Node<T> temp = front;
        for (int i = 0; i < size; i++) {
            if (temp.compareTo(other) == 0) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> implements Comparable<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        public Node(Node<E> prev, E data) {
            this(prev, data, null);
        }

        public Node(E data, Node<E> next) {
            this(null, data, next);
        }

        @Override
        public int compareTo(E other) {
            if (other != null && other.equals(this.data)) {
                return 0;
            } else if (other == this.data) {
                return 0;
            } else {
                return -1;
            }
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            T item = current.data;
            current = current.next;
            return item;
        }
    }
}
