package com.isaksham.framework;

import com.isaksham.framework.allure.AllureAttachments;
import com.isaksham.framework.config.TestConfig;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for all UI tests. Starts a fresh browser session per test method.
 */
public abstract class BaseTest {

    protected final TestConfig config = TestConfig.get();
    protected final PlaywrightManager playwrightManager = new PlaywrightManager();
    protected Page page;

    @BeforeMethod(alwaysRun = true)
    public void setUpBase() {
        page = playwrightManager.startNewSession();
        Allure.label("browser", config.browser());
        Allure.label("headless", String.valueOf(config.headless()));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownBase(ITestResult result) {
        try {
            if (page != null && result.getStatus() == ITestResult.FAILURE) {
                AllureAttachments.attachPageInfo(page);
                AllureAttachments.attachScreenshot(page, "Failure screenshot");
                saveScreenshotToDisk(result);
            }
        } finally {
            playwrightManager.close();
        }
    }

    private void saveScreenshotToDisk(ITestResult result) {
        try {
            Path dir = Path.of("test-results", "screenshots");
            Files.createDirectories(dir);
            String safeName = result.getMethod().getMethodName()
                    .replaceAll("[^a-zA-Z0-9-_]", "_");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path file = dir.resolve(safeName + "_" + timestamp + ".png");
            page.screenshot(new Page.ScreenshotOptions().setPath(file).setFullPage(true));
        } catch (Exception ignored) {
            // best-effort disk backup
        }
    }
}
