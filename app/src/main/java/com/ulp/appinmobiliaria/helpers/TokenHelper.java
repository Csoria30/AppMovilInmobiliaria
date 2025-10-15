package com.ulp.appinmobiliaria.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ulp.appinmobiliaria.request.ApiClient;

public class TokenHelper {
    private static final String TAG = "TokenHelper";
    private static final String PREFS_NAME = "token.xml";
    private static final String TOKEN_KEY = "token";

    /** Guarda el token en SharedPreferences */
    public static void guardarToken(Context context, String token) {
        try {
            SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(TOKEN_KEY, token);
            editor.apply();
            Log.d(TAG, "Token guardado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar token: " + e.getMessage());
        }
    }

    /** Lee el token desde SharedPreferences */
    private static String leerTokenRaw(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return sp.getString(TOKEN_KEY, null);
        } catch (Exception e) {
            Log.e(TAG, "Error al leer token: " + e.getMessage());
            return null;
        }
    }

    /** Valida si un token string es válido */
    public static boolean esTokenValido(String token) {
        return token != null && !token.trim().isEmpty();
    }

    /** Obtiene el token limpio desde SharedPreferences */
    public static String obtenerToken(Context context) {
        try {
            String token = leerTokenRaw(context);
            return esTokenValido(token) ? token : null;
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener token: " + e.getMessage());
            return null;
        }
    }

    /** Obtiene el token en formato Bearer para APIs */
    public static String obtenerTokenBearer(Context context) {
        String token = obtenerToken(context);
        return token != null ? "Bearer " + token : null;
    }

    /** Verifica si existe una sesión activa válida */
    public static boolean tieneSesionActiva(Context context) {
        return obtenerToken(context) != null;
    }

    /** Elimina el token de forma segura */
    public static boolean eliminarToken(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(TOKEN_KEY);
            editor.apply();
            Log.d(TAG, "Token eliminado correctamente");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar token: " + e.getMessage());
            return false;
        }
    }

    /** Limpia completamente las preferencias de token */
    public static boolean limpiarSesion(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
            Log.d(TAG, "Sesión limpiada completamente");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error al limpiar sesión: " + e.getMessage());
            return false;
        }
    }

    /**  ===== MENSAJES DE ERROR ===== */

    public static String getMensajeTokenInvalido() {
        return "Token no encontrado. Inicie sesión nuevamente.";
    }

    public static String getMensajeSesionExpirada() {
        return "Su sesión ha expirado. Inicie sesión nuevamente.";
    }

    public static String getMensajeNoAutorizado() {
        return "No tiene permisos para realizar esta acción.";
    }


    /** Resultado de validación con información detallada */
    public static class ResultadoValidacion {
        public final boolean esValido;
        public final String token;           // Token limpio
        public final String tokenBearer;     // Token con formato Bearer
        public final String mensajeError;

        private ResultadoValidacion(boolean esValido, String token, String tokenBearer, String mensajeError) {
            this.esValido = esValido;
            this.token = token;
            this.tokenBearer = tokenBearer;
            this.mensajeError = mensajeError;
        }

        public static ResultadoValidacion exitoso(String token, String tokenBearer) {
            return new ResultadoValidacion(true, token, tokenBearer, null);
        }

        public static ResultadoValidacion error(String mensaje) {
            return new ResultadoValidacion(false, null, null, mensaje);
        }
    }

    /**
     * Validación completa con información detallada
     */
    public static ResultadoValidacion validarToken(Context context) {
        try {
            String token = obtenerToken(context);

            if (token == null) {
                return ResultadoValidacion.error(getMensajeTokenInvalido());
            }

            String tokenBearer = obtenerTokenBearer(context);
            return ResultadoValidacion.exitoso(token, tokenBearer);

        } catch (Exception e) {
            Log.e(TAG, "Error al validar token: " + e.getMessage());
            return ResultadoValidacion.error("Error interno al validar sesión");
        }
    }


}