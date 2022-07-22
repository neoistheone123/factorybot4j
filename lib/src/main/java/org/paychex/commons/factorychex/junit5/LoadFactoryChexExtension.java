package org.paychex.commons.factorychex.junit5;

import org.paychex.commons.factorychex.dsl.FactoryChex;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 * JUnit5 extension to load factories before ALL tests are started
 */
public class LoadFactoryChexExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) throws IOException {
        if (!started) {
            started = true;
            // Your "before all tests" startup logic goes here
            FactoryChex.findDefinitions();

            // The following line registers a callback hook when the root test context is shut down
            context.getRoot().getStore(GLOBAL).put(LoadFactoryChexExtension.class.getSimpleName(), this);
        }
    }

    @Override
    public void close() {
        // Your "after all tests" logic goes here
    }
}
