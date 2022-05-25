package com.bme.aut.CsaHoSz.domain;

import java.util.Arrays;

public class Roles {

    public static final String[] roles = {"ROLE_MEMBER", "ROLE_COACH", "ROLE_ADMIN"};

    public static boolean isValid(String rolename){
        return Arrays.stream(roles).anyMatch(rolename::equals);
    }

}
