"use client";

import type { ReactNode } from "react";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { BLOCK_META, CONDITION_OPERATORS } from "@/lib/block-types";
import { useEditorStore } from "@/store/editor-store";

function Field({
  label,
  htmlFor,
  children,
}: {
  label: string;
  htmlFor: string;
  children: ReactNode;
}) {
  return (
    <div className="grid gap-1.5">
      <Label htmlFor={htmlFor}>{label}</Label>
      {children}
    </div>
  );
}

export function BlockInspector() {
  const selectedNodeId = useEditorStore((state) => state.selectedNodeId);
  const nodes = useEditorStore((state) => state.nodes);
  const saved = useEditorStore((state) => state.saved);
  const updateNodeConfig = useEditorStore((state) => state.updateNodeConfig);
  const updateNodeValueKind = useEditorStore((state) => state.updateNodeValueKind);

  const node = nodes.find((item) => item.id === selectedNodeId);

  if (!node) {
    return (
      <div className="px-3 py-4 text-xs text-muted-foreground">
        Selecione um bloco para editar o config.
      </div>
    );
  }

  const meta = BLOCK_META[node.data.blockType];
  const config = node.data.config;
  const disabled = saved;

  const set = (key: string, value: unknown) => {
    updateNodeConfig(node.id, { [key]: value });
  };

  return (
    <div className="flex flex-col gap-3 p-3">
      <div>
        <p className="text-sm font-medium">{meta.label}</p>
        <p className="font-mono text-[10px] text-muted-foreground break-all">{node.id}</p>
      </div>

      {node.data.blockType === "SET_VARIABLE" ? (
        <>
          <Field label="variable" htmlFor="variable">
            <Input
              id="variable"
              disabled={disabled}
              value={String(config.variable ?? "")}
              onChange={(event) => set("variable", event.target.value)}
            />
          </Field>
          <Field label="tipo do value" htmlFor="valueKind">
            <Select
              value={node.data.valueKind ?? "string"}
              onValueChange={(value) => {
                if (value === "string" || value === "number" || value === "boolean") {
                  updateNodeValueKind(node.id, value);
                }
              }}
              disabled={disabled}
            >
              <SelectTrigger id="valueKind" className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="string">string</SelectItem>
                <SelectItem value="number">number</SelectItem>
                <SelectItem value="boolean">boolean</SelectItem>
              </SelectContent>
            </Select>
          </Field>
          {node.data.valueKind === "boolean" ? (
            <Field label="value" htmlFor="value">
              <Select
                value={String(config.value ?? "true")}
                onValueChange={(value) => {
                  if (value === "true" || value === "false") {
                    set("value", value === "true");
                  }
                }}
                disabled={disabled}
              >
                <SelectTrigger id="value" className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="true">true</SelectItem>
                  <SelectItem value="false">false</SelectItem>
                </SelectContent>
              </Select>
            </Field>
          ) : (
            <Field label="value" htmlFor="value">
              <Input
                id="value"
                disabled={disabled}
                type={node.data.valueKind === "number" ? "number" : "text"}
                value={String(config.value ?? "")}
                onChange={(event) => {
                  if (node.data.valueKind === "number") {
                    const parsed = Number(event.target.value);
                    set("value", event.target.value === "" ? "" : parsed);
                    return;
                  }
                  set("value", event.target.value);
                }}
              />
            </Field>
          )}
        </>
      ) : null}

      {node.data.blockType === "MOD" ? (
        <>
          <Field label="left" htmlFor="left">
            <Input
              id="left"
              disabled={disabled}
              value={String(config.left ?? "")}
              onChange={(event) => set("left", event.target.value)}
            />
          </Field>
          <Field label="right" htmlFor="right">
            <Input
              id="right"
              disabled={disabled}
              value={String(config.right ?? "")}
              onChange={(event) => set("right", event.target.value)}
            />
          </Field>
          <Field label="resultVariable" htmlFor="resultVariable">
            <Input
              id="resultVariable"
              disabled={disabled}
              value={String(config.resultVariable ?? "")}
              onChange={(event) => set("resultVariable", event.target.value)}
            />
          </Field>
        </>
      ) : null}

      {node.data.blockType === "CONDITION" ? (
        <>
          <Field label="left" htmlFor="left">
            <Input
              id="left"
              disabled={disabled}
              value={String(config.left ?? "")}
              onChange={(event) => set("left", event.target.value)}
            />
          </Field>
          <Field label="operator" htmlFor="operator">
            <Select
              value={String(config.operator ?? "==")}
              onValueChange={(value) => {
                if (typeof value === "string") {
                  set("operator", value);
                }
              }}
              disabled={disabled}
            >
              <SelectTrigger id="operator" className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {CONDITION_OPERATORS.map((operator) => (
                  <SelectItem key={operator} value={operator}>
                    {operator}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="right" htmlFor="right">
            <Input
              id="right"
              disabled={disabled}
              value={String(config.right ?? "")}
              onChange={(event) => {
                const raw = event.target.value;
                const asNumber = Number(raw);
                set("right", raw !== "" && !Number.isNaN(asNumber) && raw.trim() !== "" ? (raw.match(/^-?\d+(\.\d+)?$/) ? asNumber : raw) : raw);
              }}
            />
          </Field>
          <p className="text-[11px] text-muted-foreground">
            Conecte as saídas true e false no canvas. Elas viram trueNextBlockId / falseNextBlockId no save.
          </p>
        </>
      ) : null}

      {node.data.blockType === "INCREMENT" ? (
        <Field label="variable" htmlFor="variable">
          <Input
            id="variable"
            disabled={disabled}
            value={String(config.variable ?? "")}
            onChange={(event) => set("variable", event.target.value)}
          />
        </Field>
      ) : null}

      {node.data.blockType === "PROMPT_TEMPLATE" ? (
        <>
          <Field label="variable" htmlFor="variable">
            <Input
              id="variable"
              disabled={disabled}
              value={String(config.variable ?? "")}
              onChange={(event) => set("variable", event.target.value)}
            />
          </Field>
          <Field label="template" htmlFor="template">
            <Textarea
              id="template"
              disabled={disabled}
              rows={5}
              value={String(config.template ?? "")}
              onChange={(event) => set("template", event.target.value)}
              placeholder="Analyze {{input}} for {{task}}"
            />
          </Field>
        </>
      ) : null}

      {node.data.blockType === "BEDROCK_INVOKE" ? (
        <>
          <Field label="promptVariable" htmlFor="promptVariable">
            <Input
              id="promptVariable"
              disabled={disabled}
              value={String(config.promptVariable ?? "")}
              onChange={(event) => set("promptVariable", event.target.value)}
            />
          </Field>
          <Field label="outputVariable" htmlFor="outputVariable">
            <Input
              id="outputVariable"
              disabled={disabled}
              value={String(config.outputVariable ?? "")}
              onChange={(event) => set("outputVariable", event.target.value)}
            />
          </Field>
        </>
      ) : null}

      {node.data.blockType === "END" ? (
        <Field label="result" htmlFor="result">
          <Input
            id="result"
            disabled={disabled}
            value={String(config.result ?? "")}
            onChange={(event) => set("result", event.target.value)}
            placeholder="modelOutput ou um literal"
          />
        </Field>
      ) : null}
    </div>
  );
}
