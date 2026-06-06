package com.isaksham.framework;

import com.isaksham.framework.config.TestConfig;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ViewportSize;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Manages Playwright lifecycle for a single test method (one browser context per test).
 */
public final class PlaywrightManager implements AutoCloseable {

    private final TestConfig config = TestConfig.get();

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    public Page startNewSession() {
        return startNewSession(null);
    }

    public Page startNewSession(Path storageState) {
        closeQuietly();

        playwright = Playwright.create();
        browser = launchBrowser(playwright);

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(new ViewportSize(1366, 768))
                .setIgnoreHTTPSErrors(true);

        if (storageState != null && Files.isRegularFile(storageState)) {
            contextOptions.setStorageStatePath(storageState);
        }

        context = browser.newContext(contextOptions);
        context.setDefaultTimeout(config.timeoutMs());
        page = context.newPage();
        return page;
    }

    public Page page() {
        if (page == null) {
            throw new IllegalStateException("Browser session not started. Call startNewSession() first.");
        }
        return page;
    }

    public BrowserContext context() {
        return context;
    }

    public void saveAuthState(Path path) {
        if (context == null) {
            throw new IllegalStateException("No active browser context.");
        }
        try {
            Files.createDirectories(path.getParent());
        } catch (Exception ignored) {
            // parent may already exist
        }
        context.storageState(new BrowserContext.StorageStateOptions().setPath(path));
    }

    @Override
    public void close() {
        closeQuietly();
    }

    private Browser launchBrowser(Playwright playwright) {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(config.headless())
                .setSlowMo(config.slowMo());

        return switch (config.browser().toLowerCase()) {
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "webkit" -> playwright.webkit().launch(launchOptions);
            default -> playwright.chromium().launch(launchOptions);
        };
    }

    private void closeQuietly() {
        List<AutoCloseable> resources = List.of(
                () -> { if (context != null) context.close(); },
                () -> { if (browser != null) browser.close(); },
                () -> { if (playwright != null) playwright.close(); });

        for (AutoCloseable resource : resources) {
            try {
                resource.close();
            } catch (Exception ignored) {
                // best-effort cleanup
            }
        }
        context = null;
        browser = null;
        playwright = null;
        page = null;
    }
}
