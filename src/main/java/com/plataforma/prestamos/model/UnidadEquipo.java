package com.plataforma.prestamos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "unidades_equipo")
public class UnidadEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUnidad;

    @Column(unique = true, nullable = false)
    private String codigo; 

    private boolean estado; 
    private String sede;

    @ManyToOne
    @JoinColumn(name = "id_tipo")
    private TipoEquipo tipoEquipo;

    public UnidadEquipo() {}

    public UnidadEquipo(String codigo, boolean estado, String sede, TipoEquipo tipoEquipo) {
        this.codigo = codigo;
        this.estado = estado;
        this.sede = sede;
        this.tipoEquipo = tipoEquipo;
    }

    public Integer getIdUnidad() { return idUnidad; }
    public void setIdUnidad(Integer idUnidad) { this.idUnidad = idUnidad; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
    public TipoEquipo getTipoEquipo() { return tipoEquipo; }
    public void setTipoEquipo(TipoEquipo tipoEquipo) { this.tipoEquipo = tipoEquipo; }
}