---
title: PureRulesEngine Instantiation Example
excerpt: How to instantiate a Pure Rules Engine instance.
tags:
  - examples
  - pure-rules-engine
  - instantiation
  - deserialization
---

# Creating a Pure Rules Engine Instance
The code snippet below demonstrates a "complete" example of how to instantiate a Deterministic Pure Rules Engine
using the `RuleGroupSerde` class to deserialize what is expected to be a valid JSON response from an API.
```java
import com.github.sneakytowelsuit.purerules.PureRulesEngine;

public class Example {
    private class InputType {
        private String name;
        private int id;
        public InputType(String name, int id) {
            this.name = name;
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public int getId() {
            return id;
        }
    }
    public static void main(String[] args) {
        String conditionsApiResponse = """
                [
                    {
                        "field": "DummyField",
                        "operator": "DummyOperator",
                        "value": {"class": "java.lang.String", "value": "foo"}
                    },
                    {
                        conditions: [
                            {
                                "field": "DummyField1",
                                "operator": "DummyOperator1",
                                "value": {"class": "java.lang.String", "value": "bar"}
                            },
                            {
                                "field": "DummyField2",
                                "operator": "DummyOperator2",
                                "value": {"class": "java.lang.Integer", "value": 42}
                            }
                        ],
                    }
                ];
            """;
        RuleGroupSerde serde = new RuleGroupSerde<>();
        List<RuleGroup<InputType>> ruleGroups = serde.deserialize(conditionsApiResponse);
        PureRulesEngine<Example> engine = PureRulesEngine.getDeterministicEngine(
               InputType::getId,
               ruleGroups
        );
        List<InputType> inputs = List.of(
            new InputType("foo", 1),
            new InputType("bar", 2),
            new InputType("baz", 3)
        );
        Map<String, Boolean> results = engine.evaluateAll(inputs);
    }
}
```
In order to create a `PureRulesEngine` instance, you will have to use the static factory methods as follows:

## getDeterministicEngine
This method creates a deterministic rules engine instance. It requires a function that extracts the ID from the input type and a list of rule groups.

```java
public static <T> PureRulesEngine<T> getDeterministicEngine(
    Function<T, String> idExtractor,
    List<RuleGroup<T>> ruleGroups
);
```

## getProbabilisticEngine
This method creates a probabilistic rules engine instance. Similar to the deterministic engine, it requires an ID 
extractor function and a list of rule groups. However, it also requires a minimum probability threshold to be set, 
which determines the minimum confidence level for an input to be considered a match against the rules.

```java 
public static <T> PureRulesEngine<T> getProbabilisticEngine(
    Function<T, String> idExtractor,
    Float minimumProbability,
    List<RuleGroup<T>> ruleGroups
);
```