package com.github.sneakytowelsuit.purerules.context;

import com.github.sneakytowelsuit.purerules.context.deterministic.DeterministicEvaluationContext;
import com.github.sneakytowelsuit.purerules.context.probabilistic.ProbabilisticEvaluationContext;
import com.github.sneakytowelsuit.purerules.engine.EngineMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EngineContextImpl {
  private final Map<Long, DeterministicEvaluationContext> threadIdToDeterministicEvaluationContext;
  private final Map<Long, ProbabilisticEvaluationContext> threadIdToProbabilisticEvaluationContext;
  private final EnumMap<EngineMode, EvaluationContext<?>> evaluationContexts =
      new EnumMap<>(EngineMode.class);

  private static EngineContextImpl instance;

  public static EngineContextImpl getInstance() {
    if (instance == null) {
      instance = new EngineContextImpl();
    }
    return instance;
  }

  private EngineContextImpl() {
    this.threadIdToDeterministicEvaluationContext = new ConcurrentHashMap<>();
    this.threadIdToProbabilisticEvaluationContext = new ConcurrentHashMap<>();
    this.evaluationContexts.put(EngineMode.DETERMINISTIC, new DeterministicEvaluationContext());
    this.evaluationContexts.put(EngineMode.PROBABILISTIC, new ProbabilisticEvaluationContext());
  }

  public void instantiateDeterministicEvaluationContext(Long threadId) {
    this.getThreadIdBoundDeterministicEvaluationContext()
        .putIfAbsent(threadId, new DeterministicEvaluationContext());
  }

  public void instantiateProbabilisticEvaluationContext(Long threadId) {
    this.threadIdToProbabilisticEvaluationContext.putIfAbsent(
        threadId, new ProbabilisticEvaluationContext());
  }

  private Map<Long, DeterministicEvaluationContext>
      getThreadIdBoundDeterministicEvaluationContext() {
    return this.threadIdToDeterministicEvaluationContext;
  }

  private Map<Long, ProbabilisticEvaluationContext> getThreadIdToProbabilisticEvaluationContext() {
    return this.threadIdToProbabilisticEvaluationContext;
  }

  public DeterministicEvaluationContext getDeterministicEvaluationContext(Long threadId) {
    return this.getThreadIdBoundDeterministicEvaluationContext()
        .computeIfAbsent(threadId, k -> new DeterministicEvaluationContext());
  }

  public ProbabilisticEvaluationContext getProbabilisticEvaluationContext(Long threadId) {
    return this.getThreadIdToProbabilisticEvaluationContext()
        .computeIfAbsent(threadId, k -> new ProbabilisticEvaluationContext());
  }

  public void flush(Long threadId) {
    this.getThreadIdBoundDeterministicEvaluationContext().remove(threadId);
    this.getThreadIdToProbabilisticEvaluationContext().remove(threadId);
  }

  public void flushAll() {
    this.getThreadIdBoundDeterministicEvaluationContext().clear();
    this.getThreadIdToProbabilisticEvaluationContext().clear();
  }
}
