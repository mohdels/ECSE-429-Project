package test.storyTests;

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
@SelectClasspathResource("src/test/resources/features") // path to the features folder
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "storyTests") // package for step definitions
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports.html")
public class CucumberTestRunner {
}
