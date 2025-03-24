package net.justonedev.model.graph;

import net.justonedev.model.stream.DataStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * The graph nodes for the {@linkplain DatabaseGraph}.
 * @author uwwfh
 */
class Node {
    private static final String PRODUCT_NODE_LABEL_FORMAT = "%s:%d";

    private final int productId;
    private final String name;
    private final NodeType type;

    private final List<Edge> outgoingEdges;
    private final List<Edge> incomingEdges;

    /**
     * Creates a new node with a product id, it's name (lowercase) and the node type.
     * If the node type is category, the product id should be {@linkplain DatabaseGraph#PROGRAM_ID_CATEGORY}.
     * @param productId The product id.
     * @param name The name of the product or category.
     * @param type The node type.
     */
    Node(int productId, String name, NodeType type) {
        this.productId = productId;
        this.name = name;
        this.type = type;
        outgoingEdges = new ArrayList<>();
        incomingEdges = new ArrayList<>();
    }

    /**
     * Gets a stream of all nodes that this node has an outgoing connection of the given {@linkplain EdgeType} to.
     * @param edgeType The edge type to filter outgoing edges by.
     * @return A {@linkplain DataStream} of all nodes where there is an edge from this node to the
     *     other node of the given type.
     */
    DataStream<Node> getNodesWith(EdgeType edgeType) {
        return DataStream.of(outgoingEdges).filter(edge -> edge.edgeType().equals(edgeType)).map(Edge::target);
    }

    /**
     * Gets all distinct products which are contained in this category. This function filters edges, so if this category
     * has no products or the node is itself a product, the method will return an empty list.
     * @return A list of all nodes which are contained in this category, or an empty list.
     */
    List<Node> getAllDirectlyContainedProducts() {
        return getNodesWith(EdgeType.CONTAINS).filter(node -> node.type == NodeType.PRODUCT).distinct().toList();
    }

    /**
     * Gets all products which are connected with a specified edge type, directly and indirectly. Works recursively through
     * graph traversal.
     * @param edgeType The edge type to look for.
     * @return A list of all nodes that have a path from this node to itself with edges of the given type.
     */
    List<Node> getAllProductsWithRecursively(EdgeType edgeType) {
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
        return DataStream.of(collectedNodes).toList();
    }

    /**
     * Adds an outgoing edge.
     * @param edge the edge to add.
     */
    void addOutgoingEdge(Edge edge) {
        outgoingEdges.add(edge);
    }

    /**
     * Adds an incoming edge.
     * @param edge the edge to add.
     */
    void addIncomingEdge(Edge edge) {
        incomingEdges.add(edge);
    }

    /**
     * Removes an outgoing edge.
     * @param edge the edge to remove.
     */
    void removeOutgoingEdge(Edge edge) {
        outgoingEdges.remove(edge);
    }

    /**
     * Removes an incoming edge.
     * @param edge the edge to remove.
     */
    void removeIncomingEdge(Edge edge) {
        incomingEdges.remove(edge);
    }

    /**
     * Returns true if the node has neither outgoing nor incoming connections.
     * @return If the node has no connections.
     */
    boolean hasNoConnections() {
        return outgoingEdges.isEmpty() && incomingEdges.isEmpty();
    }

    /**
     * Gets a copy of all outgoing edges.
     * @return A copy of the outgoing edges.
     */
    List<Edge> getOutgoingEdges() {
        return new ArrayList<>(outgoingEdges);
    }

    /**
     * Gets the name of the product or category.
     * @return The name of the product or category.
     */
    String getName() {
        return name;
    }

    /**
     * Gets the label of the product or category. For a category, it's the same as the name.
     * For a product, it's of the format &lt;productName&gt;:&lt;productId&gt;
     * @return The with-id-formatted name of the product or category.
     */
    String getLabel() {
        return type == NodeType.CATEGORY ? name : PRODUCT_NODE_LABEL_FORMAT.formatted(name, productId);
    }

    /**
     * Gets the node type.
     * @return The node type.
     */
    NodeType getType() {
        return type;
    }

    /**
     * Gets the product id. For categories, this is {@linkplain DatabaseGraph#PROGRAM_ID_CATEGORY}.
     * @return The product id.
     */
    int getProductId() {
        return productId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return productId == node.productId && Objects.equals(name, node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name);
    }
}
