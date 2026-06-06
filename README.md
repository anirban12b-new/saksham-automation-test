# i-Saksham MIS — Java Playwright Automation

Java + [Playwright](https://playwright.dev/java/) test framework for [dev-mis.i-saksham.org](https://dev-mis.i-saksham.org/) with **TestNG** and **Allure** reporting.

## Stack

- **Java 17+**
- **Maven**
- **TestNG**
- **Allure Report**
- **Playwright Java** (Page Object Model)

## Project layout

```
src/test/java/com/isaksham/
  framework/              Browser lifecycle, base test, config
  framework/allure/       Allure screenshot & attachment helpers
  framework/auth/         Reusable login storage state
  pages/                  Page objects (@Step for Allure)
  tests/                  TestNG test classes
src/test/resources/
  testng.xml              TestNG suite + Allure listener
  allure.properties       Allure configuration
  config.properties       Base URL, browser, timeouts
  config.local.properties Credentials (gitignored)
```

## Prerequisites

1. **JDK 17 or newer** (`java -version`)
2. **Apache Maven** on your `PATH`

## Setup

1. Copy credentials (if not already present):

   ```text
   copy src\test\resources\config.local.properties.example src\test\resources\config.local.properties
   ```

2. Install Chromium (first time only):

   ```bash
   mvn -Pinstall-browsers validate
   ```

## Run tests

```bash
mvn clean test
```

Run a single TestNG class:

```bash
mvn test -Dtest=LoginTest
```

Headless:

```powershell
$env:HEADLESS='true'
mvn test
```

## Allure reports

After `mvn test`, results are written to `target/allure-results/`.

**Open interactive report (recommended):**

```bash
mvn allure:serve
```

Opens a local server with the HTML report in your browser.

**Generate static report:**

```bash
mvn allure:report
```

Report HTML: `target/site/allure-maven-plugin/index.html`

### What Allure captures

- TestNG test status, descriptions, and `@Severity`
- `@Epic` / `@Feature` grouping
- Allure steps from page objects (login flow, assertions) via `Allure.step()`
- Screenshot + page URL/title on **failure**

## Configuration

| Key / env | Description |
|-----------|-------------|
| `base.url` | Application URL |
| `browser` | `chromium`, `firefox`, or `webkit` |
| `headless` / `HEADLESS` | `true` / `false` |
| `timeout.ms` | Default Playwright timeout |
| `user.id` / `MIS_USER_ID` | Login user ID |
| `password` / `MIS_PASSWORD` | Login password |

## Debug page locators

```bash
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=com.isaksham.util.PageInspector
```

## Notes

- Login route: `#/login` — `#username`, `#password`, button **Login**
- After login: `#/` with **Welcome: &lt;user&gt;** button
- Suite file: `src/test/resources/testng.xml`
