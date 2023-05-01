package niffler.test;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import niffler.db.dao.NifflerUsersDAO;
import niffler.db.dao.NifflerUsersDAOJdbc;
import niffler.db.entity.UserEntity;
import niffler.jupiter.annotation.GenerateUser;
import niffler.jupiter.extension.GenerateUserExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(GenerateUserExtension.class)
public class LoginNewUserExtensionTest extends BaseWebTest {

    private NifflerUsersDAO usersDAO = new NifflerUsersDAOJdbc();

    @GenerateUser(
            password = "54321",
            username = "misha",
            enabled = true
    )
    @Test
    void loginTest(UserEntity user) {
        Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getUsername());
        $("input[name='password']").setValue(user.getPassword());
        $("button[type='submit']").click();

        $("a[href*='friends']").click();
        $(".header").should(visible).shouldHave(text("Niffler. The coin keeper."));
    }

    @GenerateUser(
            password = "54321",
            username = "Petr20",
            enabled = true
    )
    @Test
    void updateUserTest(UserEntity user) {
        String updateUsername = "Petr_upd20";
        user.setPassword("123456");
        user.setUsername(updateUsername);
        assertEquals(1, usersDAO.updateUser(user), "Не удалось обновить UserEntity");

        assertEquals(updateUsername, usersDAO.getUser(user.getUsername()).getUsername(), "Username не совпадает");
    }
}
