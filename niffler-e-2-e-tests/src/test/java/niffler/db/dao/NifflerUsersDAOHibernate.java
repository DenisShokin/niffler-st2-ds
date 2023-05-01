package niffler.db.dao;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import niffler.db.ServiceDB;
import niffler.db.entity.UserEntity;
import niffler.db.jpa.EmfProvider;
import niffler.db.jpa.JpaTransactionManager;

public class NifflerUsersDAOHibernate extends JpaTransactionManager implements NifflerUsersDAO {

    public NifflerUsersDAOHibernate() {
        super(EmfProvider.INSTANCE.getEmf(ServiceDB.NIFFLER_AUTH).createEntityManager());
    }

    @Override
    public int createUser(UserEntity user) {
        user.setPassword(pe.encode(user.getPassword()));
        persist(user);
        return 0;
    }

    @Override
    public String getUserId(String userName) {
        return em.createQuery("select u from UserEntity u where username=:username", UserEntity.class)
                .setParameter("username", userName)
                .getSingleResult()
                .getId()
                .toString();
    }

    @Override
    public UserEntity getUser(String userName) {
        return em.createQuery("select u from UserEntity u where username=:username", UserEntity.class)
                .setParameter("username", userName)
                .getSingleResult();
    }

    @Override
    public int updateUser(UserEntity user) {
        int executeUpdate;

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Query query = em.createQuery("update UserEntity set username=:username, password=:password, enabled=:enabled,"
                + "accountNonExpired=:accountNonExpired, accountNonLocked=:accountNonLocked, "
                + "credentialsNonExpired=:credentialsNonExpired where id=:id");
        query.setParameter("id", user.getId());
        query.setParameter("username", user.getUsername());
        query.setParameter("password", pe.encode(user.getPassword()));
        query.setParameter("enabled", user.getEnabled());
        query.setParameter("accountNonExpired", user.getAccountNonExpired());
        query.setParameter("accountNonLocked", user.getAccountNonLocked());
        query.setParameter("credentialsNonExpired", user.getCredentialsNonExpired());

        executeUpdate = query.executeUpdate();
        transaction.commit();

        return executeUpdate;
    }

    @Override
    public int removeUser(UserEntity user) {
        remove(user);
        return 0;
    }
}
