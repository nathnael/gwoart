package gwoart;

import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;
import java.text.DecimalFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.math3.util.Precision;


public class Coverage {

    public static void main(String[] args) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            List<String> testCaseList = objectMapper.readValue(
                    new File("D:\\code\\gwoart\\src\\main\\java\\gwoart\\timeTestcases.json"),
                    new TypeReference<List<String>>(){});
            List<TestCase> coverageTestCases = new ArrayList<TestCase>();
            for (int i = 0; i < testCaseList.size(); i++) {
                String testCaseName = testCaseList.get(i);
//                double coverage = Math.random();
                double coverage = Math.random() * (1.0 - 0.60) + 0.60;
                DecimalFormat df = new DecimalFormat("####0.00");
                TestCase testCase = new TestCase(testCaseName, Precision.round(coverage, 2));
                coverageTestCases.add(testCase);
            }
            ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(
                    new File("D:\\code\\gwoart\\src\\main\\java\\gwoart\\timeCoverage.json"),
                    coverageTestCases
            );
            System.out.println("Coveraged Test Cases List: " + coverageTestCases);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}