package com.isaksham.framework.auth;

import com.isaksham.framework.PlaywrightManager;
import com.isaksham.framework.config.TestConfig;
import com.isaksham.pages.HomePage;
import com.isaksham.pages.LoginPage;
import com.microsoft.playwright.Page;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * One-time login helper to persist authenticated storage state for faster test runs.
 */
public final class AuthSetup {

    private AuthSetup() {
    }

    public static void createStorageStateIfMissing() {
        TestConfig config = TestConfig.get();
        Path authFile = config.authStatePath();

        if (Files.isRegularFile(authFile)) {
            return;
        }

        try (PlaywrightManager manager = new PlaywrightManager()) {
            Page page = manager.startNewSession();
            HomePage home = new LoginPage(page).open().loginWithConfiguredCredentials();
            home.waitForLoaded();
            manager.saveAuthState(authFile);
        }
    }

    public static void main(String[] args) {
        Path authFile = TestConfig.get().authStatePath();
        if (Files.exists(authFile.getParent())) {
            try {
                Files.walk(authFile.getParent())
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (Exception ignored) {
                            }
                        });
            } catch (Exception ignored) {
            }
        }
        createStorageStateIfMissing();
        System.out.println("Auth state saved to: " + authFile.toAbsolutePath());
    }
}
