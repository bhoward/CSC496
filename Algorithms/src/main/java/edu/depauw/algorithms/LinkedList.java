package edu.depauw.algorithms;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

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
public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E> {
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

    void dataStructureInvariants() {
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
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        new Node<>(succ.prev, e, succ);
        size++;
    }

    /**
     * Unlinks non-null node x.
     */
    E unlink(Node<E> x) {
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
     * Appends the specified element to the end of this list.
     *
     * <p>
     * This method is equivalent to {@link #addLast}.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E e) {
        linkBefore(e, head);
        return true;
    }

    /**
     * Removes the first occurrence of the specified element from this list, if it
     * is present. If this list does not contain the element, it is unchanged. More
     * formally, removes the element with the lowest index {@code i} such that
     * {@code Objects.equals(o, get(i))} (if such an element exists). Returns
     * {@code true} if this list contained the specified element (or equivalently,
     * if this list changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        for (Node<E> x = head.next; x != head; x = x.next) {
            if (Objects.equals(o, x.item)) {
                unlink(x);
                return true;
            }
        }
        return false;
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
    Node<E> node(int index) {
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
     * Returns an iterator over the elements in this list (in proper sequence).
     * <p>
     *
     * This implementation merely returns a list iterator over the list.
     *
     * @return an iterator over the elements in this list (in proper sequence)
     */
    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec This implementation returns {@code listIterator(0)}.
     *
     * @see #listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
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

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     */
    @Override
    public E peek() {
        return peekFirst();
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     */
    @Override
    public E element() {
        return getFirst();
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     */
    @Override
    public E poll() {
        return pollFirst();
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     */
    @Override
    public E remove() {
        return removeFirst();
    }

    /**
     * Adds the specified element as the tail (last element) of this list.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Queue#offer})
     * @since 1.5
     */
    @Override
    public boolean offer(E e) {
        return add(e);
    }

    // Deque operations
    /**
     * Inserts the specified element at the front of this list.
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link Deque#offerFirst})
     * @since 1.6
     */
    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this list.
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link Deque#offerLast})
     * @since 1.6
     */
    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * Retrieves, but does not remove, the first element of this list, or returns
     * {@code null} if this list is empty.
     *
     * @return the first element of this list, or {@code null} if this list is empty
     * @since 1.6
     */
    @Override
    public E peekFirst() {
        if (size == 0) {
            return null;
        } else {
            return head.next.item;
        }
    }

    /**
     * Retrieves, but does not remove, the last element of this list, or returns
     * {@code null} if this list is empty.
     *
     * @return the last element of this list, or {@code null} if this list is empty
     * @since 1.6
     */
    @Override
    public E peekLast() {
        if (size == 0) {
            return null;
        } else {
            return head.prev.item;
        }
    }

    /**
     * Retrieves and removes the first element of this list, or returns {@code null}
     * if this list is empty.
     *
     * @return the first element of this list, or {@code null} if this list is empty
     * @since 1.6
     */
    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        } else {
            return unlink(head.next);
        }
    }

    /**
     * Retrieves and removes the last element of this list, or returns {@code null}
     * if this list is empty.
     *
     * @return the last element of this list, or {@code null} if this list is empty
     * @since 1.6
     */
    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        } else {
            return unlink(head.prev);
        }
    }

    /**
     * Pushes an element onto the stack represented by this list. In other words,
     * inserts the element at the front of this list.
     *
     * <p>
     * This method is equivalent to {@link #addFirst}.
     *
     * @param e the element to push
     * @since 1.6
     */
    @Override
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Pops an element from the stack represented by this list. In other words,
     * removes and returns the first element of this list.
     *
     * <p>
     * This method is equivalent to {@link #removeFirst()}.
     *
     * @return the element at the front of this list (which is the top of the stack
     *         represented by this list)
     * @throws NoSuchElementException if this list is empty
     * @since 1.6
     */
    @Override
    public E pop() {
        return removeFirst();
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
        return remove(o);
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
     * @since 1.6
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */
    private class DescendingIterator implements Iterator<E> {
        private final ListItr itr = new ListItr(size());

        @Override
        public boolean hasNext() {
            return itr.hasPrevious();
        }

        @Override
        public E next() {
            return itr.previous();
        }

        @Override
        public void remove() {
            itr.remove();
        }
    }

    // The following is copied from the implementation of java.util.LinkedList, to
    // work around a Java 21 issue: https://inside.java/2023/05/12/quality-heads-up/
    /**
     * {@inheritDoc}
     * <p>
     * Modifications to the reversed view are permitted and will be propagated to
     * this list. In addition, modifications to this list will be visible in the
     * reversed view.
     *
     * @return {@inheritDoc}
     * @since 21
     */
    @Override
    public LinkedList<E> reversed() {
        return new ReverseOrderLinkedListView<>(this, super.reversed(), Deque.super.reversed());
    }

    // all operations are delegated to the reverse-ordered views.
    // TODO audit all overridden methods
    static class ReverseOrderLinkedListView<E> extends LinkedList<E> implements java.io.Externalizable {
        final LinkedList<E> list;
        final List<E> rlist;
        final Deque<E> rdeque;

        ReverseOrderLinkedListView(LinkedList<E> list, List<E> rlist, Deque<E> rdeque) {
            this.list = list;
            this.rlist = rlist;
            this.rdeque = rdeque;
        }

        @Override
        public String toString() {
            return rlist.toString();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return rlist.retainAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return rlist.removeAll(c);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return rlist.containsAll(c);
        }

        @Override
        public boolean isEmpty() {
            return rlist.isEmpty();
        }

        @Override
        public Stream<E> parallelStream() {
            return rlist.parallelStream();
        }

        @Override
        public Stream<E> stream() {
            return rlist.stream();
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            return rlist.removeIf(filter);
        }

        @Override
        public <T> T[] toArray(IntFunction<T[]> generator) {
            return rlist.toArray(generator);
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            rlist.forEach(action);
        }

        @Override
        public Iterator<E> iterator() {
            return rlist.iterator();
        }

        @Override
        public int hashCode() {
            return rlist.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return rlist.equals(o);
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return rlist.subList(fromIndex, toIndex);
        }

        @Override
        public ListIterator<E> listIterator() {
            return rlist.listIterator();
        }

        @Override
        public void sort(Comparator<? super E> c) {
            rlist.sort(c);
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            rlist.replaceAll(operator);
        }

        @Override
        public LinkedList<E> reversed() {
            return list;
        }

        @Override
        public Spliterator<E> spliterator() {
            return rlist.spliterator();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return rlist.toArray(a);
        }

        @Override
        public Object[] toArray() {
            return rlist.toArray();
        }

        @Override
        public Iterator<E> descendingIterator() {
            return rdeque.descendingIterator();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return rlist.listIterator(index);
        }

        @Override
        public boolean removeLastOccurrence(Object o) {
            return rdeque.removeLastOccurrence(o);
        }

        @Override
        public boolean removeFirstOccurrence(Object o) {
            return rdeque.removeFirstOccurrence(o);
        }

        @Override
        public E pop() {
            return rdeque.pop();
        }

        @Override
        public void push(E e) {
            rdeque.push(e);
        }

        @Override
        public E pollLast() {
            return rdeque.pollLast();
        }

        @Override
        public E pollFirst() {
            return rdeque.pollFirst();
        }

        @Override
        public E peekLast() {
            return rdeque.peekLast();
        }

        @Override
        public E peekFirst() {
            return rdeque.peekFirst();
        }

        @Override
        public boolean offerLast(E e) {
            return rdeque.offerLast(e);
        }

        @Override
        public boolean offerFirst(E e) {
            return rdeque.offerFirst(e);
        }

        @Override
        public boolean offer(E e) {
            return rdeque.offer(e);
        }

        @Override
        public E remove() {
            return rdeque.remove();
        }

        @Override
        public E poll() {
            return rdeque.poll();
        }

        @Override
        public E element() {
            return rdeque.element();
        }

        @Override
        public E peek() {
            return rdeque.peek();
        }

        @Override
        public int lastIndexOf(Object o) {
            return rlist.lastIndexOf(o);
        }

        @Override
        public int indexOf(Object o) {
            return rlist.indexOf(o);
        }

        @Override
        public E remove(int index) {
            return rlist.remove(index);
        }

        @Override
        public void add(int index, E element) {
            rlist.add(index, element);
        }

        @Override
        public E set(int index, E element) {
            return rlist.set(index, element);
        }

        @Override
        public E get(int index) {
            return rlist.get(index);
        }

        @Override
        public void clear() {
            rlist.clear();
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return rlist.addAll(index, c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return rlist.addAll(c);
        }

        @Override
        public boolean remove(Object o) {
            return rlist.remove(o);
        }

        @Override
        public boolean add(E e) {
            return rlist.add(e);
        }

        @Override
        public int size() {
            return rlist.size();
        }

        @Override
        public boolean contains(Object o) {
            return rlist.contains(o);
        }

        @Override
        public void addLast(E e) {
            rdeque.addLast(e);
        }

        @Override
        public void addFirst(E e) {
            rdeque.addFirst(e);
        }

        @Override
        public E removeLast() {
            return rdeque.removeLast();
        }

        @Override
        public E removeFirst() {
            return rdeque.removeFirst();
        }

        @Override
        public E getLast() {
            return rdeque.getLast();
        }

        @Override
        public E getFirst() {
            return rdeque.getFirst();
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            throw new java.io.InvalidObjectException("not serializable");
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            throw new java.io.InvalidObjectException("not serializable");
        }
    }
}