package edu.depauw.algorithms.details;

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
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public abstract class LinkedListDetails<E> extends AbstractSequentialList<E> implements List<E>, Deque<E> {
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
        addLast(e);
        return true;
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
        if (size() == 0) {
            return null;
        } else {
            return getFirst();
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
        if (size() == 0) {
            return null;
        } else {
            return getLast();
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
        if (size() == 0) {
            return null;
        } else {
            return removeFirst();
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
        if (size() == 0) {
            return null;
        } else {
            return removeLast();
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
        return removeFirstOccurrence(o);
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
        private final ListIterator<E> itr = listIterator(size());

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
    public LinkedListDetails<E> reversed() {
        return new ReverseOrderLinkedListView<>(this, super.reversed(), Deque.super.reversed());
    }

    @Override
    public abstract E getFirst();

    @Override
    public abstract E getLast();

    @Override
    public abstract E removeFirst();

    @Override
    public abstract E removeLast();

    // all operations are delegated to the reverse-ordered views.
    // TODO audit all overridden methods
    static class ReverseOrderLinkedListView<E> extends LinkedListDetails<E> implements java.io.Externalizable {
        final LinkedListDetails<E> list;
        final List<E> rlist;
        final Deque<E> rdeque;

        ReverseOrderLinkedListView(LinkedListDetails<E> list, List<E> rlist, Deque<E> rdeque) {
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
        public LinkedListDetails<E> reversed() {
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
