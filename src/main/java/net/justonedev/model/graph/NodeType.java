package net.justonedev.model.graph;

/**
 * The type of node in the database graph. Edge validity is based on node types.
 * @author uwwfh
 */
enum NodeType {
    /**
     * The node is a category and will have no associated product id.
     */
    CATEGORY,
    /**
     * The node represents a product and will have a positive unique product id.
     */
    PRODUCT;

    /**
     * Infers the type based on if the provided program id equals {@linkplain DatabaseGraph#PROGRAM_ID_CATEGORY} or not.
     * @param programId The program id.
     * @return The inferred node type.
     */
    static NodeType inferType(int programId) {
        return programId == DatabaseGraph.PROGRAM_ID_CATEGORY ? CATEGORY : PRODUCT;
    }
}
