import type {
  ApiErrorBody,
  CreateFlowRequest,
  FlowExecutionResponse,
  FlowResponse,
} from "@/lib/flow-types";

async function readError(response: Response): Promise<string> {
  try {
    const body = (await response.json()) as ApiErrorBody;
    if (body.message) {
      return body.message;
    }
  } catch {
    // ignore parse errors
  }
  return `Erro ${response.status} ${response.statusText}`;
}

export async function createFlow(request: CreateFlowRequest): Promise<FlowResponse> {
  const response = await fetch("/api/flows", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(request),
  });
  if (!response.ok) {
    throw new Error(await readError(response));
  }
  return (await response.json()) as FlowResponse;
}

export async function executeFlow(
  flowId: string,
  variables: Record<string, unknown>,
): Promise<FlowExecutionResponse> {
  const response = await fetch(`/api/flows/${flowId}/execute`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(variables),
  });
  if (!response.ok) {
    throw new Error(await readError(response));
  }
  return (await response.json()) as FlowExecutionResponse;
}
