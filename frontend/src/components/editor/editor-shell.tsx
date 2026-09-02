"use client";

import { EditorHeader } from "@/components/editor/editor-header";
import { ExecutePanel } from "@/components/editor/execute-panel";
import { FlowCanvas } from "@/components/editor/flow-canvas";
import { BlockInspector } from "@/components/editor/inspector/block-inspector";
import { NodePalette } from "@/components/editor/node-palette";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { useEditorHydrated } from "@/hooks/use-editor-hydrated";

export function EditorShell() {
  const ready = useEditorHydrated();

  if (!ready) {
    return (
      <div className="flex h-dvh items-center justify-center bg-background text-sm text-muted-foreground">
        Carregando editor…
      </div>
    );
  }

  return (
    <div className="flex h-dvh flex-col bg-background">
      <EditorHeader />
      <div className="flex min-h-0 flex-1">
        <NodePalette />
        <FlowCanvas />
        <aside className="flex w-80 shrink-0 flex-col border-l border-border bg-card">
          <div className="border-b border-border px-3 py-2 text-[11px] font-semibold tracking-wide text-muted-foreground uppercase">
            Parameters
          </div>
          <ScrollArea className="h-[45%] min-h-0">
            <BlockInspector />
          </ScrollArea>
          <Separator />
          <ScrollArea className="min-h-0 flex-1">
            <ExecutePanel />
          </ScrollArea>
        </aside>
      </div>
    </div>
  );
}
