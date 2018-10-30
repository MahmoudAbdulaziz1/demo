package com.taj.controller.test;

import com.taj.model.TakatfTenderCategoryPOJO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class NotEmptyFieldsValidator implements ConstraintValidator<NotEmptyFields, List<TakatfTenderCategoryPOJO>> {

    @Override
    public void initialize(NotEmptyFields notEmptyFields) {
    }

    @Override
    public boolean isValid(List<TakatfTenderCategoryPOJO> objects, ConstraintValidatorContext context) {
        return !objects.isEmpty();
    }
}