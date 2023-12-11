package com.bginfosys.dinghyracing.validation;

import java.time.Duration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.bginfosys.dinghyracing.validation.constraints.DurationPositive;

public class DurationPositiveValidator implements ConstraintValidator<DurationPositive, Duration> {

	@Override
    public void initialize(DurationPositive duration) {
    }
	
	@Override
	public boolean isValid(Duration duration, ConstraintValidatorContext context) {
		return duration != null && !duration.isNegative() && !duration.isZero();
	}

}
