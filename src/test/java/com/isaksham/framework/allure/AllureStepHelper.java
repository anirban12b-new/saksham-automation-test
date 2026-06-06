package com.isaksham.framework.allure;

import io.qameta.allure.Allure;

import java.util.function.Supplier;

/**
 * Wraps actions as Allure steps without AspectJ (compatible with all JDK versions).
 */
public final class AllureStepHelper {

    private AllureStepHelper() {
    }

    public static void step(String name, Runnable action) {
        Allure.step(name, (Allure.ThrowableRunnableVoid) action::run);
    }

    public static <T> T step(String name, Supplier<T> action) {
        return Allure.step(name, (Allure.ThrowableRunnable<T>) action::get);
    }
}
