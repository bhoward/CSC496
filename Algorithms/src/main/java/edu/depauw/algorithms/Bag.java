package edu.depauw.algorithms;

import java.util.Iterator;

/**
 * A {@code Bag} is an unordered collection of elements, similar to a Set except
 * it allows duplicates. There is not currently an equivalent interface in the
 * Java Collections classes, and this is not meant to be a full implementation
 * on the order of the standard collections; it only has what is needed for
 * instructional use. If you need a full implementation, use the Multiset class
 * from Google's Guava library.
 * 
 * @param <E>
 */
public interface Bag<E> extends Iterable<E> {
    public int size();

    public boolean isEmpty();

    public boolean contains(Object o);

    public Iterator<E> iterator();

    public boolean add(E e);

    public boolean remove(Object o);

    public boolean addAll(Iterable<? extends E> c);

    public boolean removeAll(Iterable<? extends E> c);

    public void clear();
}
