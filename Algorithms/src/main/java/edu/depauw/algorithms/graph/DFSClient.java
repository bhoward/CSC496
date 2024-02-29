package edu.depauw.algorithms.graph;

public interface DFSClient {
    /**
     * Process vertex {@code v} in preorder position, when it is first discovered.
     * 
     * @param G the graph
     * @param v the vertex
     */
    void visitPreorder(Graph G, int v);

    /**
     * Process vertex {@code v} in postorder position, after all of its neighbors
     * have been finished.
     * 
     * @param G the graph
     * @param v the vertex
     */
    void visitPostorder(Graph G, int v);

    /**
     * Process an edge from {@code v} to {@code w}. Call {@code marked(w)} to
     * determine whether the edge will be ignored in the search.
     * 
     * @param G the graph
     * @param v the starting vertex
     * @param w the ending vertex
     */
    void processEdge(Graph G, int v, int w);
}
