package edu.depauw.algorithms;

import java.util.Deque;
import java.util.List;

import com.google.common.collect.testing.QueueTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestQueueGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ArrayDequeTest {
	public static Test suite() {
		return new ArrayDequeTest().allTests();
	}

	private Test allTests() {
		TestSuite suite = new TestSuite("edu.depauw.algorithms.ArrayDequeTest");
		suite.addTest(testGeneratedTests());
		suite.addTest(testDeque());
		return suite;
	}
		
	private Test testDeque() {
		return new TestSuite(DequeTests.class);
	}
	
	public static class DequeTests extends TestCase {
		public void testAdd() {
			Deque<Integer> deque = new edu.depauw.algorithms.ArrayDeque<>();
			for (int i = 0; i < 100; i++) {
				deque.add(i);
			}
			assertEquals(0, deque.getFirst().intValue());
			assertEquals(99, deque.getLast().intValue());
		}
		
		public void testDescendingIterator() {
			Deque<Integer> deque = new edu.depauw.algorithms.ArrayDeque<>();
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
		return QueueTestSuiteBuilder
				.using(new ArrayDequeGenerator())
				.named("generated ArrayDeque tests")
				.withFeatures(
						CollectionSize.ANY,
						CollectionFeature.ALLOWS_NULL_VALUES,
						CollectionFeature.GENERAL_PURPOSE
				)
				.createTestSuite();
	}
	
	private static class ArrayDequeGenerator implements TestQueueGenerator<Integer> {
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
		public Deque<Integer> create(Object... elements) {
			Deque<Integer> deque = new edu.depauw.algorithms.ArrayDeque<>();
			for (var e : elements) {
				deque.add((Integer) e);
			}
			return deque;
		}
	}
}