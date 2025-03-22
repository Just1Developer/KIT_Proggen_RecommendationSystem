package net.justonedev.model.g;

enum NodeType {
    CATEGORY,
    PRODUCT;

    static NodeType inferType(int programId) {
        return programId == DatabaseGraph.PROGRAM_ID_CATEGORY ? CATEGORY : PRODUCT;
    }
}
