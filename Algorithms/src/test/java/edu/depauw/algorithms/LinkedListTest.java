package edu.depauw.algorithms;

import java.util.Deque;
import java.util.List;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LinkedListTest {
    public static Test suite() {
        return new LinkedListTest().allTests();
    }

    private Test allTests() {
        TestSuite suite = new TestSuite("edu.depauw.algorithms.LinkedListTest");
        suite.addTest(testGeneratedTests());
        suite.addTest(testDeque());
        return suite;
    }

    private Test testDeque() {
        return new TestSuite(DequeTests.class);
    }

    public static class DequeTests extends TestCase {
        public void testAdd() {
            Deque<Integer> deque = new edu.depauw.algorithms.LinkedList<>();
            for (int i = 0; i < 100; i++) {
                deque.add(i);
            }
            assertEquals(0, deque.getFirst().intValue());
            assertEquals(99, deque.getLast().intValue());
        }

        public void testDescendingIterator() {
            Deque<Integer> deque = new edu.depauw.algorithms.LinkedList<>();
            for (int i = 0; i < 100; i++) {
                deque.add(i);
            }
            var it = deque.descendingIterator();
            int i = 100;
            while (it.hasNext()) {
                i--;
                assertEquals(i, it.next().intValue());
                it.remove();
            }
            assertTrue(deque.isEmpty());
        }
    }

    private Test testGeneratedTests() {
        return ListTestSuiteBuilder.using(new LinkedListGenerator()).named("generated LinkedList tests")
                .withFeatures(CollectionSize.ANY, CollectionFeature.ALLOWS_NULL_VALUES, ListFeature.GENERAL_PURPOSE)
                .createTestSuite();
    }

    private static class LinkedListGenerator implements TestListGenerator<Integer> {
        @Override
        public SampleElements<Integer> samples() {
            return new SampleElements.Ints();
        }

        @Override
        public Integer[] createArray(int length) {
            return new Integer[length];
        }

        @Override
        public Iterable<Integer> order(List<Integer> insertionOrder) {
            return insertionOrder;
        }

        @Override
        public List<Integer> create(Object... elements) {
            List<Integer> list = new edu.depauw.algorithms.LinkedList<>();
            for (var e : elements) {
                list.add((Integer) e);
            }
            return list;
        }
    }
}