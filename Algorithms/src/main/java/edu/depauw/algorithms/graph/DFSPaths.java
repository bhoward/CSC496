/**
 * Based on Sedgewick and Wayne,
 * https://github.com/kevin-wayne/algs4/
 *
 *  % java DFSPaths tinyDG.txt 0
 *  Undirected graph
 *  3 to 0:  3-2-0
 *  3 to 1:  3-2-0-1
 *  3 to 2:  3-2
 *  3 to 3:  3
 *  3 to 4:  3-2-0-5-4
 *  3 to 5:  3-2-0-5
 *  3 to 6:  3-2-0-5-4-6
 *  3 to 7:  3-2-0-5-4-6-7
 *  3 to 8:  3-2-0-5-4-6-8
 *  3 to 9:  3-2-0-5-4-6-7-9
 *  3 to 10:  3-2-0-5-4-6-7-9-10
 *  3 to 11:  3-2-0-5-4-6-7-9-10-12-11
 *  3 to 12:  3-2-0-5-4-6-7-9-10-12
 *  Directed graph
 *  3 to 0:  3-2-0
 *  3 to 1:  3-2-0-1
 *  3 to 2:  3-2
 *  3 to 3:  3
 *  3 to 4:  3-2-0-5-4
 *  3 to 5:  3-2-0-5
 *  3 to 6:  not connected
 *  3 to 7:  not connected
 *  3 to 8:  not connected
 *  3 to 9:  not connected
 *  3 to 10:  not connected
 *  3 to 11:  not connected
 *  3 to 12:  not connected
 *
 */

package edu.depauw.algorithms.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.Scanner;

import edu.depauw.algorithms.ArrayDeque;

/**
 *  The {@code DFSPaths} class represents a data type for finding
 *  paths from a source vertex <em>s</em> to every other vertex
 *  in a graph.
 *  <p>
 *  This implementation uses depth-first search.
 *  The constructor takes &Theta;(<em>V</em> + <em>E</em>) time in the
 *  worst case, where <em>V</em> is the number of vertices and
 *  <em>E</em> is the number of edges.
 *  Each instance method takes &Theta;(1) time.
 *  It uses &Theta;(<em>V</em>) extra space (not including the graph).
 *  <p>
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/41graph">Section 4.1</a>
 *  of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class DFSPaths implements DFSClient {
    private DFS dfs;
    private int[] edgeTo;        // edgeTo[v] = last edge on s-v path
    private final int s;         // source vertex

    /**
     * Computes a path between {@code s} and every other vertex in graph {@code G}.
     * @param G the graph
     * @param s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public DFSPaths(Graph G, int s) {
        dfs = new DFS(G);
        this.s = s;
        edgeTo = new int[G.V()];
        dfs.validateVertex(s);
        dfs.dfs(G, s, this);
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
        }
    }

    /**
     * Is there a path between the source vertex {@code s} and vertex {@code v}?
     * @param v the vertex
     * @return {@code true} if there is a path, {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
        dfs.validateVertex(v);
        return dfs.marked(v);
    }

    /**
     * Returns a path between the source vertex {@code s} and vertex {@code v}, or
     * {@code null} if no such path.
     * @param  v the vertex
     * @return the sequence of vertices on a path between the source vertex
     *         {@code s} and vertex {@code v}, as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Integer> pathTo(int v) {
        dfs.validateVertex(v);
        if (!hasPathTo(v)) return null;
        Deque<Integer> path = new ArrayDeque<>();
        for (int x = v; x != s; x = edgeTo[x])
            path.push(x);
        path.push(s);
        return path;
    }

    /**
     * Unit tests the {@code DepthFirstPaths} data type.
     *
     * @param args the command-line arguments
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        int s = Integer.parseInt(args[1]);
        
        System.out.println("Undirected graph");
        Scanner in = new Scanner(new File(args[0]));
        Graph G = new UndirectedGraph(in);
        in.close();
        
        DFSPaths dfs = new DFSPaths(G, s);

        for (int v = 0; v < G.V(); v++) {
            if (dfs.hasPathTo(v)) {
                System.out.printf("%d to %d:  ", s, v);
                for (int x : dfs.pathTo(v)) {
                    if (x == s) System.out.print(x);
                    else        System.out.print("-" + x);
                }
                System.out.println();
            }

            else {
                System.out.printf("%d to %d:  not connected\n", s, v);
            }
        }
        
        System.out.println("Directed graph");
        in = new Scanner(new File(args[0]));
        G = new Digraph(in);
        dfs = new DFSPaths(G, s);

        for (int v = 0; v < G.V(); v++) {
            if (dfs.hasPathTo(v)) {
                System.out.printf("%d to %d:  ", s, v);
                for (int x : dfs.pathTo(v)) {
                    if (x == s) System.out.print(x);
                    else        System.out.print("-" + x);
                }
                System.out.println();
            }

            else {
                System.out.printf("%d to %d:  not connected\n", s, v);
            }
        }
    }
}

/******************************************************************************
 *  Copyright 2002-2022, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
