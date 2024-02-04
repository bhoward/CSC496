package edu.depauw.algorithms.details;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public abstract class ArrayListDetails<E> extends AbstractList<E> implements List<E>, RandomAccess {
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
        return get(size() - 1);
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E e) {
        add(size(), e);
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
        add(size(), element);
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
        return remove(size() - 1);
    }
}
