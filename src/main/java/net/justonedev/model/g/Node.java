package net.justonedev.model.g;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
        // Streams don't allow for back-checking. If this leads to an infinite loop, re-do without streams
        return getNodesWith(edgeType).mapMulti((BiConsumer<? super Node, Consumer<Node>>) (node, consumer) -> {
            consumer.accept(node);
            node.getAllProductsWithRecursively(edgeType).forEach(consumer);
        }).distinct().toList();
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

    public Stream<Edge> getOutgoingEdges() {
        return outgoingEdges.stream();
    }

    List<Edge> getOutgoingEdgeListRef() {
        return outgoingEdges;
    }

    public Stream<Edge> getIncomingEdges() {
        return incomingEdges.stream();
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
