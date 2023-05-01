package niffler.db.dao;

import niffler.db.DataSourceProvider;
import niffler.db.ServiceDB;
import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NifflerUsersDAOJdbc implements NifflerUsersDAO {

    private static final DataSource ds = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.NIFFLER_AUTH);

    @Override
    public int createUser(UserEntity user) {
        int executeUpdate;

        try (Connection conn = ds.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement insertUserSt = conn.prepareStatement("INSERT INTO users "
                    + "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) "
                    + " VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement insertAuthoritySt = conn.prepareStatement(
                         "INSERT INTO authorities (user_id, authority) VALUES (?, ?)")) {
                insertUserSt.setString(1, user.getUsername());
                insertUserSt.setString(2, pe.encode(user.getPassword()));
                insertUserSt.setBoolean(3, user.getEnabled());
                insertUserSt.setBoolean(4, user.getAccountNonExpired());
                insertUserSt.setBoolean(5, user.getAccountNonLocked());
                insertUserSt.setBoolean(6, user.getCredentialsNonExpired());
                executeUpdate = insertUserSt.executeUpdate();

                final UUID finalUserId;

                try (ResultSet generatedKeys = insertUserSt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        finalUserId = UUID.fromString(generatedKeys.getString(1));
                        user.setId(finalUserId);
                    } else {
                        throw new SQLException("Creating user failed, no ID present");
                    }
                }

                for (AuthorityEntity authority : user.getAuthorities()) {
                    insertAuthoritySt.setObject(1, finalUserId);
                    insertAuthoritySt.setString(2, authority.getAuthority().name());
                    insertAuthoritySt.addBatch();
                    insertAuthoritySt.clearParameters();
                }
                insertAuthoritySt.executeBatch();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new RuntimeException(e);
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executeUpdate;
    }

    @Override
    public String getUserId(String userName) {
        try (Connection conn = ds.getConnection();
             PreparedStatement st = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            st.setString(1, userName);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            } else {
                throw new IllegalArgumentException("Can`t find user by given username: " + userName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserEntity getUser(String userName) {
        try (Connection conn = ds.getConnection();
             PreparedStatement userSt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
             PreparedStatement authoritiesSt = conn.prepareStatement("SELECT * FROM authorities WHERE user_id = ?")) {
            userSt.setString(1, userName);
            ResultSet userResultSet = userSt.executeQuery();
            UserEntity user = new UserEntity();
            if (userResultSet.next()) {
                user.setId((UUID) userResultSet.getObject("id"));
                user.setUsername(userResultSet.getString("username"));
                user.setPassword(userResultSet.getString("password"));
                user.setEnabled(userResultSet.getBoolean("enabled"));
                user.setAccountNonExpired(userResultSet.getBoolean("account_non_expired"));
                user.setAccountNonLocked(userResultSet.getBoolean("account_non_locked"));
                user.setCredentialsNonExpired(userResultSet.getBoolean("credentials_non_expired"));

                authoritiesSt.setObject(1, user.getId());
                ResultSet authoritiesResultSet = authoritiesSt.executeQuery();

                List<AuthorityEntity> authorities = new ArrayList<>();

                while (authoritiesResultSet.next()) {
                    AuthorityEntity entity = new AuthorityEntity();
                    entity.setId((UUID) authoritiesResultSet.getObject("id"));
                    entity.setAuthority(Authority.valueOf(authoritiesResultSet.getString("authority")));

                    authorities.add(entity);
                }
                user.setAuthorities(authorities);

                return user;
            } else {
                throw new IllegalArgumentException("Can`t find user by given username: " + userName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateUser(UserEntity user) {
        int executeUpdate;

        try (Connection conn = ds.getConnection()) {

            try (PreparedStatement insertUserSt = conn.prepareStatement("UPDATE users "
                    + "SET username = ?,"
                    + " password = ?, "
                    + "enabled = ?, "
                    + "account_non_expired = ?, "
                    + "account_non_locked = ?, "
                    + "credentials_non_expired = ? WHERE id = ?")) {
                insertUserSt.setString(1, user.getUsername());
                insertUserSt.setString(2, pe.encode(user.getPassword()));
                insertUserSt.setBoolean(3, user.getEnabled());
                insertUserSt.setBoolean(4, user.getAccountNonExpired());
                insertUserSt.setBoolean(5, user.getAccountNonLocked());
                insertUserSt.setBoolean(6, user.getCredentialsNonExpired());
                insertUserSt.setObject(7, user.getId());

                executeUpdate = insertUserSt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executeUpdate;
    }

    @Override
    public int removeUser(UserEntity user) {
        int executeUpdate;

        try (Connection conn = ds.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement deleteUserSt = conn.prepareStatement("DELETE FROM users WHERE id = ?");
                 PreparedStatement deleteAuthoritySt = conn.prepareStatement(
                         "DELETE FROM authorities WHERE user_id = ?")) {
                deleteUserSt.setObject(1, user.getId());
                deleteAuthoritySt.setObject(1, user.getId());

                deleteAuthoritySt.executeUpdate();
                executeUpdate = deleteUserSt.executeUpdate();

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new RuntimeException(e);
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executeUpdate;
    }
}
