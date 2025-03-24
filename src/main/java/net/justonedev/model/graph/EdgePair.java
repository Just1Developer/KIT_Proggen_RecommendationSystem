package net.justonedev.model.graph;

/**
 * A pair of edges, with some additional data for internal processing. Wrapper type for graph-internal operations.
 *
 * @author uwwfh
 *
 * @param fromNode The origin node for the regular edge.
 * @param toNode The destination node for the regular edge.
 * @param edge The regular edge.
 * @param inverseEdge The exact inverse edge. From target to origin with inverted edge type.
 * @param valid If the edge pair is valid.
 * @param errorMessage The error message if the edge pair is not valid.
 */
record EdgePair(Node fromNode, Node toNode, Edge edge, Edge inverseEdge, boolean valid, String errorMessage) {
    /**
     * Creates a new valid edge pair to be returned. Autofills other fields.
     * @param fromNode The origin node.
     * @param toNode The destination node.
     * @param edge The edge.
     * @param inverseEdge The exact inverted edge.
     * @return A new valid edge pair packed with the provided data.
     */
    static EdgePair valid(Node fromNode, Node toNode, Edge edge, Edge inverseEdge) {
        return new EdgePair(fromNode, toNode, edge, inverseEdge, true, "");
    }

    /**
     * Creates a new invalid edge pair to be returned. Autofills other fields.
     * @param errorMessage The error message to be passed to IO.
     * @return A new invalid edge pair with the error message.
     */
    static EdgePair invalid(String errorMessage) {
        return new EdgePair(null, null, null, null, false, errorMessage);
    }
}
