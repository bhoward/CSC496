/**
 * Based on Sedgewick and Wayne,
 * https://github.com/kevin-wayne/algs4/
 */

package edu.depauw.algorithms.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.Scanner;

import edu.depauw.algorithms.ArrayDeque;

/**
 * The {@code DFSBipartite} class represents a data type for determining whether
 * an undirected graph is <em>bipartite</em> or whether it has an <em>odd-length
 * cycle</em>. A graph is bipartite if and only if it has no odd-length cycle.
 * The <em>isBipartite</em> operation determines whether the graph is bipartite.
 * If so, the <em>color</em> operation determines a bipartition; if not, the
 * <em>oddCycle</em> operation determines a cycle with an odd number of edges.
 * <p>
 * This implementation uses <em>depth-first search</em>. The constructor takes
 * &Theta;(<em>V</em> + <em>E</em>) time in the worst case, where <em>V</em> is
 * the number of vertices and <em>E</em> is the number of edges. Each instance
 * method takes &Theta;(1) time. It uses &Theta;(<em>V</em>) extra space (not
 * including the graph). See {@link BFSBipartite} for a nonrecursive version that
 * uses breadth-first search.
 * <p>
 * For additional documentation, see
 * <a href="https://algs4.cs.princeton.edu/41graph">Section 4.1</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class DFSBipartite implements DFSClient {
    private DFS dfs;
    private boolean isBipartite; // is the graph bipartite?
    private boolean[] color; // color[v] gives vertices on one side of bipartition
    private int[] edgeTo; // edgeTo[v] = last edge on path to v
    private Deque<Integer> cycle; // odd-length cycle

    /**
     * Determines whether an undirected graph is bipartite and finds either a
     * bipartition or an odd-length cycle.
     *
     * @param G the graph
     */
    public DFSBipartite(UndirectedGraph G) {
        dfs = new NonrecDFS(G);
        isBipartite = true;
        color = new boolean[G.V()];
        edgeTo = new int[G.V()];

        for (int v = 0; v < G.V(); v++) {
            if (!dfs.marked(v) && !dfs.halted()) {
            	color[v] = false;
                dfs.dfs(G, v, this);
            }
        }
    }

    @Override
    public void visitPreorder(Graph G, int v) {
        // Do nothing
    }

    @Override
    public void visitPostorder(Graph G, int v) {
        // Do nothing
    }

    @Override
    public void processEdge(Graph G, int v, int w) {
        if (!dfs.marked(w)) {
            edgeTo[w] = v;
            color[w] = !color[v];
        } else if (color[w] == color[v]) {
            isBipartite = false;
            cycle = new ArrayDeque<>();
            cycle.push(w); // don't need this unless you want to include start vertex twice
            for (int x = v; x != w; x = edgeTo[x]) {
                cycle.push(x);
            }
            cycle.push(w);
            
            dfs.halt();
        }
    }

    /**
     * Returns true if the graph is bipartite.
     *
     * @return {@code true} if the graph is bipartite; {@code false} otherwise
     */
    public boolean isBipartite() {
        return isBipartite;
    }

    /**
     * Returns the side of the bipartite that vertex {@code v} is on.
     *
     * @param v the vertex
     * @return the side of the bipartition that vertex {@code v} is on; two vertices
     *         are in the same side of the bipartition if and only if they have the
     *         same color
     * @throws IllegalArgumentException      unless {@code 0 <= v < V}
     * @throws UnsupportedOperationException if this method is called when the graph
     *                                       is not bipartite
     */
    public boolean color(int v) {
        dfs.validateVertex(v);
        if (!isBipartite)
            throw new UnsupportedOperationException("graph is not bipartite");
        return color[v];
    }

    /**
     * Returns an odd-length cycle if the graph is not bipartite, and {@code null}
     * otherwise.
     *
     * @return an odd-length cycle if the graph is not bipartite (and hence has an
     *         odd-length cycle), and {@code null} otherwise
     */
    public Iterable<Integer> oddCycle() {
        return cycle;
    }

    /**
     * Unit tests the {@code DFSBipartite} data type.
     *
     * @param args the command-line arguments
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File(args[0]));
        UndirectedGraph G = new UndirectedGraph(in);
        in.close();
//        System.out.println(G);

        DFSBipartite b = new DFSBipartite(G);
        if (b.isBipartite()) {
            System.out.println("Graph is bipartite");
            for (int v = 0; v < G.V(); v++) {
                System.out.println(v + ": " + b.color(v));
            }
        } else {
            System.out.print("Graph has an odd-length cycle: ");
            for (int x : b.oddCycle()) {
                System.out.print(x + " ");
            }
            System.out.println();
        }
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
