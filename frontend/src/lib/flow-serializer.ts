import type { BlockType } from "@/lib/block-types";
import type {
  CreateBlockRequest,
  CreateFlowRequest,
  FlowEdge,
  FlowNode,
} from "@/lib/flow-types";

export type SerializeOk = { ok: true; request: CreateFlowRequest };
export type SerializeErr = { ok: false; error: string };
export type SerializeResult = SerializeOk | SerializeErr;

function coerceLiteral(value: unknown): unknown {
  if (typeof value !== "string") {
    return value;
  }
  if (value === "true") {
    return true;
  }
  if (value === "false") {
    return false;
  }
  if (/^-?\d+(\.\d+)?$/.test(value)) {
    return Number(value);
  }
  return value;
}

function requiredString(config: Record<string, unknown>, field: string): string | null {
  const value = config[field];
  if (typeof value !== "string" || value.trim() === "") {
    return null;
  }
  return value;
}

function validateConfig(type: BlockType, config: Record<string, unknown>): string | null {
  switch (type) {
    case "SET_VARIABLE":
      if (!requiredString(config, "variable")) {
        return "SET_VARIABLE exige config.variable";
      }
      if (config.value === undefined || config.value === "") {
        return "SET_VARIABLE exige config.value";
      }
      return null;
    case "MOD":
      if (!requiredString(config, "left") && typeof config.left !== "number") {
        return "MOD exige config.left";
      }
      if (!requiredString(config, "right") && typeof config.right !== "number") {
        return "MOD exige config.right";
      }
      if (!requiredString(config, "resultVariable")) {
        return "MOD exige config.resultVariable";
      }
      return null;
    case "CONDITION":
      if (config.left === undefined || config.left === "") {
        return "CONDITION exige config.left";
      }
      if (config.right === undefined || config.right === "") {
        return "CONDITION exige config.right";
      }
      if (typeof config.operator !== "string" || config.operator.trim() === "") {
        return "CONDITION exige config.operator";
      }
      return null;
    case "INCREMENT":
      if (!requiredString(config, "variable")) {
        return "INCREMENT exige config.variable";
      }
      return null;
    case "PROMPT_TEMPLATE":
      if (!requiredString(config, "variable")) {
        return "PROMPT_TEMPLATE exige config.variable";
      }
      if (!requiredString(config, "template")) {
        return "PROMPT_TEMPLATE exige config.template";
      }
      return null;
    case "BEDROCK_INVOKE":
      if (!requiredString(config, "promptVariable")) {
        return "BEDROCK_INVOKE exige config.promptVariable";
      }
      if (!requiredString(config, "outputVariable")) {
        return "BEDROCK_INVOKE exige config.outputVariable";
      }
      return null;
    case "END":
      if (config.result === undefined || config.result === "") {
        return "END exige config.result";
      }
      return null;
  }
}

function outgoingHandle(edge: FlowEdge): string {
  return edge.sourceHandle ?? "next";
}

export function serializeFlow(
  flowId: string,
  flowName: string,
  nodes: FlowNode[],
  edges: FlowEdge[],
): SerializeResult {
  const name = flowName.trim();
  if (!name) {
    return { ok: false, error: "O flow precisa de um nome." };
  }
  if (nodes.length === 0) {
    return { ok: false, error: "Adicione pelo menos um bloco." };
  }
  if (!nodes.some((node) => node.data.blockType === "END")) {
    return { ok: false, error: "O flow precisa de um bloco END." };
  }

  const incoming = new Set(edges.map((edge) => edge.target));
  const starts = nodes.filter((node) => !incoming.has(node.id));
  if (starts.length === 0) {
    return {
      ok: false,
      error: "Nenhum bloco inicial: todos têm entrada. Remova um ciclo ou uma conexão.",
    };
  }
  if (starts.length > 1) {
    return {
      ok: false,
      error: `Há ${starts.length} blocos sem entrada. Deixe só um início (startBlockId).`,
    };
  }

  const blocks: CreateBlockRequest[] = [];

  for (const node of nodes) {
    const { blockType, config: nodeConfig } = node.data;
    const configError = validateConfig(blockType, nodeConfig);
    if (configError) {
      return { ok: false, error: `${blockType}: ${configError}` };
    }

    const outgoing = edges.filter((edge) => edge.source === node.id);
    const config = { ...nodeConfig };

    if (blockType === "MOD" || blockType === "CONDITION") {
      config.left = coerceLiteral(config.left);
      config.right = coerceLiteral(config.right);
    }

    if (blockType === "CONDITION") {
      const trueEdge = outgoing.find((edge) => outgoingHandle(edge) === "true");
      const falseEdge = outgoing.find((edge) => outgoingHandle(edge) === "false");
      if (!trueEdge || !falseEdge) {
        return {
          ok: false,
          error: "CONDITION precisa das duas saídas: true e false.",
        };
      }
      config.trueNextBlockId = trueEdge.target;
      config.falseNextBlockId = falseEdge.target;
      blocks.push({
        id: node.id,
        type: blockType,
        config,
      });
      continue;
    }

    if (blockType === "END") {
      config.result = coerceLiteral(config.result);
      blocks.push({
        id: node.id,
        type: blockType,
        config,
      });
      continue;
    }

    if (outgoing.length !== 1) {
      return {
        ok: false,
        error: `${blockType} precisa de exatamente uma conexão de saída.`,
      };
    }

    blocks.push({
      id: node.id,
      type: blockType,
      nextBlockId: outgoing[0].target,
      config,
    });
  }

  return {
    ok: true,
    request: {
      id: flowId,
      name,
      startBlockId: starts[0].id,
      blocks,
    },
  };
}

export function parseExecuteValue(
  raw: string,
  valueType: "string" | "number" | "boolean",
): unknown {
  if (valueType === "number") {
    const parsed = Number(raw);
    if (Number.isNaN(parsed)) {
      throw new Error(`Valor numérico inválido: ${raw}`);
    }
    return parsed;
  }
  if (valueType === "boolean") {
    if (raw === "true") {
      return true;
    }
    if (raw === "false") {
      return false;
    }
    throw new Error(`Valor boolean inválido: ${raw}`);
  }
  return raw;
}
