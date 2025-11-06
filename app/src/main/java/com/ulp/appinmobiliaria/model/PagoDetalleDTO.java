package com.ulp.appinmobiliaria.model;

import java.io.Serializable;

public class PagoDetalleDTO implements Serializable {
    private int idContrato;
    private String direccionInmueble;
    private double montoContrato;
    private double montoPago;
    private String fechaPago;
    private String detalle;
    private boolean estado;

    public int getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(int idContrato) {
        this.idContrato = idContrato;
    }

    public String getDireccionInmueble() {
        return direccionInmueble;
    }

    public void setDireccionInmueble(String direccionInmueble) {
        this.direccionInmueble = direccionInmueble;
    }

    public double getMontoContrato() {
        return montoContrato;
    }

    public void setMontoContrato(double montoContrato) {
        this.montoContrato = montoContrato;
    }

    public double getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(double montoPago) {
        this.montoPago = montoPago;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
