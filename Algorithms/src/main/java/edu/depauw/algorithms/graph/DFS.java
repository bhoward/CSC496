/******************************************************************************
 *  Compilation:  javac DepthFirstSearch.java
 *  Execution:    java DepthFirstSearch filename.txt s
 *  Dependencies: Graph.java StdOut.java
 *  Data files:   https://algs4.cs.princeton.edu/41graph/tinyG.txt
 *                https://algs4.cs.princeton.edu/41graph/mediumG.txt
 *
 *  Run depth first search on an undirected graph.
 *  Runs in O(E + V) time.
 *
 *  % java DFSConnected tinyG.txt 0
 *  0 1 2 3 4 5 6
 *  NOT connected
 *
 *  % java DFSConnected tinyG.txt 9
 *  9 10 11 12
 *  NOT connected
 *
 ******************************************************************************/

package edu.depauw.algorithms.graph;

/**
 * The {@code DFS} class represents a data type for determining the vertices
 * connected to a given source vertex <em>s</em> in an undirected graph. For
 * versions that find the paths, see {@link DFSPaths} and
 * {@link BreadthFirstPaths}.
 * <p>
 * This implementation uses depth-first search. See {@link NonrecursiveDFS} for
 * a non-recursive version. The constructor takes &Theta;(<em>V</em> +
 * <em>E</em>) time in the worst case, where <em>V</em> is the number of
 * vertices and <em>E</em> is the number of edges. Each instance method takes
 * &Theta;(1) time. It uses &Theta;(<em>V</em>) extra space (not including the
 * graph).
 * <p>
 * For additional documentation, see
 * <a href="https://algs4.cs.princeton.edu/41graph">Section 4.1</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class DFS {
    private boolean[] marked; // marked[v] = is there an s-v path?
    private boolean halt;

    /**
     * Computes the vertices in graph {@code G} that are connected to the source
     * vertex {@code s}.
     * 
     * @param G the graph
     * @param s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public DFS(Graph G) {
        marked = new boolean[G.V()];
        halt = false;
    }

    // depth first search from v
    /**
     * Perform one pass of depth first search from {@code v}.
     * 
     * @param G        the graph
     * @param v        the starting vertex
     * @param strategy additional processing for each vertex and edge
     */
    public void dfs(Graph G, int v, DFSClient strategy) {
        strategy.visitPreorder(G, v);
        marked[v] = true;
        for (int w : G.adj(v)) {
            if (halt)
                return;
            strategy.processEdge(G, v, w);
            if (!marked[w]) {
                dfs(G, w, strategy);
            }
        }
        strategy.visitPostorder(G, v);
    }

    /**
     * Is there a path between the source vertex {@code s} and vertex {@code v}?
     * 
     * @param v the vertex
     * @return {@code true} if there is a path, {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean marked(int v) {
        validateVertex(v);
        return marked[v];
    }

    /**
     * Throw an IllegalArgumentException unless {@code 0 <= v < V}
     * 
     * @param v the presumed vertex
     */
    public void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    /**
     * Throw an IllegalArgumentException if vertices is null, has zero vertices, or
     * has a vertex not between 0 and V-1.
     * 
     * @param vertices
     */
    public void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int vertexCount = 0;
        for (Integer v : vertices) {
            vertexCount++;
            if (v == null) {
                throw new IllegalArgumentException("vertex is null");
            }
            validateVertex(v);
        }
        if (vertexCount == 0) {
            throw new IllegalArgumentException("zero vertices");
        }
    }

    /**
     * Signal an early exit from the search.
     */
    public void halt() {
        halt = true;
    }
}

/******************************************************************************
 * Copyright 2002-2022, Robert Sedgewick and Kevin Wayne.
 *
 * This file is part of algs4.jar, which accompanies the textbook
 *
 * Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne, Addison-Wesley
 * Professional, 2011, ISBN 0-321-57351-X. http://algs4.cs.princeton.edu
 *
 *
 * algs4.jar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * algs4.jar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * algs4.jar. If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
