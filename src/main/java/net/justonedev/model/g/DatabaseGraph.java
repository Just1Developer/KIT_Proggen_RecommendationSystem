package net.justonedev.model.g;

import net.justonedev.command.CommandResult;
import net.justonedev.model.FilterStrategy;
import net.justonedev.model.RecommendationResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DatabaseGraph {
    public static final int PROGRAM_ID_CATEGORY = -1;

    private final HashMap<String, Node> nodeRefs;

    public DatabaseGraph() {
        nodeRefs = new HashMap<>();
    }

    // -------------------------- GRAPH PROCESSING --------------------------

    public RecommendationResult getRecommendations(int productId, FilterStrategy filterStrategy) {
        Optional<Node> nodeOptional = getNodeById(productId);
        if (nodeOptional.isEmpty()) {
            return RecommendationResult.invalid("Could not find the product with id " + productId);
        }
        Node node = nodeOptional.get();
        return switch (filterStrategy) {
            case SIBLING -> getSiblings(node);
            case SUCCESSOR -> getSuccessors(node);
            case PREDECESSOR -> getPredecessors(node);
        };
    }

    private RecommendationResult getSiblings(Node node) {
        List<String> siblings = node.getNodesWith(EdgeType.CONTAINED_IN)
                .mapMulti((BiConsumer<? super Node, Consumer<Node>>) (n, consumer)
                        -> n.getAllDirectlyContainedProducts().forEach(consumer))
                .filter(n -> !n.equals(node)).sorted(Comparator.comparing(Node::getName)).map(Node::getLabel).toList();
        return RecommendationResult.valid(siblings);
    }

    private RecommendationResult getSuccessors(Node node) {
        // Not an accident with successor/predecessor. We follow this to get all the successors
        List<String> successors = node.getAllProductsWithRecursively(EdgeType.PREDECESSOR).stream()
                .filter(n -> !n.equals(node)).sorted(Comparator.comparing(Node::getName)).map(Node::getLabel).toList();
        return RecommendationResult.valid(successors);
    }

    private RecommendationResult getPredecessors(Node node) {
        // Not an accident with successor/predecessor. We follow this to get all the predecessors
        List<String> predecessors = node.getAllProductsWithRecursively(EdgeType.SUCCESSOR).stream()
                .filter(n -> !n.equals(node)).sorted(Comparator.comparing(Node::getName)).map(Node::getLabel).toList();
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
        return productId != PROGRAM_ID_CATEGORY && nodeRefs.values().stream().anyMatch(node -> node.getProductId() == productId);
    }

    public CommandResult addEdge(EdgeData edgeData) {
        EdgePair edges = getEdgePair(edgeData);
        if (!edges.valid()) {
            return CommandResult.failure(edges.errorMessage());
        }

        if (edgeExists(edges.edge())) {
            return CommandResult.failure("The given relation is already present in the database");
        }

        edges.fromNode().addOutgoingEdge(edges.edge());
        edges.fromNode().addIncomingEdge(edges.inverseEdge());
        edges.toNode().addIncomingEdge(edges.edge());
        edges.toNode().addOutgoingEdge(edges.inverseEdge());
        return CommandResult.success();
    }

    public CommandResult removeEdge(EdgeData edgeData) {
        EdgePair edges = getEdgePair(edgeData);
        if (!edges.valid()) {
            return CommandResult.failure(edges.errorMessage());
        }

        if (!edgeExists(edges.edge())) {
            return CommandResult.failure("The given relation is not present in the database");
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
            return EdgePair.invalid("Source node %s has an invalid program Id.".formatted(edgeData.fromNodeName()));
        }
        if (toNodeOpt.isEmpty()) {
            return EdgePair.invalid("Destination node %s has an invalid program Id.".formatted(edgeData.toNodeName()));
        }

        Node fromNode = fromNodeOpt.get();
        Node toNode = toNodeOpt.get();

        if (fromNode.equals(toNode)) {
            return EdgePair.invalid("Source and destination nodes can't be the same.");
        }

        Edge edge = new Edge(edgeData.type(), fromNode, toNode);

        Optional<String> invalidEdgeMessage = edge.isValid();
        if (invalidEdgeMessage.isPresent()) {
            return EdgePair.invalid(invalidEdgeMessage.get());
        }

        Edge inverseEdge = new Edge(EdgeType.getInverseType(edgeData.type()), toNode, fromNode);

        return EdgePair.valid(fromNode, toNode, edge, inverseEdge);
    }

    private boolean edgeExists(Edge edge) {
        return edge.source().getOutgoingEdgeListRef().contains(edge);
    }

    private Stream<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Node node : nodeRefs.values()) {
            edges.addAll(node.getOutgoingEdgeListRef());
        }
        return edges.stream();
    }

    public List<String> formatDigraph() {
        List<String> output = new ArrayList<>(getEdges()
                .sorted(Comparator.comparing(Edge::formatToSortOrder)
                        .thenComparing(edge -> edge.edgeType().getOrder()))
                .map(edge -> "%s -> %s [label=%s]"
                .formatted(edge.source().getName(), edge.target().getName(), edge.edgeType().getDigraphLabelName()))
                .toList());
        output.addAll(nodeRefs.values().stream().map(node -> node.getType() == NodeType.CATEGORY ? "%s [shape=box]".formatted(node.getName()) : null)
                .filter(Objects::nonNull).sorted().toList());
        return output;
    }

    public List<String> formatEdges() {
        return getEdges().sorted(Comparator.comparing(Edge::formatToSortOrder).thenComparing(edge -> edge.edgeType().getOrder())).map(edge -> "%s-[%s]->%s"
                        .formatted(edge.source().getLabel(), edge.edgeType().getEdgeDisplayName(), edge.target().getLabel()))
                .toList();
    }

    public List<String> formatNodes() {
        return nodeRefs.values().stream().sorted(Comparator.comparing(Node::getName)).map(Node::getLabel).toList();
    }
}
