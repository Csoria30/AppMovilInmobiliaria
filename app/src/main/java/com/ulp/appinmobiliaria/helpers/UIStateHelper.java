package com.ulp.appinmobiliaria.helpers;

import com.ulp.appinmobiliaria.R;

public class UIStateHelper {

    /** === CLASE BASE === */
    public static abstract class BaseUIState {
        public String mensaje;
        public TipoMensaje tipoMensaje;
        public boolean mostrarMensaje;
        public boolean mostrarToast;
        public boolean cargando;

        /**
         * NINGUNO: Sin mensaje
         * INFO: Informacion General - Azul
         * SUCCESS: Exito - Verde
         * ERROR: - Error critico - Rojo
         * VALIDATION: Error de validacion - Naranja
         * WARNING: Advertencia - Amarillo
         * */
        public enum TipoMensaje {
            NINGUNO, INFO, SUCCESS, ERROR, VALIDATION, WARNING
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


    /** === UIState : Formularios Editables === */
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

        public FormUIState(boolean modoEdicion, String textoBoton, int iconoBoton, boolean habilitado) {
            super();
            this.mostrarCamposEditables = modoEdicion;
            this.mostrarCamposVisualizacion = !modoEdicion;
            this.textoBoton = textoBoton;
            this.iconoBoton = iconoBoton;
            this.botonHabilitado = habilitado;
            this.mostrarBotonCancelar = modoEdicion;
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
    }

    /** === UIState: Listas (Inmuebles  Contratos) === */
    public static class ListUIState extends BaseUIState {
        public boolean mostrarLista;
        public boolean mostrarVacio;
        public boolean mostrarError;
        public boolean mostrarCargando;
        public int cantidadItems;

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
    public static class PerfilUIStates {

        /** Estado para visualizar datos del perfil sin posibilidad de edición */
        public static FormUIState modoVista(boolean cargando) {
            FormUIState state = new FormUIState(
                    false,
                    "Editar Perfil",
                    android.R.drawable.ic_menu_edit,
                    !cargando
            );
            state.conBotonSecundario(true, !cargando);
            state.conCarga(cargando);
            return state;
        }

        /** Estado para editar campos del perfil con controles de edición activos */
        public static FormUIState modoEdicion(boolean cargando) {
            // ✅ CORREGIDO: Eliminar casting problemático
            FormUIState state = new FormUIState(
                    true,
                    "Guardar Cambios",
                    android.R.drawable.ic_menu_save,
                    !cargando
            );
            state.conBotonSecundario(false, false);
            state.conCarga(cargando);
            return state;
        }

        /** Estado cuando hay errores en la validación de campos del formulario */
        public static FormUIState errorValidacion(String mensaje, String campoError) {
            FormUIState state = new FormUIState(
                    true,
                    "Guardar Cambios",
                    android.R.drawable.ic_menu_save,
                    true
            );
            state.conBotonSecundario(false, false);
            state.conCarga(false);
            state.conMensaje(mensaje, BaseUIState.TipoMensaje.VALIDATION);
            state.campoError = campoError;
            return state;
        }

        /** Estado mientras se están cargando los datos del perfil desde el servidor */
        public static FormUIState cargandoPerfil() {
            FormUIState state = new FormUIState(
                    false,
                    "Cargando...",
                    android.R.drawable.ic_popup_sync,
                    false
            );
            state.conBotonSecundario(false, false);
            state.conCarga(true);
            //state.conMensaje("Cargando perfil...", BaseUIState.TipoMensaje.INFO);
            return state;
        }

        /** Estado mientras se están enviando los cambios del perfil al servidor */
        public static FormUIState guardandoCambios() {
            FormUIState state = new FormUIState(
                    true,
                    "Guardando...",
                    android.R.drawable.ic_popup_sync,
                    false
            );
            state.conBotonSecundario(false, false);
            state.conCarga(true);
            state.conMensaje("Guardando cambios...", BaseUIState.TipoMensaje.INFO);
            return state;
        }

        /** Estado cuando los cambios del perfil se guardaron correctamente */
        public static FormUIState exitoGuardado() {
            FormUIState state = new FormUIState(
                    false,
                    "Editar Perfil",
                    android.R.drawable.ic_menu_edit,
                    true
            );
            state.conBotonSecundario(true, true);
            state.conCarga(false);
            state.conMensaje("✅ Perfil actualizado correctamente", BaseUIState.TipoMensaje.SUCCESS);
            state.conToast(true);
            state.actualizacionExitosa = true;
            return state;
        }

        /** Estado para errores generales de conexión, servidor o token */
        public static FormUIState error(String mensaje) {
            FormUIState state = new FormUIState(
                    false,
                    "Reintentar",
                    android.R.drawable.ic_menu_rotate,
                    true
            );
            state.conBotonSecundario(false, false);
            state.conCarga(false);
            state.conMensaje(mensaje, BaseUIState.TipoMensaje.ERROR);
            return state;
        }

        /** Estado para errores específicos de token o autorización */
        public static FormUIState errorToken(String mensaje) {
            FormUIState state = new FormUIState(
                    false,
                    "Iniciar Sesión",
                    android.R.drawable.ic_lock_idle_lock,
                    true
            );
            state.conBotonSecundario(false, false);
            state.conCarga(false);
            state.conMensaje(mensaje, BaseUIState.TipoMensaje.ERROR);
            return state;
        }

        /** Estado inicial cuando se abre el perfil */
        public static FormUIState inicial() {
            FormUIState state = new FormUIState(
                    false,
                    "Cargar Perfil",
                    R.drawable.ic_menu_refresh,
                    true //Btn habilitado Click
            );
            state.conBotonSecundario(false, false);      //Btn Secundario
            state.conCarga(false);                             // ProgressBar
            return state;
        }

        /** Estado cuando no hay datos de perfil cargados */
        public static FormUIState sinDatos() {
            FormUIState state = new FormUIState(
                    false,
                    "Cargar Perfil",
                    R.drawable.ic_menu_refresh,
                    true                                    //Btn habilitado Click
            );
            state.conBotonSecundario(false, false);
            state.conCarga(false);                              // Carga de Progress Bar
            state.conMensaje("No se pudieron cargar los datos del perfil", BaseUIState.TipoMensaje.WARNING); // ✅ CORREGIDO
            return state;
        }
    }
}