package edu.depauw.algorithms;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

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

        public boolean equals(Object o) {
            return o instanceof Map.Entry<?, ?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }

        public int hashCode() {
            int keyHash = (key==null ? 0 : key.hashCode());
            int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    @Override
    public Comparator<? super K> comparator() {
        return comparator;
    }

    private int compare(K a, K b) {
        return comparator.compare(a, b);
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
        var node = getEntry(key);
        return node != null;
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        var node = getEntry(key);
        if (node == null) {
            return null;
        } else {
            return node.value;
        }
    }

    @SuppressWarnings("unchecked")
    private Entry<K, V> getEntry(Object key) {
        return getEntry(root, (K) key);
    }

    private Entry<K, V> getEntry(Entry<K, V> node, K key) {
        if (node != null) {
            int compare = compare(key, node.key);
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

    private Entry<K, V> exportEntry(Entry<K, V> entry) {
        if (entry == null) {
            return null;
        }
        return new Entry<>(entry);
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException();
        }
        var node = getEntry(key);
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
            int compare = compare(key, node.key);
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
        var node = getEntry(key);
        if (node != null) {
            var oldValue = node.value;

            // if both children of root are black, set root to red
            if (isBlack(root.left) && isBlack(root.right)) {
                root.red = true;
            }

            root = delete(root, (K) key);
            size--;
            if (!isEmpty()) {
                root.red = false;
            }
            return oldValue;
        }

        return null;
    }

    // delete the key-value pair with the given key rooted at h
    private Entry<K, V> delete(Entry<K, V> node, K key) {
        int compare = compare(key, node.key);
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

        var oldEntry = exportEntry(firstEntry());

        if (isBlack(root.left) && isBlack(root.right)) {
            root.red = true;
        }
        root = deleteMin(root);
        size--;
        if (!isEmpty()) {
            root.red = false;
        }

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

        var oldEntry = exportEntry(lastEntry());

        if (isBlack(root.left) && isBlack(root.right)) {
            root.red = true;
        }
        root = deleteMax(root);
        size--;
        if (!isEmpty()) {
            root.red = false;
        }

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

    // Based on https://gist.github.com/rcaloras/36f9e5f94f4334e0827c5b52ec0d8115
    private Entry<K, V> successor(Entry<K, V> node) {
        if (node.right != null) {
            return min(node.right);
        }

        return searchSucc(node, root, null);
    }

    private Entry<K, V> searchSucc(Entry<K, V> node, Entry<K, V> current, Entry<K, V> candidate) {
        if (current == node) {
            return candidate;
        }

        if (compare(node.key, current.key) < 0) {
            return searchSucc(node, current.left, current);
        } else {
            return searchSucc(node, current.right, candidate);
        }
    }

    private Entry<K, V> predecessor(Entry<K, V> node) {
        if (node.left != null) {
            return max(node.left);
        }

        return searchPred(node, root, null);
    }

    private Entry<K, V> searchPred(Entry<K, V> node, Entry<K, V> current, Entry<K, V> candidate) {
        if (current == node) {
            return candidate;
        }

        if (compare(node.key, current.key) < 0) {
            return searchPred(node, current.left, candidate);
        } else {
            return searchPred(node, current.right, current);
        }
    }

    private K key(Entry<K, V> node) {
        if (node == null) {
            throw new NoSuchElementException();
        } else {
            return node.key;
        }
    }

    private K keyOrNull(Entry<K, V> node) {
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

        int compare = compare(key, node.key);
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
        return keyOrNull(lowerEntry(key));
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

        int compare = compare(key, node.key);
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
        return keyOrNull(floorEntry(key));
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

        int compare = compare(key, node.key);
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
        return keyOrNull(ceilingEntry(key));
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

        int compare = compare(key, node.key);
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
        return keyOrNull(higherEntry(key));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    // Views

    /**
     * Fields initialized to contain an instance of the entry set view the first
     * time this view is requested. Views are stateless, so there's no reason to
     * create more than one.
     */
    private transient EntrySet entrySet;
    private transient KeySet navigableKeySet;
    private transient NavigableMap<K, V> descendingMap;
    private transient Collection<V> values;

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     *
     * <p>
     * The set's iterator returns the keys in ascending order. The set's spliterator
     * is <em><a href="Spliterator.html#binding">late-binding</a></em>,
     * <em>fail-fast</em>, and additionally reports {@link Spliterator#SORTED} and
     * {@link Spliterator#ORDERED} with an encounter order that is ascending key
     * order. The spliterator's comparator (see
     * {@link java.util.Spliterator#getComparator()}) is {@code null} if the tree
     * map's comparator (see {@link #comparator()}) is {@code null}. Otherwise, the
     * spliterator's comparator is the same as or imposes the same total ordering as
     * the tree map's comparator.
     *
     * <p>
     * The set is backed by the map, so changes to the map are reflected in the set,
     * and vice-versa. If the map is modified while an iteration over the set is in
     * progress (except through the iterator's own {@code remove} operation), the
     * results of the iteration are undefined. The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * {@code Iterator.remove}, {@code Set.remove}, {@code removeAll},
     * {@code retainAll}, and {@code clear} operations. It does not support the
     * {@code add} or {@code addAll} operations.
     */
    @Override
    public Set<K> keySet() {
        return navigableKeySet();
    }

    /**
     * @since 1.6
     */
    @Override
    public NavigableSet<K> navigableKeySet() {
        KeySet nks = navigableKeySet;
        return (nks != null) ? nks : (navigableKeySet = new KeySet(this));
    }

    /**
     * @since 1.6
     */
    @Override
    public NavigableSet<K> descendingKeySet() {
        return descendingMap().navigableKeySet();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     *
     * <p>
     * The collection's iterator returns the values in ascending order of the
     * corresponding keys. The collection's spliterator is
     * <em><a href="Spliterator.html#binding">late-binding</a></em>,
     * <em>fail-fast</em>, and additionally reports {@link Spliterator#ORDERED} with
     * an encounter order that is ascending order of the corresponding keys.
     *
     * <p>
     * The collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa. If the map is modified while an iteration
     * over the collection is in progress (except through the iterator's own
     * {@code remove} operation), the results of the iteration are undefined. The
     * collection supports element removal, which removes the corresponding mapping
     * from the map, via the {@code Iterator.remove}, {@code Collection.remove},
     * {@code removeAll}, {@code retainAll} and {@code clear} operations. It does
     * not support the {@code add} or {@code addAll} operations.
     */
    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     *
     * <p>
     * The set's iterator returns the entries in ascending key order. The set's
     * spliterator is <em><a href="Spliterator.html#binding">late-binding</a></em>,
     * <em>fail-fast</em>, and additionally reports {@link Spliterator#SORTED} and
     * {@link Spliterator#ORDERED} with an encounter order that is ascending key
     * order.
     *
     * <p>
     * The set is backed by the map, so changes to the map are reflected in the set,
     * and vice-versa. If the map is modified while an iteration over the set is in
     * progress (except through the iterator's own {@code remove} operation, or
     * through the {@code setValue} operation on a map entry returned by the
     * iterator) the results of the iteration are undefined. The set supports
     * element removal, which removes the corresponding mapping from the map, via
     * the {@code Iterator.remove}, {@code Set.remove}, {@code removeAll},
     * {@code retainAll} and {@code clear} operations. It does not support the
     * {@code add} or {@code addAll} operations.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    /**
     * @since 1.6
     */
    @Override
    public NavigableMap<K, V> descendingMap() {
        NavigableMap<K, V> km = descendingMap;
        return (km != null) ? km : (descendingMap = new DescendingSubMap(this, true, null, true, true, null, true));
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     if {@code fromKey} or {@code toKey} is null
     *                                  and this map uses natural ordering, or its
     *                                  comparator does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return new AscendingSubMap(this, false, fromKey, fromInclusive, false, toKey, toInclusive);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     if {@code toKey} is null and this map uses
     *                                  natural ordering, or its comparator does not
     *                                  permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return new AscendingSubMap(this, true, null, true, false, toKey, inclusive);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     if {@code fromKey} is null and this map uses
     *                                  natural ordering, or its comparator does not
     *                                  permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return new AscendingSubMap(this, false, fromKey, inclusive, true, null, true);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     if {@code fromKey} or {@code toKey} is null
     *                                  and this map uses natural ordering, or its
     *                                  comparator does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     if {@code toKey} is null and this map uses
     *                                  natural ordering, or its comparator does not
     *                                  permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return headMap(toKey, false);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException     if {@code fromKey} is null and this map uses
     *                                  natural ordering, or its comparator does not
     *                                  permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return tailMap(fromKey, true);
    }

    class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator(firstEntry());
        }

        @Override
        public int size() {
            return TreeMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return TreeMap.this.containsValue(o);
        }

        @Override
        public boolean remove(Object o) {
            for (Entry<K, V> e = firstEntry(); e != null; e = successor(e)) {
                if (Objects.equals(e.getValue(), o)) {
                    TreeMap.this.remove(e.getKey());
                    return true;
                }
            }
            return false;
        }

        @Override
        public void clear() {
            TreeMap.this.clear();
        }

        @Override
        public Spliterator<V> spliterator() {
            return new ValueSpliterator(TreeMap.this, null, null, 0, -1);
        }
    }

    class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator(firstEntry());
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> entry)) {
                return false;
            }
            Object value = entry.getValue();
            Entry<K, V> p = getEntry(entry.getKey());
            return p != null && Objects.equals(p.getValue(), value);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry<?, ?> entry)) {
                return false;
            }
            Object value = entry.getValue();
            Entry<K, V> p = getEntry(entry.getKey());
            if (p != null && Objects.equals(p.getValue(), value)) {
                TreeMap.this.remove(p.getKey());
                return true;
            }
            return false;
        }

        @Override
        public int size() {
            return TreeMap.this.size();
        }

        @Override
        public void clear() {
            TreeMap.this.clear();
        }

        @Override
        public Spliterator<Map.Entry<K, V>> spliterator() {
            return new EntrySpliterator(TreeMap.this, null, null, 0, -1);
        }
    }

    /*
     * Unlike Values and EntrySet, the KeySet class is static, delegating to a
     * NavigableMap to allow use by SubMaps, which outweighs the ugliness of needing
     * type-tests for the following Iterator methods that are defined appropriately
     * in main versus submap classes.
     */

    Iterator<K> keyIterator() {
        return new KeyIterator(firstEntry());
    }

    Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator(lastEntry());
    }

    final class KeySet extends AbstractSet<K> implements NavigableSet<K> {
        private final NavigableMap<K, V> m;

        KeySet(NavigableMap<K, V> map) {
            m = map;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterator<K> iterator() {
            if (m instanceof TreeMap<K, V> tm) {
                return tm.keyIterator();
            } else if (m instanceof TreeMap.NavigableSubMap nsm) {
                return nsm.keyIterator();
            }
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterator<K> descendingIterator() {
            if (m instanceof TreeMap<K, V> tm) {
                return tm.descendingKeyIterator();
            } else if (m instanceof TreeMap.NavigableSubMap nsm) {
                return nsm.descendingKeyIterator();
            }
            return null;
        }

        @Override
        public int size() {
            return m.size();
        }

        @Override
        public boolean isEmpty() {
            return m.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return m.containsKey(o);
        }

        @Override
        public void clear() {
            m.clear();
        }

        @Override
        public K lower(K e) {
            return m.lowerKey(e);
        }

        @Override
        public K floor(K e) {
            return m.floorKey(e);
        }

        @Override
        public K ceiling(K e) {
            return m.ceilingKey(e);
        }

        @Override
        public K higher(K e) {
            return m.higherKey(e);
        }

        @Override
        public K first() {
            return m.firstKey();
        }

        @Override
        public K last() {
            return m.lastKey();
        }

        @Override
        public Comparator<? super K> comparator() {
            return m.comparator();
        }

        @Override
        public K pollFirst() {
            Map.Entry<K, V> e = m.pollFirstEntry();
            return (e == null) ? null : e.getKey();
        }

        @Override
        public K pollLast() {
            Map.Entry<K, V> e = m.pollLastEntry();
            return (e == null) ? null : e.getKey();
        }

        @Override
        public boolean remove(Object o) {
            int oldSize = size();
            m.remove(o);
            return size() != oldSize;
        }

        @Override
        public NavigableSet<K> subSet(K fromElement, boolean fromInclusive, K toElement, boolean toInclusive) {
            return new KeySet(m.subMap(fromElement, fromInclusive, toElement, toInclusive));
        }

        @Override
        public NavigableSet<K> headSet(K toElement, boolean inclusive) {
            return new KeySet(m.headMap(toElement, inclusive));
        }

        @Override
        public NavigableSet<K> tailSet(K fromElement, boolean inclusive) {
            return new KeySet(m.tailMap(fromElement, inclusive));
        }

        @Override
        public SortedSet<K> subSet(K fromElement, K toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        @Override
        public SortedSet<K> headSet(K toElement) {
            return headSet(toElement, false);
        }

        @Override
        public SortedSet<K> tailSet(K fromElement) {
            return tailSet(fromElement, true);
        }

        @Override
        public NavigableSet<K> descendingSet() {
            return new KeySet(m.descendingMap());
        }

        @Override
        public Spliterator<K> spliterator() {
            return keySpliteratorFor(m);
        }
    }

    /**
     * Base class for TreeMap Iterators
     */
    abstract class PrivateEntryIterator<T> implements Iterator<T> {
        Entry<K, V> next;
        Entry<K, V> lastReturned;

        PrivateEntryIterator(Entry<K, V> first) {
            lastReturned = null;
            next = first;
        }

        @Override
        public final boolean hasNext() {
            return next != null;
        }

        final Entry<K, V> nextEntry() {
            Entry<K, V> e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            next = successor(e);
            lastReturned = e;
            return e;
        }

        final Entry<K, V> prevEntry() {
            Entry<K, V> e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            next = predecessor(e);
            lastReturned = e;
            return e;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            // deleted entries are replaced by their successors
            if (lastReturned.left != null && lastReturned.right != null) {
                next = lastReturned;
            }
            TreeMap.this.remove(lastReturned.getKey());
            lastReturned = null;
        }
    }

    final class EntryIterator extends PrivateEntryIterator<Map.Entry<K, V>> {
        EntryIterator(Entry<K, V> first) {
            super(first);
        }

        @Override
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    final class ValueIterator extends PrivateEntryIterator<V> {
        ValueIterator(Entry<K, V> first) {
            super(first);
        }

        @Override
        public V next() {
            return nextEntry().value;
        }
    }

    final class KeyIterator extends PrivateEntryIterator<K> {
        KeyIterator(Entry<K, V> first) {
            super(first);
        }

        @Override
        public K next() {
            return nextEntry().key;
        }
    }

    final class DescendingKeyIterator extends PrivateEntryIterator<K> {
        DescendingKeyIterator(Entry<K, V> first) {
            super(first);
        }

        @Override
        public K next() {
            return prevEntry().key;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            TreeMap.this.remove(lastReturned.getKey());
            lastReturned = null;
        }
    }

    // SubMaps

    /**
     * Dummy value serving as unmatchable fence key for unbounded SubMapIterators
     */
    private static final Object UNBOUNDED = new Object();

    abstract class NavigableSubMap extends AbstractMap<K, V> implements NavigableMap<K, V> {
        /**
         * The backing map.
         */
        final TreeMap<K, V> m;

        /**
         * Endpoints are represented as triples (fromStart, lo, loInclusive) and (toEnd,
         * hi, hiInclusive). If fromStart is true, then the low (absolute) bound is the
         * start of the backing map, and the other values are ignored. Otherwise, if
         * loInclusive is true, lo is the inclusive bound, else lo is the exclusive
         * bound. Similarly for the upper bound.
         */
        final K lo;
        final K hi;
        final boolean fromStart, toEnd;
        final boolean loInclusive, hiInclusive;

        NavigableSubMap(TreeMap<K, V> m, boolean fromStart, K lo, boolean loInclusive, boolean toEnd, K hi,
                boolean hiInclusive) {
            if (!fromStart && !toEnd) {
                if (m.compare(lo, hi) > 0) {
                    throw new IllegalArgumentException("fromKey > toKey");
                }
            } else {
                if (!fromStart) { // type check
                    m.compare(lo, lo);
                }
                if (!toEnd) {
                    m.compare(hi, hi);
                }
            }

            this.m = m;
            this.fromStart = fromStart;
            this.lo = lo;
            this.loInclusive = loInclusive;
            this.toEnd = toEnd;
            this.hi = hi;
            this.hiInclusive = hiInclusive;
        }

        // internal utilities

        @SuppressWarnings("unchecked")
        final boolean tooLow(Object key) {
            if (!fromStart) {
                int c = m.compare((K) key, lo);
                if (c < 0 || (c == 0 && !loInclusive)) {
                    return true;
                }
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        final boolean tooHigh(Object key) {
            if (!toEnd) {
                int c = m.compare((K) key, hi);
                if (c > 0 || (c == 0 && !hiInclusive)) {
                    return true;
                }
            }
            return false;
        }

        final boolean inRange(Object key) {
            return !tooLow(key) && !tooHigh(key);
        }

        @SuppressWarnings("unchecked")
        final boolean inClosedRange(Object key) {
            return (fromStart || m.compare((K) key, lo) >= 0) && (toEnd || m.compare(hi, (K) key) >= 0);
        }

        final boolean inRange(Object key, boolean inclusive) {
            return inclusive ? inRange(key) : inClosedRange(key);
        }

        /*
         * Absolute versions of relation operations. Subclasses map to these using
         * like-named "sub" versions that invert senses for descending maps
         */

        final TreeMap.Entry<K, V> absLowest() {
            TreeMap.Entry<K, V> e = (fromStart ? m.firstEntry()
                    : (loInclusive ? m.ceilingEntry(lo) : m.higherEntry(lo)));
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K, V> absHighest() {
            TreeMap.Entry<K, V> e = (toEnd ? m.lastEntry() : (hiInclusive ? m.floorEntry(hi) : m.lowerEntry(hi)));
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final TreeMap.Entry<K, V> absCeiling(K key) {
            if (tooLow(key)) {
                return absLowest();
            }
            TreeMap.Entry<K, V> e = m.ceilingEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K, V> absHigher(K key) {
            if (tooLow(key)) {
                return absLowest();
            }
            TreeMap.Entry<K, V> e = m.higherEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K, V> absFloor(K key) {
            if (tooHigh(key)) {
                return absHighest();
            }
            TreeMap.Entry<K, V> e = m.floorEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final TreeMap.Entry<K, V> absLower(K key) {
            if (tooHigh(key)) {
                return absHighest();
            }
            TreeMap.Entry<K, V> e = m.lowerEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        /** Returns the absolute high fence for ascending traversal */
        final TreeMap.Entry<K, V> absHighFence() {
            return (toEnd ? null : (hiInclusive ? m.higherEntry(hi) : m.ceilingEntry(hi)));
        }

        /** Return the absolute low fence for descending traversal */
        final TreeMap.Entry<K, V> absLowFence() {
            return (fromStart ? null : (loInclusive ? m.lowerEntry(lo) : m.floorEntry(lo)));
        }

        // Abstract methods defined in ascending vs descending classes
        // These relay to the appropriate absolute versions

        abstract TreeMap.Entry<K, V> subLowest();

        abstract TreeMap.Entry<K, V> subHighest();

        abstract TreeMap.Entry<K, V> subCeiling(K key);

        abstract TreeMap.Entry<K, V> subHigher(K key);

        abstract TreeMap.Entry<K, V> subFloor(K key);

        abstract TreeMap.Entry<K, V> subLower(K key);

        /** Returns ascending iterator from the perspective of this submap */
        abstract Iterator<K> keyIterator();

        abstract Spliterator<K> keySpliterator();

        /** Returns descending iterator from the perspective of this submap */
        abstract Iterator<K> descendingKeyIterator();

        // public methods

        @Override
        public boolean isEmpty() {
            return (fromStart && toEnd) ? m.isEmpty() : entrySet().isEmpty();
        }

        @Override
        public int size() {
            return (fromStart && toEnd) ? m.size() : entrySet().size();
        }

        @Override
        public final boolean containsKey(Object key) {
            return inRange(key) && m.containsKey(key);
        }

        @Override
        public final V put(K key, V value) {
            if (!inRange(key)) {
                throw new IllegalArgumentException("key out of range");
            }
            return m.put(key, value);
        }

        @Override
        public V putIfAbsent(K key, V value) {
            if (!inRange(key)) {
                throw new IllegalArgumentException("key out of range");
            }
            return m.putIfAbsent(key, value);
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            if (!inRange(key)) {
                throw new IllegalArgumentException("key out of range");
            }
            return m.merge(key, value, remappingFunction);
        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            if (!inRange(key)) {
                // Do not throw if mapping function returns null
                // to preserve compatibility with default computeIfAbsent implementation
                if (mappingFunction.apply(key) == null) {
                    return null;
                }
                throw new IllegalArgumentException("key out of range");
            }
            return m.computeIfAbsent(key, mappingFunction);
        }

        @Override
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            if (!inRange(key)) {
                // Do not throw if remapping function returns null
                // to preserve compatibility with default computeIfAbsent implementation
                if (remappingFunction.apply(key, null) == null) {
                    return null;
                }
                throw new IllegalArgumentException("key out of range");
            }
            return m.compute(key, remappingFunction);
        }

        @Override
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return !inRange(key) ? null : m.computeIfPresent(key, remappingFunction);
        }

        @Override
        public final V get(Object key) {
            return !inRange(key) ? null : m.get(key);
        }

        @Override
        public final V remove(Object key) {
            return !inRange(key) ? null : m.remove(key);
        }

        @Override
        public final Map.Entry<K, V> ceilingEntry(K key) {
            return exportEntry(subCeiling(key));
        }

        @Override
        public final K ceilingKey(K key) {
            return keyOrNull(subCeiling(key));
        }

        @Override
        public final Map.Entry<K, V> higherEntry(K key) {
            return exportEntry(subHigher(key));
        }

        @Override
        public final K higherKey(K key) {
            return keyOrNull(subHigher(key));
        }

        @Override
        public final Map.Entry<K, V> floorEntry(K key) {
            return exportEntry(subFloor(key));
        }

        @Override
        public final K floorKey(K key) {
            return keyOrNull(subFloor(key));
        }

        @Override
        public final Map.Entry<K, V> lowerEntry(K key) {
            return exportEntry(subLower(key));
        }

        @Override
        public final K lowerKey(K key) {
            return keyOrNull(subLower(key));
        }

        @Override
        public final K firstKey() {
            return key(subLowest());
        }

        @Override
        public final K lastKey() {
            return key(subHighest());
        }

        @Override
        public final Map.Entry<K, V> firstEntry() {
            return exportEntry(subLowest());
        }

        @Override
        public final Map.Entry<K, V> lastEntry() {
            return exportEntry(subHighest());
        }

        @Override
        public final Map.Entry<K, V> pollFirstEntry() {
            TreeMap.Entry<K, V> e = subLowest();
            Map.Entry<K, V> result = exportEntry(e);
            if (e != null) {
                m.remove(e.getKey());
            }
            return result;
        }

        @Override
        public final Map.Entry<K, V> pollLastEntry() {
            TreeMap.Entry<K, V> e = subHighest();
            Map.Entry<K, V> result = exportEntry(e);
            if (e != null) {
                m.remove(e.getKey());
            }
            return result;
        }

        // Views
        transient NavigableMap<K, V> descendingMapView;
        transient EntrySetView entrySetView;
        transient KeySet navigableKeySetView;

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public final NavigableSet<K> navigableKeySet() {
            KeySet nksv = navigableKeySetView;
            return (nksv != null) ? nksv : (navigableKeySetView = new TreeMap.KeySet(this));
        }

        @Override
        public final Set<K> keySet() {
            return navigableKeySet();
        }

        @Override
        public NavigableSet<K> descendingKeySet() {
            return descendingMap().navigableKeySet();
        }

        @Override
        public final SortedMap<K, V> subMap(K fromKey, K toKey) {
            return subMap(fromKey, true, toKey, false);
        }

        @Override
        public final SortedMap<K, V> headMap(K toKey) {
            return headMap(toKey, false);
        }

        @Override
        public final SortedMap<K, V> tailMap(K fromKey) {
            return tailMap(fromKey, true);
        }

        // View classes

        abstract class EntrySetView extends AbstractSet<Map.Entry<K, V>> {
            @Override
            public int size() {
                if (fromStart && toEnd) {
                    return m.size();
                }
                int size = 0;
                    Iterator<?> i = iterator();
                    while (i.hasNext()) {
                        size++;
                        i.next();
                    }
                return size;
            }

            @Override
            public boolean isEmpty() {
                TreeMap.Entry<K, V> n = absLowest();
                return n == null || tooHigh(n.key);
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Entry<?, ?> entry)) {
                    return false;
                }
                Object key = entry.getKey();
                if (!inRange(key)) {
                    return false;
                }
                TreeMap.Entry<?, ?> node = m.getEntry(key);
                return node != null && Objects.equals(node.getValue(), entry.getValue());
            }

            @Override
            public boolean remove(Object o) {
                if (!(o instanceof Entry<?, ?> entry)) {
                    return false;
                }
                Object key = entry.getKey();
                if (!inRange(key)) {
                    return false;
                }
                TreeMap.Entry<K, V> node = m.getEntry(key);
                if (node != null && Objects.equals(node.getValue(), entry.getValue())) {
                    m.remove(node.getKey());
                    return true;
                }
                return false;
            }
        }

        /**
         * Iterators for SubMaps
         */
        abstract class SubMapIterator<T> implements Iterator<T> {
            TreeMap.Entry<K, V> lastReturned;
            TreeMap.Entry<K, V> next;
            final Object fenceKey;

            SubMapIterator(TreeMap.Entry<K, V> first, TreeMap.Entry<K, V> fence) {
                lastReturned = null;
                next = first;
                fenceKey = fence == null ? UNBOUNDED : fence.key;
            }

            @Override
            public final boolean hasNext() {
                return next != null && next.key != fenceKey;
            }

            final TreeMap.Entry<K, V> nextEntry() {
                TreeMap.Entry<K, V> e = next;
                if (e == null || e.key == fenceKey) {
                    throw new NoSuchElementException();
                }
                next = successor(e);
                lastReturned = e;
                return e;
            }

            final TreeMap.Entry<K, V> prevEntry() {
                TreeMap.Entry<K, V> e = next;
                if (e == null || e.key == fenceKey) {
                    throw new NoSuchElementException();
                }
                next = predecessor(e);
                lastReturned = e;
                return e;
            }

            final void removeAscending() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                // deleted entries are replaced by their successors
//                if (lastReturned.left != null && lastReturned.right != null) {
//                    next = lastReturned;
//                }
                m.remove(lastReturned.getKey());
                lastReturned = null;
            }

            final void removeDescending() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                m.remove(lastReturned.getKey());
                lastReturned = null;
            }

        }

        final class SubMapEntryIterator extends SubMapIterator<Map.Entry<K, V>> {
            SubMapEntryIterator(TreeMap.Entry<K, V> first, TreeMap.Entry<K, V> fence) {
                super(first, fence);
            }

            @Override
            public Map.Entry<K, V> next() {
                return nextEntry();
            }

            @Override
            public void remove() {
                removeAscending();
            }
        }

        final class DescendingSubMapEntryIterator extends SubMapIterator<Map.Entry<K, V>> {
            DescendingSubMapEntryIterator(TreeMap.Entry<K, V> last, TreeMap.Entry<K, V> fence) {
                super(last, fence);
            }

            @Override
            public Map.Entry<K, V> next() {
                return prevEntry();
            }

            @Override
            public void remove() {
                removeDescending();
            }
        }

        // Implement minimal Spliterator as KeySpliterator backup
        final class SubMapKeyIterator extends SubMapIterator<K> implements Spliterator<K> {
            SubMapKeyIterator(TreeMap.Entry<K, V> first, TreeMap.Entry<K, V> fence) {
                super(first, fence);
            }

            @Override
            public K next() {
                return nextEntry().key;
            }

            @Override
            public void remove() {
                removeAscending();
            }

            @Override
            public Spliterator<K> trySplit() {
                return null;
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                while (hasNext()) {
                    action.accept(next());
                }
            }

            @Override
            public boolean tryAdvance(Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }

            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

            @Override
            public int characteristics() {
                return Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.SORTED;
            }

            @Override
            public final Comparator<? super K> getComparator() {
                return NavigableSubMap.this.comparator();
            }
        }

        final class DescendingSubMapKeyIterator extends SubMapIterator<K> implements Spliterator<K> {
            DescendingSubMapKeyIterator(TreeMap.Entry<K, V> last, TreeMap.Entry<K, V> fence) {
                super(last, fence);
            }

            @Override
            public K next() {
                return prevEntry().key;
            }

            @Override
            public void remove() {
                removeDescending();
            }

            @Override
            public Spliterator<K> trySplit() {
                return null;
            }

            @Override
            public void forEachRemaining(Consumer<? super K> action) {
                while (hasNext()) {
                    action.accept(next());
                }
            }

            @Override
            public boolean tryAdvance(Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }

            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

            @Override
            public int characteristics() {
                return Spliterator.DISTINCT | Spliterator.ORDERED;
            }
        }
    }

    final class AscendingSubMap extends NavigableSubMap {
        AscendingSubMap(TreeMap<K, V> m, boolean fromStart, K lo, boolean loInclusive, boolean toEnd, K hi,
                boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        @Override
        public Comparator<? super K> comparator() {
            return m.comparator();
        }

        @Override
        public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive)) {
                throw new IllegalArgumentException("fromKey out of range");
            }
            if (!inRange(toKey, toInclusive)) {
                throw new IllegalArgumentException("toKey out of range");
            }
            return new AscendingSubMap(m, false, fromKey, fromInclusive, false, toKey, toInclusive);
        }

        @Override
        public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
            if (!inRange(toKey, inclusive)) {
                throw new IllegalArgumentException("toKey out of range");
            }
            return new AscendingSubMap(m, fromStart, lo, loInclusive, false, toKey, inclusive);
        }

        @Override
        public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
            if (!inRange(fromKey, inclusive)) {
                throw new IllegalArgumentException("fromKey out of range");
            }
            return new AscendingSubMap(m, false, fromKey, inclusive, toEnd, hi, hiInclusive);
        }

        @Override
        public NavigableMap<K, V> descendingMap() {
            NavigableMap<K, V> mv = descendingMapView;
            return (mv != null) ? mv
                    : (descendingMapView = new DescendingSubMap(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive));
        }

        @Override
        Iterator<K> keyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        @Override
        Spliterator<K> keySpliterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        @Override
        Iterator<K> descendingKeyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        final class AscendingEntrySetView extends EntrySetView {
            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new SubMapEntryIterator(absLowest(), absHighFence());
            }
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new AscendingEntrySetView());
        }

        @Override
        TreeMap.Entry<K, V> subLowest() {
            return absLowest();
        }

        @Override
        TreeMap.Entry<K, V> subHighest() {
            return absHighest();
        }

        @Override
        TreeMap.Entry<K, V> subCeiling(K key) {
            return absCeiling(key);
        }

        @Override
        TreeMap.Entry<K, V> subHigher(K key) {
            return absHigher(key);
        }

        @Override
        TreeMap.Entry<K, V> subFloor(K key) {
            return absFloor(key);
        }

        @Override
        TreeMap.Entry<K, V> subLower(K key) {
            return absLower(key);
        }
    }

    final class DescendingSubMap extends NavigableSubMap {
        DescendingSubMap(TreeMap<K, V> m, boolean fromStart, K lo, boolean loInclusive, boolean toEnd, K hi,
                boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        private final Comparator<? super K> reverseComparator = Collections.reverseOrder(m.comparator);

        @Override
        public Comparator<? super K> comparator() {
            return reverseComparator;
        }

        @Override
        public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive)) {
                throw new IllegalArgumentException("fromKey out of range");
            }
            if (!inRange(toKey, toInclusive)) {
                throw new IllegalArgumentException("toKey out of range");
            }
            return new DescendingSubMap(m, false, toKey, toInclusive, false, fromKey, fromInclusive);
        }

        @Override
        public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
            if (!inRange(toKey, inclusive)) {
                throw new IllegalArgumentException("toKey out of range");
            }
            return new DescendingSubMap(m, false, toKey, inclusive, toEnd, hi, hiInclusive);
        }

        @Override
        public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
            if (!inRange(fromKey, inclusive)) {
                throw new IllegalArgumentException("fromKey out of range");
            }
            return new DescendingSubMap(m, fromStart, lo, loInclusive, false, fromKey, inclusive);
        }

        @Override
        public NavigableMap<K, V> descendingMap() {
            NavigableMap<K, V> mv = descendingMapView;
            return (mv != null) ? mv
                    : (descendingMapView = new AscendingSubMap(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive));
        }

        @Override
        Iterator<K> keyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        @Override
        Spliterator<K> keySpliterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        @Override
        Iterator<K> descendingKeyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        final class DescendingEntrySetView extends EntrySetView {
            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new DescendingSubMapEntryIterator(absHighest(), absLowFence());
            }
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new DescendingEntrySetView());
        }

        @Override
        TreeMap.Entry<K, V> subLowest() {
            return absHighest();
        }

        @Override
        TreeMap.Entry<K, V> subHighest() {
            return absLowest();
        }

        @Override
        TreeMap.Entry<K, V> subCeiling(K key) {
            return absFloor(key);
        }

        @Override
        TreeMap.Entry<K, V> subHigher(K key) {
            return absLower(key);
        }

        @Override
        TreeMap.Entry<K, V> subFloor(K key) {
            return absCeiling(key);
        }

        @Override
        TreeMap.Entry<K, V> subLower(K key) {
            return absHigher(key);
        }
    }

    /**
     * Currently, we support Spliterator-based versions only for the full map, in
     * either plain of descending form, otherwise relying on defaults because size
     * estimation for submaps would dominate costs. The type tests needed to check
     * these for key views are not very nice but avoid disrupting existing class
     * structures. Callers must use plain default spliterators if this returns null.
     */
    Spliterator<K> keySpliteratorFor(NavigableMap<K, V> m) {
        if (m instanceof TreeMap) {
            TreeMap<K, V> t = (TreeMap<K, V>) m;
            return t.keySpliterator();
        }
        if (m instanceof DescendingSubMap) {
            DescendingSubMap dm = (DescendingSubMap) m;
            TreeMap<K, ?> tm = dm.m;
            if (dm == tm.descendingMap) {
                @SuppressWarnings("unchecked")
                TreeMap<K, V> t = (TreeMap<K, V>) tm;
                return t.descendingKeySpliterator();
            }
        }
        NavigableSubMap sm = (NavigableSubMap) m;
        return sm.keySpliterator();
    }

    final Spliterator<K> keySpliterator() {
        return new KeySpliterator(this, null, null, 0, -1);
    }

    final Spliterator<K> descendingKeySpliterator() {
        return new DescendingKeySpliterator(this, null, null, 0, -2);
    }

    /**
     * Base class for spliterators. Iteration starts at a given origin and continues
     * up to but not including a given fence (or null for end). At top-level, for
     * ascending cases, the first split uses the root as left-fence/right-origin.
     * From there, right-hand splits replace the current fence with its left child,
     * also serving as origin for the split-off spliterator. Left-hands are
     * symmetric. Descending versions place the origin at the end and invert
     * ascending split rules. This base class is non-committal about directionality,
     * or whether the top-level spliterator covers the whole tree. This means that
     * the actual split mechanics are located in subclasses. Some of the subclass
     * trySplit methods are identical (except for return types), but not nicely
     * factorable.
     *
     * Currently, subclass versions exist only for the full map (including
     * descending keys via its descendingMap). Others are possible but currently not
     * worthwhile because submaps require O(n) computations to determine size, which
     * substantially limits potential speed-ups of using custom Spliterators versus
     * default mechanics.
     *
     * To bootstrap initialization, external constructors use negative size
     * estimates: -1 for ascend, -2 for descend.
     */
    static class TreeMapSpliterator<K, V> {
        final TreeMap<K, V> tree;
        TreeMap.Entry<K, V> current; // traverser; initially first node in range
        TreeMap.Entry<K, V> fence; // one past last, or null
        int side; // 0: top, -1: is a left split, +1: right
        int est; // size estimate (exact only for top-level)

        TreeMapSpliterator(TreeMap<K, V> tree, TreeMap.Entry<K, V> origin, TreeMap.Entry<K, V> fence, int side,
                int est) {
            this.tree = tree;
            this.current = origin;
            this.fence = fence;
            this.side = side;
            this.est = est;
        }

        final int getEstimate() { // force initialization
            int s;
            TreeMap<K, V> t;
            if ((s = est) < 0) {
                if ((t = tree) != null) {
                    current = (s == -1) ? t.firstEntry() : t.lastEntry();
                    s = est = t.size;
                } else {
                    s = est = 0;
                }
            }
            return s;
        }

        public final long estimateSize() {
            return getEstimate();
        }
    }

    final class KeySpliterator extends TreeMapSpliterator<K, V> implements Spliterator<K> {
        KeySpliterator(TreeMap<K, V> tree, TreeMap.Entry<K, V> origin, TreeMap.Entry<K, V> fence, int side, int est) {
            super(tree, origin, fence, side, est);
        }

        @Override
        public KeySpliterator trySplit() {
            if (est < 0) {
                getEstimate(); // force initialization
            }
            int d = side;
            TreeMap.Entry<K, V> e = current, f = fence, s = ((e == null || e == f) ? null : // empty
                    (d == 0) ? tree.root : // was top
                            (d > 0) ? e.right : // was right
                                    (d < 0 && f != null) ? f.left : // was left
                                            null);
            if (s != null && s != e && s != f && tree.compare(e.key, s.key) < 0) { // e not already past s
                side = 1;
                return new KeySpliterator(tree, e, current = s, -1, est >>>= 1);
            }
            return null;
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            if (est < 0) {
                getEstimate(); // force initialization
            }
            TreeMap.Entry<K, V> f = fence, e = current;
            if (e != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.key);
                    e = successor(e);
                } while (e != null && e != f);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super K> action) {
            TreeMap.Entry<K, V> e;
            if (action == null) {
                throw new NullPointerException();
            }
            if (est < 0) {
                getEstimate(); // force initialization
            }
            if ((e = current) == null || e == fence) {
                return false;
            }
            current = successor(e);
            action.accept(e.key);
            return true;
        }

        @Override
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.DISTINCT | Spliterator.SORTED
                    | Spliterator.ORDERED;
        }

        @Override
        public final Comparator<? super K> getComparator() {
            return tree.comparator;
        }

    }

    final class DescendingKeySpliterator extends TreeMapSpliterator<K, V> implements Spliterator<K> {
        DescendingKeySpliterator(TreeMap<K, V> tree, TreeMap.Entry<K, V> origin, TreeMap.Entry<K, V> fence, int side,
                int est) {
            super(tree, origin, fence, side, est);
        }

        @Override
        public DescendingKeySpliterator trySplit() {
            if (est < 0) {
                getEstimate(); // force initialization
            }
            int d = side;
            TreeMap.Entry<K, V> e = current, f = fence, s = ((e == null || e == f) ? null : // empty
                    (d == 0) ? tree.root : // was top
                            (d < 0) ? e.left : // was left
                                    (d > 0 && f != null) ? f.right : // was right
                                            null);
            if (s != null && s != e && s != f && tree.compare(e.key, s.key) > 0) { // e not already past s
                side = 1;
                return new DescendingKeySpliterator(tree, e, current = s, -1, est >>>= 1);
            }
            return null;
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            if (est < 0) {
                getEstimate(); // force initialization
            }
            TreeMap.Entry<K, V> f = fence, e = current;
            if (e != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.key);
                    e = predecessor(e);
                } while (e != null && e != f);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super K> action) {
            TreeMap.Entry<K, V> e;
            if (action == null) {
                throw new NullPointerException();
            }
            if (est < 0) {
                getEstimate(); // force initialization
            }
            if ((e = current) == null || e == fence) {
                return false;
            }
            current = predecessor(e);
            action.accept(e.key);
            return true;
        }

        @Override
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.DISTINCT | Spliterator.ORDERED;
        }
    }

    final class ValueSpliterator extends TreeMapSpliterator<K, V> implements Spliterator<V> {
        ValueSpliterator(TreeMap<K, V> tree, TreeMap.Entry<K, V> origin, TreeMap.Entry<K, V> fence, int side, int est) {
            super(tree, origin, fence, side, est);
        }

        @Override
        public ValueSpliterator trySplit() {
            if (est < 0) {
                getEstimate(); // force initialization
            }
            int d = side;
            TreeMap.Entry<K, V> e = current, f = fence, s = ((e == null || e == f) ? null : // empty
                    (d == 0) ? tree.root : // was top
                            (d > 0) ? e.right : // was right
                                    (d < 0 && f != null) ? f.left : // was left
                                            null);
            if (s != null && s != e && s != f && tree.compare(e.key, s.key) < 0) { // e not already past s
                side = 1;
                return new ValueSpliterator(tree, e, current = s, -1, est >>>= 1);
            }
            return null;
        }

        @Override
        public void forEachRemaining(Consumer<? super V> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            if (est < 0) {
                getEstimate(); // force initialization
            }
            TreeMap.Entry<K, V> f = fence, e = current;
            if (e != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.value);
                    e = successor(e);
                } while (e != null && e != f);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super V> action) {
            TreeMap.Entry<K, V> e;
            if (action == null) {
                throw new NullPointerException();
            }
            if (est < 0) {
                getEstimate(); // force initialization
            }
            if ((e = current) == null || e == fence) {
                return false;
            }
            current = successor(e);
            action.accept(e.value);
            return true;
        }

        @Override
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.ORDERED;
        }
    }

    final class EntrySpliterator extends TreeMapSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
        EntrySpliterator(TreeMap<K, V> tree, TreeMap.Entry<K, V> origin, TreeMap.Entry<K, V> fence, int side, int est) {
            super(tree, origin, fence, side, est);
        }

        @Override
        public EntrySpliterator trySplit() {
            if (est < 0) {
                getEstimate(); // force initialization
            }
            int d = side;
            TreeMap.Entry<K, V> e = current, f = fence, s = ((e == null || e == f) ? null : // empty
                    (d == 0) ? tree.root : // was top
                            (d > 0) ? e.right : // was right
                                    (d < 0 && f != null) ? f.left : // was left
                                            null);
            if (s != null && s != e && s != f && tree.compare(e.key, s.key) < 0) { // e not already past s
                side = 1;
                return new EntrySpliterator(tree, e, current = s, -1, est >>>= 1);
            }
            return null;
        }

        @Override
        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            if (est < 0) {
                getEstimate(); // force initialization
            }
            TreeMap.Entry<K, V> f = fence, e = current;
            if (e != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e);
                    e = successor(e);
                } while (e != null && e != f);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
            TreeMap.Entry<K, V> e;
            if (action == null) {
                throw new NullPointerException();
            }
            if (est < 0) {
                getEstimate(); // force initialization
            }
            if ((e = current) == null || e == fence) {
                return false;
            }
            current = successor(e);
            action.accept(e);
            return true;
        }

        @Override
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.DISTINCT | Spliterator.SORTED
                    | Spliterator.ORDERED;
        }

        @Override
        public Comparator<Map.Entry<K, V>> getComparator() {
            // Adapt or create a key-based comparator
            if (tree.comparator != null) {
                return Map.Entry.comparingByKey(tree.comparator);
            } else {
                return (Comparator<Map.Entry<K, V>>) (e1, e2) -> {
                    @SuppressWarnings("unchecked")
                    Comparable<? super K> k1 = (Comparable<? super K>) e1.getKey();
                    return k1.compareTo(e2.getKey());
                };
            }
        }
    }
}
