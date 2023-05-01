package niffler.test.database;

import io.qameta.allure.AllureId;
import niffler.db.dao.NifflerUsersDAOHibernate;
import niffler.db.entity.UserEntity;
import niffler.jupiter.annotation.GenerateUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserHibernateTest {

    private final NifflerUsersDAOHibernate usersDAOHibernate = new NifflerUsersDAOHibernate();
    private final String TEST_PWD = "654321";

    @AllureId("601")
    @GenerateUser(
            password = TEST_PWD,
            username = "nikolay"
    )
    @Test
    void getUserHibernateTest(UserEntity user) {
        Assertions.assertEquals(user.getUsername(),
                usersDAOHibernate.getUser(user.getUsername()).getUsername(),
                "Username не совпадают");
    }

    @AllureId("602")
    @GenerateUser(
            password = TEST_PWD,
            username = "kirill"
    )
    @Test
    void updateUserHibernateTest(UserEntity user) {
        user.setUsername("kirill_upd");
        Assertions.assertEquals(1, usersDAOHibernate.updateUser(user), "Не удалось обновить UserEntity");
    }
}
