package net.justonedev.model.graph;

import net.justonedev.command.CommandResult;
import net.justonedev.model.RecommendationStrategy;
import net.justonedev.model.RecommendationResult;
import net.justonedev.model.stream.DataStream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The graph representing the database.
 * @author uwwfh
 */
public class DatabaseGraph {
    /**
     * The fallback program id for categories, such that it will be marked as ignored.
     */
    public static final int PROGRAM_ID_CATEGORY = -1;

    private static final String ERROR_PRODUCT_NOT_FOUND = "Could not find the product with id %d";
    private static final String ERROR_EDGE_ALREADY_EXISTS = "The given relation is already present in the database";
    private static final String ERROR_EDGE_DOESNT_EXIST = "The given relation is not present in the database";
    private static final String ERROR_SOURCE_HAS_INVALID_ID = "Source node %s has an invalid program Id.";
    private static final String ERROR_TARGET_HAS_INVALID_ID = "Destination node %s has an invalid program Id.";
    private static final String ERROR_SELF_CONNECTION = "Source and destination nodes can't be the same.";

    private static final String FORMAT_EDGE_DIGRAPH = "%s -> %s [label=%s]";
    private static final String FORMAT_CATEGORY_DIGRAPH = "%s [shape=box]";
    private static final String FORMAT_EDGE_LIST = "%s-[%s]->%s";

    private final Map<String, Node> nodeRefs;

    /**
     * Creates a new and empty database graph.
     */
    public DatabaseGraph() {
        nodeRefs = new HashMap<>();
    }

    // -------------------------- GRAPH PROCESSING --------------------------

    /**
     * Gets the recommendations for a given product id using a given filter strategy.
     * Returns an invalid recommendation result with an error message for displaying in IO if anything goes wrong,
     * for example if no product with the given product id exists.<br/>
     * Otherwise, gets the recommendations as a list of product labels.
     *
     * @param productId The product id of the source product.
     * @param recommendationStrategy The recommendation strategy.
     * @return A recommendation result of the recommendations.
     */
    public RecommendationResult getRecommendations(int productId, RecommendationStrategy recommendationStrategy) {
        Optional<Node> nodeOptional = getNodeById(productId);
        if (nodeOptional.isEmpty()) {
            return RecommendationResult.invalid(ERROR_PRODUCT_NOT_FOUND.formatted(productId));
        }
        Node node = nodeOptional.get();
        return switch (recommendationStrategy) {
            case SIBLING -> getSiblings(node);
            case SUCCESSOR -> getSuccessors(node);
            case PREDECESSOR -> getPredecessors(node);
        };
    }

    private static RecommendationResult getSiblings(Node node) {
        List<String> siblings = DataStream.unwrap(node.getNodesWith(EdgeType.CONTAINED_IN).map(Node::getAllDirectlyContainedProducts))
                .filter(n -> !n.equals(node)).sorted(Node::getName).map(Node::getLabel).toList();
        return RecommendationResult.valid(siblings);
    }

    private static RecommendationResult getSuccessors(Node node) {
        // Not an accident with successor/predecessor. We follow this to get all the successors
        List<String> successors = DataStream.of(node.getAllProductsWithRecursively(EdgeType.PREDECESSOR))
                .filter(n -> !n.equals(node)).sorted(Node::getName).map(Node::getLabel).toList();
        return RecommendationResult.valid(successors);
    }

    private static RecommendationResult getPredecessors(Node node) {
        // Not an accident with successor/predecessor. We follow this to get all the predecessors
        List<String> predecessors = DataStream.of(node.getAllProductsWithRecursively(EdgeType.SUCCESSOR))
                .filter(n -> !n.equals(node)).sorted(Node::getName).map(Node::getLabel).toList();
        return RecommendationResult.valid(predecessors);
    }

