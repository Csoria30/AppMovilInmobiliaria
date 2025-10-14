package com.ulp.appinmobiliaria.helpers;

import android.content.Context;
import android.util.Log;

import com.ulp.appinmobiliaria.request.ApiClient;

public class TokenHelper {
    private static final String TAG = "TokenHelper";

    /** Valida si un token string es válido */
    public static boolean esTokenValido(String token) {
        return token != null && !token.trim().isEmpty();
    }

    /** Obtiene el token limpio desde SharedPreferences */
    public static String obtenerToken(Context context) {
        try {
            String token = ApiClient.leerToken(context);
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
            return ApiClient.eliminarToken(context);
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar token: " + e.getMessage());
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