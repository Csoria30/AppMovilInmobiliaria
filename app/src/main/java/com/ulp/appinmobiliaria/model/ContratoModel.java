package com.ulp.appinmobiliaria.model;

import java.io.Serializable;

public class ContratoModel implements Serializable {
    private int idContrato;
    private String fechaInicio;
    private String fechaFinalizacion;
    private double montoAlquiler;
    private boolean estado;
    private int idInquilino;
    private int idInmueble;
    private InquilinoModel inquilino;
    private InmuebleModel inmueble;

    // Getters y Setters
    public int getIdContrato() { return idContrato; }
    public void setIdContrato(int idContrato) { this.idContrato = idContrato; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(String fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }

    public double getMontoAlquiler() { return montoAlquiler; }
    public void setMontoAlquiler(double montoAlquiler) { this.montoAlquiler = montoAlquiler; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public int getIdInquilino() { return idInquilino; }
    public void setIdInquilino(int idInquilino) { this.idInquilino = idInquilino; }

    public int getIdInmueble() { return idInmueble; }
    public void setIdInmueble(int idInmueble) { this.idInmueble = idInmueble; }

    public InquilinoModel getInquilino() { return inquilino; }
    public void setInquilino(InquilinoModel inquilino) { this.inquilino = inquilino; }

    public InmuebleModel getInmueble() { return inmueble; }
    public void setInmueble(InmuebleModel inmueble) { this.inmueble = inmueble; }
}
