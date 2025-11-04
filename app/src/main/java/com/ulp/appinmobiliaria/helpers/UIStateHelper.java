package com.ulp.appinmobiliaria.helpers;

import com.ulp.appinmobiliaria.R;

/**
 * Clase helper para manejar estados de UI en la aplicación.
 * Proporciona estados base para formularios y listas.
 */
public class UIStateHelper {

    /** === CLASE BASE === */
    public static abstract class BaseUIState {
        public String mensaje;
        public TipoMensaje tipoMensaje;
        public boolean mostrarMensaje;
        public boolean mostrarToast;
        public boolean cargando;

        /** === TIPOS DE MENSAJE === */
        public enum TipoMensaje {
            NINGUNO,    // Sin mensaje
            INFO,       // Información General - Azul
            SUCCESS,    // Éxito - Verde
            ERROR,      // Error crítico - Rojo
            VALIDATION, // Error de validación - Naranja
            WARNING     // Advertencia - Amarillo
        }

        public BaseUIState() {
            this.mensaje = "";
            this.tipoMensaje = TipoMensaje.NINGUNO;
            this.mostrarMensaje = false;
            this.cargando = false;
        }

        public BaseUIState conMensaje(String mensaje, TipoMensaje tipo) {
            this.mensaje = mensaje;
            this.tipoMensaje = tipo;
            this.mostrarMensaje = mensaje != null && !mensaje.isEmpty();
            return this;
        }

        public BaseUIState sinMensaje() {
            this.mensaje = "";
            this.tipoMensaje = TipoMensaje.NINGUNO;
            this.mostrarMensaje = false;
            return this;
        }

        public BaseUIState conCarga(boolean cargando) {
            this.cargando = cargando;
            return this;
        }

        public BaseUIState conToast(boolean mostrarToast) {
            this.mostrarToast = mostrarToast;
            return this;
        }
    }


    /**
     * Estado base para todos los formularios editables.
     * Maneja la visibilidad de campos, botones y mensajes.
     */
    public static class FormUIState extends BaseUIState {
        public boolean mostrarCamposEditables;
        public boolean mostrarCamposVisualizacion;
        public boolean actualizacionExitosa;
        public boolean botonHabilitado;
        public boolean mostrarBotonCancelar;
        public boolean mostrarBotonSecundario;
        public boolean habilitarBotonSecundario;
        public String textoBoton;
        public int iconoBoton;
        public String campoError;
        public boolean esNuevo;
        public String textoBotonCancelar;

        public FormUIState(boolean modoEdicion, String textoBoton, int iconoBoton, boolean habilitado) {
            super();
            this.mostrarCamposEditables = modoEdicion;
            this.mostrarCamposVisualizacion = !modoEdicion;
            this.textoBoton = textoBoton;
            this.iconoBoton = iconoBoton;
            this.botonHabilitado = habilitado;
            this.mostrarBotonCancelar = modoEdicion;
            this.esNuevo = false;
            this.textoBotonCancelar = "Cancelar"; //Valor Default

        }

        public FormUIState conTextoBotonCancelar(String texto) {
            this.textoBotonCancelar = texto;
            return this;
        }
        public FormUIState conMensaje(String mensaje, TipoMensaje tipo) {
            super.conMensaje(mensaje, tipo);
            return this;
        }

        @Override
        public FormUIState sinMensaje() {
            super.sinMensaje();
            return this;
        }

        @Override
        public FormUIState conCarga(boolean cargando) {
            super.conCarga(cargando);
            this.botonHabilitado = this.botonHabilitado && !cargando;
            this.habilitarBotonSecundario = this.habilitarBotonSecundario && !cargando;
            return this;
        }

        public FormUIState conBotonSecundario(boolean mostrar, boolean habilitar) {
            this.mostrarBotonSecundario = mostrar;
            this.habilitarBotonSecundario = habilitar && !cargando;
            return this;
        }

        public static FormUIState modoVista(String textoBoton, int icono) {
            FormUIState state = new FormUIState(
                    false,
                    textoBoton,
                    icono,
                    true
            );
            state.mostrarBotonSecundario = true;
            return state;
        }
        public static FormUIState modoVista() {
            return modoVista("Editar", android.R.drawable.ic_menu_edit);
        }
        public static FormUIState modoEdicion(String textoBoton, int icono) {
            FormUIState state = new FormUIState(
                    true,
                    textoBoton,
                    icono,
                    true
            );
            return state;
        }
        public static FormUIState modoEdicion() {
            return modoEdicion("Guardar", android.R.drawable.ic_menu_save);
        }
        public static FormUIState errorValidacion(String mensaje, String campo) {
            FormUIState state = new FormUIState(
                    true,
                    "Guardar",
                    android.R.drawable.ic_menu_save,
                    true
            );
            state.conMensaje(mensaje, TipoMensaje.VALIDATION);
            state.campoError = campo;
            return state;
        }

        public static FormUIState guardando(String mensaje) {
            FormUIState state = new FormUIState(
                    true,
                    "Guardando...",
                    android.R.drawable.ic_popup_sync,
                    false
            );
            state.conCarga(true);
            state.conMensaje(mensaje, TipoMensaje.INFO);
            return state;
        }

