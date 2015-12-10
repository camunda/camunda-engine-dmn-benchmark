/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.camunda.bpm.dmn.engine.benchmark;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Benchmark for evaluating decision tables.
 *
 * @author Philipp Ossler
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(3)
@Threads(1)
public class DmnEngineBenchmark {

  private static final String DMN_DIRECTORY = "/org/camunda/bpm/dmn/engine/benchmark/";

  // decision ids
  private static final String DONT_CARE = "dontCare";
  private static final String ONE_RULE = "oneRule";
  private static final String FIVE_RULES = "fiveRules";
  private static final String TEN_RULES = "tenRules";
  private static final String ONE_HUNDRED_RULES = "oneHundredRules";

  // decision tables to evaluate
  @Param({ DONT_CARE, ONE_RULE, FIVE_RULES, TEN_RULES, ONE_HUNDRED_RULES })
  public String decisionDefinitionKey;

  // 1.0 => 100% - all rules of the decision table will match
  // 0.5 => 50% - half of the rules of the decision table will match
  @Param({ "1.0", "0.5" })
  public double numberOfMatchingRules;

  private DmnEngine dmnEngine;

  private final Map<String, DmnDecision> parsedDecision = new HashMap<String, DmnDecision>();

  @Setup
  public void buildEngine() throws Exception {

    DmnEngineConfiguration configuration = DmnEngineConfiguration.createDefaultDmnEngineConfiguration();
    // configure as needed

    dmnEngine = configuration.buildEngine();

    // parse all decisions before evaluate it
    parseDecision(dmnEngine, "dontCare.dmn", DONT_CARE);
    parseDecision(dmnEngine, "oneRule.dmn", ONE_RULE);
    parseDecision(dmnEngine, "5Rules.dmn", FIVE_RULES);
    parseDecision(dmnEngine, "10Rules.dmn", TEN_RULES);
    parseDecision(dmnEngine, "100Rules.dmn", ONE_HUNDRED_RULES);
  }

  @Benchmark
  public void evaluateDecisionTable() {

    DmnDecision decision = getParsedDecision();
    Map<String, Object> variables = createVariables(numberOfMatchingRules);

    dmnEngine.evaluateDecisionTable(decision, variables);
  }

  private void parseDecision(DmnEngine dmnEngine, String fileName, String decisionDefinitionKey) throws FileNotFoundException {
    String dmnFile = DMN_DIRECTORY + fileName;

    InputStream inputStream = getClass().getResourceAsStream(dmnFile);
    if (inputStream == null) {
      throw new FileNotFoundException("DMN file '" + dmnFile + "' not found");
    }

    DmnDecision decision = dmnEngine.parseDecision(decisionDefinitionKey, inputStream);

    parsedDecision.put(decisionDefinitionKey, decision);
  }

  private DmnDecision getParsedDecision() {
    DmnDecision decision = parsedDecision.get(decisionDefinitionKey);
    if (decision == null) {
      throw new IllegalStateException("No decision with key '" + decisionDefinitionKey + "' is parsed. Check the test setup.");
    }
    return decision;
  }

  private static Map<String, Object> createVariables(double numberOfMatchingRules) {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("input", numberOfMatchingRules);
    return variables;
  }

}
