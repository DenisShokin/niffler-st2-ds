package niffler.db.dao;

import niffler.db.entity.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface NifflerUsersDAO {

    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    int createUser(UserEntity user);

    String getUserId(String userName);

    UserEntity getUser(String userName);

    int updateUser(UserEntity user);

    int removeUser(UserEntity user);

}
