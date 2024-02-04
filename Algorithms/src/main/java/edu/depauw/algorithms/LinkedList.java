package edu.depauw.algorithms;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

import edu.depauw.algorithms.details.LinkedListDetails;

/**
 * Reimplementation of java.util.LinkedList for instructional purposes. Based on
 * the real thing, but with substantial editing. Instead of first and last
 * pointers, with null to indicate end of list or empty list, this uses a dummy
 * head node to hold the first and last pointers.
 *
 * The iterators are not fail-fast.
 *
 * @author bhoward
 */
public class LinkedList<E> extends LinkedListDetails<E> implements List<E>, Deque<E> {
    private int size = 0;

    /**
     * Dummy head node.
     */
    private Node<E> head;

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        /**
         * Construct a dummy head node.
         */
        Node() {
            this.item = null;
            this.next = this;
            this.prev = this;
        }

        /**
         * Link a new node between prev and next.
         *
         * @param prev
         * @param element
         * @param next
         */
        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
            next.prev = this;
            prev.next = this;
        }
    }

    /** debugging */
    void checkInvariants() {
        assert (size == 0) ? (head.next == head && head.prev == head)
                : (head.next.prev == head && head.prev.next == head);
    }

    /**
     * Constructs an empty list.
     */
    public LinkedList() {
        head = new Node<>();
    }

    /**
     * Constructs a list containing the elements of the specified collection, in the
     * order they are returned by the collection's iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public LinkedList(Collection<? extends E> c) {
        this();
        for (E e : c) {
            this.add(e);
        }
    }

    /**
     * Inserts element e before non-null Node succ.
     */
    private void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        new Node<>(succ.prev, e, succ);
        size++;
    }

    /**
     * Unlinks non-null node x.
     */
    private E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;

        x.prev.next = x.next;
        x.next.prev = x.prev;

        x.item = null;
        x.prev = null;
        x.next = null;

        size--;
        return element;
    }

    /**
     * Returns the first element in this list.
     *
     * @return the first element in this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return head.next.item;
    }

    /**
     * Returns the last element in this list.
     *
     * @return the last element in this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return head.prev.item;
    }

    /**
     * Removes and returns the first element from this list.
     *
     * @return the first element from this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return unlink(head.next);
    }

    /**
     * Removes and returns the last element from this list.
     *
     * @return the last element from this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return unlink(head.prev);
    }

    /**
     * Inserts the specified element at the beginning of this list.
     *
     * @param e the element to add
     */
    @Override
    public void addFirst(E e) {
        linkBefore(e, head.next);
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * <p>
     * This method is equivalent to {@link #add}.
     *
     * @param e the element to add
     */
    @Override
    public void addLast(E e) {
        linkBefore(e, head);
    }

    /**
     * Returns {@code true} if this list contains the specified element. More
     * formally, returns {@code true} if and only if this list contains at least one
     * element {@code e} such that {@code Objects.equals(o, e)}.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all of the elements from this list. The list will be empty after this
     * call returns.
     */
    @Override
    public void clear() {
        // Clearing all of the links between nodes is "unnecessary", but:
        // - helps a generational GC if the discarded nodes inhabit
        // more than one generation
        // - is sure to free memory even if there is a reachable Iterator
        for (Node<E> x = head.next; x != head;) {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        head.next = head;
        head.prev = head;
        size = 0;
    }

    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E get(int index) {
        Util.checkIndexExclusive(index, size);
        return node(index).item;
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        Util.checkIndexExclusive(index, size);
        Node<E> x = node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }

    /**
     * Inserts the specified element at the specified position in this list. Shifts
     * the element currently at that position (if any) and any subsequent elements
     * to the right (adds one to their indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        Util.checkIndexInclusive(index, size);

        if (index == size) {
            linkBefore(element, head);
        } else {
            linkBefore(element, node(index));
        }
    }

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices). Returns
     * the element that was removed from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        Util.checkIndexExclusive(index, size);
        return unlink(node(index));
    }

    /**
     * Returns the (non-head) Node at the specified element index.
     */
    private Node<E> node(int index) {
        // assert isElementIndex(index);

        if (index < (size >> 1)) {
            Node<E> x = head.next;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
            return x;
        } else {
            Node<E> x = head.prev;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;
            }
            return x;
        }
    }

    // Search Operations

    /**
     * Returns the index of the first occurrence of the specified element in this
     * list, or -1 if this list does not contain the element. More formally, returns
     * the lowest index {@code i} such that {@code Objects.equals(o, get(i))}, or -1
     * if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in this
     *         list, or -1 if this list does not contain the element
     */
    @Override
    public int indexOf(Object o) {
        int index = 0;
        for (Node<E> x = head.next; x != head; x = x.next) {
            if (Objects.equals(o, x.item)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element in this
     * list, or -1 if this list does not contain the element. More formally, returns
     * the highest index {@code i} such that {@code Objects.equals(o, get(i))}, or
     * -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in this
     *         list, or -1 if this list does not contain the element
     */
    @Override
    public int lastIndexOf(Object o) {
        int index = size;
        for (Node<E> x = head.prev; x != head; x = x.prev) {
            index--;
            if (Objects.equals(o, x.item)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Removes the first occurrence of the specified element in this list (when
     * traversing the list from head to tail). If the list does not contain the
     * element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    @Override
    public boolean removeFirstOccurrence(Object o) {
        for (Node<E> x = head.next; x != head; x = x.next) {
            if (Objects.equals(o, x.item)) {
                unlink(x);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the last occurrence of the specified element in this list (when
     * traversing the list from head to tail). If the list does not contain the
     * element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    @Override
    public boolean removeLastOccurrence(Object o) {
        for (Node<E> x = head.prev; x != head; x = x.prev) {
            if (Objects.equals(o, x.item)) {
                unlink(x);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list-iterator of the elements in this list (in proper sequence),
     * starting at the specified position in the list. Obeys the general contract of
     * {@code List.listIterator(int)}.
     * <p>
     *
     * @param index index of the first element to be returned from the list-iterator
     *              (by a call to {@code next})
     * @return a ListIterator of the elements in this list (in proper sequence),
     *         starting at the specified position in the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see List#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        Util.checkIndexInclusive(index, size);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        private Node<E> lastReturned;
        private Node<E> next;
        private int nextIndex;

        ListItr(int index) {
            // assert isPositionIndex(index);
            next = (index == size) ? head : node(index);
            nextIndex = index;
        }

        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }

            lastReturned = next.prev;
            next = next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned) {
                next = lastNext;
            } else {
                nextIndex--;
            }
            lastReturned = null;
        }

        @Override
        public void set(E e) {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            lastReturned.item = e;
        }

        @Override
        public void add(E e) {
            lastReturned = null;
            linkBefore(e, next);
            nextIndex++;
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            while (nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
        }
    }
}