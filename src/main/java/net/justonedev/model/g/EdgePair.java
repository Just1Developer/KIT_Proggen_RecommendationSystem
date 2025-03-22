package net.justonedev.model.g;

record EdgePair(Node fromNode, Node toNode, Edge edge, Edge inverseEdge, boolean valid, String errorMessage) {
    static EdgePair valid(Node fromNode, Node toNode, Edge edge, Edge inverseEdge) {
        return new EdgePair(fromNode, toNode, edge, inverseEdge, true, "");
    }
    static EdgePair invalid(String errorMessage) {
        return new EdgePair(null, null, null, null, false, errorMessage);
    }
}
