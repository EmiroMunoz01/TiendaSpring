package org.example.tiendaspring.modelo.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
public class ProductoDto {

    @NotEmpty(message = "El nombre es requerido")
    private String nombre;

    @NotEmpty(message = "La marca es requerida")
    private String marca;

    @NotEmpty(message = "La categoria es requerida")
    private String categoria;

    @Min(0)
    private double precio;

    @Size(min = 10, message = "La descripcion debe tener minimo 10 caracteres")
    @Size(max = 2000, message = "La descripcion debe tener maximo 10 caracteres")
    private String descripcion;

    private MultipartFile imagen;
}
