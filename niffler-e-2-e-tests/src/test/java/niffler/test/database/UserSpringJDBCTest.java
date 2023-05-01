package niffler.test.database;

import io.qameta.allure.AllureId;
import niffler.db.dao.NifflerUsersDAOSpringJdbc;
import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import niffler.jupiter.annotation.GenerateUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserSpringJDBCTest {

    private final NifflerUsersDAOSpringJdbc usersDAOSpringJdbc = new NifflerUsersDAOSpringJdbc();
    private final String TEST_PWD = "6543210";

    @GenerateUser(
            password = TEST_PWD,
            username = "ivan"
    )
    @AllureId("801")
    @Test
    void getUserSpringTest(UserEntity user) {
        assertEquals(user.getUsername(),
                usersDAOSpringJdbc.getUser(user.getUsername()).getUsername(),
                "Username не совпадает");
    }

    @AllureId("802")
    @GenerateUser(
            password = TEST_PWD,
            username = "michail"
    )
    @Test
    void updateUserSpringTest(UserEntity user) {
        user.setUsername("michail_upd");
        Assertions.assertEquals(1, usersDAOSpringJdbc.updateUser(user), "Не удалось обновить UserEntity");
    }

    @AllureId("803")
    @Test
    void createUserSpringTest() {
        UserEntity user = new UserEntity();
        user.setUsername("alex");
        user.setPassword(TEST_PWD);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(Arrays.stream(Authority.values()).map(
                a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    ae.setUser(user);
                    return ae;
                }
        ).toList());
        usersDAOSpringJdbc.createUser(user);
    }

}
