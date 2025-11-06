package com.ulp.appinmobiliaria.ui.contrato;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.helpers.ValidationHelper;
import com.ulp.appinmobiliaria.model.ContratoModel;
import com.ulp.appinmobiliaria.model.InmuebleConContratoModel;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.model.PagoDetalleDTO;
import com.ulp.appinmobiliaria.model.PagoModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagosViewModel extends AndroidViewModel {
    private MutableLiveData<List<PagoModel>> mListaPagos = new MutableLiveData<>();
    private MutableLiveData<PagoDetalleDTO> mPagoDTO = new MutableLiveData<>();
    private MutableLiveData<PagoModel> mPagoSeleccionado = new MutableLiveData<>();
    private MutableLiveData<UIStateHelper.ListUIState> mUIStateLista = new MutableLiveData<>();
    private MutableLiveData<UIStateHelper.FormUIState> mUIStateForm = new MutableLiveData<>();
    private List<PagoModel> _listaPagos = new ArrayList<>();
    private Context context;


    // ===== CONSTRUCTOR =====
    public PagosViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
    }

    // ===== GETTERS PARA LIVEDATA =====
    public LiveData<List<PagoModel>> getmListaPagos() {
        return mListaPagos;
    }

    public LiveData<UIStateHelper.ListUIState> getmUIStateLista() {
        return mUIStateLista;
    }

    public LiveData<UIStateHelper.FormUIState> getmUIStateForm() {
        return mUIStateForm;
    }

    public LiveData<PagoDetalleDTO> getmPagoDTO() {
        return mPagoDTO;
    }

    //  ===== METODOS  PRINCIPALES =====

    public void obtenerPagos(){
        //progessBar cargando perfil
        mUIStateForm.setValue(UIStateHelper.FormUIState.cargando());

        // Obtener token
        TokenHelper.ResultadoValidacion validacion = TokenHelper.validarToken(context);

        if (!validacion.esValido) {
            // mUIState.setValue(UIStateHelper.InmueblesUIStates.error(validacion.mensajeError));
            return;
        }

        int idcontrato = mPagoDTO.getValue().getIdContrato();

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<List<PagoModel>> call = api.obtenerPagosPorContrato(validacion.tokenBearer, idcontrato);

        call.enqueue(new Callback<List<PagoModel>>() {
            @Override
            public void onResponse(Call<List<PagoModel>> call, Response<List<PagoModel>> response) {
                _listaPagos.clear();
                if(response.isSuccessful() && response.body() != null){
                    mListaPagos.postValue(response.body());
                }else {
                    mListaPagos.postValue(new ArrayList<>());
                }

                //UI
                UIStateHelper.FormUIState state = UIStateHelper.InmuebleUIState.inicial();
                state.conCarga(false);
                mUIStateForm.postValue(state);
            }

            @Override
            public void onFailure(Call<List<PagoModel>> call, Throwable t) {
                mListaPagos.postValue(new ArrayList<>());
                mUIStateForm.postValue(UIStateHelper.FormUIState.error("Error de conexión"));
            }
        });
    }

    public void obtenerPagosActuales(Bundle pagosActuales){
        if(pagosActuales != null && pagosActuales.containsKey("pagoDetalleDTO")){
            //progessBar cargando perfil
            mUIStateForm.setValue(UIStateHelper.FormUIState.cargando());
            PagoDetalleDTO p = (PagoDetalleDTO) pagosActuales.getSerializable("pagoDetalleDTO");

            if(p != null)
                this.mPagoDTO.setValue(p);

            UIStateHelper.FormUIState state = UIStateHelper.InmuebleUIState.inicial();
            state.conCarga(false);
            state.conEsNuevo(false);
            mUIStateForm.setValue(state);
        }
    }

    /** ===== GETTERS Para UI
     *  Nota: Evitamos retornos nulos
     * */

    //Seleccionar pago de la lista
    public void seleccionarPagoPorIndice(int index){
        List<PagoModel> lista = mListaPagos.getValue();
        if (lista == null || index < 0 || index >= lista.size()) {
            mPagoSeleccionado.setValue(null);
            return;
        }
        mPagoSeleccionado.setValue(lista.get(index));
    }

    //Getter acorde al index
    public String getFechaPagoSeleccionado() {
        PagoModel pago = mPagoSeleccionado.getValue();
        String raw = pago != null && pago.getFechaPago() != null ? pago.getFechaPago() : "";
        String fecha =  ValidationHelper.formatearFecha(raw);
        return "Fecha pago: " + (fecha.isEmpty() ? "-" : fecha);
    }

    public String getMontoPagoSeleccionado() {
        PagoModel pago = mPagoSeleccionado.getValue();
        double monto = pago != null ? pago.getMonto() : 0d;
        return String.format("Monto: $ %.2f", monto);
    }

    public String getDetallePagoSeleccionado() {
        PagoModel pago = mPagoSeleccionado.getValue();
        String det = pago != null && pago.getDetalle() != null ? pago.getDetalle() : "-";
        return "Detalle: " + det;
    }

    public String getEstadoPagoSeleccionado() {
        PagoModel pago = mPagoSeleccionado.getValue();
        if (pago == null) return "Estado: -";
        return "Estado: " + (pago.isEstado() ? "Registrado" : "Pendiente");
    }

    // ===== Getter informacion contrato
    public String getDireccionDto(){
        PagoDetalleDTO pagoDetalleDTO = mPagoDTO.getValue();
        String data = (pagoDetalleDTO != null && pagoDetalleDTO.getDireccionInmueble() != null) ? pagoDetalleDTO.getDireccionInmueble().trim() : "-";
        return "Dirección: " + data;
    }

    public String getMontoContratoDto() {
        PagoDetalleDTO pagoDetalleDTO = mPagoDTO.getValue();
        double monto = pagoDetalleDTO != null ? pagoDetalleDTO.getMontoContrato() : 0d;
        return "Monto contrato: " + String.format("$ %.2f", monto);
    }

    // ===== Calcular Total
    public double calcularTotalPagado() {
        double total = 0d;
        List<PagoModel> lista = mListaPagos.getValue();
        if (lista == null) return 0d;
        for (PagoModel p : lista) {
            if (p != null && p.isEstado()) total += p.getMonto();
        }
        return total;
    }

    public String getTotalPagadoTexto() {
        return String.format("Total pagado: $ %.2f", calcularTotalPagado());
    }


}