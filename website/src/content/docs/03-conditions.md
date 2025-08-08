---
title: Conditions & Groups
order: 3
---

PureRules composes logic using `Condition<T>` implemented by `Rule` and `RuleGroup`.

- Rule: a single check on a `Field` using an `Operator` and a value
- RuleGroup: a set of conditions combined with a `Combinator` (AND / OR)

Each `Condition` exposes a `getId()` and `getWeight()`:

- `id` lets you map results to domain concepts
- `weight` tunes influence in probabilistic mode (ignored in deterministic)

Example outline:

```java
Rule<User> emailIsGmail = Rule.of(
  "email_is_gmail",
  Field.of("email"),
  new StringEndsWithOperator(),
  "@gmail.com",
  2 // weight
);

RuleGroup<User> profileComplete = RuleGroup.and(
  "profile_complete",
  emailIsGmail,
  // ...other rules
);
```
