"use client";

import { Plus, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import type { ExecuteVarType } from "@/lib/flow-types";
import { useEditorStore } from "@/store/editor-store";

function isExecuteVarType(value: string): value is ExecuteVarType {
  return value === "string" || value === "number" || value === "boolean";
}

export function ExecutePanel() {
  const executeVars = useEditorStore((state) => state.executeVars);
  const setExecuteVars = useEditorStore((state) => state.setExecuteVars);
  const lastResult = useEditorStore((state) => state.lastResult);
  const saved = useEditorStore((state) => state.saved);

  const update = (id: string, patch: Partial<(typeof executeVars)[number]>) => {
    setExecuteVars(executeVars.map((item) => (item.id === id ? { ...item, ...patch } : item)));
  };

  return (
    <div className="flex flex-col gap-3 p-3">
      <div className="flex items-center justify-between">
        <p className="text-sm font-medium">Execute variables</p>
        <Button
          type="button"
          variant="outline"
          size="xs"
          onClick={() =>
            setExecuteVars([
              ...executeVars,
              { id: crypto.randomUUID(), key: "", value: "", valueType: "string" },
            ])
          }
        >
          <Plus />
          Par
        </Button>
      </div>
      <ul className="flex flex-col gap-2">
        {executeVars.map((item) => (
          <li key={item.id} className="grid grid-cols-[1fr_72px_1fr_auto] items-end gap-1.5">
            <div className="grid gap-1">
              <Label htmlFor={`key-${item.id}`} className="text-[10px]">
                chave
              </Label>
              <Input
                id={`key-${item.id}`}
                value={item.key}
                onChange={(event) => update(item.id, { key: event.target.value })}
              />
            </div>
            <div className="grid gap-1">
              <Label htmlFor={`type-${item.id}`} className="text-[10px]">
                tipo
              </Label>
              <Select
                value={item.valueType}
                onValueChange={(value) => {
                  if (value && isExecuteVarType(value)) {
                    update(item.id, { valueType: value });
                  }
                }}
              >
                <SelectTrigger id={`type-${item.id}`} className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="string">str</SelectItem>
                  <SelectItem value="number">num</SelectItem>
                  <SelectItem value="boolean">bool</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-1">
              <Label htmlFor={`value-${item.id}`} className="text-[10px]">
                valor
              </Label>
              <Input
                id={`value-${item.id}`}
                value={item.value}
                onChange={(event) => update(item.id, { value: event.target.value })}
              />
            </div>
            <Button
              type="button"
              variant="ghost"
              size="icon-sm"
              aria-label="Remover variável"
              onClick={() => setExecuteVars(executeVars.filter((entry) => entry.id !== item.id))}
            >
              <Trash2 />
            </Button>
          </li>
        ))}
      </ul>
      <Separator />
      <div>
        <p className="mb-1 text-[10px] font-semibold tracking-wide text-muted-foreground uppercase">
          Result
        </p>
        {lastResult === null || lastResult === undefined ? (
          <p className="text-xs text-muted-foreground">
            {saved ? "Ainda sem execução." : "Salve o flow para executar."}
          </p>
        ) : (
          <pre className="overflow-auto rounded-md bg-muted p-2 font-mono text-xs">
            {JSON.stringify(lastResult, null, 2)}
          </pre>
        )}
      </div>
    </div>
  );
}
