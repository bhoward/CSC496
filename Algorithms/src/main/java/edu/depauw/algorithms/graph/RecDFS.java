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
 * The {@code RecDFS} class represents a data type for determining the vertices
 * connected to a given source vertex <em>s</em> in a graph. For
 * versions that find the paths, see {@link DFSPaths} and
 * {@link BFSPaths}.
 * <p>
 * This implementation uses depth-first search. See {@link NonrecDFS} for
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
public class RecDFS extends DFS {
    /**
     * Computes the vertices in graph {@code G} that are connected to the source
     * vertex {@code s}.
     * 
     * @param G the graph
     * @param s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public RecDFS(Graph G) {
        super(G);
    }

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
