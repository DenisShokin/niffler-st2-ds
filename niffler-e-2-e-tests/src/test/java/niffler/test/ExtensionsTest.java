package niffler.test;

import niffler.jupiter.extension.TestExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestExtension.class)
public class ExtensionsTest {

    @BeforeAll
    static void beforeAll(){
        System.out.println("# Before All");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("## Before Each");
    }

    @Test
    void firstTest() {
        System.out.println("### First test");
    }

    @Test
    void secondTest() {
        System.out.println("### Second test");
    }

    @AfterEach
    void afterEach() {
        System.out.println("## After Each");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("# After All");
    }
}
