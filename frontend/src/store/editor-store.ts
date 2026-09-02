import {
  addEdge,
  applyEdgeChanges,
  applyNodeChanges,
  type Connection,
  type EdgeChange,
  type NodeChange,
} from "@xyflow/react";
import { create } from "zustand";
import { persist } from "zustand/middleware";
import { defaultConfig, type BlockType } from "@/lib/block-types";
import type { ExecuteVar, FlowEdge, FlowNode } from "@/lib/flow-types";

type EditorSnapshot = {
  flowId: string;
  flowName: string;
  saved: boolean;
  nodes: FlowNode[];
  edges: FlowEdge[];
  selectedNodeId: string | null;
  executeVars: ExecuteVar[];
  lastResult: unknown;
};

type EditorState = EditorSnapshot & {
  setFlowName: (name: string) => void;
  setSelectedNodeId: (id: string | null) => void;
  onNodesChange: (changes: NodeChange<FlowNode>[]) => void;
  onEdgesChange: (changes: EdgeChange<FlowEdge>[]) => void;
  onConnect: (connection: Connection) => void;
  addNode: (type: BlockType, position: { x: number; y: number }) => void;
  updateNodeConfig: (id: string, config: Record<string, unknown>) => void;
  updateNodeValueKind: (id: string, valueKind: FlowNode["data"]["valueKind"]) => void;
  setExecuteVars: (vars: ExecuteVar[]) => void;
  setLastResult: (result: unknown) => void;
  markSaved: () => void;
  resetNew: () => void;
};

function freshState(): EditorSnapshot {
  return {
    flowId: crypto.randomUUID(),
    flowName: "Untitled flow",
    saved: false,
    nodes: [],
    edges: [],
    selectedNodeId: null,
    executeVars: [
      {
        id: crypto.randomUUID(),
        key: "input",
        value: "17",
        valueType: "number",
      },
    ],
    lastResult: null,
  };
}

function connectionHandle(connection: Connection): string {
  return connection.sourceHandle ?? "next";
}

export const useEditorStore = create<EditorState>()(
  persist(
    (set, get) => ({
      ...freshState(),
      setFlowName: (flowName) => {
        if (get().saved) {
          return;
        }
        set({ flowName });
      },
      setSelectedNodeId: (selectedNodeId) => set({ selectedNodeId }),
      onNodesChange: (changes) => {
        const { saved, nodes } = get();
        const allowed = saved
          ? changes.filter((change) => change.type === "select")
          : changes;
        const next = applyNodeChanges(allowed, nodes);
        const selected = next.find((node) => node.selected);
        set({
          nodes: next,
          selectedNodeId: selected?.id ?? get().selectedNodeId,
        });
      },
      onEdgesChange: (changes) => {
        if (get().saved) {
          return;
        }
        set({ edges: applyEdgeChanges(changes, get().edges) });
      },
      onConnect: (connection) => {
        if (get().saved) {
          return;
        }
        const handle = connectionHandle(connection);
        const remaining = get().edges.filter(
          (edge) => !(edge.source === connection.source && (edge.sourceHandle ?? "next") === handle),
        );
        const label = handle === "true" || handle === "false" ? handle : undefined;
        set({
          edges: addEdge(
            {
              ...connection,
              sourceHandle: handle,
              label,
            },
            remaining,
          ),
        });
      },
      addNode: (type, position) => {
        if (get().saved) {
          return;
        }
        const id = crypto.randomUUID();
        const node: FlowNode = {
          id,
          type: "flowBlock",
          position,
          data: {
            blockType: type,
            config: defaultConfig(type),
            valueKind: type === "SET_VARIABLE" ? "string" : undefined,
          },
        };
        set({
          nodes: [...get().nodes, node],
          selectedNodeId: id,
        });
      },
      updateNodeConfig: (id, config) => {
        if (get().saved) {
          return;
        }
        set({
          nodes: get().nodes.map((node) =>
            node.id === id
              ? { ...node, data: { ...node.data, config: { ...node.data.config, ...config } } }
              : node,
          ),
        });
      },
      updateNodeValueKind: (id, valueKind) => {
        if (get().saved) {
          return;
        }
        set({
          nodes: get().nodes.map((node) =>
            node.id === id ? { ...node, data: { ...node.data, valueKind } } : node,
          ),
        });
      },
      setExecuteVars: (executeVars) => set({ executeVars }),
      setLastResult: (lastResult) => set({ lastResult }),
      markSaved: () => set({ saved: true }),
      resetNew: () => set(freshState()),
    }),
    {
      name: "flownocode-editor",
      skipHydration: true,
      partialize: (state) => ({
        flowId: state.flowId,
        flowName: state.flowName,
        saved: state.saved,
        nodes: state.nodes,
        edges: state.edges,
        selectedNodeId: state.selectedNodeId,
        executeVars: state.executeVars,
        lastResult: state.lastResult,
      }),
    },
  ),
);
