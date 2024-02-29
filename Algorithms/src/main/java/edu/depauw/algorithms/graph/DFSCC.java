/**
 * Based on Sedgewick and Wayne,
 * https://github.com/kevin-wayne/algs4/
 *
 *  % java DFSCC tinyG.txt
 *  3 components
 *  0 1 2 3 4 5 6
 *  7 8
 *  9 10 11 12
 *
 *  % java DFSCC mediumG.txt
 *  1 components
 *  0 1 2 3 4 5 6 7 8 9 10 ...
 *
 *  % java -Xss50m DFSCC largeG.txt
 *  1 components
 *  0 1 2 3 4 5 6 7 8 9 10 ...
 *
 */

package edu.depauw.algorithms.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

import edu.depauw.algorithms.ArrayDeque;
import edu.depauw.algorithms.ArrayList;

/**
 *  The {@code DFSCC} class represents a data type for
 *  determining the connected components in an undirected graph.
 *  The <em>id</em> operation determines in which connected component
 *  a given vertex lies; the <em>connected</em> operation
 *  determines whether two vertices are in the same connected component;
 *  the <em>count</em> operation determines the number of connected
 *  components; and the <em>size</em> operation determines the number
 *  of vertices in the connect component containing a given vertex.

 *  The <em>component identifier</em> of a connected component is one of the
 *  vertices in the connected component: two vertices have the same component
 *  identifier if and only if they are in the same connected component.

 *  <p>
 *  This implementation uses depth-first search.
 *  The constructor takes &Theta;(<em>V</em> + <em>E</em>) time,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the
 *  number of edges.
 *  Each instance method takes &Theta;(1) time.
 *  It uses &Theta;(<em>V</em>) extra space (not including the graph).
 *  <p>
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/41graph">Section 4.1</a>
 *  of <em>Algorithms, 4th Edition</em> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class DFSCC implements DFSClient {
    private DFS dfs;
    private int[] id;           // id[v] = id of connected component containing v
    private int[] size;         // size[id] = number of vertices in given component
    private int count;          // number of connected components

    /**
     * Computes the connected components of the undirected graph {@code G}.
     *
     * @param G the undirected graph
     */
    public DFSCC(UndirectedGraph G) {
        dfs = new DFS(G);
        id = new int[G.V()];
        size = new int[G.V()];
        count = 0;
        
        for (int v = 0; v < G.V(); v++) {
            if (!dfs.marked(v)) {
                dfs.dfs(G, v, this);
                count++;
            }
        }
    }

    @Override
    public void visitPreorder(Graph G, int v) {
        id[v] = count;
        size[count]++;
    }

    @Override
    public void visitPostorder(Graph G, int v) {
        // Do nothing
    }

    @Override
    public void processEdge(Graph G, int v, int w) {
        // Do nothing
    }

    /**
     * Returns the component id of the connected component containing vertex {@code v}.
     *
     * @param  v the vertex
     * @return the component id of the connected component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int id(int v) {
        dfs.validateVertex(v);
        return id[v];
    }

    /**
     * Returns the number of vertices in the connected component containing vertex {@code v}.
     *
     * @param  v the vertex
     * @return the number of vertices in the connected component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int size(int v) {
        dfs.validateVertex(v);
        return size[id[v]];
    }

    /**
     * Returns the number of connected components in the graph {@code G}.
     *
     * @return the number of connected components in the graph {@code G}
     */
    public int count() {
        return count;
    }

    /**
     * Returns true if vertices {@code v} and {@code w} are in the same
     * connected component.
     *
     * @param  v one vertex
     * @param  w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same
     *         connected component; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     */
    public boolean connected(int v, int w) {
        dfs.validateVertex(v);
        dfs.validateVertex(w);
        return id(v) == id(w);
    }

    /**
     * Unit tests the {@code DFSCC} data type.
     *
     * @param args the command-line arguments
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File(args[0]));
        UndirectedGraph G = new UndirectedGraph(in);
        DFSCC cc = new DFSCC(G);

        // number of connected components
        int m = cc.count();
        System.out.println(m + " components");

        // compute list of vertices in each connected component
        List<Deque<Integer>> components = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            components.add(i, new ArrayDeque<>());
        }
        for (int v = 0; v < G.V(); v++) {
            components.get(cc.id(v)).add(v);
        }

        // print results
        for (int i = 0; i < m; i++) {
            for (int v : components.get(i)) {
                System.out.print(v + " ");
            }
            System.out.println();
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
