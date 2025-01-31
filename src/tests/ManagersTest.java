package tests;

import managers.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void managerGetDefaultReturnsNotNull() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void managersGetDefaultReturnNotNull() {
        assertNotNull(Managers.getDefaultHistory());
    }
}