package edu.depauw.algorithms.graph;

public interface Graph {
    /**
     * Returns the number of vertices in this graph.
     *
     * @return the number of vertices in this graph
     */
    int V();

    /**
     * Returns the number of edges in this graph.
     *
     * @return the number of edges in this graph
     */
    int E();

    /**
     * Adds an edge from v to w to this graph.
     *
     * @param  v the first vertex
     * @param  w the second vertex
     * @throws IllegalArgumentException unless both {@code 0 <= v < V} and {@code 0 <= w < V}
     */
    void addEdge(int v, int w);

    /**
     * Returns the vertices adjacent to vertex {@code v}.
     *
     * @param  v the vertex
     * @return the vertices adjacent to vertex {@code v}, as an iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    Iterable<Integer> adj(int v);
}