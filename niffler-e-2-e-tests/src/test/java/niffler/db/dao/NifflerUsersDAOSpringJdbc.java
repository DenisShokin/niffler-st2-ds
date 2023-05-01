package niffler.db.dao;

import niffler.db.DataSourceProvider;
import niffler.db.ServiceDB;
import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NifflerUsersDAOSpringJdbc implements NifflerUsersDAO {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;

    public NifflerUsersDAOSpringJdbc() {
        DataSourceTransactionManager transactionManager = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.NIFFLER_AUTH));
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.jdbcTemplate = new JdbcTemplate(transactionManager.getDataSource());
    }

    @Override
    public int createUser(UserEntity user) {
        return transactionTemplate.execute(st -> {
            String insertUser = "INSERT INTO users (username, password, enabled,"
                    + "account_non_expired, account_non_locked, credentials_non_expired) VALUES (?,?,?,?,?,?)";

            String insertAuthorities = "INSERT INTO authorities (user_id, authority) VALUES (?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            int executeUpdate = jdbcTemplate.update(con -> {
                PreparedStatement insertUserSt = con.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
                insertUserSt.setString(1, user.getUsername());
                insertUserSt.setString(2, pe.encode(user.getPassword()));
                insertUserSt.setBoolean(3, user.getEnabled());
                insertUserSt.setBoolean(4, user.getAccountNonExpired());
                insertUserSt.setBoolean(5, user.getAccountNonLocked());
                insertUserSt.setBoolean(6, user.getCredentialsNonExpired());

                return insertUserSt;
            }, keyHolder);

            final UUID finalUserId = (UUID) keyHolder.getKeys().get("id");
            user.setId(finalUserId);

            jdbcTemplate.batchUpdate(insertAuthorities, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, finalUserId);
                    ps.setString(2, user.getAuthorities().get(i).getAuthority().name());
                }

                @Override
                public int getBatchSize() {
                    return user.getAuthorities().size();
                }
            });

            return executeUpdate;
        });
    }

    @Override
    public String getUserId(String userName) {
        return jdbcTemplate.query("SELECT * FROM users WHERE username = ?",
                rs -> {
                    return rs.getString(1);
                },
                userName
        );
    }

    @Override
    public UserEntity getUser(String userName) {
        UserEntity userEntity = jdbcTemplate.queryForObject("SELECT id, username, password, enabled, account_non_expired, "
                        + "account_non_locked, credentials_non_expired"
                        + " FROM users WHERE username = ?", (resultSet, rowNum) -> {
                    UserEntity user = new UserEntity();
                    user.setId(UUID.fromString(resultSet.getString("id")));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEnabled(resultSet.getBoolean("enabled"));
                    user.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                    return user;
                },
                userName);

        List<AuthorityEntity> authorityList = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, authority FROM authorities WHERE user_id = ?",
                userEntity.getId());
        for (Map row : rows) {
            AuthorityEntity authorityEntity = new AuthorityEntity();
            authorityEntity.setId((UUID) row.get("id"));
            authorityEntity.setAuthority(Authority.valueOf((String) row.get("authority")));
            authorityList.add(authorityEntity);
        }
        userEntity.setAuthorities(authorityList);

        return userEntity;
    }

    @Override
    public int updateUser(UserEntity user) {
        return jdbcTemplate.update("UPDATE users "
                        + "SET username = ?,"
                        + "password = ?, "
                        + "enabled = ?, "
                        + "account_non_expired = ?, "
                        + "account_non_locked = ?, "
                        + "credentials_non_expired = ? WHERE id = ?", user.getUsername(), pe.encode(user.getPassword()), user.getEnabled(),
                user.getAccountNonExpired(), user.getAccountNonLocked(), user.getCredentialsNonExpired(), user.getId());
    }

    @Override
    public int removeUser(UserEntity user) {
        return transactionTemplate.execute(st -> {
            jdbcTemplate.update("DELETE FROM authorities WHERE user_id = ?", user.getId());
            return jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.getId());
        });
    }
}
