package niffler.test;

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

public class UserJDBCTest {

    private NifflerUsersDAOSpringJdbc usersDAOSpringJdbc = new NifflerUsersDAOSpringJdbc();


    @GenerateUser(
            password = "54321",
            username = "ivan9"
    )
    @AllureId("501")
    @Test
    void getUserSpringTest(UserEntity user) {
        assertEquals(user.getUsername(),
                usersDAOSpringJdbc.getUser(user.getUsername()).getUsername(),
                "Username не совпадает");
    }

    @AllureId("502")
    @GenerateUser(
            password = "654321",
            username = "michail3"
    )
    @Test
    void updateUserSpringTest(UserEntity user) {
        user.setUsername("michail3_upd");
        Assertions.assertEquals(1, usersDAOSpringJdbc.updateUser(user), "Не удалось обновить UserEntity");
    }

    @AllureId("503")
    @Test
    void createUserSpringTest() {
        UserEntity user = new UserEntity();
        user.setUsername("spring2");
        user.setPassword("123456");
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
