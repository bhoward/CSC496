package edu.depauw.algorithms;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Reimplementation of java.util.ArrayDeque for instructional purposes. Based on
 * the real thing, but with substantial editing. This shows only the essential
 * parts of the dynamic array reallocation implementation of a circular queue,
 * with no optimization cleverness.
 * 
 * The iterators are not fail-fast.
 * 
 * @author bhoward
 */
public class ArrayDeque<E> extends AbstractCollection<E> implements Deque<E> {
	private static final int DEFAULT_CAPACITY = 10;
	private static final double GROWTH_FACTOR = 1.5;

	private Object[] data;

	/**
	 * The index of the element at the head of the deque (which is the element that
	 * would be removed by remove() or pop()); or an arbitrary number 0 <= head <
	 * data.length equal to tail if the deque is empty.
	 */
	private int head;

	/**
	 * The index at which the next element would be added to the tail of the deque
	 * (via addLast(E), add(E), or push(E)).
	 */
	private int tail;

	/**
	 * The current number of elements in the deque. Note that head == tail could
	 * mean either that the deque is empty or full; we use size == 0 or size ==
	 * data.length to disambiguate.
	 */
	private int size;

	/**
	 * Constructs an empty array deque with an initial capacity sufficient to hold
	 * the specified number of elements.
	 *
	 * @param initialCapacity the initial capacity of the deque
	 * @throws IllegalArgumentException if the specified initial capacity is
	 *                                  negative
	 */
	public ArrayDeque(int initialCapacity) {
		if (initialCapacity >= 0) {
			this.data = new Object[Math.min(initialCapacity, 1)];
			this.head = 0;
			this.tail = 0;
			this.size = 0;
		} else {
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
	}

	/**
	 * Constructs an empty array deque with an initial capacity of DEFAULT_CAPACITY.
	 */
	public ArrayDeque() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Constructs a deque containing the elements of the specified collection, in
	 * the order they are returned by the collection's iterator. (The first element
	 * returned by the collection's iterator becomes the first element, or
	 * <i>front</i> of the deque.)
	 *
	 * @param c the collection whose elements are to be placed into the deque
	 * @throws NullPointerException if the specified collection is null
	 */
	public ArrayDeque(Collection<? extends E> c) {
		this(c.size());
		for (E e : c) {
			this.add(e);
		}
	}

	/**
	 * Increases the capacity of this deque by at least the given amount.
	 *
	 * @param needed the required minimum extra capacity; must be positive
	 */
	private void grow(int needed) {
		final int oldCapacity = data.length;
		final int newCapacity = Math.max(oldCapacity + needed, (int) (oldCapacity * GROWTH_FACTOR));
		data = Arrays.copyOf(data, newCapacity);

		// If active data wraps around end of array, shift the part from head to the end
		// up to new end
		if (tail <= head && size != 0) {
			int shift = newCapacity - oldCapacity;
			System.arraycopy(data, head, data, head + shift, oldCapacity - head);
			for (int i = head; i < head + shift; i++) {
				data[i] = null;
			}
			head += shift;
		}
	}

	/**
	 * Circularly increments i, mod modulus. Precondition and postcondition: 0 <= i
	 * < modulus.
	 */
	static final int inc(int i, int modulus) {
		return (i + 1) % modulus;
	}

	/**
	 * Adds j to i, mod modulus. Precondition: 0 <= i < modulus, 0 <= j.
	 */
	static final int add(int i, int j, int modulus) {
		return (i + j) % modulus;
	}

	/**
	 * Circularly decrements i, mod modulus. Precondition and postcondition: 0 <= i
	 * < modulus.
	 */
	static final int dec(int i, int modulus) {
		return (i - 1 + modulus) % modulus;
	}

	/**
	 * Subtracts j from i, mod modulus. Index i must be logically ahead of index j.
	 * Precondition: 0 <= i < modulus, 0 <= j < modulus.
	 * 
	 * @return the "circular distance" from j to i; corner case i == j is
	 *         disambiguated to "empty", returning 0.
	 */
	static final int sub(int i, int j, int modulus) {
		return (i - j + modulus) % modulus;
	}

