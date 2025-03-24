package net.justonedev.command;

import net.justonedev.model.graph.EdgeData;

/**
 * The action for what to do with edge data, usually used for adding or removed edges
 * in combination with a general parse-and-perform function.
 * @author uwwfh
 */
interface EdgeDataAction {
    /**
     * The action for what to do with edge data, usually used for adding or removed edges
     * in combination with a general parse-and-perform function.<br/>
     * Must recommend a command result.
     *
     * @param edgeData the edge data to do something with.
     * @return The result of running the arbitrary function.
     */
    CommandResult run(EdgeData edgeData);
}
