package Biosidus.GestionDeFlota.validations;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class NotEmptyFilesValidator implements ConstraintValidator<NotEmptyFiles, MultipartFile[]> {
    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext context) {
        if (files == null || files.length == 0) {
            return false;
        }

        // Verifica que al menos un archivo no esté vacío
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
