package com.github.sneakytowelsuit.purerules.context;

import java.util.List;
import java.util.Map;

public interface EvaluationContext<T> {
  Map<List<String>, T> getConditionResults();
}
