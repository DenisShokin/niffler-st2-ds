package niffler.test;

import niffler.api.impl.UserDataServiceImpl;
import niffler.jupiter.annotation.ClasspathUser;
import niffler.model.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import retrofit2.Response;

public class UserApiTest {

    private final UserDataServiceImpl service = new UserDataServiceImpl();

    @ValueSource(strings = {
            "testdata/dimaCorrectUpdate.json",
            "testdata/denisCorrectUpdate.json"
    })
    @ParameterizedTest
    void successUpdateUserInfo(@ClasspathUser UserJson user) {
        Response response = service.updateUserData(user);
        Assertions.assertTrue(response.isSuccessful(), "Error update userData");
    }

    @ValueSource(strings = {
            "testdata/incorrectUsername.json",
            "testdata/emptyUsername.json"
    })
    @ParameterizedTest
    void errorUpdateUserInfo(@ClasspathUser UserJson user) {
        Response response = service.updateUserData(user);
        Assertions.assertFalse(response.isSuccessful(), "Success update userData");
    }
}
