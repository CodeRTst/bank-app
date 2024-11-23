package tgpr.bank.model;

import tgpr.framework.Error;
import tgpr.framework.ErrorList;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

public abstract class UserValidator {

    public static Error isValidBirthdate(LocalDate birthdate) {
        if (birthdate == null)
            return new Error("BirthDate required", User.field.Birthdate);
        var age = Period.between(birthdate,LocalDate.now()).getYears();
        if (age < 18)
            return new Error("must be 18 years old",User.field.Birthdate);
        else if (age > 150)
            return new Error("may not be older than 150 years",User.field.Birthdate);

        return Error.NOERROR;
    }

    //utiliser methode isValidEmail lors de la creation d'un client.
    public static Error isValidEmail(String email) {
        if (email == null || email.isBlank())
            return new Error("email required", User.field.Email);
        if (!Pattern.matches("^\\w+@\\w+\\.\\w+$",email))
            return new Error("invalid email",User.field.Email);

        return Error.NOERROR;
    }

    public static Error isValidUniqueEmail(String email) {
        var error = isValidEmail(email);
        if (error != Error.NOERROR)
            return error;
        if (User.getByEmail(email) != null)
            return new Error("email already exist",User.field.Email);
        System.out.println(User.getByEmail(email));
        return Error.NOERROR;
    }

    //utiliser methode isValidAvailableEmail lors de l'authentification (login).
    public static Error isValidAvailableEmail(String email) {
        var error = isValidEmail(email);
        if (error != Error.NOERROR)
            return error;
        if (User.getByEmail(email) != null)
            return new Error("email doesn't exist",User.field.Email);
        System.out.println(User.getByEmail(email));
        return Error.NOERROR;
    }

    public static Error isValidPassword(String password) {
        if (password == null || password.isBlank())
            return new Error("password required", User.field.Password);
        if (!Pattern.matches("[a-zA-Z0-9]{3,}", password))
            return new Error("invalid password", User.field.Password);
        return Error.NOERROR;
    }

    public static Error isValidFirstName(String firstName) {
        if (firstName == null || firstName.isBlank())
            return new Error("firstname required", User.field.FirstName);
        if (!Pattern.matches("[\\u00C0-\\u017Fa-zA-Z']+([- ][\\u00C0-\\u017Fa-zA-Z']+){0,20}", firstName))
            return new Error("invalid first name", User.field.FirstName);
        return Error.NOERROR;
    }

    public static Error isValidLastName(String lastName) {
        if (lastName == null || lastName.isBlank())
            return new Error("Last name required", User.field.LastName);
        if (!Pattern.matches("[\\u00C0-\\u017Fa-zA-Z']+([- ][\\u00C0-\\u017Fa-zA-Z']+){0,20}", lastName))
            return new Error("invalid last name", User.field.LastName);
        return Error.NOERROR;
    }

    public static List<Error> validate(User user) {
        var errors = new ErrorList();
        errors.add(isValidEmail(user.getEmail()));
        errors.add(isValidFirstName(user.getFirstName()));
        errors.add(isValidLastName(user.getLastName()));
        errors.add(isValidBirthdate(user.getBirthDate()));

        // cross-fields validations
        /*if (member.getProfile() != null && !member.getProfile().isBlank() && member.getProfile().equals(member.getPseudo()))
            errors.add("profile must be different from pseudo", Member.Fields.Profile);

        */
        return errors;
    }


}
