package com.isaksham.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Loads {@code config.properties}, optional {@code config.local.properties},
 * and environment variable overrides.
 */
public final class TestConfig {

    private static final String CONFIG_FILE = "config.properties";
    private static final String LOCAL_CONFIG_FILE = "config.local.properties";

    private static final TestConfig INSTANCE = new TestConfig();

    private final Properties properties = new Properties();

    private TestConfig() {
        loadClasspath(CONFIG_FILE);
        loadClasspath(LOCAL_CONFIG_FILE);
        loadExternalLocalFile();
    }

    public static TestConfig get() {
        return INSTANCE;
    }

    public String baseUrl() {
        return required("base.url");
    }

    public String browser() {
        return getString("browser", "chromium");
    }

    public boolean headless() {
        return Boolean.parseBoolean(getString("headless", "false"));
    }

    public int slowMo() {
        return Integer.parseInt(getString("slow.mo", "0"));
    }

    public int timeoutMs() {
        return Integer.parseInt(getString("timeout.ms", "30000"));
    }

    public String userId() {
        return firstNonBlank(
                System.getenv("MIS_USER_ID"),
                getString("user.id", ""),
                System.getenv("USER_ID"));
    }

    public String password() {
        return firstNonBlank(
                System.getenv("MIS_PASSWORD"),
                getString("password", ""),
                System.getenv("PASSWORD"));
    }

    public Path authStatePath() {
        return Path.of("playwright", ".auth", "user.json");
    }

    private void loadClasspath(String name) {
        try (InputStream in = TestConfig.class.getClassLoader().getResourceAsStream(name)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load " + name, e);
        }
    }

    private void loadExternalLocalFile() {
        Path external = Path.of("src", "test", "resources", LOCAL_CONFIG_FILE);
        if (!Files.isRegularFile(external)) {
            return;
        }
        try (InputStream in = Files.newInputStream(external)) {
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load " + external, e);
        }
    }

    private String required(String key) {
        String value = resolve(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config: " + key);
        }
        return value;
    }

    private String getString(String key, String defaultValue) {
        String value = resolve(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private String resolve(String key) {
        String fromProperty = System.getProperty(key);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty;
        }
        String envKey = key.toUpperCase().replace('.', '_');
        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return properties.getProperty(key);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
