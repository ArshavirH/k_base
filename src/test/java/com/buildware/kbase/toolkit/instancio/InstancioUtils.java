package com.buildware.kbase.toolkit.instancio;

import static org.instancio.Select.root;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.ThreadLocalRandom;

public class InstancioUtils {

    /**
     * Create a random object with the correct settings and customizations necessary for the project.
     */
    public static <T> T random(Class<T> clazz) {
        return instancioOf(clazz).create();
    }

    /**
     * Create a random Enum object except the values passed as varargs
     */
    @SafeVarargs
    public static <E extends Enum<E>> E randomExcept(E... items) {
        var declaringClass = items[0].getDeclaringClass();
        List<E> filteredValuesInEnum = Arrays.stream(declaringClass.getEnumConstants())
            .filter(it -> !List.of(items).contains(it))
            .toList();

        return instancioOf(declaringClass)
            .generate(root(), gen -> gen.oneOf(filteredValuesInEnum))
            .create();
    }

    /**
     * Shortcut to create list with random objects
     */
    public static <T> List<T> randomList(Class<T> clazz) {
        var size = ThreadLocalRandom.getInstance().get().intRange(1, 10);
        return instancioOf(clazz).stream().limit(size).collect(Collectors.toList());
    }

    /**
     * Generates a `InstancioApi` for a specific class using the common settings. Instancio can't generate objects from
     * interfaces, so we can configure a concrete class in the settings using `mapType` function. If we want to change
     * how the objects are generated, we can use `supply` and set a custom generator.
     */
    private static <T> InstancioApi<T> instancioOf(Class<T> clazz) {
        var settings = Settings.defaults()
            .set(Keys.STRING_FIELD_PREFIX_ENABLED, true)
            .set(Keys.BEAN_VALIDATION_ENABLED, true)
            .set(Keys.COLLECTION_MIN_SIZE, 1)
            .lock();
        return Instancio.of(clazz)
            .withSettings(settings);
    }
}
