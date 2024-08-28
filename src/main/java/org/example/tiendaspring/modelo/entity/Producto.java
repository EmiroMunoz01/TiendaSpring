package org.example.tiendaspring.modelo.entity;


import java.util.Date;

import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Entity
@Getter
@Setter
@Table(name = "productos")

public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String nombre;
    private String marca;
    private String categoria;
    private double precio;


    @Column(columnDefinition="TEXT")
    private String descripcion;
    private Date fechaCreacion;
    private String nombreArchivoImagen;
}

