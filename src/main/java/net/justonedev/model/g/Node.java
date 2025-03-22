package net.justonedev.model.g;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

class Node {
    private final int productId;
    private final String name;
    private final NodeType type;

    private final List<Edge> outgoingEdges;
    private final List<Edge> incomingEdges;

    public Node(int productId, String name, NodeType type) {
        this.productId = productId;
        this.name = name;
        this.type = type;
        outgoingEdges = new ArrayList<>();
        incomingEdges = new ArrayList<>();
    }

    public Stream<Node> getNodesWith(EdgeType edgeType) {
        return outgoingEdges.stream().filter(edge -> edge.edgeType().equals(edgeType)).map(Edge::target);
    }

    public List<Node> getAllDirectlyContainedProducts() {
        return getNodesWith(EdgeType.CONTAINS).filter(node -> node.type == NodeType.PRODUCT).distinct().toList();
    }

    public List<Node> getAllProductsWithRecursively(EdgeType edgeType) {
        Set<Node> collectedNodes = new HashSet<>();
        Queue<Node> remainingNodes = new LinkedList<>();
        remainingNodes.add(this);
        while (!remainingNodes.isEmpty()) {
            Node current = remainingNodes.poll();
            collectedNodes.add(current);
            current.getNodesWith(edgeType)
                    .filter(node -> !collectedNodes.contains(node))
                    .forEach(remainingNodes::add);
        }
        return collectedNodes.stream().toList();
    }

    public void addOutgoingEdge(Edge edge) {
        outgoingEdges.add(edge);
    }

    public void addIncomingEdge(Edge edge) {
        incomingEdges.add(edge);
    }

    public void removeOutgoingEdge(Edge edge) {
        outgoingEdges.remove(edge);
    }

    public void removeIncomingEdge(Edge edge) {
        incomingEdges.remove(edge);
    }

    public boolean hasNoConnections() {
        return outgoingEdges.isEmpty() && incomingEdges.isEmpty();
    }

    List<Edge> getOutgoingEdgeListRef() {
        return outgoingEdges;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return type == NodeType.CATEGORY ? name : "%s:%d".formatted(name, productId);
    }

    public NodeType getType() {
        return type;
    }

    public int getProductId() {
        return productId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return productId == node.productId && Objects.equals(name, node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name);
    }
}
