import type { LucideIcon } from "lucide-react";
import {
  Braces,
  Calculator,
  GitBranch,
  Plus,
  FileText,
  Sparkles,
  CircleStop,
} from "lucide-react";

export const BLOCK_TYPES = [
  "SET_VARIABLE",
  "MOD",
  "CONDITION",
  "INCREMENT",
  "PROMPT_TEMPLATE",
  "BEDROCK_INVOKE",
  "END",
] as const;

export type BlockType = (typeof BLOCK_TYPES)[number];

export type BlockMeta = {
  type: BlockType;
  label: string;
  description: string;
  accent: string;
  icon: LucideIcon;
};

export const BLOCK_META: Record<BlockType, BlockMeta> = {
  SET_VARIABLE: {
    type: "SET_VARIABLE",
    label: "Set variable",
    description: "Grava uma variável no contexto",
    accent: "#3b82f6",
    icon: Braces,
  },
  MOD: {
    type: "MOD",
    label: "Mod",
    description: "Resto da divisão",
    accent: "#14b8a6",
    icon: Calculator,
  },
  CONDITION: {
    type: "CONDITION",
    label: "Condition",
    description: "Ramifica true / false",
    accent: "#f59e0b",
    icon: GitBranch,
  },
  INCREMENT: {
    type: "INCREMENT",
    label: "Increment",
    description: "Soma 1 a uma variável",
    accent: "#8b5cf6",
    icon: Plus,
  },
  PROMPT_TEMPLATE: {
    type: "PROMPT_TEMPLATE",
    label: "Prompt template",
    description: "Interpola {{variáveis}}",
    accent: "#ec4899",
    icon: FileText,
  },
  BEDROCK_INVOKE: {
    type: "BEDROCK_INVOKE",
    label: "Bedrock invoke",
    description: "Chama o modelo no Bedrock",
    accent: "#f97316",
    icon: Sparkles,
  },
  END: {
    type: "END",
    label: "End",
    description: "Encerra e define o result",
    accent: "#ef4444",
    icon: CircleStop,
  },
};

export const CONDITION_OPERATORS = ["==", "!=", ">", "<", ">=", "<="] as const;

export type ConditionOperator = (typeof CONDITION_OPERATORS)[number];

export function defaultConfig(type: BlockType): Record<string, unknown> {
  switch (type) {
    case "SET_VARIABLE":
      return { variable: "", value: "" };
    case "MOD":
      return { left: "", right: "", resultVariable: "" };
    case "CONDITION":
      return { left: "", operator: "==", right: "" };
    case "INCREMENT":
      return { variable: "" };
    case "PROMPT_TEMPLATE":
      return { variable: "", template: "" };
    case "BEDROCK_INVOKE":
      return { promptVariable: "", outputVariable: "" };
    case "END":
      return { result: "" };
  }
}

export function isBlockType(value: string): value is BlockType {
  return (BLOCK_TYPES as readonly string[]).includes(value);
}
