package com.example.taskapi.validation;

import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserPasswordValidation implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    String password= (String) target;

      if(password.isBlank())
      {
          errors.rejectValue("password", "field.required", "Password is required.");
      }
      if (password.length() < 8 )
      {
          errors.rejectValue("password", "field.error", "Password Length Must be more than 8 Character.");

      }


    }
}
