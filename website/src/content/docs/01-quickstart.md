---
title: Quickstart
order: 1
---

## Deterministic mode

```java
var engine = PureRulesEngine.getDeterministicEngine(User::getId, List.of(
  // your conditions
));

var resultByRuleId = engine.evaluate(user);
```

## Probabilistic mode

```java
var engine = PureRulesEngine.getProbabilisticEngine(User::getId, 0.6f, List.of(
  // your conditions
));

var resultByRuleId = engine.evaluate(user); // Map<String, Boolean>
```