	/**
	 * Returns element at array index i.
	 */
	@SuppressWarnings("unchecked")
	private final E elementAt(int i) {
		return (E) data[i];
	}

	/**
	 * Returns the number of elements in this deque.
	 *
	 * @return the number of elements in this deque
	 */
	public int size() {
		return size;
	}

	// The main insertion and extraction methods are addFirst,
	// addLast, pollFirst, pollLast. The other methods are defined in
	// terms of these.

	/**
	 * Inserts the specified element at the front of this deque.
	 *
	 * @param e the element to add
	 */
	public void addFirst(E e) {
		if (size == data.length) {
			grow(1);
		}
		head = dec(head, data.length);
		data[head] = e;
		size++;
	}

	/**
	 * Inserts the specified element at the end of this deque.
	 *
	 * <p>
	 * This method is equivalent to {@link #add}.
	 *
	 * @param e the element to add
	 */
	public void addLast(E e) {
		if (size == data.length) {
			grow(1);
		}
		data[tail] = e;
		tail = inc(tail, data.length);
		size++;
	}

	/**
	 * Inserts the specified element at the front of this deque.
	 *
	 * @param e the element to add
	 * @return {@code true} (as specified by {@link Deque#offerFirst})
	 * @throws NullPointerException if the specified element is null
	 */
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
	public boolean offerLast(E e) {
		addLast(e);
		return true;
	}

	public E pollFirst() {
		if (size == 0) {
			return null;
		}
		E e = elementAt(head);
		data[head] = null;
		head = inc(head, data.length);
		size--;
		return e;
	}

	public E pollLast() {
		if (size == 0) {
			return null;
		}
		tail = dec(tail, data.length);
		E e = elementAt(tail);
		data[tail] = null;
		size--;
		return e;
	}

	/**
	 * @throws NoSuchElementException {@inheritDoc}
	 */
	public E removeFirst() {
		if (size == 0) {
			throw new NoSuchElementException();
		}
		return pollFirst();
	}

	/**
	 * @throws NoSuchElementException {@inheritDoc}
	 */
	public E removeLast() {
		if (size == 0) {
			throw new NoSuchElementException();
		}
		return pollLast();
	}

	/**
	 * @throws NoSuchElementException {@inheritDoc}
	 */
	public E getFirst() {
		if (size == 0) {
			throw new NoSuchElementException();
		}
		return elementAt(head);
	}

	/**
	 * @throws NoSuchElementException {@inheritDoc}
	 */
	public E getLast() {
		if (size == 0) {
			throw new NoSuchElementException();
		}
		return elementAt(dec(tail, data.length));
	}

	public E peekFirst() {
		return elementAt(head);
	}

