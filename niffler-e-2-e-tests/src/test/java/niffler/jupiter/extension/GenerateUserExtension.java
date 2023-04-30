package niffler.jupiter.extension;

import niffler.db.dao.NifflerUsersDAO;
import niffler.db.dao.NifflerUsersDAOJdbc;
import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import niffler.jupiter.annotation.GenerateUser;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;

public class GenerateUserExtension implements ParameterResolver, BeforeEachCallback {

    public static ExtensionContext.Namespace USER_ENTITY_EXTENSION_NAMESPACE = ExtensionContext.Namespace.create(GenerateUserExtension.class);

    private final NifflerUsersDAO nifflerUsersDAO = new NifflerUsersDAOJdbc();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        GenerateUser annotation = context.getRequiredTestMethod()
                .getAnnotation(GenerateUser.class);

        if (annotation != null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(annotation.username());
            userEntity.setPassword(annotation.password());
            userEntity.setEnabled(annotation.enabled());
            userEntity.setAccountNonExpired(true);
            userEntity.setAccountNonLocked(true);
            userEntity.setCredentialsNonExpired(true);
            userEntity.setAuthorities(Arrays.stream(Authority.values()).map(
                    a -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(a);
                        ae.setUser(userEntity);
                        return ae;
                    }
            ).toList());

            nifflerUsersDAO.createUser(userEntity);

            context.getStore(USER_ENTITY_EXTENSION_NAMESPACE).put("user", userEntity);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(USER_ENTITY_EXTENSION_NAMESPACE).get("user", UserEntity.class);
    }
}
