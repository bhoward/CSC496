package edu.depauw.algorithms;

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

public class ArrayListTest {
	public static Test suite() {
		return new ArrayListTest().allTests();
	}

	private Test allTests() {
		TestSuite suite = new TestSuite("edu.depauw.algorithms.ArrayListTest");
		suite.addTest(testGeneratedTests());
		suite.addTest(testLargeData());
		return suite;
	}

	private Test testLargeData() {
		return new TestSuite(LargeDataTests.class);
	}
	
	public static class LargeDataTests extends TestCase {		
		public void testAdd() {
			List<Integer> list = new edu.depauw.algorithms.ArrayList<>();
			for (int i = 0; i < 100; i++) {
				list.add(i);
			}
			assertEquals(0, list.get(0).intValue());
			assertEquals(99, list.get(99).intValue());
		}
		
		public void testTrimToSize() {
			edu.depauw.algorithms.ArrayList<Integer> list = new edu.depauw.algorithms.ArrayList<>();
			for (int i = 0; i < 100; i++) {
				list.add(i);
			}
			list.trimToSize();
			assertEquals(0, list.getFirst().intValue());
			assertEquals(99, list.getLast().intValue());
		}
		
	}
	
	private Test testGeneratedTests() {
		return ListTestSuiteBuilder
				.using(new ArrayListGenerator())
				.named("generated ArrayList tests")
				.withFeatures(
						CollectionSize.ANY,
						CollectionFeature.ALLOWS_NULL_VALUES,
						ListFeature.GENERAL_PURPOSE
				)
				.createTestSuite();
	}
	
	private static class ArrayListGenerator implements TestListGenerator<Integer> {
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
			List<Integer> list = new edu.depauw.algorithms.ArrayList<>();
			for (var e : elements) {
				list.add((Integer) e);
			}
			return list;
		}
	}
}