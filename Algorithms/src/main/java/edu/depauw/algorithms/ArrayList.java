package edu.depauw.algorithms;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * Reimplementation of java.util.ArrayList for instructional purposes. Based on
 * the real thing, but with substantial editing. This shows only the essential
 * parts of the dynamic array reallocation implementation, with no optimization
 * cleverness.
 *
 * The iterators are not fail-fast.
 *
 * @author bhoward
 */
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess {
    private static final int DEFAULT_CAPACITY = 10;
    private static final double GROWTH_FACTOR = 1.5;

    private Object[] data;
    private int size;

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is
     *                                  negative
     */
    public ArrayList(int initialCapacity) {
        if (initialCapacity >= 0) {
            this.data = new Object[initialCapacity];
            this.size = 0;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    /**
     * Constructs an empty list with an initial capacity of DEFAULT_CAPACITY.
     */
    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs a list containing the elements of the specified collection, in the
     * order they are returned by the collection's iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public ArrayList(Collection<? extends E> c) {
        this(c.size());
        for (E e : c) {
            this.add(e);
        }
    }

    /**
     * Trims the capacity of this {@code ArrayList} instance to be the list's
     * current size. An application can use this operation to minimize the storage
     * of an {@code ArrayList} instance.
     */
    public void trimToSize() {
        if (size < data.length) {
            data = Arrays.copyOf(data, size);
        }
    }

    /**
     * Increases the capacity of this {@code ArrayList} instance, if necessary, to
     * ensure that it can hold at least the number of elements specified by the
     * minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            grow(minCapacity);
        }
    }

    /**
     * Increases the capacity to ensure that it can hold at least the number of
     * elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        final int oldCapacity = data.length;
        final int newCapacity = Math.max(minCapacity, (int) (oldCapacity * GROWTH_FACTOR)); // ignores overflow...
        data = Arrays.copyOf(data, newCapacity);
    }

    /**
     * Grow by at least one element.
     */
    private void grow() {
        grow(size + 1);
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

    @SuppressWarnings("unchecked")
    private E elementData(int index) {
        return (E) data[index];
    }

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
        return elementData(index);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NoSuchElementException {@inheritDoc}
     * @since 21
     */
    @Override
    public E getFirst() {
        return get(0);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NoSuchElementException {@inheritDoc}
     * @since 21
     */
    @Override
    public E getLast() {
        return get(size - 1);
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
        E oldValue = elementData(index);
        data[index] = element;
        return oldValue;
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
        if (size == data.length) {
            grow();
        }
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size = size + 1;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E e) {
        add(size, e);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @since 21
     */
    @Override
    public void addFirst(E element) {
        add(0, element);
    }

    /**
     * {@inheritDoc}
     *
     * @since 21
     */
    @Override
    public void addLast(E element) {
        add(size, element);
    }

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        Util.checkIndexExclusive(index, size);

        final E oldValue = elementData(index);

        size = size - 1;
        if (size > index) {
            System.arraycopy(data, index + 1, data, index, size - index);
        }
        data[size] = null;

        return oldValue;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NoSuchElementException {@inheritDoc}
     * @since 21
     */
    @Override
    public E removeFirst() {
        return remove(0);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NoSuchElementException {@inheritDoc}
     * @since 21
     */
    @Override
    public E removeLast() {
        return remove(size - 1);
    }

    void checkInvariants() {
        assert size >= 0;
        assert size == data.length || data[size] == null;
    }
}