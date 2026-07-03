package com.automation.framework.utilities;

import net.datafaker.Faker;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates realistic random test data using Datafaker. Thread-safe.
 * Handy for filling forms with non-colliding data on each run.
 */
public final class RandomDataGenerator {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private RandomDataGenerator() {
    }

    public static String firstName() {
        return FAKER.name().firstName();
    }

    public static String lastName() {
        return FAKER.name().lastName();
    }

    public static String fullName() {
        return FAKER.name().fullName();
    }

    /** Unique-ish email using a timestamp to avoid collisions across runs. */
    public static String email() {
        return "qa_" + System.currentTimeMillis() + "_"
                + ThreadLocalRandom.current().nextInt(1000)
                + "@example.com";
    }

    public static String phoneNumber() {
        return FAKER.phoneNumber().cellPhone();
    }

    public static String city() {
        return FAKER.address().city();
    }

    public static String company() {
        return FAKER.company().name();
    }

    public static String alphanumeric(int length) {
        return FAKER.regexify("[A-Za-z0-9]{" + length + "}");
    }

    public static int number(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
