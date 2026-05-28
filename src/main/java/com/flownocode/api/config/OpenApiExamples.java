package com.flownocode.api.config;

public final class OpenApiExamples {

    private OpenApiExamples() {
    }

    public static final String CREATE_FLOW_REQUEST = """
            {
              "id": "550e8400-e29b-41d4-a716-446655440000",
              "name": "Prime Number Validation Flow",
              "startBlockId": "11111111-1111-1111-1111-111111111111",
              "blocks": [
                {
                  "id": "11111111-1111-1111-1111-111111111111",
                  "type": "SET_VARIABLE",
                  "nextBlockId": "22222222-2222-2222-2222-222222222222",
                  "config": {
                    "variable": "divisor",
                    "value": 2
                  }
                },
                {
                  "id": "22222222-2222-2222-2222-222222222222",
                  "type": "MOD",
                  "nextBlockId": "33333333-3333-3333-3333-333333333333",
                  "config": {
                    "left": "input",
                    "right": "divisor",
                    "resultVariable": "remainder"
                  }
                },
                {
                  "id": "33333333-3333-3333-3333-333333333333",
                  "type": "CONDITION",
                  "config": {
                    "left": "remainder",
                    "operator": "==",
                    "right": 0,
                    "trueNextBlockId": "44444444-4444-4444-4444-444444444444",
                    "falseNextBlockId": "55555555-5555-5555-5555-555555555555"
                  }
                },
                {
                  "id": "44444444-4444-4444-4444-444444444444",
                  "type": "END",
                  "config": {
                    "result": false
                  }
                },
                {
                  "id": "55555555-5555-5555-5555-555555555555",
                  "type": "INCREMENT",
                  "nextBlockId": "66666666-6666-6666-6666-666666666666",
                  "config": {
                    "variable": "divisor"
                  }
                },
                {
                  "id": "66666666-6666-6666-6666-666666666666",
                  "type": "CONDITION",
                  "config": {
                    "left": "divisor",
                    "operator": "<",
                    "right": "input",
                    "trueNextBlockId": "22222222-2222-2222-2222-222222222222",
                    "falseNextBlockId": "77777777-7777-7777-7777-777777777777"
                  }
                },
                {
                  "id": "77777777-7777-7777-7777-777777777777",
                  "type": "END",
                  "config": {
                    "result": true
                  }
                }
              ]
            }
            """;

    public static final String CREATE_FLOW_RESPONSE = """
            {
              "id": "550e8400-e29b-41d4-a716-446655440000",
              "name": "Prime Number Validation Flow",
              "startBlockId": "11111111-1111-1111-1111-111111111111",
              "blockCount": 7,
              "createdAt": "2026-05-27T00:00:00"
            }
            """;

    public static final String EXECUTE_FLOW_REQUEST = """
            {
              "input": 4
            }
            """;

    public static final String EXECUTE_FLOW_RESPONSE = """
            {
              "flowId": "550e8400-e29b-41d4-a716-446655440000",
              "flowName": "Prime Number Validation Flow",
              "output": {
                "result": false
              }
            }
            """;

    public static final String ERROR_CONFLICT = """
            {
              "status": 409,
              "error": "Conflict",
              "message": "Flow already exists with id: 550e8400-e29b-41d4-a716-446655440000",
              "timestamp": "2026-05-27T00:00:00"
            }
            """;

    public static final String ERROR_NOT_FOUND = """
            {
              "status": 404,
              "error": "Not Found",
              "message": "Flow not found with id: 550e8400-e29b-41d4-a716-446655440000",
              "timestamp": "2026-05-27T00:00:00"
            }
            """;

    public static final String ERROR_BUSINESS = """
            {
              "status": 422,
              "error": "Business Error",
              "message": "startBlockId does not match any block id in the blocks list",
              "timestamp": "2026-05-27T00:00:00"
            }
            """;
}