	public E peekLast() {
		return elementAt(dec(tail, data.length));
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
	public boolean add(E e) {
		addLast(e);
		return true;
	}

	/**
	 * Removes the element at the specified position in the elements array. Shifts
	 * elements from head to i forwards.
	 *
	 * <p>
	 * This method is called delete rather than remove to emphasize that its
	 * semantics differ from those of {@link List#remove(int)}.
	 *
	 * @return true if elements near tail moved backwards
	 */
	private boolean delete(int i) {
		final int capacity = data.length;
		// number of elements before to-be-deleted elt
		final int front = sub(i, head, capacity);
		// move front elements forwards
		if (head <= i) {
			System.arraycopy(data, head, data, head + 1, front);
		} else { // Wrap around
			System.arraycopy(data, 0, data, 1, i);
			data[0] = data[capacity - 1];
			System.arraycopy(data, head, data, head + 1, front - (i + 1));
		}
		data[head] = null;
		head = inc(head, capacity);
		size--;
		return false;
	}

	/**
	 * Removes the first occurrence of the specified element in this deque (when
	 * traversing the deque from head to tail). If the deque does not contain the
	 * element, it is unchanged. More formally, removes the first element {@code e}
	 * such that {@code Objects.equals(o, e)} (if such an element exists). Returns
	 * {@code true} if this deque contained the specified element (or equivalently,
	 * if this deque changed as a result of the call).
	 *
	 * @param o element to be removed from this deque, if present
	 * @return {@code true} if the deque contained the specified element
	 */
	public boolean removeFirstOccurrence(Object o) {
		Iterator<?> it = iterator();
		while (it.hasNext()) {
			if (Objects.equals(o, it.next())) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the last occurrence of the specified element in this deque (when
	 * traversing the deque from head to tail). If the deque does not contain the
	 * element, it is unchanged. More formally, removes the last element {@code e}
	 * such that {@code Objects.equals(o, e)} (if such an element exists). Returns
	 * {@code true} if this deque contained the specified element (or equivalently,
	 * if this deque changed as a result of the call).
	 *
	 * @param o element to be removed from this deque, if present
	 * @return {@code true} if the deque contained the specified element
	 */
	public boolean removeLastOccurrence(Object o) {
		Iterator<?> it = descendingIterator();
		while (it.hasNext()) {
			if (Objects.equals(o, it.next())) {
				it.remove();
				return true;
			}
		}
		return false;
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
	public E pop() {
		return removeFirst();
	}

	/**
	 * Returns an iterator over the elements in this deque. The elements will be
	 * ordered from first (head) to last (tail). This is the same order that
	 * elements would be dequeued (via successive calls to {@link #remove} or popped
	 * (via successive calls to {@link #pop}).
	 *
	 * @return an iterator over the elements in this deque
	 */
	public Iterator<E> iterator() {
		return new DeqIterator();
	}

	public Iterator<E> descendingIterator() {
		return new DescendingIterator();
	}

	private class DeqIterator implements Iterator<E> {
		/** Index of element to be returned by subsequent call to next. */
		int cursor;

		/** Number of elements yet to be returned. */
		int remaining = size();

		/**
		 * Index of element returned by most recent call to next. Reset to -1 if element
		 * is deleted by a call to remove.
		 */
		int lastRet = -1;

		DeqIterator() {
			cursor = head;
		}

		public final boolean hasNext() {
			return remaining > 0;
		}

		public E next() {
			if (remaining <= 0)
				throw new NoSuchElementException();
			E e = elementAt(cursor);
			lastRet = cursor;
			cursor = inc(cursor, data.length);
			remaining--;
			return e;
		}

		public void remove() {
			if (lastRet < 0)
				throw new IllegalStateException();
			delete(lastRet);
			lastRet = -1;
		}

		public void forEachRemaining(Consumer<? super E> action) {
			for (int i = 0; i < remaining; i++) {
				action.accept(elementAt(add(cursor, i, data.length)));
			}
		}
	}

	private class DescendingIterator extends DeqIterator {
		DescendingIterator() {
			cursor = dec(tail, data.length);
		}

		public final E next() {
			if (remaining <= 0)
				throw new NoSuchElementException();
			E e = elementAt(cursor);
			lastRet = cursor;
			cursor = dec(cursor, data.length);
			remaining--;
			return e;
		}

		public void remove() {
			if (lastRet < 0)
				throw new IllegalStateException();
			delete(lastRet);
			cursor = inc(cursor, data.length);
			lastRet = -1;
		}

		public final void forEachRemaining(Consumer<? super E> action) {
			for (int i = 0; i < remaining; i++) {
				action.accept(elementAt(sub(cursor, i, data.length)));
			}
		}
	}

	/** debugging */
	void checkInvariants() {
		try {
			int capacity = data.length;
			assert 0 <= head && head < capacity;
			assert 0 <= tail && tail < capacity;
			assert capacity > 0;
			assert size() <= capacity;
			assert data[tail] == null;
		} catch (Throwable t) {
			System.err.printf("head=%d tail=%d capacity=%d%n", head, tail, data.length);
			System.err.printf("elements=%s%n", Arrays.toString(data));
			throw t;
		}
	}
}