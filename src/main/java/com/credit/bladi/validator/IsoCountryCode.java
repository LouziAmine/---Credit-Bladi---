package com.credit.bladi.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsoCountryCodeValidator.class)
public @interface IsoCountryCode {

    String message() default "Nationality code is not a recognized ISO 3166-1 alpha-2 country code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
