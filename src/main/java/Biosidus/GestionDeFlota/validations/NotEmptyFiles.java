package Biosidus.GestionDeFlota.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyFilesValidator.class)

public @interface NotEmptyFiles {
    String message() default "Debe seleccionar al menos un archivo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}