import type { Edge, Node } from "@xyflow/react";
import type { BlockType } from "@/lib/block-types";

export type FlowNodeData = {
  blockType: BlockType;
  config: Record<string, unknown>;
  valueKind?: "string" | "number" | "boolean";
};

export type FlowNode = Node<FlowNodeData, "flowBlock">;
export type FlowEdge = Edge;

export type CreateBlockRequest = {
  id: string;
  type: BlockType;
  nextBlockId?: string | null;
  config: Record<string, unknown>;
};

export type CreateFlowRequest = {
  id: string;
  name: string;
  startBlockId: string;
  blocks: CreateBlockRequest[];
};

export type FlowResponse = {
  id: string;
  name: string;
  startBlockId: string;
  blockCount: number;
  createdAt: string;
};

export type FlowExecutionResponse = {
  flowId: string;
  flowName: string;
  output: { result?: unknown } & Record<string, unknown>;
};

export type ApiErrorBody = {
  status?: number;
  error?: string;
  message?: string;
};

export type ExecuteVarType = "string" | "number" | "boolean";

export type ExecuteVar = {
  id: string;
  key: string;
  value: string;
  valueType: ExecuteVarType;
};
