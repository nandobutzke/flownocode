"use client";

import {
  Background,
  BackgroundVariant,
  Controls,
  ReactFlow,
  ReactFlowProvider,
  useReactFlow,
  type Connection,
  type IsValidConnection,
} from "@xyflow/react";
import { useCallback, type DragEvent } from "react";
import { FlowBlockNode } from "@/components/editor/nodes/flow-block-node";
import { PALETTE_MIME } from "@/components/editor/node-palette";
import { isBlockType } from "@/lib/block-types";
import type { FlowEdge, FlowNode } from "@/lib/flow-types";
import { useEditorStore } from "@/store/editor-store";

import "@xyflow/react/dist/style.css";

const nodeTypes = {
  flowBlock: FlowBlockNode,
};

function FlowCanvasInner() {
  const nodes = useEditorStore((state) => state.nodes);
  const edges = useEditorStore((state) => state.edges);
  const saved = useEditorStore((state) => state.saved);
  const onNodesChange = useEditorStore((state) => state.onNodesChange);
  const onEdgesChange = useEditorStore((state) => state.onEdgesChange);
  const onConnect = useEditorStore((state) => state.onConnect);
  const addNode = useEditorStore((state) => state.addNode);
  const setSelectedNodeId = useEditorStore((state) => state.setSelectedNodeId);
  const { screenToFlowPosition } = useReactFlow();

  const isValidConnection = useCallback<IsValidConnection<FlowEdge>>(
    (connection: Connection | FlowEdge) => {
      const source = nodes.find((node) => node.id === connection.source);
      if (!source || source.data.blockType === "END") {
        return false;
      }
      if (source.data.blockType === "CONDITION") {
        return connection.sourceHandle === "true" || connection.sourceHandle === "false";
      }
      return connection.sourceHandle === "next" || connection.sourceHandle == null;
    },
    [nodes],
  );

  const onDragOver = useCallback((event: DragEvent) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = "move";
  }, []);

  const onDrop = useCallback(
    (event: DragEvent) => {
      event.preventDefault();
      if (saved) {
        return;
      }
      const type = event.dataTransfer.getData(PALETTE_MIME);
      if (!isBlockType(type)) {
        return;
      }
      const position = screenToFlowPosition({
        x: event.clientX,
        y: event.clientY,
      });
      addNode(type, position);
    },
    [addNode, saved, screenToFlowPosition],
  );

  return (
    <ReactFlow<FlowNode, FlowEdge>
      nodes={nodes}
      edges={edges}
      nodeTypes={nodeTypes}
      onNodesChange={onNodesChange}
      onEdgesChange={onEdgesChange}
      onConnect={onConnect}
      isValidConnection={isValidConnection}
      onDrop={onDrop}
      onDragOver={onDragOver}
      onNodeClick={(_event, node) => setSelectedNodeId(node.id)}
      onPaneClick={() => setSelectedNodeId(null)}
      nodesDraggable={!saved}
      nodesConnectable={!saved}
      elementsSelectable
      deleteKeyCode={saved ? null : ["Backspace", "Delete"]}
      fitView
      proOptions={{ hideAttribution: true }}
      className="bg-[var(--canvas)]"
    >
      <Background variant={BackgroundVariant.Dots} gap={18} size={1} />
      <Controls />
    </ReactFlow>
  );
}

export function FlowCanvas() {
  return (
    <div className="relative h-full min-w-0 flex-1">
      <ReactFlowProvider>
        <FlowCanvasInner />
      </ReactFlowProvider>
    </div>
  );
}
