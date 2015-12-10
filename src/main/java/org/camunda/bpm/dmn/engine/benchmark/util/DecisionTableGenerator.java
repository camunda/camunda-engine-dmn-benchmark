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
    String output = OUTPUT_DIR + "5Rules.dmn";

    String decisionId = "fiveRules";

    long numberOfRules = 5;
    long numberOfInputs = 1;

    DecisionTableGenerator generator = new DecisionTableGenerator();
    generator.generateDmn(template, output, decisionId, numberOfRules, numberOfInputs);
  }

  public void generateDmn(String template, String output, String decisionId, long numberOfRules, long numberOfInputs) {
    InputStream inputStream = getClass().getResourceAsStream(template);
    DmnModelInstance dmnModelInstance = Dmn.readModelFromStream(inputStream);

    // set id of the decision
    Decision decision = dmnModelInstance.getModelElementById("template");
    decision.setId(decisionId);

    // add the rules
    DecisionTable decisionTable = dmnModelInstance.getModelElementById("decisionTable");

    for (int i = 0; i < numberOfRules; i++) {
      double x = (double) i / numberOfRules;

      Rule rule = createRule(dmnModelInstance, numberOfInputs, x);
      decisionTable.getRules().add(rule);
    }

    // write the dmn file
    File dmnFile = new File(output);
    Dmn.writeModelToFile(dmnFile, dmnModelInstance);

    System.out.println("generate dmn file: " + dmnFile.getAbsolutePath());
  }

  private Rule createRule(DmnModelInstance dmnModelInstance, double numberOfInputs, double x) {

    OutputEntry outputEntry = createOutputEntry(dmnModelInstance, "\"matched\"");

    Rule rule = dmnModelInstance.newInstance(Rule.class);

    for (int i = 0; i < numberOfInputs; i++) {
      InputEntry inputEntry = createInputEntry(dmnModelInstance, "> " + x);
      rule.getInputEntries().add(inputEntry);
    }

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
