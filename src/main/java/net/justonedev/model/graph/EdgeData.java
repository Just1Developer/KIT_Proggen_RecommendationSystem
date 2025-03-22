package net.justonedev.model.graph;

public record EdgeData(String fromNodeName, int fromNodeId, String toNodeName, int toNodeId, EdgeType type) {
}
