package com.ulp.appinmobiliaria.ui.login;

public class LoginRepository {
    private static final String USER_EMAIL = "correo@correo.com";
    private static final String USER_PASS = "123";

    public boolean validarCredenciales(String email, String password) {
        return email.equals(USER_EMAIL) && password.equals(USER_PASS);
    }
}
