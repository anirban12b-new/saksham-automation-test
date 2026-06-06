package com.isaksham.pages;

import com.isaksham.framework.allure.AllureStepHelper;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

/**
 * Post-login landing view (dashboard at {@code #/}).
 */
public class HomePage extends BasePage {

    private static final String LOGIN_ROUTE = "login";

    public HomePage(Page page) {
        super(page);
    }

    public HomePage waitForLoaded() {
        return AllureStepHelper.step("Wait for home page to load", () -> {
            page.waitForURL(url -> !url.contains(LOGIN_ROUTE),
                    new Page.WaitForURLOptions().setTimeout(config.timeoutMs()));
            welcomeButton().waitFor();
            return this;
        });
    }

    public void assertLoggedIn() {
        AllureStepHelper.step("Verify user is logged in", () -> {
            PlaywrightAssertions.setDefaultAssertionTimeout(config.timeoutMs());
            PlaywrightAssertions.assertThat(welcomeButton()).isVisible();
        });
    }

    public String welcomeText() {
        return welcomeButton().innerText().trim();
    }

    public String currentUrl() {
        return page.url();
    }

    public String pageTitle() {
        return page.title();
    }

    private Locator welcomeButton() {
        return page.locator("button").filter(new Locator.FilterOptions().setHasText("Welcome:"));
    }
}
