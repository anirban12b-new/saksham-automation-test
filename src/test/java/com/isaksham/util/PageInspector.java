package com.isaksham.util;

import com.isaksham.framework.PlaywrightManager;
import com.isaksham.framework.config.TestConfig;
import com.isaksham.pages.LoginPage;
import com.microsoft.playwright.Page;

/**
 * Utility to print page structure after navigation/login (run via Maven exec).
 */
public final class PageInspector {

    public static void main(String[] args) {
        TestConfig config = TestConfig.get();
        try (PlaywrightManager manager = new PlaywrightManager()) {
            Page page = manager.startNewSession();
            new LoginPage(page).open();
            System.out.println("=== BEFORE LOGIN ===");
            System.out.println("URL: " + page.url());
            System.out.println("Title: " + page.title());
            printInputs(page);

            new LoginPage(page).loginWithConfiguredCredentials();
            page.waitForTimeout(5000);

            System.out.println("=== AFTER LOGIN ===");
            System.out.println("URL: " + page.url());
            System.out.println("Title: " + page.title());
            printInputs(page);
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(java.nio.file.Path.of("test-results", "after-login.png"))
                    .setFullPage(true));
            System.out.println("Screenshot: test-results/after-login.png");
        }
    }

    private static void printInputs(Page page) {
        String json = (String) page.evaluate("""
            () => JSON.stringify(
              [...document.querySelectorAll('input, button')].map(el => ({
                tag: el.tagName,
                type: el.type,
                id: el.id,
                name: el.name,
                placeholder: el.placeholder,
                formcontrolname: el.getAttribute('formcontrolname'),
                text: el.innerText?.trim()?.slice(0, 40)
              })),
              null,
              2
            )
            """);
        System.out.println(json);
    }
}
