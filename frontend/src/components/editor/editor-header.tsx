"use client";

import { Loader2, Play, Plus, Save } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { ThemeToggle } from "@/components/editor/theme-toggle";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { createFlow, executeFlow } from "@/lib/api";
import { parseExecuteValue, serializeFlow } from "@/lib/flow-serializer";
import { useEditorStore } from "@/store/editor-store";

export function EditorHeader() {
  const flowName = useEditorStore((state) => state.flowName);
  const flowId = useEditorStore((state) => state.flowId);
  const saved = useEditorStore((state) => state.saved);
  const nodes = useEditorStore((state) => state.nodes);
  const edges = useEditorStore((state) => state.edges);
  const executeVars = useEditorStore((state) => state.executeVars);
  const setFlowName = useEditorStore((state) => state.setFlowName);
  const markSaved = useEditorStore((state) => state.markSaved);
  const resetNew = useEditorStore((state) => state.resetNew);
  const setLastResult = useEditorStore((state) => state.setLastResult);
  const [saving, setSaving] = useState(false);
  const [executing, setExecuting] = useState(false);

  const onSave = async () => {
    const result = serializeFlow(flowId, flowName, nodes, edges);
    if (!result.ok) {
      toast.error(result.error);
      return;
    }
    setSaving(true);
    try {
      await createFlow(result.request);
      markSaved();
      toast.success("Flow salvo. O canvas ficou somente leitura.");
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Falha ao salvar");
    } finally {
      setSaving(false);
    }
  };

  const onExecute = async () => {
    if (!saved) {
      toast.error("Salve o flow antes de executar.");
      return;
    }
    const variables: Record<string, unknown> = {};
    try {
      for (const item of executeVars) {
        if (!item.key.trim()) {
          continue;
        }
        variables[item.key.trim()] = parseExecuteValue(item.value, item.valueType);
      }
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Variável inválida");
      return;
    }
    setExecuting(true);
    try {
      const response = await executeFlow(flowId, variables);
      setLastResult(response.output);
      toast.success("Execução concluída");
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Falha ao executar");
    } finally {
      setExecuting(false);
    }
  };

  const onNew = () => {
    if (!window.confirm("Descartar o draft atual e começar um flow novo?")) {
      return;
    }
    resetNew();
    toast.message("Novo flow criado");
  };

  return (
    <header className="flex h-12 shrink-0 items-center gap-3 border-b border-border bg-card px-3">
      <p className="text-sm font-semibold tracking-tight">Flow No Code</p>
      <Input
        aria-label="Nome do flow"
        value={flowName}
        disabled={saved}
        onChange={(event) => setFlowName(event.target.value)}
        className="h-8 max-w-xs"
      />
      <Badge variant={saved ? "default" : "secondary"}>{saved ? "salvo" : "draft"}</Badge>
      <div className="ml-auto flex items-center gap-2">
        <Button type="button" variant="outline" size="sm" onClick={onNew}>
          <Plus />
          New
        </Button>
        <Button type="button" size="sm" onClick={() => void onSave()} disabled={saved || saving}>
          {saving ? <Loader2 className="animate-spin" /> : <Save />}
          Save
        </Button>
        <Button type="button" size="sm" onClick={() => void onExecute()} disabled={!saved || executing}>
          {executing ? <Loader2 className="animate-spin" /> : <Play />}
          Execute
        </Button>
        <ThemeToggle />
      </div>
    </header>
  );
}
