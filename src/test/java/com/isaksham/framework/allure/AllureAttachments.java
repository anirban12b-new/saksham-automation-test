package com.isaksham.framework.allure;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Attaches Playwright screenshots and text to the Allure report.
 */
public final class AllureAttachments {

    private AllureAttachments() {
    }

    public static void attachScreenshot(Page page, String name) {
        if (page == null) {
            return;
        }
        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.getLifecycle().addAttachment(name, "image/png", "png", screenshot);
        } catch (Exception e) {
            Allure.addAttachment(name + " (error)", e.getMessage());
        }
    }

    public static void attachPageInfo(Page page) {
        if (page == null) {
            return;
        }
        Allure.addAttachment("Page URL", page.url());
        Allure.addAttachment("Page title", page.title());
    }

    public static void attachScreenshotFile(Path file, String name) {
        if (file == null || !Files.isRegularFile(file)) {
            return;
        }
        try {
            byte[] screenshot = Files.readAllBytes(file);
            Allure.getLifecycle().addAttachment(name, "image/png", "png", screenshot);
        } catch (Exception e) {
            Allure.addAttachment(name + " (error)", e.getMessage());
        }
    }
}
