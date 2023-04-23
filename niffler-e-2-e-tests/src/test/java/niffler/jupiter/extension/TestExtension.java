package niffler.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestExtension implements
        BeforeAllCallback,
        BeforeEachCallback {
    @Override
    public void beforeAll(ExtensionContext context) {
        System.out.println("context.getRequiredTestClass() available: " + context.getRequiredTestClass());
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        System.out.println("context.getRequiredTestMethod() available: " + context.getRequiredTestMethod());

        System.out.println("context.getRequiredTestInstance() available:" + context.getRequiredTestInstance());
    }
}
