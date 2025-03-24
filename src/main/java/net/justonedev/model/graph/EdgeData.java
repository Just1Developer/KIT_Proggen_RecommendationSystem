package net.justonedev.model.graph;

/**
 * Data record for an edge, stores all relevant information that is obtainable and usable outside the graph,
 * as nodes and edges are only available within.
 *
 * @author uwwfh
 *
 * @param fromNodeName The name of the source node of the edge, in lowercase.
 * @param fromNodeId The node's product id. If it's a category, use {@linkplain DatabaseGraph#PROGRAM_ID_CATEGORY} constant.
 * @param toNodeName The name of the destination node of the edge, in lowercase.
 * @param toNodeId The node's product id. If it's a category, use {@linkplain DatabaseGraph#PROGRAM_ID_CATEGORY} constant.
 * @param type The edge type.
 */
public record EdgeData(String fromNodeName, int fromNodeId, String toNodeName, int toNodeId, EdgeType type) {
}
