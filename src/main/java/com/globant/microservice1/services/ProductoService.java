package com.globant.microservice1.services;

import com.globant.microservice1.entities.Categoria;
import com.globant.microservice1.entities.Producto;
import com.globant.microservice1.repositories.CategoriaRepository;
import com.globant.microservice1.repositories.ProductoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }
    @Transactional
    public int procesarArchivo(MultipartFile file, List<String> noProcesados) {
        int procesados = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linea;
            // Saltar la primera línea (encabezados)
            reader.readLine();
            Map<String, Producto> productosMap = new HashMap<>();
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";");
                try {
                    String nombreProducto = partes[0];
                    String marcaProducto = partes[1];
                    Double precioProducto = Double.parseDouble(partes[2]);
                    LocalDate fechaProducto = LocalDate.parse(partes[3].replace("\"", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String nombreCategoria = partes[4];

                    String productoKey = nombreProducto + marcaProducto + fechaProducto.toString();

                    if (productosMap.containsKey(productoKey)) {
                        throw new DataIntegrityViolationException("El producto ya existe");
                    }

                    Categoria categoria = categoriaRepository.findByNombre(nombreCategoria)
                            .orElseGet(() -> {
                                Categoria nuevaCategoria = new Categoria();
                                nuevaCategoria.setNombre(nombreCategoria);
                                return categoriaRepository.save(nuevaCategoria);
                            });

                    Producto producto = new Producto();
                    producto.setNombre(nombreProducto);
                    producto.setMarca(marcaProducto);
                    producto.setPrecio(precioProducto);
                    producto.setFecha(fechaProducto);
                    producto.setCategoria(categoria);

                    productosMap.put(productoKey, producto);
                    procesados++;
                } catch (DataIntegrityViolationException e) {
                    noProcesados.add(linea);
                } catch (Exception e) {
                    noProcesados.add(linea);
                }
            }
            // Guardar products en base de datos
            productoRepository.saveAll(productosMap.values());
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo", e);
        }
        return procesados;
    }
}
