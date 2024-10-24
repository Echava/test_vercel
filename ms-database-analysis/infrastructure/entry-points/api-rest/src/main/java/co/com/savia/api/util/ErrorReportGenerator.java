package co.com.savia.api.util;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class ErrorReportGenerator {

    // Un Map para almacenar los errores por cada registro
    private List<Map<String, String>> errorRecords = new ArrayList<>();

    // Método para agregar un error para una categoría específica
    public void addError(String recordId, String id, String profDiC3, Map<String, String> errors) {
        // Crear un mapa con los campos iniciales y errores de las categorías
        Map<String, String> errorRecord = new LinkedHashMap<>();
        errorRecord.put("record_id", recordId);
        errorRecord.put("id", id);
        errorRecord.put("prof_di_c3", profDiC3);

        // Rellenar las categorías con sus errores
        errorRecord.put("nulidad", errors.getOrDefault("nulidad", ""));
        errorRecord.put("tipo", errors.getOrDefault("tipo", ""));
        errorRecord.put("tamaño", errors.getOrDefault("tamaño", ""));
        errorRecord.put("duplicación", errors.getOrDefault("duplicación", ""));
        errorRecord.put("comparación", errors.getOrDefault("comparación", ""));
        errorRecord.put("fecha", errors.getOrDefault("fecha", ""));
        errorRecord.put("min_max", errors.getOrDefault("min_max", ""));

        // Agregar el registro de errores a la lista
        errorRecords.add(errorRecord);
    }

    // Método para generar el archivo CSV
    public void generateCsv(String filePath) throws IOException {
        FileWriter csvWriter = new FileWriter(filePath);

        // Escribir los headers
        csvWriter.append("record_id,id,prof_di_c3,nulidad,tipo,tamaño,duplicación,comparación,fecha,min_max\n");

        // Escribir cada registro con sus errores
        for (Map<String, String> errorRecord : errorRecords) {
            csvWriter.append(String.join(",", errorRecord.values()));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }
}

