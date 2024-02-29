package edu.depauw.algorithms.graph;

public abstract class DFS {
    boolean[] marked; // marked[v] = is there an s-v path?
    protected boolean halt;

    public DFS(Graph G) {
        marked = new boolean[G.V()];
        halt = false;
    }

    /**
     * Perform one pass of depth first search from {@code v}.
     * 
     * @param G        the graph
     * @param s        the starting vertex
     * @param strategy additional processing for each vertex and edge
     */
    public abstract void dfs(Graph G, int s, DFSClient strategy);

    /**
     * Is there a path between the source vertex {@code s} and vertex {@code v}?
     * 
     * @param v the vertex
     * @return {@code true} if there is a path, {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean marked(int v) {
        validateVertex(v);
        return marked[v];
    }

    /**
     * Throw an IllegalArgumentException unless {@code 0 <= v < V}
     * 
     * @param v the presumed vertex
     */
    public void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    /**
     * Throw an IllegalArgumentException if vertices is null, has zero vertices, or
     * has a vertex not between 0 and V-1.
     * 
     * @param vertices
     */
    public void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int vertexCount = 0;
        for (Integer v : vertices) {
            vertexCount++;
            if (v == null) {
                throw new IllegalArgumentException("vertex is null");
            }
            validateVertex(v);
        }
        if (vertexCount == 0) {
            throw new IllegalArgumentException("zero vertices");
        }
    }

    /**
     * Signal an early exit from the search.
     */
    public void halt() {
        halt = true;
    }

    /**
     * Check whether the search has been halted.
     * 
     * @return true if {@code halt()} has been called
     */
    public boolean halted() {
        return halt;
    }
}