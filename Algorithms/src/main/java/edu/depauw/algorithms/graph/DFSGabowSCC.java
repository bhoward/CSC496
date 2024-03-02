/**
 * Based on Sedgewick and Wayne,
 * https://github.com/kevin-wayne/algs4/
 *
 *  % java DFSGabowSCC tinyDG.txt
 *  5 components
 *  1
 *  0 2 3 4 5
 *  9 10 11 12
 *  6 8
 *  7
 *
 ******************************************************************************/

package edu.depauw.algorithms.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

import edu.depauw.algorithms.ArrayDeque;
import edu.depauw.algorithms.ArrayList;

/**
 *  The {@code DFSGabowSCC} class represents a data type for
 *  determining the strong components in a digraph.
 *  The <em>id</em> operation determines in which strong component
 *  a given vertex lies; the <em>areStronglyConnected</em> operation
 *  determines whether two vertices are in the same strong component;
 *  and the <em>count</em> operation determines the number of strong
 *  components.

 *  The <em>component identifier</em> of a component is one of the
 *  vertices in the strong component: two vertices have the same component
 *  identifier if and only if they are in the same strong component.

 *  <p>
 *  This implementation uses the Gabow's algorithm.
 *  The constructor takes &Theta;(<em>V</em> + <em>E</em>) time,
 *  where <em>V</em> is the number of vertices and <em>E</em> is
 *  the number of edges.
 *  Each instance method takes &Theta;(1) time.
 *  It uses &Theta;(<em>V</em>) extra space (not including the digraph).
 *  For alternative implementations of the same API, see
 *  {@link DFSKosarajuSharirSCC} and {@link DFSTarjanSCC}.
 *  <p>
 *  For additional documentation,
 *  see <a href="https://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class DFSGabowSCC implements DFSClient {
    private DFS dfs;
    private int[] id;                // id[v] = id of strong component containing v
    private int[] preorder;          // preorder[v] = preorder of v
    private int pre;                 // preorder number counter
    private int count;               // number of strongly-connected components
    private Deque<Integer> stack1;
    private Deque<Integer> stack2;


    /**
     * Computes the strong components of the digraph {@code G}.
     * @param G the digraph
     */
    public DFSGabowSCC(Digraph G) {
        dfs = new NonrecDFS(G);
        stack1 = new ArrayDeque<>();
        stack2 = new ArrayDeque<>();
        id = new int[G.V()];
        preorder = new int[G.V()];
        for (int v = 0; v < G.V(); v++)
            id[v] = -1;

        for (int v = 0; v < G.V(); v++) {
            if (!dfs.marked(v)) dfs.dfs(G, v, this);
        }
    }

    @Override
    public void visitPreorder(Graph G, int v) {
        preorder[v] = pre++;
        stack1.push(v);
        stack2.push(v);
    }

    @Override
    public void visitPostorder(Graph G, int v) {
        // found strong component containing v
        if (stack2.peek() == v) {
            stack2.pop();
            int w;
            do {
                w = stack1.pop();
                id[w] = count;
            } while (w != v);
            count++;
        }
    }

    @Override
    public void processEdge(Graph G, int v, int w) {
        if (dfs.marked(w) && id[w] == -1) {
            while (preorder[stack2.peek()] > preorder[w])
                stack2.pop();
        }
    }

    /**
     * Returns the number of strong components.
     * @return the number of strong components
     */
    public int count() {
        return count;
    }

    /**
     * Are vertices {@code v} and {@code w} in the same strong component?
     * @param  v one vertex
     * @param  w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same
     *         strong component, and {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     */
    public boolean stronglyConnected(int v, int w) {
        dfs.validateVertex(v);
        dfs.validateVertex(w);
        return id[v] == id[w];
    }

    /**
     * Returns the component id of the strong component containing vertex {@code v}.
     * @param  v the vertex
     * @return the component id of the strong component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int id(int v) {
        dfs.validateVertex(v);
        return id[v];
    }

    /**
     * Unit tests the {@code GabowSCC} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File(args[0]));
        Digraph G = new Digraph(in);
        in.close();
        DFSGabowSCC scc = new DFSGabowSCC(G);

        // number of connected components
        int m = scc.count();
        System.out.println(m + " strong components");

        // compute list of vertices in each strong component
        List<List<Integer>> components = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            components.add(i, new ArrayList<>());
        }
        for (int v = 0; v < G.V(); v++) {
            components.get(scc.id(v)).add(v);
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
