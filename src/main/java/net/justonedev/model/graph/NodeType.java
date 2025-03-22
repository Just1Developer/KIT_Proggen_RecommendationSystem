package net.justonedev.model.graph;

enum NodeType {
    CATEGORY,
    PRODUCT;

    static NodeType inferType(int programId) {
        return programId == DatabaseGraph.PROGRAM_ID_CATEGORY ? CATEGORY : PRODUCT;
    }
}
