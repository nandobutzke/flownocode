"use client";

import { BLOCK_META, BLOCK_TYPES } from "@/lib/block-types";
import { useEditorStore } from "@/store/editor-store";
import { cn } from "@/lib/utils";

export const PALETTE_MIME = "application/flownocode-block";

export function NodePalette() {
  const saved = useEditorStore((state) => state.saved);

  return (
    <aside className="flex h-full w-56 shrink-0 flex-col border-r border-border bg-sidebar">
      <div className="border-b border-border px-3 py-2 text-[11px] font-semibold tracking-wide text-muted-foreground uppercase">
        Nodes
      </div>
      <ul className="flex flex-1 flex-col gap-1 overflow-y-auto p-2">
        {BLOCK_TYPES.map((type) => {
          const meta = BLOCK_META[type];
          const Icon = meta.icon;
          return (
            <li key={type}>
              <button
                type="button"
                disabled={saved}
                draggable={!saved}
                onDragStart={(event) => {
                  event.dataTransfer.setData(PALETTE_MIME, type);
                  event.dataTransfer.effectAllowed = "move";
                }}
                className={cn(
                  "flex w-full items-start gap-2 rounded-md border border-transparent px-2 py-2 text-left text-sm transition-colors",
                  saved
                    ? "cursor-not-allowed opacity-50"
                    : "hover:border-border hover:bg-sidebar-accent",
                )}
              >
                <span
                  className="mt-0.5 flex size-6 shrink-0 items-center justify-center rounded-sm text-white"
                  style={{ backgroundColor: meta.accent }}
                >
                  <Icon className="size-3.5" aria-hidden />
                </span>
                <span>
                  <span className="block text-xs font-medium text-foreground">{meta.label}</span>
                  <span className="block text-[11px] text-muted-foreground">{meta.description}</span>
                </span>
              </button>
            </li>
          );
        })}
      </ul>
    </aside>
  );
}
