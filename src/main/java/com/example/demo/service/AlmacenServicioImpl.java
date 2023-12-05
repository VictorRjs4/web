package com.example.demo.service;
import  org.springframework.util.FileSystemUtils;
import com.example.demo.excepciones.AlmacenExcepcion;
import com.example.demo.excepciones.FileNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class AlmacenServicioImpl implements AlmacenServicio {
    @Value("${storage.location}")
    private String storageLocation;

    // Este método se ejecuta después de que se ha creado la instancia de la clase
    @PostConstruct
    @Override
    public void iniciarAlmacenDeArchivos() {
        try {
            Files.createDirectories(Path.of(storageLocation));
        } catch (IOException e) {
            throw new AlmacenExcepcion("Error al inicializar la ubicación en el almacen de archivos", e);
        }
    }

    @Override
    public String almacenarArchivo(MultipartFile archivo) {
        String nombreArchivo = archivo.getOriginalFilename();

        if (archivo.isEmpty()) {
            throw new AlmacenExcepcion("No se puede almacenar un archivo vacío");
        }

        try (InputStream inputStream = archivo.getInputStream()) {
            Files.copy(inputStream, Path.of(storageLocation).resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AlmacenExcepcion("Error al almacenar el archivo " + nombreArchivo, e);
        }

        return nombreArchivo;
    }

    @Override
    public Path cargarArchivo(String nombreArchivo) {
        return Path.of(storageLocation).resolve(nombreArchivo);
    }

    @Override
    public Resource cargarComoRecurso(String nombreArchivo) {
        try {
            Path archivo = cargarArchivo(nombreArchivo);
            Resource recurso = new UrlResource(archivo.toUri());

            if (recurso.exists() || recurso.isReadable()) {
                return recurso;
            } else {
                throw new FileNotFoundException("No se pudo encontrar el archivo " + nombreArchivo);
            }

        } catch (MalformedURLException e) {
            throw new FileNotFoundException("No se pudo encontrar el archivo " + nombreArchivo, e);
        }
    }

    @Override
    public void eliminarArchivo(String nombreArchivo) {
        Path archivo = cargarArchivo(nombreArchivo);
        try {
            FileSystemUtils.deleteRecursively(archivo);
        } catch (IOException e) {
            throw new AlmacenExcepcion("No se pudo eliminar el archivo " + nombreArchivo, e);
        }
    }
}
