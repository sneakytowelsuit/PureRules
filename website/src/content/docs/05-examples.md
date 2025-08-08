---
title: Examples
order: 5
---

### Evaluate one input

```java
var engine = PureRulesEngine.getDeterministicEngine(User::getId, List.of(/* conditions */));
var map = engine.evaluate(user); // Map<String, Boolean>
```

### Evaluate many inputs

```java
var result = engine.evaluateAll(users); // Map<UserId, Map<RuleId, Boolean>>
```
