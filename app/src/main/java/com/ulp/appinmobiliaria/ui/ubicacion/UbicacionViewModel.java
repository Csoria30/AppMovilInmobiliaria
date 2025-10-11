package com.ulp.appinmobiliaria.ui.ubicacion;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class UbicacionViewModel extends AndroidViewModel {
    private MutableLiveData<MapaInmobiliaria> mMapa = new MutableLiveData<>();

    public UbicacionViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<MapaInmobiliaria> getmMapa(){
        return mMapa;
    }

    public void cargarMapa() {
        MapaInmobiliaria mapaInmobiliaria = new MapaInmobiliaria();
        mMapa.setValue(mapaInmobiliaria);
    }

    //Clase interna
    public class MapaInmobiliaria implements OnMapReadyCallback {
        LatLng centroSanLuis = new LatLng(-33.280576, -66.332482); // Centro de San Luis
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {

            // Marcador del centro de San Luis (zona de cobertura)
            MarkerOptions marcadorCentro = new MarkerOptions();
            marcadorCentro.position(centroSanLuis);
            marcadorCentro.title("Centro San Luis");
            marcadorCentro.snippet("🏢 Zona de cobertura - Propiedades disponibles");

            //Agregar marcadores al mapa
            googleMap.addMarker(marcadorCentro);

            // Tipo de mapa
            //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //Posicion Camara
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(centroSanLuis)  // Centrar en la inmobiliaria
                    .zoom(18)              // Zoom apropiado para ver ambos puntos
                    .bearing(0)            // Sin rotación
                    .tilt(0)               // Sin inclinación
                    .build();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.animateCamera(cameraUpdate);

            // Configurar controles del mapa
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
        }
    }
}