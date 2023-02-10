package com.lambdatest.tunnel;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IExecutionListener;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.lambdatest.tunnel.Tunnel;

public class MavenSingle implements IExecutionListener {
  Tunnel t;

  WebDriver driver = null;
  public static String status = "passed";

  String username = System.getenv("LT_USERNAME");
  String access_key = System.getenv("LT_ACCESS_KEY");

  @BeforeTest
  @org.testng.annotations.Parameters(value = { "browser", "version", "platform", "resolution" })
  public void setUp(String browser, String version, String platform, String resolution) throws Exception {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability("build", "Single Maven Tunnel");
    capabilities.setCapability("name", "Maven Tunnel");
    capabilities.setCapability("browserName", browser);
    capabilities.setCapability("version", version);
    capabilities.setCapability("platform", platform);
    capabilities.setCapability("tunnel", true);
    capabilities.setCapability("network", true);
    capabilities.setCapability("console", true);
    capabilities.setCapability("visual", true);
    capabilities.setCapability("tunnelName", "MavenSingle");

    //create tunnel instance
    t = new Tunnel();
    HashMap<String, String> options = new HashMap<String, String>();
    options.put("user", username);
    options.put("key", access_key);
    options.put("tunnelName", "MavenSingle");

    //start tunnel
    t.start(options);
    driver = new RemoteWebDriver(new URL("http://" + username + ":" + access_key + "@hub.lambdatest.com/wd/hub"),
      capabilities);
    System.out.println("Started session");
  }

  @Test()
  public void testTunnel() throws Exception {
    //Check LocalHost on XAMPP
    driver.get("http://localhost.lambdatest.com");
    // Let's check that the item we added is added in the list.
    driver.get("https://google.com");
  }

  @AfterTest
  public void tearDown() throws Exception {
    ((JavascriptExecutor) driver).executeScript("lambda-status=" + status);
    driver.quit();
    //close tunnel
    t.stop();
  }
}