    private Optional<Node> getNodeById(int productId) {
        for (Node node : nodeRefs.values()) {
            if (node.getProductId() == productId) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    // -------------------------- GRAPH CREATION --------------------------

    private Optional<Node> getOrCreateNode(String id, int programId) {
        Node node = nodeRefs.get(id);
        if (node == null) {
            if (isIdTaken(programId)) {
                return Optional.empty();
            }
            node = new Node(programId, id, NodeType.inferType(programId));
            nodeRefs.put(id, node);
        }
        return node.getProductId() == programId ? Optional.of(node) : Optional.empty();
    }

    private boolean isIdTaken(int productId) {
        return productId != PROGRAM_ID_CATEGORY && DataStream.of(nodeRefs.values()).anyMatch(node -> node.getProductId() == productId);
    }

    /**
     * Adds a new edge to the database graph, extrapolated from the given edge data.
     * Returns a command result if anything goes wrong. Attempts to create missing nodes, but will return a
     * failed result if for example the node exists with a different product id, or the product id is already
     * taken by another product.<br/>
     * Will contain a useful error message. A successful result has no message.
     *
     * @param edgeData The data of the edge to add.
     * @return The result.
     */
    public CommandResult addEdge(EdgeData edgeData) {
        EdgePair edges = getEdgePair(edgeData);
        if (!edges.valid()) {
            return CommandResult.failure(edges.errorMessage());
        }

        if (edgeExists(edges.edge())) {
            return CommandResult.failure(ERROR_EDGE_ALREADY_EXISTS);
        }

        edges.fromNode().addOutgoingEdge(edges.edge());
        edges.fromNode().addIncomingEdge(edges.inverseEdge());
        edges.toNode().addIncomingEdge(edges.edge());
        edges.toNode().addOutgoingEdge(edges.inverseEdge());
        return CommandResult.success();
    }

    /**
     * Removes an edge from the database graph, extrapolated from the given edge data.
     * Returns a command result if anything goes wrong. Attempts to find and delete the given edge, but will return a
     * failed result if the edge does not exist.<br/>
     * This also deletes nodes if there are no connection to or from the node post-deletion.<br/>
     * Will contain a useful error message. A successful result has no message.
     *
     * @param edgeData The data of the edge to add.
     * @return The result.
     */
    public CommandResult removeEdge(EdgeData edgeData) {
        EdgePair edges = getEdgePair(edgeData);
        if (!edges.valid()) {
            return CommandResult.failure(edges.errorMessage());
        }

        if (!edgeExists(edges.edge())) {
            return CommandResult.failure(ERROR_EDGE_DOESNT_EXIST);
        }

        edges.fromNode().removeOutgoingEdge(edges.edge());
        edges.fromNode().removeIncomingEdge(edges.inverseEdge());
        edges.toNode().removeIncomingEdge(edges.edge());
        edges.toNode().removeOutgoingEdge(edges.inverseEdge());

        if (edges.fromNode().hasNoConnections()) {
            nodeRefs.remove(edges.fromNode().getName());
        }
        if (edges.toNode().hasNoConnections()) {
            nodeRefs.remove(edges.toNode().getName());
        }

        return CommandResult.success();
    }

    private EdgePair getEdgePair(EdgeData edgeData) {
        Optional<Node> fromNodeOpt = getOrCreateNode(edgeData.fromNodeName(), edgeData.fromNodeId());
        Optional<Node> toNodeOpt = getOrCreateNode(edgeData.toNodeName(), edgeData.toNodeId());

        if (fromNodeOpt.isEmpty()) {
            return EdgePair.invalid(ERROR_SOURCE_HAS_INVALID_ID.formatted(edgeData.fromNodeName()));
        }
        if (toNodeOpt.isEmpty()) {
            return EdgePair.invalid(ERROR_TARGET_HAS_INVALID_ID.formatted(edgeData.toNodeName()));
        }

        Node fromNode = fromNodeOpt.get();
        Node toNode = toNodeOpt.get();

        if (fromNode.equals(toNode)) {
            return EdgePair.invalid(ERROR_SELF_CONNECTION);
        }

        Edge edge = new Edge(edgeData.type(), fromNode, toNode);

        Optional<String> invalidEdgeMessage = edge.checkValidity();
        if (invalidEdgeMessage.isPresent()) {
            return EdgePair.invalid(invalidEdgeMessage.get());
        }

        Edge inverseEdge = new Edge(EdgeType.getInverseType(edgeData.type()), toNode, fromNode);

        return EdgePair.valid(fromNode, toNode, edge, inverseEdge);
    }

    private static boolean edgeExists(Edge edge) {
        return edge.source().getOutgoingEdges().contains(edge);
    }

    private DataStream<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Node node : nodeRefs.values()) {
            edges.addAll(node.getOutgoingEdges());
        }
        return DataStream.of(edges);
    }

    /**
     * Formats this database graph to a digraph format and returns a list of the line-wise digraph, sorted.
     * @return A list of all the lines for the properly formatted digraph.
     */
    public List<String> formatDigraph() {
        List<String> output = new ArrayList<>(getEdges()
                .sorted(Edge::formatToSortOrder, edge -> edge.edgeType().getOrder())
                .map(edge -> FORMAT_EDGE_DIGRAPH
                .formatted(edge.source().getName(), edge.target().getName(), edge.edgeType().getDigraphLabelName()))
                .toList());
        output.addAll(DataStream.of(nodeRefs.values())
                .map(node -> node.getType() == NodeType.CATEGORY ? FORMAT_CATEGORY_DIGRAPH.formatted(node.getName()) : null)
                .filter(Objects::nonNull).sorted(Comparator.naturalOrder()).toList());
        return output;
    }

    /**
     * Formats all the edges, line-wise and sorted.
     * @return All edges, formatted and sorted, as a list of lines.
     */
    public List<String> formatEdges() {
        return getEdges().sorted(Edge::formatToSortOrder, edge -> edge.edgeType().getOrder())
                .map(edge -> FORMAT_EDGE_LIST
                        .formatted(edge.source().getLabel(), edge.edgeType().getEdgeDisplayName(), edge.target().getLabel()))
                .toList();
    }

    /**
     * Formats all the nodes, sorted.
     * @return All nodes, formatted and sorted, as a list of nodes.
     */
    public List<String> formatNodes() {
        return DataStream.of(nodeRefs.values()).sorted(Node::getName).map(Node::getLabel).toList();
    }
}
