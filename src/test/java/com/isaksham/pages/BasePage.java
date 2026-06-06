package com.isaksham.pages;

import com.isaksham.framework.config.TestConfig;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

/**
 * Shared helpers for Page Object classes.
 */
public abstract class BasePage {

    protected final Page page;
    protected final TestConfig config = TestConfig.get();

    protected BasePage(Page page) {
        this.page = page;
    }

    protected void navigate(String url) {
        page.navigate(url, new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(config.timeoutMs()));
        page.waitForLoadState(LoadState.NETWORKIDLE,
                new Page.WaitForLoadStateOptions().setTimeout(config.timeoutMs()));
    }

    protected Locator firstVisible(Locator... candidates) {
        for (Locator candidate : candidates) {
            if (candidate.count() > 0 && candidate.first().isVisible()) {
                return candidate.first();
            }
        }
        return candidates[0].first();
    }
}
