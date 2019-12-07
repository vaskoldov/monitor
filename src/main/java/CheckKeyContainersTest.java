import static org.junit.jupiter.api.Assertions.*;

class CheckKeyContainersTest {
    CheckKeyContainers check = new CheckKeyContainers();
    @org.junit.jupiter.api.Test
    void isSignAvailable() {
        assertEquals(true, check.isSignAvailable("RutokenStore\\FADM01"));
    }

    @org.junit.jupiter.api.Test
    void signValidTill() {
        System.out.println(check.signValidTill("RutokenStore\\FADM01"));
    }
}