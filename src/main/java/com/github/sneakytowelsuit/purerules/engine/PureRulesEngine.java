package com.github.sneakytowelsuit.purerules.engine;

import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.context.EngineContext;
import com.github.sneakytowelsuit.purerules.context.EngineContextImpl;
import java.util.List;

public class PureRulesEngine<TInput> {
  private final EngineContext context;
  private final List<RuleGroup<TInput>> ruleGroups;
  private EngineMode engineMode = EngineMode.DETERMINISTIC;

  public PureRulesEngine(List<RuleGroup<TInput>> ruleGroups) {
    this.context = new EngineContextImpl();
    this.ruleGroups = ruleGroups;
  }

  public PureRulesEngine(List<RuleGroup<TInput>> ruleGroups, EngineMode engineMode) {
    this.context = new EngineContextImpl();
    this.ruleGroups = ruleGroups;
    if (engineMode != null) {
      this.engineMode = engineMode;
    }
  }
}
