package test.java.storyTests;

import io.cucumber.core.cli.Main;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.cucumber.junit.CucumberOptions;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features")
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("src/test/resources/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "test.java.storyTests")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports.html")
public class CucumberTestRunner {
    public static void main(String[] args) {
        long seed = 12345L;
        Random random = new Random(seed);

        List<String> featureFiles = getFeatureFiles("src/test/resources/features");
        Collections.shuffle(featureFiles, random);

        System.out.println("Shuffled Order of Feature Files:");
        featureFiles.forEach(System.out::println);

        List<String> cucumberArgs = new ArrayList<>();
        cucumberArgs.add("--glue");
        cucumberArgs.add("test.java.storyTests");

        cucumberArgs.add("--plugin");
        cucumberArgs.add("pretty");

        cucumberArgs.addAll(featureFiles);

        try {
            Main.main(cucumberArgs.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> getFeatureFiles(String directoryPath) {
        List<String> featureFiles = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            findFeatureFiles(directory, featureFiles);
        }
        return featureFiles;
    }

    private static void findFeatureFiles(File directory, List<String> featureFiles) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                findFeatureFiles(file, featureFiles);
            } else if (file.getName().endsWith(".feature")) {
                featureFiles.add(file.getAbsolutePath());
            }
        }
    }
}
