package niffler.jupiter.extension;

import io.qameta.allure.AllureId;
import niffler.db.dao.NifflerUsersDAO;
import niffler.db.dao.NifflerUsersDAOJdbc;
import niffler.db.entity.UserEntity;
import niffler.jupiter.annotation.DeleteUser;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static niffler.jupiter.extension.GenerateUserExtension.USER_ENTITY_EXTENSION_NAMESPACE;

public class DeleteUserExtension implements AfterEachCallback {

    private final NifflerUsersDAO nifflerUsersDAO = new NifflerUsersDAOJdbc();

    @Override
    public void afterEach(ExtensionContext context) {
        DeleteUser annotation = context.getRequiredTestMethod()
                .getAnnotation(DeleteUser.class);

        if (annotation != null) {
            UserEntity foundUser = nifflerUsersDAO.getUser(annotation.username());

            nifflerUsersDAO.removeUser(foundUser);
        }

        String allureIdValue = context.getRequiredTestMethod().getAnnotation(AllureId.class).value();

        context.getStore(USER_ENTITY_EXTENSION_NAMESPACE).remove("user" + allureIdValue);
    }
}
