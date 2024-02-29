/**
 * Based on Sedgewick and Wayne,
 * https://github.com/kevin-wayne/algs4/
 *
 *  % java DFSOrder tinyDAG.txt
 *     v  pre post
 *  --------------
 *     0    0    8
 *     1    1    0
 *     2    9   10
 *     3   10    9
 *     4    3    1
 *     5    2    2
 *     6    4    7
 *     7   11   11
 *     8   12   12
 *     9    5    6
 *    10    6    3
 *    11    7    5
 *    12    8    4
 *  Preorder:  0 1 5 4 6 9 10 11 12 2 3 7 8
 *  Postorder: 1 4 5 10 12 11 9 6 0 3 2 7 8
 *  Reverse postorder: 8 7 2 3 0 6 9 11 12 10 5 4 1
 *
 */

package edu.depauw.algorithms.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.Scanner;

import edu.depauw.algorithms.ArrayDeque;

/**
 *  The {@code DFSOrder} class represents a data type for
 *  determining depth-first search ordering of the vertices in a digraph
 *  or edge-weighted digraph, including preorder, postorder, and reverse postorder.
 *  <p>
 *  This implementation uses depth-first search.
 *  Each constructor takes &Theta;(<em>V</em> + <em>E</em>) time,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the
 *  number of edges.
 *  Each instance method takes &Theta;(1) time.
 *  It uses &Theta;(<em>V</em>) extra space (not including the digraph).
 *  <p>
 *  For additional documentation,
 *  see <a href="https://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class DFSOrder implements DFSClient {
    private DFS dfs;
    private int[] pre;                 // pre[v]    = preorder  number of v
    private int[] post;                // post[v]   = postorder number of v
    private Deque<Integer> preorder;   // vertices in preorder
    private Deque<Integer> postorder;  // vertices in postorder
    private int preCounter;            // counter or preorder numbering
    private int postCounter;           // counter for postorder numbering

    /**
     * Determines a depth-first order for the digraph {@code G}.
     * @param G the digraph
     */
    public DFSOrder(Digraph G) {
        dfs = new DFS(G);
        pre    = new int[G.V()];
        post   = new int[G.V()];
        postorder = new ArrayDeque<Integer>();
        preorder  = new ArrayDeque<Integer>();
        for (int v = 0; v < G.V(); v++)
            if (!dfs.marked(v)) dfs.dfs(G, v, this);
    }

    @Override
    public void visitPreorder(Graph G, int v) {
        pre[v] = preCounter++;
        preorder.add(v);
    }

    @Override
    public void visitPostorder(Graph G, int v) {
        postorder.add(v);
        post[v] = postCounter++;
    }

    @Override
    public void processEdge(Graph G, int v, int w) {
        // Do nothing
    }

    /**
     * Returns the preorder number of vertex {@code v}.
     * @param  v the vertex
     * @return the preorder number of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int pre(int v) {
        dfs.validateVertex(v);
        return pre[v];
    }

    /**
     * Returns the postorder number of vertex {@code v}.
     * @param  v the vertex
     * @return the postorder number of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int post(int v) {
        dfs.validateVertex(v);
        return post[v];
    }

    /**
     * Returns the vertices in postorder.
     * @return the vertices in postorder, as an iterable of vertices
     */
    public Iterable<Integer> post() {
        return postorder;
    }

    /**
     * Returns the vertices in preorder.
     * @return the vertices in preorder, as an iterable of vertices
     */
    public Iterable<Integer> pre() {
        return preorder;
    }

    /**
     * Returns the vertices in reverse postorder.
     * @return the vertices in reverse postorder, as an iterable of vertices
     */
    public Iterable<Integer> reversePost() {
        return postorder.reversed();
    }

    /**
     * Unit tests the {@code DFSOrder} data type.
     *
     * @param args the command-line arguments
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File(args[0]));
        Digraph G = new Digraph(in);

        DFSOrder dfs = new DFSOrder(G);
        System.out.println("   v  pre post");
        System.out.println("--------------");
        for (int v = 0; v < G.V(); v++) {
            System.out.printf("%4d %4d %4d\n", v, dfs.pre(v), dfs.post(v));
        }

        System.out.print("Preorder:  ");
        for (int v : dfs.pre()) {
            System.out.print(v + " ");
        }
        System.out.println();

        System.out.print("Postorder: ");
        for (int v : dfs.post()) {
            System.out.print(v + " ");
        }
        System.out.println();

        System.out.print("Reverse postorder: ");
        for (int v : dfs.reversePost()) {
            System.out.print(v + " ");
        }
        System.out.println();
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
