/**
 * Based on Sedgewick and Wayne,
 * https://github.com/kevin-wayne/algs4/
 *
 *  % java KosarajuSharirSCC tinyDG.txt
 *  5 strong components
 *  1
 *  0 2 3 4 5
 *  9 10 11 12
 *  6 8
 *  7
 *
 *  % java KosarajuSharirSCC mediumDG.txt
 *  10 strong components
 *  21
 *  2 5 6 8 9 11 12 13 15 16 18 19 22 23 25 26 28 29 30 31 32 33 34 35 37 38 39 40 42 43 44 46 47 48 49
 *  41
 *  7
 *  3 4 17 20 24 27 36
 *  14
 *  45
 *  1
 *  0
 *  10
 *
 *  % java KosarajuSharirSCC largeDG.txt
 *  25 strong components
 *  7 11 32 36 61 84 95 116 121 128 230   ...
 *  28 73 80 104 115 143 149 164 184 185  ...
 *  38 40 200 201 207 218 286 387 418 422 ...
 *  12 14 56 78 87 103 216 269 271 272    ...
 *  42 48 112 135 160 217 243 246 273 346 ...
 *  46 76 96 97 224 237 297 303 308 309   ...
 *  9 15 21 22 27 90 167 214 220 225 227  ...
 *  74 99 133 146 161 166 202 205 245 262 ...
 *  43 83 94 120 125 183 195 206 244 254  ...
 *  1 13 54 91 92 93 106 140 156 194 208  ...
 *  10 39 67 69 131 144 145 154 168 258   ...
 *  6 52 66 113 118 122 139 147 212 213   ...
 *  8 127 150 182 203 204 249 367 400 432 ...
 *  63 65 101 107 108 136 169 170 171 173 ...
 *  55 71 102 155 159 198 228 252 325 419 ...
 *  4 25 34 58 70 152 172 196 199 210 226 ...
 *  2 44 50 88 109 138 141 178 197 211    ...
 *  57 89 129 162 174 179 188 209 238 276 ...
 *  33 41 49 119 126 132 148 181 215 221  ...
 *  3 18 23 26 35 64 105 124 157 186 251  ...
 *  5 16 17 20 31 47 81 98 158 180 187    ...
 *  24 29 51 59 75 82 100 114 117 134 151 ...
 *  30 45 53 60 72 85 111 130 137 142 163 ...
 *  19 37 62 77 79 110 153 352 353 361    ...
 *  0 68 86 123 165 176 193 239 289 336   ...
 *
 ******************************************************************************/

package edu.depauw.algorithms.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import edu.depauw.algorithms.ArrayList;

/**
 *  The {@code KosarajuSharirSCC} class represents a data type for
 *  determining the strong components in a digraph.
 *  The <em>id</em> operation determines in which strong component
 *  a given vertex lies; the <em>areStronglyConnected</em> operation
 *  determines whether two vertices are in the same strong component;
 *  and the <em>count</em> operation determines the number of strong
 *  components.
 *  <p>
 *  The <em>component identifier</em> of a component is one of the
 *  vertices in the strong component: two vertices have the same component
 *  identifier if and only if they are in the same strong component.
 *  <p>
 *  This implementation uses the Kosaraju-Sharir algorithm.
 *  The constructor takes &Theta;(<em>V</em> + <em>E</em>) time,
 *  where <em>V</em> is the number of vertices and <em>E</em>
 *  is the number of edges.
 *  Each instance method takes &Theta;(1) time.
 *  It uses &Theta;(<em>V</em>) extra space (not including the digraph).
 *  For alternative implementations of the same API, see
 *  {@link TarjanSCC} and {@link GabowSCC}.
 *  <p>
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class KosarajuSharirSCC implements DFSClient {
    private DFS dfs;
    private int[] id;             // id[v] = id of strong component containing v
    private int count;            // number of strongly-connected components

    /**
     * Computes the strong components of the digraph {@code G}.
     * @param G the digraph
     */
    public KosarajuSharirSCC(Digraph G) {
        // compute reverse postorder of reverse graph
        Iterable<Integer> post = new DFSOrder(G.reverse()).reversePost();

        // run DFS on G, using reverse postorder to guide calculation
        dfs = new NonrecDFS(G);
        id = new int[G.V()];
        for (int v : post) {
            if (!dfs.marked(v)) {
                dfs.dfs(G, v, this);
                count++;
            }
        }
    }

    @Override
    public void visitPreorder(Graph G, int v) {
        id[v] = count;
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
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public int id(int v) {
        dfs.validateVertex(v);
        return id[v];
    }

    /**
     * Unit tests the {@code KosarajuSharirSCC} data type.
     *
     * @param args the command-line arguments
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File(args[0]));
        Digraph G = new Digraph(in);
        in.close();
        KosarajuSharirSCC scc = new KosarajuSharirSCC(G);

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
