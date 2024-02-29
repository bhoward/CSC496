package edu.depauw.algorithms;

import java.util.Iterator;

public class TreeBag<E> implements Bag<E> {
    private TreeMap<E, Integer> map;
    private int size;
    
    public TreeBag() {
        this.map = new TreeMap<>();
        this.size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o) && map.get(o) > 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new TreeBagIterator();
    }

    @Override
    public boolean add(E e) {
        map.merge(e, 1, (a, b) -> a + b);
        size++;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        if (map.containsKey(o) && map.get(o) > 0) {
            map.put((E) o, map.get(o) - 1);
            size--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addAll(Iterable<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            this.add(e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Iterable<? extends E> c) {
        boolean modified = false;
        Iterator<?> it = c.iterator();
        while (it.hasNext()) {
            var x = it.next();
            if (this.contains(x)) {
                this.remove(x);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        map.clear();
    }

    private class TreeBagIterator implements Iterator<E> {
        private Iterator<E> it;
        private E current;
        private int n;
        private int count;
        private int total;

        public TreeBagIterator() {
            this.it = map.keyIterator();
            this.current = (it.hasNext()) ? it.next() : null;
            this.n = (current != null) ? map.get(current) : 0;
            this.count = 0;
            this.total = 0;
        }

        @Override
        public boolean hasNext() {
            return total < size;
        }

        @Override
        public E next() {
            while (count >= n) {
                current = it.next();
                n = map.get(current);
                count = 0;
            }
            count++;
            total++;
            return current;
        }
    }
}
