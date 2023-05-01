package niffler.jupiter.annotation;

import niffler.jupiter.extension.DeleteUserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(DeleteUserExtension.class)
public @interface DeleteUser {
    String username();
}
