package com.gem.auth.services;

import org.apache.commons.validator.routines.EmailValidator;

import javax.ws.rs.*;

import static com.gem.commons.Checker.checkParamNotNull;
import static com.gem.commons.Utils.strip;

public class Verifier {

    public static void checkUserName(String name) {
        checkParamNotNull("name", name);
        name = name.toLowerCase();

        if(name.length() < 3){
            throw new BadRequestException("The user name must be at least 6 characters long.");
        }

        if(EmailValidator.getInstance().isValid(name)){
            return;
        }

        //If not an email, then a name that allows alphanumeric characters and
        //some special chars like dash(-), dot(.) and underscore(_).
        //---------------------------------------------------------------------
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            //Allows:
            // 1 - Characters from a to z
            // 2 - Digits from 0 to 9
            // 3 - Special characters: '.', '-' and '_'
            if (    (c >= 'a' && c <= 'z') ||
                    (c >= '0' && c <= '9') ||
                    c == '.' ||
                    c == '-' ||
                    c == '_') {
                //good chars let's continue
                continue;
            }

            throw new BadRequestException("Invalid user name. " +
                    "Only letter, digit, dot(.), dash(-) and underscore(_) characters are allowed.");
        }

        //Other conditions:
        // * Must start with [a-z] or [0-9]
        char firstChar = name.charAt(0);
        if (((firstChar >= 'a' && firstChar <= 'z')
                || (firstChar >= '0' && firstChar <= '9')) == false) {
            throw new BadRequestException(
                    "Invalid user name. It must start with a letter or digit.");
        }

        // * The dot(.), dash(-) and underscore(_) characters, can not be sequential.
        for (int i = 0; i < name.length(); i++) {
            char crt = name.charAt(i);
            if (i > 0) {
                char prev = name.charAt(i - 1);

                if (crt == '.' || crt == '-' || crt == '_') {
                    if (prev == '.' || prev == '-' || prev == '_') {
                        throw new BadRequestException("Invalid user name. " +
                                "It can not have two consecutive special characters ('.', '-' or '_').");
                    }
                }
            }
        }

        // * Must end with [a-z] or [0-9]
        char lastChar = name.charAt(name.length() - 1);
        if (((lastChar >= 'a' && lastChar <= 'z')
                || (lastChar >= '0' && lastChar <= '9')) == false) {
            throw new BadRequestException(
                    "Invalid " + name + ". It must end with a letter or digit.");
        }

        //---------------------------------------------------------------------
    }


    public static void checkPassword(String pass){
        checkParamNotNull("pass", pass);

        pass = strip(pass);

        if(pass == null){
            throw new BadRequestException("The password is required.");
        }

        if(pass.length() < 6){
            throw new BadRequestException("The minimun password length is 6.");
        }
    }
}
