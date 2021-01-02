package com.niklasarndt.healthchecksio;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

/**
 * <p>Contains information about the current build (e.g. version and build date).</p>
 *
 * @since 1.0.0
 */
public class HealthchecksInfo {

    private static String NAME;
    private static String DESCRIPTION;
    private static String VERSION;
    private static String TARGET_JDK;
    private static String TIMESTAMP;
    private static String URL;

    static {
        try {
            Properties properties = new Properties();
            properties.load(HealthchecksInfo.class.getClassLoader()
                    .getResourceAsStream("healthchecks-build.properties"));

            NAME = properties.getProperty("build.name");
            DESCRIPTION = properties.getProperty("build.description");
            VERSION = properties.getProperty("build.version");
            TARGET_JDK = properties.getProperty("build.targetJdk");
            TIMESTAMP = properties.getProperty("build.timestamp");
            URL = properties.getProperty("build.url");
        } catch (IOException ignored) {
        }

        //Set null fields to UNKNOWN
        for (Field field : HealthchecksInfo.class.getDeclaredFields()) {
            try {
                if (field.getType().isAssignableFrom(String.class)
                        && field.getModifiers() == (Modifier.PRIVATE | Modifier.STATIC)
                        && field.get(null) == null) {
                    field.set(null, "UNKNOWN");
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * @return The name of the Library (e.g. Healthchecks.io API for Java)
     */
    public static String NAME() {
        return NAME;
    }

    /**
     * @return The description of the Library
     */
    public static String DESCRIPTION() {
        return DESCRIPTION;
    }

    /**
     * @return The current version of the Library (e.g. 1.0.0)
     */
    public static String VERSION() {
        return VERSION;
    }

    /**
     * @return The target JDK version (supports every JRE above or
     *         equal to this version) of the Library (e.g. 1.8)
     */
    public static String TARGETJDK() {
        return TARGET_JDK;
    }

    /**
     * @return The build timestamp of the Library (e.g. 2020-12-24 14:31 UTC)
     */
    public static String TIMESTAMP() {
        return TIMESTAMP;
    }

    /**
     * @return The project URL (e.g. <a href="https://github.com/niklas2810/healthchecksio-java">
     *         https://github.com/niklas2810/healthchecksio-java</a>)
     */
    public static String URL() {
        return URL;
    }
}
