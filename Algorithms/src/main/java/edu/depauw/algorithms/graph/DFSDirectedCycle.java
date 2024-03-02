/**
 * Based on Sedgewick and Wayne,
 * https://github.com/kevin-wayne/algs4/
 *
 *  % java DFSDirectedCycle tinyDG.txt
 *  Directed cycle: 2 0 5 4 2
 *
 *  %  java DFSDirectedCycle tinyDAG.txt
 *  No directed cycle
 *
 */

package edu.depauw.algorithms.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.Scanner;

import edu.depauw.algorithms.ArrayDeque;

/**
 * The {@code DFSDirectedCycle} class represents a data type for determining
 * whether a digraph has a directed cycle. The <em>hasCycle</em> operation
 * determines whether the digraph has a simple directed cycle and, if so, the
 * <em>cycle</em> operation returns one.
 * <p>
 * This implementation uses depth-first search. The constructor takes
 * &Theta;(<em>V</em> + <em>E</em>) time in the worst case, where <em>V</em> is
 * the number of vertices and <em>E</em> is the number of edges. Each instance
 * method takes &Theta;(1) time. It uses &Theta;(<em>V</em>) extra space (not
 * including the digraph).
 * <p>
 * See {@link Topological} to compute a topological order if the digraph is
 * acyclic.
 * <p>
 * For additional documentation, see
 * <a href="https://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class DFSDirectedCycle implements DFSClient {
    private DFS dfs;
    private int[] edgeTo; // edgeTo[v] = previous vertex on path to v
    private boolean[] onStack; // onStack[v] = is vertex on the stack?
    private Deque<Integer> cycle; // directed cycle (or null if no such cycle)

    /**
     * Determines whether the digraph {@code G} has a directed cycle and, if so,
     * finds such a cycle.
     * 
     * @param G the digraph
     */
    public DFSDirectedCycle(Digraph G) {
        dfs = new NonrecDFS(G);
        onStack = new boolean[G.V()];
        edgeTo = new int[G.V()];
        for (int v = 0; v < G.V(); v++)
            if (!dfs.marked(v) && !dfs.halted())
                dfs.dfs(G, v, this);
    }

    @Override
    public void visitPreorder(Graph G, int v) {
        onStack[v] = true;
    }

    @Override
    public void visitPostorder(Graph G, int v) {
        onStack[v] = false;
    }

    @Override
    public void processEdge(Graph G, int v, int w) {
        if (!dfs.marked(w)) {
            edgeTo[w] = v;
        } else if (onStack[w]) {
            cycle = new ArrayDeque<>();
            for (int x = v; x != w; x = edgeTo[x]) {
                cycle.push(x);
            }
            cycle.push(w);
            cycle.push(v);
            
            dfs.halt();
        }
    }

    /**
     * Does the digraph have a directed cycle?
     * 
     * @return {@code true} if the digraph has a directed cycle, {@code false}
     *         otherwise
     */
    public boolean hasCycle() {
        return cycle != null;
    }

    /**
     * Returns a directed cycle if the digraph has a directed cycle, and
     * {@code null} otherwise.
     * 
     * @return a directed cycle (as an iterable) if the digraph has a directed
     *         cycle, and {@code null} otherwise
     */
    public Iterable<Integer> cycle() {
        return cycle;
    }

    /**
     * Unit tests the {@code DFSDirectedCycle} data type.
     *
     * @param args the command-line arguments
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File(args[0]));
        Digraph G = new Digraph(in);
        in.close();

        DFSDirectedCycle finder = new DFSDirectedCycle(G);
        if (finder.hasCycle()) {
            System.out.print("Directed cycle: ");
            for (int v : finder.cycle()) {
                System.out.print(v + " ");
            }
            System.out.println();
        }

        else {
            System.out.println("No directed cycle");
        }
        System.out.println();
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
