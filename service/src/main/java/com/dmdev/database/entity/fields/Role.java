package com.dmdev.database.entity.fields;

public enum Role {
    USER, ADMIN;

    public static Boolean isAdmin(Role role) {
        return ADMIN.equals(role);
    }
}
