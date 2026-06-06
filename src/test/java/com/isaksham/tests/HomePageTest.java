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
@Feature("Home")
public class HomePageTest extends BaseTest {

    @Test(description = "Authenticated user can open home")
    @Description("After login, user should leave the login route and see the welcome control.")
    @Severity(SeverityLevel.CRITICAL)
    public void authenticatedUserCanOpenHome() {
        HomePage home = new LoginPage(page)
                .open()
                .loginWithConfiguredCredentials()
                .waitForLoaded();

        home.assertLoggedIn();
        Assert.assertFalse(
                home.currentUrl().contains("login"),
                "Expected to leave login route after authentication");
    }
}
