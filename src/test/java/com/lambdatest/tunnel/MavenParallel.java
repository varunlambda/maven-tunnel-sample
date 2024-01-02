package com.lambdatest.tunnel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IExecutionListener;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class MavenParallel implements IExecutionListener {

  public RemoteWebDriver driver = null;
  String status = "passed";
  String username = System.getenv("LT_USERNAME");
  String accessKey = System.getenv("LT_ACCESS_KEY");
  Tunnel t;

  @Override
  public void onExecutionStart() {
    try {
      // start the tunnel
      t = new Tunnel();
      HashMap<String, String> options = new HashMap<String, String>();
      options.put("user", username);
      options.put("key", accessKey);
      options.put("tunnelName", "MavenParallel");
      t.start(options);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @BeforeTest
  @org.testng.annotations.Parameters(value = { "browser", "version", "platform", "resolution" })
  public void setUp(String browser, String version, String platform, String resolution) throws Exception {
    DesiredCapabilities capabilities = new DesiredCapabilities();

    capabilities.setCapability("build", "Parallel Maven Tunnel");
    capabilities.setCapability("name", "Maven Tunnel");
    capabilities.setCapability("browserName", browser);
    capabilities.setCapability("version", version);
    capabilities.setCapability("platform", platform);
    capabilities.setCapability("tunnel", true);
    capabilities.setCapability("network", true);
    capabilities.setCapability("console", true);
    capabilities.setCapability("visual", true);
    capabilities.setCapability("tunnelName", "MavenParallel");

    try {
      driver = new RemoteWebDriver(new URL("https://" + username + ":" + accessKey + "@hub.lambdatest.com/wd/hub"),
          capabilities);
    } catch (MalformedURLException e) {
      System.out.println("Invalid grid URL");
    }
  }

  @Test()
  public void testTunnel() throws Exception {
    // Check LocalHost on XAMPP
    driver.get("http://localhost.lambdatest.com");
    // Let's check that the item we added is added in the list.
    driver.get("https://google.com");
  }

  @AfterTest
  public void tearDown() throws Exception {
    if (driver != null) {
      ((JavascriptExecutor) driver).executeScript("lambda-status=" + status);
      driver.quit();
    }
  }

  @Override
  public void onExecutionFinish() {
    try {
      // stop the Tunnel;
      t.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
