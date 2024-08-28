package org.example.tiendaspring.servicios;

import org.example.tiendaspring.modelo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductosRepositorio extends JpaRepository<Producto, Integer>{

}
