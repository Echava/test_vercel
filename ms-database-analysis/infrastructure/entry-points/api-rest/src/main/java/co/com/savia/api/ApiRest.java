package co.com.savia.api;

import co.com.savia.api.models.ValidationRules;
import co.com.savia.api.util.Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Log4j2
public class ApiRest {
//    private final MyUseCase useCase;


    //@PreAuthorize("hasRole('permission')")
    @GetMapping(path = "/path")
    public String commandName() {
//      return useCase.doAction();
        return "Hello World";
    }


    //@PreAuthorize("hasRole('permission')")
    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExcel(@RequestParam("db-file") MultipartFile dbFile,
                                              @RequestParam("br-file") MultipartFile brFile) {

        log.info("Se inicia el proceso");
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = dbFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            // Asume que trabajamos con la primera hoja
            Sheet sheet = workbook.getSheetAt(0);

            // Toma la primera fila (cabeceras)
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                return new ResponseEntity<>("El archivo no contiene cabeceras", HttpStatus.BAD_REQUEST);
            }

            // Itera sobre las celdas de la primera fila (cabeceras) y las imprime en consola
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                String header = headerRow.getCell(i).getStringCellValue();
                log.info("Cabecera: " + header);
            }
            JsonNode jsonNode = objectMapper.readTree(brFile.getInputStream());
            log.info("Contenido del archivo JSON: {}", jsonNode.toString());

            return new ResponseEntity<>("Cabeceras procesadas correctamente", HttpStatus.OK);

        } catch (IOException e) {

            log.error("error en el proceso: {}", e.getMessage());
            return new ResponseEntity<>("Error al procesar el archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //@PreAuthorize("hasRole('permission')")
    @PostMapping("/upload-excel-dos")
    public ResponseEntity<String> uploadExcel2(@RequestParam("db-file") MultipartFile dbFile,
                                               @RequestParam("br-file") MultipartFile brFile) {

        if (dbFile.isEmpty() || brFile.isEmpty()) {
            log.warn("Uno o ambos archivos están vacíos.");
            return ResponseEntity.badRequest().body("Ambos archivos son requeridos y no deben estar vacíos.");
        }
        log.info("Se inicia el analisis de la base de datos: {} con el archivo de config: {}",
                dbFile.getOriginalFilename(), brFile.getOriginalFilename());
        try {

            List<Map<String, String>> records = processExcelFile(dbFile);
            log.info("Archivo de Excel leido");

            ValidationRules rules = processJsonFile(brFile);
            log.info("Archivo de reglas de negocio leido: {}", rules);

            String fileName = dbFile.getOriginalFilename().replaceAll(" ", "")
                    .replaceAll("\\.(xlsx|csv)$", "");

            log.info("Se pasa a validar cada registro...");
            String errorFilePath = validateRecords(records, rules, fileName);
            log.info("Registros validados...");

            if (errorFilePath != null && !errorFilePath.isEmpty()) {
                File errorFile = new File(errorFilePath);

                // Verificar si el archivo realmente existe en la ruta
                if (errorFile.exists()) {
                    log.info("Se generó un archivo de errores en la ruta: {}", errorFilePath);
                    return ResponseEntity.ok().body("Se encontraron errores en los registros. El archivo de errores se encuentra en: " + errorFilePath);
                } else {
                    log.error("El archivo de errores no se pudo encontrar en la ruta: {}", errorFilePath);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Se encontraron errores, pero no se pudo generar el archivo de errores.");
                }
            }

            return ResponseEntity.badRequest().body("Se encontraron errores, revisa el archivo de errores.");
        } catch (Exception e) {
            log.error("error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar los archivos: " + e.getMessage());
        }
    }

    // Procesar el archivo Excel
    private List<Map<String, String>> processExcelFile(MultipartFile file) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Obtener la fila de encabezados
        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        headerRow.forEach(cell -> headers.add(cell.getStringCellValue()));

        // Iterar sobre cada fila de datos
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Map<String, String> record = new HashMap<>();

            for (int j = 0; j < headers.size(); j++) {
                try {
                    Cell cell = row.getCell(j);
                    String header = headers.get(j);

                    // Verificar si la celda es nula o está vacía
                    if (cell == null || cell.getCellType() == CellType.BLANK) {
                        record.put(header, "");
                    } else {
                        switch (cell.getCellType()) {
                            case STRING:
                                record.put(header, cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    record.put(header, cell.getDateCellValue().toString());
                                } else {
                                    double numericValue = cell.getNumericCellValue();
                                    if (numericValue == Math.floor(numericValue)) {
                                        record.put(header, String.valueOf((int) numericValue));
                                    } else {
                                        record.put(header, String.valueOf(numericValue));
                                    }
                                }
                                break;
                            case BOOLEAN:
                                record.put(header, String.valueOf(cell.getBooleanCellValue()));
                                break;
                            default:
                                record.put(header, cell.toString());
                        }
                    }
                } catch (Exception e) {
                    // Registrar el error con información detallada
                    log.error("Error procesando celda en la columna '{}' y fila '{}': {}", headers.get(j), i, e.getMessage());
                    throw new RuntimeException("Error procesando el archivo Excel: " + e.getMessage());
                }
            }

            // Agregar el registro procesado a la lista
            records.add(record);
        }

        return records;
    }


    // Procesar el archivo JSON
    private ValidationRules processJsonFile(MultipartFile brFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(brFile.getInputStream(), ValidationRules.class);
    }

    // Validar los registros según las reglas de negocio
    /*
    public List<String> validateRecords(List<Map<String, String>> records, ValidationRules validationRules) {
        List<String> errors = new ArrayList<>();
        Categories rules = validationRules.getRules().getCategories();

        // Recorrer cada registro
        for (Map<String, String> record : records) {

            // Validar nulidad
            Util.validateFieldsNull(record, rules.getNulidadFunction(), errors);

            // Validar tipo de variable
            Util.validateVariableType(record, rules.getVariableTypeFunction(), errors);

            // Validar longitud de cadena
            Util.validateSize(record, rules.getSizeFunction(), errors);

            // Validar mínimos y máximos
            Util.validateMinMax(record, rules.getMinimiumAndMaximunFunction(), errors);

            // Validar duplicación (esto podría ser otra función si necesitas analizar duplicados entre varios registros)
            Util.validateDuplications(record, records, rules.getDuplicationFunction(), errors);

            // Validar comparaciones entre columnas
            Util.validateComparisons(record, rules.getComparisonsWithOtherColumnFunction(), errors);

            // Validar comparaciones de fechas
            Util.validateDateComparisons(record, rules.getComparisonsWithDateFunction(), errors);
        }

        return errors;
    }

     */
    public String validateRecords(List<Map<String, String>> records, ValidationRules rules, String fileName) {

        List<String[]> errorRows = new ArrayList<>();
        List<String> reportHeaders = rules.getReport().getHeaders();

        List<String> completeHeaders = new ArrayList<>(reportHeaders);

        // Añadir las categorías de validación
        completeHeaders.addAll(Arrays.asList("nulidad-vacios", "tipo-variable", "tamano", "duplicacion", "comparacion-entre-campos", "comparacion-fecha", "min_max"));

        String[] header = completeHeaders.toArray(new String[0]);

        errorRows.add(header);

        // Iterar sobre cada registro y realizar las validaciones
        for (Map<String, String> record : records) {

            String[] errorRow = new String[header.length];
            for (int i = 0; i < reportHeaders.size(); i++) {
                errorRow[i] = record.get(reportHeaders.get(i));
            }

            // Listas para recolectar errores por categoría
            List<String> errorsNulidad = new ArrayList<>();
            List<String> errorsTipo = new ArrayList<>();
            List<String> errorsSize = new ArrayList<>();
            List<String> errorsDuplicacion = new ArrayList<>();
            List<String> errorsComparacion = new ArrayList<>();
            List<String> errorsComparacionFecha = new ArrayList<>();
            List<String> errorsMinMax = new ArrayList<>();

            // Validar campos no nulos
            Util.validateFieldsNull(record, rules.getRules().getCategories().getNulidadFunction(), errorsNulidad);

            // Validar tipo de variables
            Util.validateVariableType(record, rules.getRules().getCategories().getVariableTypeFunction(), errorsTipo);

            // Validar tamaños
            Util.validateSize(record, rules.getRules().getCategories().getSizeFunction(), errorsSize);

            // Validar mínimos y máximos
            Util.validateMinMax(record, rules.getRules().getCategories().getMinimiumAndMaximunFunction(), errorsMinMax);

            // Validar duplicaciones
            Util.validateDuplications(record, records, rules.getRules().getCategories().getDuplicationFunction(), errorsDuplicacion);

            // Validar comparaciones entre campos
            Util.validateComparisons(record, rules.getRules().getCategories().getComparisonsWithOtherColumnFunction(), errorsComparacion);

            // Validar comparaciones de fechas
            Util.validateDateComparisons(record, rules.getRules().getCategories().getComparisonsWithDateFunction(), errorsComparacionFecha);

            // Agregar los errores a las columnas correspondientes
            errorRow[3] = String.join("; ", errorsNulidad); // Nulidad
            errorRow[4] = String.join("; ", errorsTipo);    // Tipo de variable
            errorRow[5] = String.join("; ", errorsSize);  // Tamaño
            errorRow[6] = String.join("; ", errorsDuplicacion); // Duplicación
            errorRow[7] = String.join("; ", errorsComparacion); // Comparación entre campos
            errorRow[8] = String.join("; ", errorsComparacionFecha); // Comparación entre fechas
            errorRow[9] = String.join("; ", errorsMinMax); // MinMax

            // Agregar los errores por cada categoría, separados por comas en una sola fila
            // Añadir la fila con errores a la lista de errores
            errorRows.add(errorRow);
        }
        // Generar el archivo de errores

        return generateErrorFileExcel(errorRows, fileName);
    }



    // Generar archivo con los errores encontrados
    public String generateErrorFileCsv(List<String[]> errorRows) {
        String filePath = "C:\\Projects\\GitHub\\Savia\\erroresNuevos.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            for (String[] row : errorRows) {
                writer.writeNext(row);
            }
        } catch (Exception e) {
            log.error("Error creando archivo CSV: {}", e.getMessage());
            filePath = null;
        }
        return filePath;
    }

    public String generateErrorFileExcel(List<String[]> errorRows, String fileName) {

        String filePath = "C:\\Projects\\GitHub\\Savia\\Errors-".concat(fileName).concat(".xlsx");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Errores");

            // Recorrer las filas de errores y escribirlas en el archivo Excel
            int rowNum = 0;
            for (String[] row : errorRows) {
                if (hasErrors(row)) {
                    Row excelRow = sheet.createRow(rowNum++); // Crear una fila en la hoja
                    for (int i = 0; i < row.length; i++) {
                        Cell cell = excelRow.createCell(i); // Crear una celda en la fila
                        cell.setCellValue(row[i]); // Establecer el valor de la celda
                    }
                }
            }

            // Ajustar el tamaño de las columnas al contenido
            for (int i = 0; i < errorRows.get(0).length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir el archivo en la ruta especificada
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            log.error("Error creando archivo Excel: {}", e.getMessage());
            filePath = null;
        }
        return filePath;
    }
    private boolean hasErrors(String[] row) {
        int accumulate = 0;
        for (int i = 3; i < row.length; i++) {
            String value = row[i];
            if (value != null && !value.trim().isEmpty()) {
                accumulate++;
            }
        }
        return accumulate != 0;
    }



}
