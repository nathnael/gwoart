/*
*
* Fixed Sized Candidate Set Adaptive Random Testing
*
* */


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


public class Fscsart {
    public static boolean totalTestCasesInMap (Map<String, List<TestCase>> clusteredTestCases) {
        int size = clusteredTestCases.values().stream()
                .mapToInt(Collection::size)
                .sum();
        return size > 0;
    }

    public static void main(String[] args) {
        List<TestCase> prioritizedList = new ArrayList<TestCase>();
        List<String> prioritizedNameList = new ArrayList<String>();
        List<TestCase> iterationSet = new ArrayList<TestCase>();
        TestCase randomTestCase = new TestCase();
        int iterationSetSize = 10;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            List<TestCase> testCaseList = objectMapper.readValue(
                    new File("D:\\code\\gwoart\\src\\main\\java\\gwoart\\raw.json"),
                    new TypeReference<List<TestCase>>(){});
            Random rand = new Random();
            TestCase testCase = testCaseList.get(rand.nextInt(testCaseList.size()));
            prioritizedList.add(testCase);
            testCaseList.remove(testCase);

            for (int i = 0; i < testCaseList.size(); i++) {
                Collections.shuffle(testCaseList);
                iterationSet = testCaseList.stream().limit(iterationSetSize).collect(Collectors.toList());
                TestCase selectedTestCase = new TestCase();
                double testCasePerformance = 0.0;

                for (TestCase testCaseItem : iterationSet) {
                    for (TestCase prioritizedTestCase : prioritizedList) {
                        testCasePerformance = Math.abs(testCaseItem.getValue() - prioritizedTestCase.getValue());
                        if (testCasePerformance > selectedTestCase.getValue()) {
                            selectedTestCase = testCaseItem;
                        }
                    }
                }
                prioritizedList.add(testCase);
                prioritizedNameList.add(testCase.getName());
                testCaseList.remove(testCase);
            }

            ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(
                    new File("D:\\code\\gwoart\\src\\main\\java\\gwoart\\prioritized.json"),
                    prioritizedNameList
            );
            System.out.println("Prioritized Test Cases List: " + prioritizedNameList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
