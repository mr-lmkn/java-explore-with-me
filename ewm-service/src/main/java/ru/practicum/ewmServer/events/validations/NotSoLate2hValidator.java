package ru.practicum.ewmServer.events.validations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;


@Slf4j
public class NotSoLate2hValidator implements ConstraintValidator<NotSoLate2hDate, LocalDateTime> {

    @Override
    public void initialize(NotSoLate2hDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        boolean isOk = true;
        if (!ObjectUtils.isEmpty(value)) {
            log.info(String.format("Validate date start: %s", value.toString()));
            if (LocalDateTime.now().plusHours(2).isAfter(value)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("It's so late.").addConstraintViolation();
                isOk = false;
            }
        }
        return isOk;
    }
}