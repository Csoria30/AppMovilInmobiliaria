package com.ulp.appinmobiliaria.helpers;

import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Helper para validaciones comunes en la aplicación
 */
public class ValidationHelper {

    // Patrones de validación
    private static final Pattern PATTERN_DNI = Pattern.compile("^\\d{7,8}$");
    private static final Pattern PATTERN_TELEFONO = Pattern.compile("^\\d{8,15}$");
    private static final Pattern PATTERN_SOLO_LETRAS = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    private static final Pattern PATTERN_ALPHANUMERICO = Pattern.compile("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$");


    /** Si no es null o vacio  Esta  OK*/
    public static boolean esCampoValido(String campo) {
        return campo != null && !campo.trim().isEmpty();
    }

    /** Valida que un campo tenga una longitud mínima */
    public static boolean tieneLongitudMinima(String campo, int longitudMinima) {
        return esCampoValido(campo) && campo.trim().length() >= longitudMinima;
    }

    /** Valida que un campo esté dentro de un rango de longitud */
    public static boolean tieneRangoLongitud(String campo, int longitudMinima, int longitudMaxima) {
        if (!esCampoValido(campo)) return false;
        int longitud = campo.trim().length();
        return longitud >= longitudMinima && longitud <= longitudMaxima;
    }

    /** Valida formato de email  */
    public static boolean esEmailValido(String email) {
        return esCampoValido(email) &&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    /**
        Valida longitud y formato de contraseña
        Longitud mínima requerida (por defecto 6)
     */
    public static boolean esPasswordValido(String password, int longitudMinima) {
        return tieneLongitudMinima(password, longitudMinima);
    }

    /**  Valida longitud de contraseña con valor por defecto (6 caracteres) */
    public static boolean esPasswordValido(String password) {
        return esPasswordValido(password, 6);
    }

    /** Valida que dos contraseñas coincidan */
    public static boolean passwordsCoinciden(String password1, String password2) {
        return esCampoValido(password1) &&
                esCampoValido(password2) &&
                password1.trim().equals(password2.trim());
    }

    /** Valida formato de DNI argentino (7 u 8 dígitos) */
    public static boolean esDniValido(String dni) {
        return esCampoValido(dni) && PATTERN_DNI.matcher(dni.trim()).matches();
    }

    /** Valida formato de teléfono (8 a 15 dígitos) */
    public static boolean esTelefonoValido(String telefono) {
        return esCampoValido(telefono) && PATTERN_TELEFONO.matcher(telefono.trim()).matches();
    }

    /** Valida que un nombre contenga solo letras y espacios */
    public static boolean esNombreValido(String nombre) {
        return esCampoValido(nombre) &&
                PATTERN_SOLO_LETRAS.matcher(nombre.trim()).matches();
    }

    /** Valida nombre con longitud mínima (default 2) */
    public static boolean esNombreValidoConLongitud(String nombre, int longitudMinima) {
        return esNombreValido(nombre) && tieneLongitudMinima(nombre, longitudMinima);
    }

    /** Valida nombre con longitud mínima por defecto (2 caracteres)*/
    public static boolean esNombreValidoConLongitud(String nombre) {
        return esNombreValidoConLongitud(nombre, 2);
    }

    /**Valida que sea diferente a otro valor (útil para nueva vs actual contraseña) */
    public static boolean esDiferente(String valorNuevo, String valorActual) {
        return esCampoValido(valorNuevo) &&
                esCampoValido(valorActual) &&
                !valorNuevo.trim().equals(valorActual.trim());
    }

    /** Sanitiza un string removiendo espacios extras */
    public static String sanitizar(String input) {
        return input != null ? input.trim() : null;
    }

    /** Sanitiza y convierte a formato título (Primera Letra Mayúscula) */
    public static String sanitizarYConvertirATitulo(String input) {
        if (!esCampoValido(input)) return input;

        String sanitizado = sanitizar(input).toLowerCase();
        return sanitizado.substring(0, 1).toUpperCase() + sanitizado.substring(1);
    }

    /** Valida múltiples campos obligatorios de una vez */
    public static boolean validarCamposObligatorios(String... campos) {
        for (String campo : campos) {
            if (!esCampoValido(campo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Formatea una fecha ISO (yyyy-MM-dd) a un formato legible para el usuario en español,
     * p. ej. "1 de agosto de 2025".
     * Usa java.time si está disponible (API 26+), y SimpleDateFormat como fallback.
     */
    public static String formatearFecha(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) return "";
        try {
            // Intentamos java.time (API 26+)
            LocalDate ld = LocalDate.parse(isoDate);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es"));
            return ld.format(fmt);
        } catch (Throwable ignored) {
            try {
                SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date d = in.parse(isoDate);
                SimpleDateFormat out = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es"));
                return out.format(d);
            } catch (Exception e) {
                return isoDate; // si falla, devolvemos el original
            }
        }
    }
}