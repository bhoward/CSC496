package edu.depauw.algorithms;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import edu.depauw.algorithms.details.AbstractSortedSet;

/**
 * Red-Black tree implementation based on Sedgewick, "Algorithms" (4th edition).
 *
 * @param <K>
 * @param <V>
 */
public class TreeMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V> {
    private Comparator<? super K> comparator;
    private Entry<K, V> root;
    private int size;

    private static class Entry<K, V> implements Map.Entry<K, V> {
        private K key;
        private V value;
        private Entry<K, V> left, right;
        private boolean red;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.red = true;
        }

        public Entry(Entry<K, V> entry) {
            this.key = entry.key;
            this.value = entry.value;
            this.left = null;
            this.right = null;
            this.red = false;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    @Override
    public Comparator<? super K> comparator() {
        return comparator;
    }

    @SuppressWarnings("unchecked")
    public TreeMap() {
        this.comparator = (Comparator<? super K>) Comparator.naturalOrder();
        this.root = null;
        this.size = 0;
    }

    public TreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.root = null;
        this.size = 0;
    }

    public TreeMap(Map<? extends K, ? extends V> map) {
        this();
        putAll(map);
    }

    @Override
    public boolean containsKey(Object key) {
        @SuppressWarnings("unchecked")
        var node = getEntry(root, (K) key);
        return node != null;
    }

    @Override
    public V get(Object key) {
        @SuppressWarnings("unchecked")
        var node = getEntry(root, (K) key);
        if (node == null) {
            return null;
        } else {
            return node.value;
        }
    }

