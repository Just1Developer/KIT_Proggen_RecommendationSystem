package net.justonedev.model.g;

public record EdgeData(String fromNodeName, int fromNodeId, String toNodeName, int toNodeId, EdgeType type) {
}
