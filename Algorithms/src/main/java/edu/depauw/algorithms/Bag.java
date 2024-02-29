package edu.depauw.algorithms;

import java.util.Iterator;

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
