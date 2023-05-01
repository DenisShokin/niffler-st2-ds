package niffler.test.database;

import io.qameta.allure.AllureId;
import niffler.db.dao.NifflerUsersDAO;
import niffler.db.dao.NifflerUsersDAOJdbc;
import niffler.db.entity.UserEntity;
import niffler.jupiter.annotation.GenerateUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserJDBCTest {

    private NifflerUsersDAO usersDAO = new NifflerUsersDAOJdbc();

    @GenerateUser(
            password = "54321",
            username = "Petr"
    )
    @AllureId("701")
    @Test
    void updateUserTest(UserEntity user) {
        String updateUsername = "Petr_upd";
        user.setPassword("123456");
        user.setUsername(updateUsername);
        assertEquals(1, usersDAO.updateUser(user), "Не удалось обновить UserEntity");

        assertEquals(updateUsername, usersDAO.getUser(user.getUsername()).getUsername(), "Username не совпадает");
    }

}
