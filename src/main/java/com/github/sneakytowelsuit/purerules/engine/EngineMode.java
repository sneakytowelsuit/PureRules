package com.github.sneakytowelsuit.purerules.engine;

// spotless:off
/**
 * Defines the evaluation mode for the PureRules engine, determining how rules are processed and
 * results are calculated.
 *
 * <p>The engine mode affects both the evaluation logic and the type of results returned:
 *
 * <ul>
 *   <li>{@link #DETERMINISTIC}: Rules are evaluated as strict boolean conditions, returning
 *       definitive true/false results based on exact matches
 *   <li>{@link #PROBABILISTIC}: Rules are evaluated with probability scoring, returning results
 *       based on confidence thresholds and weighted calculations
 * </ul>
 */
// spotless:on
public enum EngineMode {
  // spotless:off
  /**
   * Deterministic evaluation mode where rules produce strict boolean results. Each condition either
   * passes or fails with no uncertainty.
   */
  // spotless:on
  DETERMINISTIC,

  // spotless:off
  /**
   * Probabilistic evaluation mode where rules produce probability scores. Results are determined by
   * comparing calculated probabilities against a minimum threshold.
   */
  // spotless:on
  PROBABILISTIC;
}
