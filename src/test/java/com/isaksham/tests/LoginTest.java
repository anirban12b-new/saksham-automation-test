package com.isaksham.tests;

import com.isaksham.framework.BaseTest;
import com.isaksham.pages.HomePage;
import com.isaksham.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("i-Saksham MIS")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Test(description = "User can log in with valid credentials")
    @Description("Valid User ID and password should open the dashboard with a welcome message.")
    @Severity(SeverityLevel.CRITICAL)
    public void userCanLoginWithValidCredentials() {
        HomePage home = new LoginPage(page)
                .open()
                .loginWithConfiguredCredentials()
                .waitForLoaded();

        home.assertLoggedIn();
        Assert.assertTrue(
                home.welcomeText().toLowerCase().contains(config.userId().toLowerCase()),
                "Welcome message should include logged-in user");
        Assert.assertTrue(
                home.currentUrl().contains("i-saksham.org"),
                "Expected to remain on i-Saksham MIS domain");
        Assert.assertFalse(home.pageTitle().isBlank(), "Page title should be set after login");
    }

    @Test(description = "Login form is shown when not authenticated")
    @Description("Unauthenticated users should see username and password fields on the login route.")
    @Severity(SeverityLevel.NORMAL)
    public void loginFormIsVisible() {
        LoginPage loginPage = new LoginPage(page).open();
        Assert.assertTrue(
                loginPage.isLoginFormVisible(),
                "Login form should be visible for unauthenticated users");
    }
}
