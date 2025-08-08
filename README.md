# <img src="./thinking_duke.svg" width="30" alt="pure-rules-logo"/> PureRules: A JVM Rules Engine for Modern Engineering Teams

## Overview
PureRules is a JVM-based rules engine designed to offer a flexible, developer-friendly alternative to traditional engines like Drools. It provides a balance between generic, user-defined rules and robust lifecycle control, empowering engineering teams to manage rule evaluation with precision and clarity.

### Key Advantages Over Other JVM Rules Engines (e.g., Drools)
- **Simplicity & Flexibility:** PureRules allows you to define rules in a straightforward, user-friendly format (including JSON), reducing the learning curve and boilerplate code.
- **Lifecycle Control:** Unlike many engines that abstract away the evaluation process, PureRules gives engineering teams explicit control over the rule evaluation lifecycle, making it easier to debug, test, and extend.
- **Generic Rule Support:** Supports highly generic, user-defined rules, enabling a wide range of use cases without being tied to a specific domain or syntax.
- **Lightweight & Performant:** Minimal dependencies and a focus on performance make PureRules suitable for both small and large-scale applications.

## Deterministic vs Probabilistic Evaluation
PureRulesEngine supports two evaluation modes, each with distinct implementation and use cases:

| Aspect                | Deterministic Evaluation                | Probabilistic Evaluation                  |
|-----------------------|----------------------------------------|-------------------------------------------|
| Service Used          | DeterministicEvaluationService          | ProbabilisticEvaluationService            |
| Output Type           | Boolean (true/false)                    | Probability score (float), thresholded    |
| Use Case              | Strict rule matching                    | Fuzzy/uncertain rule matching             |
| Threshold             | Not applicable                          | Minimum probability threshold required    |
| Result Map            | Rule ID → Boolean                       | Rule ID → Boolean (after thresholding)    |

- **Deterministic Evaluation:**
  - Uses `DeterministicEvaluationService`.
  - Each rule or condition is evaluated as a strict boolean (true/false).
  - Results are based solely on whether the input satisfies the defined logic.
  - Returns a map of rule IDs to boolean results.

- **Probabilistic Evaluation:**
  - Uses `ProbabilisticEvaluationService`.
  - Each rule or condition produces a probability score (float, typically 0–1) representing the likelihood that the input satisfies the rule.
  - Applies a minimum probability threshold: if the score is below this threshold, the result is false; otherwise, true.
  - Useful for scenarios with uncertainty or partial matches.

## Getting Started

### Instantiating the PureRulesEngine
PureRulesEngine is instantiated using static factory methods. Rules (conditions) are provided at construction time and cannot be registered or changed dynamically after instantiation. This design ensures immutability and thread safety for rule evaluation.

#### Example: Deterministic Engine
```java
PureRulesEngine<MyInputType, MyInputIdType> engine =
    PureRulesEngine.getDeterministicEngine(input -> input.getId(), myConditionsList);
```

#### Example: Probabilistic Engine
```java
PureRulesEngine<MyInputType, MyInputIdType> engine =
    PureRulesEngine.getProbabilisticEngine(input -> input.getId(), 0.5f, myConditionsList);
```

### Loading Rules from JSON
PureRules supports loading rules from JSON, making it easy to manage rules externally or integrate with other systems. You can use the `RuleGroupSerde` class to deserialize JSON into rule groups (conditions), which can then be passed to the engine at instantiation.

1. **Define Your Rules in JSON:**
   ```json
   {
     "id": "ExampleGroup",
     "combinator": "AND",
     "inverted": false,
     "bias": "NONE",
     "conditions": [ /* ... */ ]
   }
   ```
2. **Deserialize and Instantiate Engine:**
   ```java
   RuleGroupSerde<MyInputType> serde = new RuleGroupSerde<>();
   RuleGroup<MyInputType> ruleGroup = serde.deserialize(jsonString);
   List<Condition<MyInputType>> conditions = List.of(ruleGroup);
   PureRulesEngine<MyInputType, MyInputIdType> engine =
       PureRulesEngine.getDeterministicEngine(input -> input.getId(), conditions);
   ```
3. **Evaluate as Usual:**
   ```java
   Map<String, Boolean> result = engine.evaluate(input);
   ```

## Example: Full Workflow
```java
// 1. Load rules from JSON
RuleGroupSerde<MyInputType> serde = new RuleGroupSerde<>();
RuleGroup<MyInputType> ruleGroup = serde.deserialize(jsonString);
List<Condition<MyInputType>> conditions = List.of(ruleGroup);

// 2. Create engine with static factory method
PureRulesEngine<MyInputType, MyInputIdType> engine =
    PureRulesEngine.getDeterministicEngine(input -> input.getId(), conditions);

// 3. Evaluate
Map<String, Boolean> result = engine.evaluate(input);
```

## Conclusion
PureRules is designed for teams that need both flexibility and control. By supporting generic, user-defined rules and offering explicit lifecycle management, it stands out as a modern alternative to heavyweight or opaque rules engines like Drools. Note that rules are immutable after engine instantiation, ensuring predictable and thread-safe evaluation.

For more details, see the API documentation or explore the source code.
