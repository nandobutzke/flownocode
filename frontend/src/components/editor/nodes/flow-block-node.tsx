"use client";

import { Handle, Position, type NodeProps } from "@xyflow/react";
import { memo } from "react";
import { BLOCK_META } from "@/lib/block-types";
import type { FlowNode } from "@/lib/flow-types";
import { cn } from "@/lib/utils";

function FlowBlockNodeComponent({ data, selected }: NodeProps<FlowNode>) {
  const meta = BLOCK_META[data.blockType];
  const Icon = meta.icon;
  const isCondition = data.blockType === "CONDITION";
  const isEnd = data.blockType === "END";

  return (
    <div
      className={cn(
        "min-w-[200px] overflow-hidden rounded-md border bg-card shadow-md",
        selected ? "border-primary ring-2 ring-primary/40" : "border-border",
      )}
    >
      <Handle
        type="target"
        position={Position.Left}
        className="!size-2.5 !border-2 !border-background !bg-muted-foreground"
      />
      <div
        className="flex items-center gap-2 px-3 py-1.5 text-xs font-semibold text-white"
        style={{ backgroundColor: meta.accent }}
      >
        <Icon className="size-3.5" aria-hidden />
        <span>{meta.label}</span>
      </div>
      <div className="px-3 py-2 text-[11px] text-muted-foreground">{meta.description}</div>
      {isCondition ? (
        <>
          <Handle
            type="source"
            id="true"
            position={Position.Right}
            style={{ top: "38%" }}
            className="!size-2.5 !border-2 !border-background !bg-emerald-500"
          />
          <Handle
            type="source"
            id="false"
            position={Position.Right}
            style={{ top: "70%" }}
            className="!size-2.5 !border-2 !border-background !bg-rose-500"
          />
          <div className="flex justify-end gap-3 px-3 pb-2 text-[10px] font-medium">
            <span className="text-emerald-500">true</span>
            <span className="text-rose-500">false</span>
          </div>
        </>
      ) : null}
      {!isCondition && !isEnd ? (
        <Handle
          type="source"
          id="next"
          position={Position.Right}
          className="!size-2.5 !border-2 !border-background !bg-muted-foreground"
        />
      ) : null}
    </div>
  );
}

export const FlowBlockNode = memo(FlowBlockNodeComponent);
