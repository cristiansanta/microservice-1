package com.globant.microservice1.contorller;

import com.globant.microservice1.services.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping("/cargar")
    public ResponseEntity<Map<String, Object>> cargarArchivo(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        int procesados = 0;
        List<String> noProcesados = new ArrayList<>();

        procesados = productoService.procesarArchivo(file, noProcesados);
        response.put("registrosProcesados", procesados);
        response.put("registrosNoProcesados", noProcesados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}