    private Entry<K, V> getEntry(Entry<K, V> node, K key) {
        if (node != null) {
            int compare = comparator.compare(key, node.key);
            if (compare < 0) {
                return getEntry(node.left, key);
            } else if (compare > 0) {
                return getEntry(node.right, key);
            } else {
                return node;
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        var node = getEntry(root, key);
        if (node != null) {
            var oldValue = node.value;
            node.value = value;
            return oldValue;
        }

        // It is a minor inefficiency that we re-traverse from the root if the key was
        // not found, but the recursive code is cleaner than more efficient alternative.
        root = putNew(root, key, value);
        root.red = false;
        size++;
        return null;
    }

    /**
     * Insert a new Entry for the given key, value. Precondition: key does not
     * already exist in the tree under node.
     *
     * Red-black tree maintenance based on Sedgewick.
     *
     * @param node
     * @param key
     * @param value
     * @return
     */
    private Entry<K, V> putNew(Entry<K, V> node, K key, V value) {
        if (node != null) {
            int compare = comparator.compare(key, node.key);
            if (compare < 0) {
                node.left = putNew(node.left, key, value);
            } else {
                node.right = putNew(node.right, key, value);
            }

            if (isRed(node.right) && isBlack(node.left)) {
                node = rotateLeft(node);
            }
            if (isRed(node.left) && isRed(node.left.left)) {
                node = rotateRight(node);
            }
            if (isRed(node.left) && isRed(node.right)) {
                flipColors(node);
            }

            return node;
        }

        return new Entry<>(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object key) {
        var node = getEntry(root, (K) key);
        if (node != null) {
            var oldValue = node.value;

            // if both children of root are black, set root to red
            if (isBlack(root.left) && isBlack(root.right)) {
                root.red = true;
            }

            root = delete(root, (K) key);
            if (!isEmpty()) {
                root.red = false;
            }
            size--;
            return oldValue;
        }

        return null;
    }

    // delete the key-value pair with the given key rooted at h
    private Entry<K, V> delete(Entry<K, V> node, K key) {
        int compare = comparator.compare(key, node.key);
        if (compare < 0) {
            if (isBlack(node.left) && isBlack(node.left.left)) {
                node = moveRedLeft(node);
            }
            node.left = delete(node.left, key);
        } else {
            if (isRed(node.left)) {
                node = rotateRight(node);
            }
            if (compare == 0 && (node.right == null)) {
                return null;
            }
            if (isBlack(node.right) && isBlack(node.right.left)) {
                node = moveRedRight(node);
            }
            if (compare == 0) {
                var x = min(node.right);
                node.key = x.key;
                node.value = x.value;
                node.right = deleteMin(node.right);
            } else {
                node.right = delete(node.right, key);
            }
        }
        return balance(node);
    }

    @Override
    public Entry<K, V> firstEntry() {
        if (root == null) {
            return null;
        }

        return min(root);
    }

    private Entry<K, V> min(Entry<K, V> node) {
        if (node.left == null) {
            return node;
        }

        return min(node.left);
    }

    @Override
    public Entry<K, V> lastEntry() {
        if (root == null) {
            return null;
        }

        return max(root);
    }

    private Entry<K, V> max(Entry<K, V> node) {
        if (node.right == null) {
            return node;
        }

        return max(node.right);
    }

    @Override
    public Entry<K, V> pollFirstEntry() {
        if (root == null) {
            return null;
        }

        var oldEntry = new Entry<>(firstEntry());

        if (isBlack(root.left) && isBlack(root.right)) {
            root.red = true;
        }
        root = deleteMin(root);
        if (!isEmpty()) {
            root.red = false;
        }
        size--;

        return oldEntry;
    }

    private Entry<K, V> deleteMin(Entry<K, V> node) {
        if (node.left == null) {
            return null;
        }
        if (isBlack(node.left) && isBlack(node.left.left)) {
            node = moveRedLeft(node);
        }
        node.left = deleteMin(node.left);
        return balance(node);
    }

    @Override
    public Entry<K, V> pollLastEntry() {
        if (root == null) {
            return null;
        }

        var oldEntry = new Entry<>(lastEntry());

        if (isBlack(root.left) && isBlack(root.right)) {
            root.red = true;
        }
        root = deleteMax(root);
        if (!isEmpty()) {
            root.red = false;
        }
        size--;

        return oldEntry;
    }

    // delete the key-value pair with the maximum key rooted at node
    private Entry<K, V> deleteMax(Entry<K, V> node) {
        if (isRed(node.left)) {
            node = rotateRight(node);
        }

        if (node.right == null) {
            return null;
        }

        if (isBlack(node.right) && isBlack(node.right.left)) {
            node = moveRedRight(node);
        }

        node.right = deleteMax(node.right);

        return balance(node);
    }

    // flip the colors of a Entry<K, V> and its two children
    void flipColors(Entry<K, V> node) {
        node.red = !node.red;
        node.left.red = !node.left.red;
        node.right.red = !node.right.red;
    }

    // make a left-leaning link lean to the right
    private Entry<K, V> rotateRight(Entry<K, V> node) {
        var x = node.left;
        node.left = x.right;
        x.right = node;
        x.red = node.red;
        node.red = true;
        return x;
    }

    // make a right-leaning link lean to the left
    private Entry<K, V> rotateLeft(Entry<K, V> node) {
        var x = node.right;
        node.right = x.left;
        x.left = node;
        x.red = node.red;
        node.red = true;
        return x;
    }

    // Assuming that node is red and both node.left and node.left.left
    // are black, make node.left or one of its children red.
    private Entry<K, V> moveRedLeft(Entry<K, V> node) {
        flipColors(node);
        if (isRed(node.right.left)) {
            node.right = rotateRight(node.right);
            node = rotateLeft(node);
            flipColors(node);
        }
        return node;
    }

    // Assuming that node is red and both node.right and node.right.left
    // are black, make node.right or one of its children red.
    private Entry<K, V> moveRedRight(Entry<K, V> node) {
        flipColors(node);
        if (isRed(node.left.left)) {
            node = rotateRight(node);
            flipColors(node);
        }
        return node;
    }

    // restore red-black tree invariant
    private Entry<K, V> balance(Entry<K, V> node) {
        if (isRed(node.right) && isBlack(node.left)) {
            node = rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }
        return node;
    }

    private boolean isRed(Entry<K, V> node) {
        return node != null && node.red;
    }

    private boolean isBlack(Entry<K, V> node) {
        return node == null || !node.red;
    }

    // TODO use the java.util.TreeMap versions of these, and put in details package
    @Override
	public Set<K> keySet() {
		return new AbstractSortedSet<K>() {
			@Override
			public int size() {
				return size;
			}

			@Override
			public Iterator<K> iterator() {
				return new KeySetIterator(firstEntry());
			}
			
			@Override
			public Comparator<? super K> comparator() {
				return comparator;
			}

			@Override
			public K first() {
				return firstKey();
			}

			@Override
			public K last() {
				return lastKey();
			}

			@Override
			public SortedSet<K> subSet(K fromElement, K toElement) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SortedSet<K> headSet(K toElement) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SortedSet<K> tailSet(K fromElement) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSortedSet<>() {
            @Override
            public int size() {
                return size;
            }

            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new EntrySetIterator(firstEntry());
            }

			@Override
			public Comparator<? super Map.Entry<K, V>> comparator() {
				return (e1, e2) -> comparator.compare(e1.getKey(), e2.getKey());
			}

			@Override
			public Map.Entry<K, V> first() {
				return firstEntry();
			}

			@Override
			public Map.Entry<K, V> last() {
				return lastEntry();
			}

			@Override
			public SortedSet<Map.Entry<K, V>> subSet(Map.Entry<K, V> fromElement,
					Map.Entry<K, V> toElement) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SortedSet<Map.Entry<K, V>> headSet(Map.Entry<K, V> toElement) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SortedSet<Map.Entry<K, V>> tailSet(Map.Entry<K, V> fromElement) {
				// TODO Auto-generated method stub
				return null;
			}
        };
    }

    private class KeySetIterator implements Iterator<K> {
        private Entry<K, V> next;

        public KeySetIterator(Entry<K, V> entry) {
            this.next = entry;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public K next() {
            var result = next.key;
            next = successor(next);
            return result;
        }
    }

    private class EntrySetIterator implements Iterator<Map.Entry<K, V>> {
        private Entry<K, V> next;

        public EntrySetIterator(Entry<K, V> entry) {
            this.next = entry;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            var result = next;
            next = successor(next);
            return result;
        }
    }

    // Based on https://gist.github.com/rcaloras/36f9e5f94f4334e0827c5b52ec0d8115
    private Entry<K, V> successor(Entry<K, V> node) {
        if (node.right != null) {
            return min(node.right);
        }

        Entry<K, V> result = null;
        var n = root;
        while (n != node) {
            if (comparator.compare(node.key, n.key) < 0) {
                result = n;
                n = n.left;
            } else {
                n = n.right;
            }
        }
        return result;
    }

    private K key(Entry<K, V> node) {
        if (node == null) {
            return null;
        } else {
            return node.key;
        }
    }

    @Override
    public K firstKey() {
        return key(firstEntry());
    }

    @Override
    public K lastKey() {
        return key(lastEntry());
    }

    @Override
    public Entry<K, V> lowerEntry(K key) {
        if (root == null) {
            return null;
        }

        return lower(root, key);
    }

    // the largest key in the subtree rooted at x strictly less than the given
    // key
    private Entry<K, V> lower(Entry<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int compare = comparator.compare(key, node.key);
        if (compare <= 0) {
            return lower(node.left, key);
        }
        var t = lower(node.right, key);
        if (t != null) {
            return t;
        } else {
            return node;
        }
    }

    @Override
    public K lowerKey(K key) {
        return key(lowerEntry(key));
    }

    @Override
    public Entry<K, V> floorEntry(K key) {
        if (root == null) {
            return null;
        }

        return floor(root, key);
    }

    // the largest key in the subtree rooted at x less than or equal to the given
    // key
    private Entry<K, V> floor(Entry<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int compare = comparator.compare(key, node.key);
        if (compare == 0) {
            return node;
        }
        if (compare < 0) {
            return floor(node.left, key);
        }
        var t = floor(node.right, key);
        if (t != null) {
            return t;
        } else {
            return node;
        }
    }

    @Override
    public K floorKey(K key) {
        return key(floorEntry(key));
    }

    @Override
    public Entry<K, V> ceilingEntry(K key) {
        if (root == null) {
            return null;
        }

        return ceiling(root, key);
    }

    // the smallest entry in the subtree rooted at x greater than or equal to the
    // given key
    private Entry<K, V> ceiling(Entry<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int compare = comparator.compare(key, node.key);
        if (compare == 0) {
            return node;
        }
        if (compare > 0) {
            return ceiling(node.right, key);
        }
        var t = ceiling(node.left, key);
        if (t != null) {
            return t;
        } else {
            return node;
        }
    }

    @Override
    public K ceilingKey(K key) {
        return key(ceilingEntry(key));
    }

    @Override
    public Entry<K, V> higherEntry(K key) {
        if (root == null) {
            return null;
        }
        
        return higher(root, key);
    }

    // the smallest key in the subtree rooted at x strictly greater than the given
    // key
    private Entry<K, V> higher(Entry<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int compare = comparator.compare(key, node.key);
        if (compare >= 0) {
            return higher(node.right, key);
        }
        var t = higher(node.left, key);
        if (t != null) {
            return t;
        } else {
            return node;
        }
    }

    @Override
    public K higherKey(K key) {
        return key(higherEntry(key));
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        // TODO Auto-generated method stub
        return null;
    }
}
