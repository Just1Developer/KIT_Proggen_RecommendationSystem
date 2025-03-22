package net.justonedev.command;

import net.justonedev.model.graph.EdgeData;

interface EdgeDataAction {
    CommandResult run(EdgeData edgeData);
}
