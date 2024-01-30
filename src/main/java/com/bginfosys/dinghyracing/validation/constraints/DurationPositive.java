package com.bginfosys.dinghyracing.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.bginfosys.dinghyracing.validation.DurationPositiveValidator;

@Documented
@Constraint(validatedBy = DurationPositiveValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationPositive {
	String message() default "Lap time must be greater than zero";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
