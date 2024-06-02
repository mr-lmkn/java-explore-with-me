package ru.practicum.ewmServer.events.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotSoLate2hValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotSoLate2hDate {
    String message() default "It's so late.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
