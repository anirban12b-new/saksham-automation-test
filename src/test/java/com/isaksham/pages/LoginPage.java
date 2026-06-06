package com.isaksham.pages;

import com.isaksham.framework.allure.AllureStepHelper;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Login screen at {@code #/login} for i-Saksham MIS.
 */
public class LoginPage extends BasePage {

    private static final String LOGIN_ROUTE = "login";

    public LoginPage(Page page) {
        super(page);
    }

    public LoginPage open() {
        return AllureStepHelper.step("Open login page", () -> {
            navigate(config.baseUrl());
            waitForLoginForm();
            return this;
        });
    }

    public HomePage login(String userId, String password) {
        return AllureStepHelper.step("Log in as user: " + userId, () -> {
            userIdField().fill(userId);
            passwordField().fill(password);
            loginButton().click();
            page.waitForURL(url -> !url.contains(LOGIN_ROUTE),
                    new Page.WaitForURLOptions().setTimeout(config.timeoutMs()));
            page.waitForLoadState();
            return new HomePage(page);
        });
    }

    public HomePage loginWithConfiguredCredentials() {
        return AllureStepHelper.step("Log in with configured credentials", () -> {
            String userId = config.userId();
            String password = config.password();
            if (userId.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                        "Credentials missing. Set user.id/password in config.local.properties "
                                + "or MIS_USER_ID / MIS_PASSWORD environment variables.");
            }
            return login(userId, password);
        });
    }

    public boolean isLoginFormVisible() {
        return AllureStepHelper.step("Check login form visibility", () -> userIdField().isVisible());
    }

    private void waitForLoginForm() {
        if (!page.url().contains(LOGIN_ROUTE)) {
            page.waitForURL(url -> url.contains(LOGIN_ROUTE),
                    new Page.WaitForURLOptions().setTimeout(config.timeoutMs()));
        }
        userIdField().waitFor();
    }

    private Locator userIdField() {
        return page.locator("#username");
    }

    private Locator passwordField() {
        return page.locator("#password");
    }

    private Locator loginButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login").setExact(true));
    }
}
