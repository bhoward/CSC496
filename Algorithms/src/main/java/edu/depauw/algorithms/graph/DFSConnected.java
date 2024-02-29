/**
 * Based on Sedgewick and Wayne,
 * https://github.com/kevin-wayne/algs4/
 *
 *  % java DFSConnected tinyDG.txt 0 9
 *  Undirected graph
 *  0 1 2 3 4 5 6 7 8 9 10 11 12
 *  connected
 *  Directed graph
 *  0 1 2 3 4 5 9 10 11 12
 *
 */

package edu.depauw.algorithms.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.depauw.algorithms.Bag;
import edu.depauw.algorithms.TreeBag;

/**
 *  The {@code DFSConnected} class represents a data type for
 *  determining the vertices connected to a given source vertex <em>s</em>
 *  in a graph. For versions that find the paths, see
 *  {@link DFSPaths} and {@link BFSPaths}.
 *  <p>
 *  This implementation uses depth-first search.
 *  See {@link NonrecursiveDFS} for a non-recursive version.
 *  The constructor takes &Theta;(<em>V</em> + <em>E</em>) time in the worst
 *  case, where <em>V</em> is the number of vertices and <em>E</em>
 *  is the number of edges.
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
public class DFSConnected implements DFSClient {
    private DFS dfs;
    private int count;           // number of vertices connected to s

    /**
     * Computes the vertices in graph {@code G} that are
     * connected to the source vertex {@code s}.
     * @param G the graph
     * @param s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public DFSConnected(Graph G, int s) {
        dfs = new RecDFS(G);
        dfs.validateVertex(s);
        dfs.dfs(G, s, this);
    }

    /**
     * Computes the vertices in graph {@code G} that are
     * connected to any of the source vertices {@code sources}.
     * @param G the graph
     * @param sources the source vertices
     * @throws IllegalArgumentException if {@code sources} is {@code null}
     * @throws IllegalArgumentException if {@code sources} contains no vertices
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     *         for each vertex {@code s} in {@code sources}
     */
    public DFSConnected(Graph G, Iterable<Integer> sources) {
        dfs = new RecDFS(G);
        dfs.validateVertices(sources);
        for (int v : sources) {
            if (!dfs.marked(v)) dfs.dfs(G, v, this);
        }
    }
    
    @Override
    public void visitPreorder(Graph G, int v) {
        count++;
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
     * Returns the number of vertices connected to the source vertex {@code s}.
     * @return the number of vertices connected to the source vertex {@code s}
     */
    public int count() {
        return count;
    }

    /**
     * Is there a path between the source vertex {@code s} and vertex {@code v}?
     * @param v the vertex
     * @return {@code true} if there is a path, {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean marked(int v) {
        return dfs.marked(v);
    }

    /**
     * Unit tests the {@code DepthFirstSearch} data type.
     *
     * @param args the command-line arguments
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        // read in sources from command-line arguments
        Bag<Integer> sources = new TreeBag<>();
        for (int i = 1; i < args.length; i++) {
            int s = Integer.parseInt(args[i]);
            sources.add(s);
        }
        
        System.out.println("Undirected graph");
        Scanner in = new Scanner(new File(args[0]));
        Graph G = new UndirectedGraph(in);
        in.close();
        
        // Just use the first source for the undirected case
        int s = sources.iterator().next();
        DFSConnected search = new DFSConnected(G, s);
        for (int v = 0; v < G.V(); v++) {
            if (search.marked(v))
                System.out.print(v + " ");
        }

        System.out.println();
        if (search.count() != G.V()) System.out.println("NOT connected");
        else                         System.out.println("connected");
        
        System.out.println("Directed graph");
        in = new Scanner(new File(args[0]));
        G = new Digraph(in);
        in.close();
        
        search = new DFSConnected(G, sources);
        for (int v = 0; v < G.V(); v++) {
            if (search.marked(v))
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
