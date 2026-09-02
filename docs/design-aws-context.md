# AWS design context

Concept first. Implement an item only when explicitly asked.

Reference diagram: `docs/workflow-ai-bedrock-runtime.drawio`

## Sequence

1. Conta/região, VPC, subnets (público vs privado)
2. RDS PostgreSQL privado
3. ECS/Fargate + ALB (API Spring Boot)
4. IAM: ECS task role `bedrock:InvokeModel` + logs
5. API Java + Bedrock (modelo central, AWS SDK)
6. CloudWatch (logs sanitizados, sem prompt)
7. Só então o código dos blocos de IA
