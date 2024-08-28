package org.example.tiendaspring.controladores;


import jakarta.validation.Valid;
import org.example.tiendaspring.modelo.entity.Producto;
import org.example.tiendaspring.modelo.entity.ProductoDto;
import org.example.tiendaspring.servicios.ProductosRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductosControlador {

    @Autowired
    private ProductosRepositorio repositorio;
    //metodo para leer los datos

    @GetMapping({"", "/"})
    public String mostrarListaProductos(Model modelo) {
        List<Producto> productos = repositorio.findAll();
        modelo.addAttribute("productos", productos);
        return "productos/index";
    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        ProductoDto productoDto = new ProductoDto();
        modelo.addAttribute("productoDto", productoDto);
        return "productos/CrearProducto";
    }

    @PostMapping("/crear")
    public String crearProducto(
            @Valid @ModelAttribute ProductoDto productoDto,
            BindingResult result
    ) {
        if (productoDto.getImagen().isEmpty()) {
            result.addError(new FieldError("productoDto", "imagen", "El archivo de imagen es requerido."));

        }

        if (result.hasErrors()) {
            return "productos/CrearProducto";
        }

        //en este punto no tenemos errores, entonces almacenaremos la imagen en la bd
        MultipartFile imagen = productoDto.getImagen();
        Date fechaCreacion = new Date();
        String almacenarNombreArchivo = fechaCreacion.getTime() + "__" + imagen.getOriginalFilename();

        try {
            String uploadDir = "public/imagenes/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = imagen.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + almacenarNombreArchivo),
                        StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (Exception ex) {
            System.out.println("Excepcion " + ex.getMessage());
        }
        Producto producto = new Producto();
        producto.setNombre(productoDto.getNombre());
        producto.setMarca(productoDto.getMarca());
        producto.setCategoria(productoDto.getCategoria());
        producto.setPrecio(productoDto.getPrecio());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setFechaCreacion(fechaCreacion);
        producto.setNombreArchivoImagen(almacenarNombreArchivo);

        repositorio.save(producto);

        return "redirect:/productos";


    }

    @GetMapping("/editar")
    public String verEditarPagina(Model modelo, @RequestParam int id) {

        try {
            Producto producto = repositorio.findById(id).get();
            modelo.addAttribute("producto", producto);

            ProductoDto productoDto = new ProductoDto();
            productoDto.setNombre(producto.getNombre());
            productoDto.setMarca(producto.getMarca());
            productoDto.setCategoria(producto.getCategoria());
            productoDto.setPrecio(producto.getPrecio());
            productoDto.setDescripcion(producto.getDescripcion());

            modelo.addAttribute("productoDto", productoDto);

        } catch (Exception ex) {
            System.out.println("Excepcion " + ex.getMessage());
            return "redirect:/productos";
        }
        return "productos/editarProducto";
    }

    @PostMapping("/editar")
    public String actualizarProducto(Model model,
                                     @RequestParam int id,
                                     @Valid @ModelAttribute ProductoDto productoDto,
                                     BindingResult result) {

        try {
            Producto producto = repositorio.findById(id).get();
            model.addAttribute("productoDto", productoDto);

            if (result.hasErrors()) {
                return "productos/editarProducto";
            }

            if (!productoDto.getImagen().isEmpty()) {

                //eliminar imagen anterior

                String uploadDir = "public/imagenes/";
                Path oldImagenPath = Paths.get(uploadDir + productoDto.getImagen().getOriginalFilename());

                try {
                    Files.delete(oldImagenPath);
                } catch (Exception ex) {
                    System.out.println("Excepcion " + ex.getMessage());
                }

                //guardar imagen nueva

                MultipartFile imagen = productoDto.getImagen();
                Date fechaCreacion = new Date();
                String nombreArchivoAlmacenado = fechaCreacion.getTime() + "__" + imagen.getOriginalFilename();

                try (InputStream inputStream = imagen.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + nombreArchivoAlmacenado), StandardCopyOption.REPLACE_EXISTING);
                }
                producto.setNombreArchivoImagen(nombreArchivoAlmacenado);
            }

            //esto actualizara los demas valores
            producto.setNombre(productoDto.getNombre());
            producto.setMarca(productoDto.getMarca());
            producto.setCategoria(productoDto.getCategoria());
            producto.setPrecio(productoDto.getPrecio());
            producto.setDescripcion(productoDto.getDescripcion());

            repositorio.save(producto);

        } catch (Exception ex) {
            System.out.println("Excepcion " + ex.getMessage());
        }
        return "redirect:/productos";
    }


    @GetMapping("/eliminar")
    public String eliminarProducto(@RequestParam int id) {

        try {
            Producto producto = repositorio.findById(id).get();
            //eliminar imagen de la base de datos
            Path imagePath = Paths.get("public/imagenes/" + producto.getNombreArchivoImagen());

            try {
                Files.delete(imagePath);
            }catch (Exception ex) {
                System.out.println("Excepcion " + ex.getMessage());
            }

            //eliminar el producto

            repositorio.delete(producto);

        } catch (Exception ex) {
            System.out.println("Excepcion " + ex.getMessage());
        }


        return "redirect:/productos";
    }

}
