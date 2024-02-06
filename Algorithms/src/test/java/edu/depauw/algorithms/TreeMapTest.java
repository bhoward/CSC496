package edu.depauw.algorithms;

import java.util.Map.Entry;
import java.util.SortedMap;

import com.google.common.collect.testing.NavigableMapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringSortedMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TreeMapTest {
    public static Test suite() {
        return new TreeMapTest().allTests();
    }

    private Test allTests() {
        TestSuite suite = new TestSuite("edu.depauw.algorithms.TreeMapTest");
        suite.addTest(testGeneratedTests());
        return suite;
    }

    private Test testGeneratedTests() {
        return NavigableMapTestSuiteBuilder.using(new TreeMapGenerator()).named("generated TreeMap tests")
                .withFeatures(MapFeature.GENERAL_PURPOSE, MapFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE, CollectionFeature.KNOWN_ORDER, CollectionSize.ANY)
                .createTestSuite();
    }

    private static class TreeMapGenerator extends TestStringSortedMapGenerator {
        @Override
        protected SortedMap<String, String> create(Entry<String, String>[] entries) {
            SortedMap<String, String> map = new edu.depauw.algorithms.TreeMap<>();
//            SortedMap<String, String> map = new java.util.TreeMap<>();
            for (var entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            return map;
        }
    }
}