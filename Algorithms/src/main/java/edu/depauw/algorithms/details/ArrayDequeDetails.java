package edu.depauw.algorithms.details;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Queue;

public abstract class ArrayDequeDetails<E> extends AbstractCollection<E> implements Deque<E> {
    /**
     * Inserts the specified element at the front of this deque.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Deque#offerFirst})
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this deque.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Deque#offerLast})
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E pollFirst() {
        if (size() == 0) {
            return null;
        }
        return removeFirst();
    }

    @Override
    public E pollLast() {
        if (size() == 0) {
            return null;
        }
        return removeLast();
    }

    @Override
    public E peekFirst() {
        if (size() == 0) {
            return null;
        }
        return getFirst();
    }

    @Override
    public E peekLast() {
        if (size() == 0) {
            return null;
        }
        return getLast();
    }

    /**
     * Inserts the specified element at the end of this deque.
     *
     * <p>
     * This method is equivalent to {@link #addLast}.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this deque.
     *
     * <p>
     * This method is equivalent to {@link #offerLast}.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Queue#offer})
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    /**
     * Retrieves and removes the head of the queue represented by this deque.
     *
     * This method differs from {@link #poll() poll()} only in that it throws an
     * exception if this deque is empty.
     *
     * <p>
     * This method is equivalent to {@link #removeFirst}.
     *
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public E remove() {
        return removeFirst();
    }

    /**
     * Retrieves and removes the head of the queue represented by this deque (in
     * other words, the first element of this deque), or returns {@code null} if
     * this deque is empty.
     *
     * <p>
     * This method is equivalent to {@link #pollFirst}.
     *
     * @return the head of the queue represented by this deque, or {@code null} if
     *         this deque is empty
     */
    @Override
    public E poll() {
        return pollFirst();
    }

    /**
     * Retrieves, but does not remove, the head of the queue represented by this
     * deque. This method differs from {@link #peek peek} only in that it throws an
     * exception if this deque is empty.
     *
     * <p>
     * This method is equivalent to {@link #getFirst}.
     *
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public E element() {
        return getFirst();
    }

    /**
     * Retrieves, but does not remove, the head of the queue represented by this
     * deque, or returns {@code null} if this deque is empty.
     *
     * <p>
     * This method is equivalent to {@link #peekFirst}.
     *
     * @return the head of the queue represented by this deque, or {@code null} if
     *         this deque is empty
     */
    @Override
    public E peek() {
        return peekFirst();
    }

    // *** Stack methods ***

    /**
     * Pushes an element onto the stack represented by this deque. In other words,
     * inserts the element at the front of this deque.
     *
     * <p>
     * This method is equivalent to {@link #addFirst}.
     *
     * @param e the element to push
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Pops an element from the stack represented by this deque. In other words,
     * removes and returns the first element of this deque.
     *
     * <p>
     * This method is equivalent to {@link #removeFirst()}.
     *
     * @return the element at the front of this deque (which is the top of the stack
     *         represented by this deque)
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public E pop() {
        return removeFirst();
    }

}
