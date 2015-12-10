# camunda DMN engine - Benchmark

Benchmark for the [camunda DMN engine](https://github.com/camunda/camunda-engine-dmn) based on [JMH](http://openjdk.java.net/projects/code-tools/jmh/).

The benchmark measure how many decision tables can evaluate per second.

## How to run it 

Go to the project directory and install it with Maven:

```
$ mvn clean install
```

After the build is done, you will get the self-contained executable JAR, which holds the benchmark, and all essential JMH infrastructure code. Now, run the benchmark:

```
$ java -jar benchmarks.jar -rf csv -rff camunda-dmn-engine-benchmark-results.csv
```

This will take a few minutes (~ 5-10min). Then the benchmark is done, you can see the results on the command line and on the generated CSV file _camunda-dmn-engine-benchmark-results.csv_. 

## How to customize it

The benchmark class _DmnEngineBenchmark_ have some annotations that configure the benchmark, for example the iterations of measurement. See the [JMH docs](http://openjdk.java.net/projects/code-tools/jmh/) for details.

### Use your own decision table

Put your DMN decision table in the folder _src/main/resources/org/camunda/bpm/dmn/engine/benchmark_. Configure the benchmark class _DmnEngineBenchmark_:

* add the key of your decision to the parameter list on the field _decisionDefinitionKey_
* parse your DMN in the setup method _buildEngine()_ by calling the method _parseDecision()_
* adjust the variables in the method _createVariables()_ if necessary

## How to generate large decision tables

You can use the class _DecisionTableGenerator_ to generate large decision tables. It uses a template and add rules via DMN Model API.

