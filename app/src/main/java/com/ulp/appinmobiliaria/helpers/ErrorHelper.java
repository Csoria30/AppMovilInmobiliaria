package com.ulp.appinmobiliaria.helpers;

public class ErrorHelper {

    /**
     * Obtiene mensaje de error para códigos HTTP
     */
    public static String obtenerMensajeError(int codigoError) {
        switch (codigoError) {
            case 400:
                return "Datos inválidos. Verifique la información";
            case 401:
                return TokenHelper.getMensajeSesionExpirada();
            case 403:
                return TokenHelper.getMensajeNoAutorizado();
            case 404:
                return "Recurso no encontrado";
            case 500:
                return "Error del servidor. Intente más tarde";
            default:
                return "Error inesperado. Intente nuevamente";
        }
    }

    /**
     * Obtiene mensaje de error para problemas de conexión
     */
    public static String obtenerMensajeConexion(Throwable t) {
        return "Error de conexión. Verifique su internet";
    }
}