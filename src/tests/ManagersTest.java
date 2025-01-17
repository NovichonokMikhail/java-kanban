package tests;

import managers.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void ManagerGetDefaultReturnsNotNull() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void ManagersGetDefaultReturnNotNull() {
        assertNotNull(Managers.getDefaultHistory());
    }
}