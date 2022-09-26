package com.dmdev.entity.fields;

public enum Role {
    USER, ADMIN;

    public static Boolean isAdmin(Role role){
        return role.equals(ADMIN);
    }
    public static Role initRole(Boolean isAdmin){
        return isAdmin ? Role.ADMIN : Role.USER;
    }
}
