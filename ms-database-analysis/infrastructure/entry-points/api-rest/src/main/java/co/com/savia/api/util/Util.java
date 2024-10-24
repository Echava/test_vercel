package co.com.savia.api.util;

import co.com.savia.api.models.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class Util {

    public static void validateFieldsNull(Map<String, String> record, List<String> fieldsNotNulls, List<String> errors) {
        List<String> nullFieldErrors = new ArrayList<>();

        for (String field : fieldsNotNulls) {
            if (record.get(field) == null || record.get(field).isEmpty()) {
                nullFieldErrors.add("El campo " + field + " no puede ser nulo o vacío");
            }
        }

        // Si hay errores de campos nulos o vacíos, concatenarlos y añadirlos a la lista de errores
        if (!nullFieldErrors.isEmpty()) {
            errors.add(String.join(" : ", nullFieldErrors));
        }
    }



    public static void validateVariableType(Map<String, String> record, List<VariableTypeFunction> variableTypeRules, List<String> errors) {
        List<String> typeValidationErrors = new ArrayList<>(); // Lista temporal para almacenar los errores de validación de tipos

        for (VariableTypeFunction rule : variableTypeRules) {
            String value = record.get(rule.getName());

            if (value != null && !value.isEmpty()) {
                try {
                    if (rule.getType().equals("numeric") && !value.matches("-?\\d+(\\.\\d+)?")) {
                        typeValidationErrors.add("El campo " + rule.getName() + " debe ser numérico");
                    } else if (rule.getType().equals("string") && !value.matches("[\\p{L}\\p{N}\\p{P}\\p{S}\\s]+")) {
                        typeValidationErrors.add("El campo " + rule.getName() + " debe ser un texto válido");
                    } else if (rule.getType().equals("uuid") && !value.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
                        typeValidationErrors.add("El campo " + rule.getName() + " debe ser un UUID válido");
                    }
                } catch (NumberFormatException e) {
                    log.error("Error en validateVariableType: {}, en ID: {}, record: {}", e.getMessage(), record.get("id"), record);
                    typeValidationErrors.add("El campo " + rule.getName() + " debe ser numérico");
                }
            }
        }

        // Si hay errores de validación de tipo, concatenarlos y añadirlos a la lista de errores
        if (!typeValidationErrors.isEmpty()) {
            errors.add(String.join(" : ", typeValidationErrors));
        }
    }


    public static void validateSize(Map<String, String> record, List<SizeFunction> sizeRules, List<String> errors) {
        List<String> sizeErrors = new ArrayList<>();

        for (SizeFunction rule : sizeRules) {
            String value = record.get(rule.getName());
            if (value != null && value.length() > rule.getSize()) {
                sizeErrors.add("El campo " + rule.getName() + " excede la longitud maxima de " + rule.getSize() + " caracteres");
            }
        }

        if (!sizeErrors.isEmpty()) {
            errors.add(String.join(" : ", sizeErrors));
        }
    }

    public static void validateMinMax(Map<String, String> record, List<MinMaxFunction> minMaxRules, List<String> errors) {
        List<String> minMaxErrors = new ArrayList<>();

        for (MinMaxFunction rule : minMaxRules) {
            String value = record.get(rule.getName());
            if (value != null && !value.isEmpty()) {
                try {
                    double numericValue = Double.parseDouble(value);
                    if (numericValue < rule.getMinValue() || numericValue > rule.getMaxValue()) {
                        // Acumular el error en la lista temporal
                        minMaxErrors.add("El campo " + rule.getName() + " debe estar entre " + rule.getMinValue() + " y " + rule.getMaxValue());
                    }
                } catch (NumberFormatException e) {
                    log.error("Error en validateMinMax: {}, en ID: {}, record: {}", e.getMessage(), record.get("id"), record.get(rule.getName()));
                    // Acumular el error en la lista temporal
                    minMaxErrors.add("El campo " + rule.getName() + " debe ser numérico");
                }
            }
        }

        // Si se encontraron errores de min/max, los concatenamos y añadimos a la lista de errores
        if (!minMaxErrors.isEmpty()) {
            errors.add(String.join(" : ", minMaxErrors));
        }
    }


    public static void validateDuplications(Map<String, String> currentRecord, List<Map<String, String>> allRecords, List<String> duplicationFields, List<String> errors) {
        List<String> duplicationErrors = new ArrayList<>();

        for (String field : duplicationFields) {
            String currentValue = currentRecord.get(field);
            if (currentValue != null) {
                for (Map<String, String> otherRecord : allRecords) {
                    if (!otherRecord.equals(currentRecord) && currentValue.equals(otherRecord.get(field))) {
                        duplicationErrors.add("El campo " + field + " está duplicado");
                        break; // Se encuentra duplicado, pasamos al siguiente campo
                    }
                }
            }
        }

        // Si hay múltiples errores de duplicación, los concatenamos en un solo mensaje
        if (!duplicationErrors.isEmpty()) {
            errors.add(String.join(" : ", duplicationErrors));
        }
    }


    public static void validateComparisons(Map<String, String> record, List<ComparisonFunction> comparisonRules, List<String> errors) {
        List<String> comparisonErrors = new ArrayList<>(); // Lista temporal para acumular los errores de comparaciones

        for (ComparisonFunction rule : comparisonRules) {
            String valueOne = record.get(rule.getComparetorOne());
            String valueTwo = record.get(rule.getComparetorTwo());

            if ((valueOne != null && !valueOne.isEmpty()) && (valueTwo != null && !valueTwo.isEmpty())) {
                try {
                    double numOne = Double.parseDouble(valueOne);
                    double numTwo = Double.parseDouble(valueTwo);

                    switch (rule.getOperator()) {
                        case "greater_than":
                            if (!(numOne > numTwo)) {
                                comparisonErrors.add("El campo " + rule.getComparetorOne() + " debe ser mayor que " + rule.getComparetorTwo());
                            }
                            break;
                        case "less_than":
                            if (!(numOne < numTwo)) {
                                comparisonErrors.add("El campo " + rule.getComparetorOne() + " debe ser menor que " + rule.getComparetorTwo());
                            }
                            break;
                        case "equal_to":
                            if (!(numOne == numTwo)) {
                                comparisonErrors.add("El campo " + rule.getComparetorOne() + " debe ser igual a " + rule.getComparetorTwo());
                            }
                            break;
                    }
                } catch (NumberFormatException e) {
                    log.error("Error en validateComparisons: {}, en ID: {}, recordOne: {}, recordTwo: {}", e.getMessage(), record.get("id"), record.get(rule.getComparetorOne()), record.get(rule.getComparetorTwo()));
                    comparisonErrors.add("Los campos " + rule.getComparetorOne() + " y " + rule.getComparetorTwo() + " deben ser numéricos");
                }
            } else {
                log.error("Error en validateComparisons: {}, en ID: {}, recordOne: {}, recordTwo: {}", "Vacios o nulos", record.get("id"), record.get(rule.getComparetorOne()), record.get(rule.getComparetorTwo()));
                comparisonErrors.add("Los campos " + rule.getComparetorOne() + " y " + rule.getComparetorTwo() + " no deben estar vacíos o nulos");
            }
        }

        // Si se encontraron errores de comparación, los concatenamos y añadimos a la lista de errores
        if (!comparisonErrors.isEmpty()) {
            errors.add(String.join(" : ", comparisonErrors));
        }
    }



    public static void validateDateComparisons(Map<String, String> record, List<DateComparisonFunction> dateComparisonRules, List<String> errors) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> dateComparisonErrors = new ArrayList<>(); // Lista temporal para los errores de comparación de fechas

        for (DateComparisonFunction rule : dateComparisonRules) {
            String dateOne = record.get(rule.getComparetorOne());
            String dateTwo = record.get(rule.getComparetorTwo());

            if (dateOne != null && dateTwo != null) {
                try {
                    LocalDate localDateOne = LocalDate.parse(dateOne, formatter);
                    LocalDate localDateTwo = LocalDate.parse(dateTwo, formatter);

                    switch (rule.getOperator()) {
                        case "greater_than":
                            if (!localDateOne.isAfter(localDateTwo)) {
                                dateComparisonErrors.add("El campo " + rule.getComparetorOne() + " debe ser posterior a " + rule.getComparetorTwo());
                            }
                            break;
                        case "less_than":
                            if (!localDateOne.isBefore(localDateTwo)) {
                                dateComparisonErrors.add("El campo " + rule.getComparetorOne() + " debe ser anterior a " + rule.getComparetorTwo());
                            }
                            break;
                        case "equal_to":
                            if (!localDateOne.equals(localDateTwo)) {
                                dateComparisonErrors.add("El campo " + rule.getComparetorOne() + " debe ser igual a " + rule.getComparetorTwo());
                            }
                            break;
                    }
                } catch (DateTimeParseException e) {
                    log.error("Error en validateDateComparisons: {}, en ID: {}, recordOne: {}, recordTwo: {}", e.getMessage(), record.get("id"), record.get(rule.getComparetorOne()), record.get(rule.getComparetorTwo()));
                    dateComparisonErrors.add("Los campos " + rule.getComparetorOne() + " y " + rule.getComparetorTwo() + " deben tener formato de fecha válido (yyyy-MM-dd)");
                }
            } else {
                // Si uno de los campos es nulo
                log.error("Error en validateDateComparisons: {}, en ID: {}, recordOne: {}, recordTwo: {}", "Fecha nula", record.get("id"), record.get(rule.getComparetorOne()), record.get(rule.getComparetorTwo()));
                dateComparisonErrors.add("Los campos " + rule.getComparetorOne() + " y " + rule.getComparetorTwo() + " no deben estar vacíos o nulos");
            }
        }

        // Si hay errores en las comparaciones de fechas, los concatenamos y añadimos a la lista principal de errores
        if (!dateComparisonErrors.isEmpty()) {
            errors.add(String.join(" : ", dateComparisonErrors));
        }
    }


}
