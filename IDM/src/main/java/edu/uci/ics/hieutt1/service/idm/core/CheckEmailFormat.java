package edu.uci.ics.hieutt1.service.idm.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckEmailFormat {
    private String email;

    public CheckEmailFormat(String email) {
        this.email = email;
    }

    public boolean checkLength() {
        if (email == null || email.length() < 5){
            return false;
        }
        else {
            return true;
        }
    }
    public boolean checkFormat() {
        Pattern email_p = Pattern.compile("^\\p{Alnum}+@\\p{Alnum}+\\.\\p{Alnum}+$");
        Matcher m = email_p.matcher(email);
        if (m.matches()) {
            return true;
        }
        else {
            return false;
        }
    }
}

