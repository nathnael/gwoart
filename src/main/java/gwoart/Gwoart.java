package gwoart;

import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;


public class Gwoart {
    public static boolean totalTestCasesInMap (Map<String, List<TestCase>> clusteredTestCases) {
        int size = clusteredTestCases.values().stream()
                .mapToInt(Collection::size)
                .sum();
        return size > 0;
    }

    public static void main(String[] args) {
        List<String> prioritizedList = new ArrayList<String>();
        List<TestCase> iterationSet = new ArrayList<TestCase>();
        TestCase alpha, beta, delta = new TestCase();
        int iterationSetSize = 3;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            List<TestCase> testCaseList = objectMapper.readValue(
                    new File("D:\\code\\gwoart\\src\\main\\java\\gwoart\\timeCoverage.json"),
                    new TypeReference<List<TestCase>>(){});
            Map<String, List<TestCase>> clusteredTestCases = new HashMap<>();

            for (int i = 0; i < testCaseList.size(); i++) {
                TestCase testCase = testCaseList.get(i);
                String testName = testCase.getName();
                String[] splitedName = testName.split("\\.");
                if (splitedName[splitedName.length - 2] == null)
                    continue;
                String clusterName = splitedName[splitedName.length - 2];
                if(clusteredTestCases.get(clusterName) == null) {
                    List<TestCase> newCluster = new ArrayList<TestCase>();
                    newCluster.add(testCase);
                    clusteredTestCases.put(clusterName, newCluster);
                } else {
                    List<TestCase> existingCluster = clusteredTestCases.get(clusterName);
                    existingCluster.add(testCase);
                    clusteredTestCases.put(clusterName, existingCluster);
                }
            }
            while (totalTestCasesInMap(clusteredTestCases)) {
                List<String> clusterNames = new ArrayList<String>(clusteredTestCases.keySet());
                Random rand = new Random();
                String currentClusterName = clusterNames.get(rand.nextInt(clusterNames.size()));

                List<TestCase> currentCluster = clusteredTestCases.get(currentClusterName);
                Collections.shuffle(currentCluster);
                iterationSet = currentCluster.stream().limit(iterationSetSize).collect(Collectors.toList());
                alpha = iterationSet.stream().max(Comparator.comparing(TestCase::getValue)).get();
                final TestCase alpha_copy = alpha;
                List<TestCase> currentClusterWithoutAlpha = iterationSet.stream()
                                                            .filter(value -> !alpha_copy.equals(value))
                                                            .collect(Collectors.toList());
                beta = currentClusterWithoutAlpha.size() > 0 ? currentClusterWithoutAlpha.stream().max(Comparator.comparing(TestCase::getValue)).get() : new TestCase();
                final TestCase beta_copy = beta;
                List<TestCase> currentClusterWithoutAlphaBeta = currentClusterWithoutAlpha.stream()
                                                                .filter(value -> !beta_copy.equals(value))
                                                                .collect(Collectors.toList());
                delta = currentClusterWithoutAlphaBeta.size() > 0 ? currentClusterWithoutAlphaBeta.stream().max(Comparator.comparing(TestCase::getValue)).get() : new TestCase();
                TestCase selectedTestCase = new TestCase();

                for (TestCase testCase : iterationSet) {
                    double coefficient = 2 * Math.random();
                    double alphaDistance = Math.abs((coefficient * alpha.getValue()) - testCase.getValue());
                    double betaDistance = Math.abs((coefficient * beta.getValue()) - testCase.getValue());
                    double deltaDistance = Math.abs((coefficient * delta.getValue()) - testCase.getValue());
                    double testCasePerformance = (alphaDistance + betaDistance + deltaDistance) / 3;
                    if (testCasePerformance > selectedTestCase.getValue()) {
                        selectedTestCase = testCase;
                    }
                }
                prioritizedList.add(selectedTestCase.getName());
                List<TestCase> targetCluster = clusteredTestCases.get(currentClusterName);
                targetCluster.remove(selectedTestCase);
                if (targetCluster.size() <= 0) {
                    clusteredTestCases.remove(currentClusterName);
                } else {
                    clusteredTestCases.put(currentClusterName, targetCluster);
                }
            }
            ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(
                    new File("D:\\code\\gwoart\\src\\main\\java\\gwoart\\timePrioritized.json"),
                    prioritizedList
            );
            System.out.println("Prioritized Test Cases List: " + prioritizedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}