package com.example.demo.controller;

import com.example.demo.service.AlmacenServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assets")
public class AssetsController {
    @Autowired
    private AlmacenServicioImpl servicio;
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> obtenerRecurso(@PathVariable("filename") String filename) {
        try {
            // Cargar el recurso desde el servicio
            Resource recurso = servicio.cargarComoRecurso(filename);

            // Configurar los encabezados de respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);

            // Devolver la respuesta con el recurso y encabezados
            return new ResponseEntity<>(recurso, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Manejar cualquier excepción y devolver una respuesta de error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