        public static FormUIState exitoGuardado(String mensaje, String textoBoton) {
            FormUIState state = new FormUIState(
                    false,
                    textoBoton != null ? textoBoton : "Editar",
                    android.R.drawable.ic_menu_edit,
                    true
            );
            state.conMensaje(mensaje, TipoMensaje.SUCCESS);
            state.conToast(true);
            state.actualizacionExitosa = true;
            return state;
        }

        public void cambiarModoEdicion(boolean modoEdicion) {
            this.mostrarCamposEditables = modoEdicion;
            this.mostrarCamposVisualizacion = !modoEdicion;
            this.mostrarBotonCancelar = modoEdicion;
        }

        public void actualizarBotones(String texto, int icono, boolean habilitado) {
            this.textoBoton = texto;
            this.iconoBoton = icono;
            this.botonHabilitado = habilitado && !cargando;
        }

        public void limpiarError() {
            this.campoError = null;
            this.mensaje = null;
            this.tipoMensaje = TipoMensaje.NINGUNO;
        }

        public static FormUIState error(String mensaje) {
            FormUIState state = new FormUIState(
                    false,
                    "Reintentar",
                    android.R.drawable.ic_menu_rotate,
                    true
            );
            state.conBotonSecundario(false, false);
            state.conCarga(false);
            state.conMensaje(mensaje, TipoMensaje.ERROR);
            return state;
        }

        public static FormUIState cargando() {
            FormUIState state = new FormUIState(
                    false,
                    "Cargando...",
                    android.R.drawable.ic_popup_sync,
                    false
            );
            state.conBotonSecundario(false, false);
            state.conCarga(true);
            return state;
        }

        // Sobrecarga: permitir texto de botón personalizado
        public static FormUIState cargando(String textoBoton) {
            FormUIState state = new FormUIState(
                    false,
                    textoBoton != null && !textoBoton.isEmpty() ? textoBoton : "Cargando...",
                    android.R.drawable.ic_popup_sync,
                    false
            );
            state.conBotonSecundario(false, false);
            state.conCarga(true);
            return state;
        }

        public FormUIState conEsNuevo(boolean esNuevo) {
            this.esNuevo = esNuevo;
            return this;
        }
    }

    /** === UIState: Listas (Inmuebles  Contratos) === */
    public static class ListUIState extends BaseUIState {
        public int cantidadItems;
        public boolean mostrarLista;
        public boolean mostrarVacio;
        public boolean mostrarError;
        public boolean mostrarCargando;

        public ListUIState() {
            super();
        }

        public static ListUIState cargando() {
            ListUIState state = new ListUIState();
            state.mostrarCargando = true;
            state.mostrarLista = false;
            state.mostrarVacio = false;
            state.mostrarError = false;
            return state;
        }

        public static ListUIState conDatos(int cantidadItems) {
            ListUIState state = new ListUIState();
            state.cantidadItems = cantidadItems;
            state.mostrarLista = cantidadItems > 0;
            state.mostrarVacio = cantidadItems == 0;
            state.mostrarCargando = false;
            state.mostrarError = false;
            return state;
        }

        public static ListUIState error(String mensaje) {
            ListUIState state = new ListUIState();
            state.mostrarError = true;
            state.mostrarLista = false;
            state.mostrarVacio = false;
            state.mostrarCargando = false;
            state.conMensaje(mensaje, TipoMensaje.ERROR);
            return state;
        }
    }


    /** === FACTORY METHODS PARA ESTADOS COMUNES === */
    public static class PerfilUIStates extends FormUIState {

        public PerfilUIStates(boolean modoEdicion, String textoBoton, int iconoBoton, boolean habilitado) {
            super(modoEdicion, textoBoton, iconoBoton, habilitado);
        }

        public static FormUIState inicial() {
            return modoVista("Editar Perfil", android.R.drawable.ic_menu_edit);
        }

        public static FormUIState guardandoPerfil() {
            return guardando("Guardando cambios del perfil...");
        }

        public static FormUIState perfilGuardado() {
            return exitoGuardado("Perfil actualizado correctamente", "Editar Perfil");
        }

        public static FormUIState errorCampoVacio(String campo) {
            return errorValidacion("El campo " + campo + " es obligatorio", campo);
        }

        public static FormUIState cancelarEdicion() {
            FormUIState state = inicial();
            state.mostrarToast = true;
            state.mensaje = "Edición cancelada";
            state.tipoMensaje = TipoMensaje.INFO;
            return state;
        }
    }

    public static class InmuebleUIState extends FormUIState {
        public InmuebleUIState(boolean modoEdicion, String textoBoton, int iconoBoton, boolean habilitado) {
            super(modoEdicion, textoBoton, iconoBoton, habilitado);
        }

        public static FormUIState inicial() {
            return modoVista("Editar Inmueble", android.R.drawable.ic_menu_edit);
        }

        public static FormUIState guardandoInmueble() {
            return guardando("Guardando datos del inmueble...");
        }

        public static FormUIState inmuebleGuardado() {
            return exitoGuardado("Inmueble actualizado correctamente", "Editar Inmueble");
        }

        public static FormUIState errorCampoInvalido(String campo, String razon) {
            return errorValidacion("El campo " + campo + " " + razon, campo);
        }

        public static FormUIState cancelarCreacion() {
            FormUIState state = inicial();
            state.mostrarToast = true;
            state.mensaje = "Creación cancelada";
            state.tipoMensaje = TipoMensaje.INFO;
            return state;
        }
    }
}