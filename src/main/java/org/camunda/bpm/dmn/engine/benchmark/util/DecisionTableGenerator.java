/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.dmn.engine.benchmark.util;

import java.io.File;
import java.io.InputStream;

import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.camunda.bpm.model.dmn.instance.Text;

/**
 * Generate DMN decision tables with a given number of rules.
 *
 * @author Philipp Ossler
 */
public class DecisionTableGenerator {

  private static final String TEMPLATE_DIR = "/org/camunda/bpm/dmn/engine/benchmark/templates/";
  private static final String OUTPUT_DIR = "src/main/resources/org/camunda/bpm/dmn/engine/benchmark/";

  public static void main(String[] args) {

    String template = TEMPLATE_DIR + "template_one_input.dmn";
    String decisionId = "oneHundredRules";
    long rules = 100;

    DecisionTableGenerator generator = new DecisionTableGenerator();
    generator.generateDmn(template, decisionId, rules);
  }

  public void generateDmn(String template, String decisionId, long rules) {
    InputStream inputStream = getClass().getResourceAsStream(template);
    DmnModelInstance dmnModelInstance = Dmn.readModelFromStream(inputStream);

    // set id of the decision
    Decision decision = dmnModelInstance.getModelElementById("template");
    decision.setId(decisionId);

    // add the rules
    DecisionTable decisionTable = dmnModelInstance.getModelElementById("decisionTable");

    for (int i = 0; i < rules; i++) {
      double x = (double) i / rules;

      Rule rule = createRule(dmnModelInstance, x);
      decisionTable.getRules().add(rule);
    }

    // write the dmn file
    File dmnFile = new File(OUTPUT_DIR, rules + "Rules.dmn");
    Dmn.writeModelToFile(dmnFile, dmnModelInstance);

    System.out.println("generate dmn file: " + dmnFile.getAbsolutePath());
  }

  private Rule createRule(DmnModelInstance dmnModelInstance, double x) {

    InputEntry inputEntry = createInputEntry(dmnModelInstance, "< " + x);

    OutputEntry outputEntry = createOutputEntry(dmnModelInstance, "\"matched\"");

    Rule rule = dmnModelInstance.newInstance(Rule.class);
    rule.getInputEntries().add(inputEntry);
    rule.getOutputEntries().add(outputEntry);

    return rule;
  }

  private InputEntry createInputEntry(DmnModelInstance dmnModelInstance, String expression) {
    Text text = dmnModelInstance.newInstance(Text.class);
    text.setTextContent(expression);

    InputEntry inputEntry = dmnModelInstance.newInstance(InputEntry.class);
    inputEntry.setText(text);
    return inputEntry;
  }

  private OutputEntry createOutputEntry(DmnModelInstance dmnModelInstance, String expression) {
    Text text = dmnModelInstance.newInstance(Text.class);
    text.setTextContent(expression);

    OutputEntry outputEntry = dmnModelInstance.newInstance(OutputEntry.class);
    outputEntry.setText(text);
    return outputEntry;
  }

}
