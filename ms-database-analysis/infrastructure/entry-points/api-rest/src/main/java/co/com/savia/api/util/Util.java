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

    // 1 No Nulidad
    public static void validateFieldsNotNull(Map<String, String> record, List<String> fieldsNotNulls, List<String> errors) {
        List<String> nullFieldErrors = new ArrayList<>();

        for (String field : fieldsNotNulls) {
            if (record.get("_record_id") == null || record.get(field).isEmpty()) {
                nullFieldErrors.add("El campo " + field + " no puede ser nulo o vacio");
            }
        }

        if (!nullFieldErrors.isEmpty()) {
            errors.add(String.join("; ", nullFieldErrors));
        }
    }

    // 1,1 Nulidad
    public static void validateFieldsNull(Map<String, String> record, List<String> fieldNulls, List<String> errors) {
        List<String> nullFieldErrors = new ArrayList<>();

        for (String field : fieldNulls) {
            if (record.get(field) != null || !record.get(field).isEmpty()) {
                nullFieldErrors.add("El campo " + field + " debe ser nulo o vacio");
            }
        }

        if (!nullFieldErrors.isEmpty()) {
            errors.add(String.join("; ", nullFieldErrors));
        }
    }

    // 2 Tipo de variable
    public static void validateVariableType(Map<String, String> record, List<VariableTypeFunction> variableTypeRules, List<String> errors) {
        List<String> typeValidationErrors = new ArrayList<>(); // Lista temporal para almacenar los errores de validación de tipos

        for (VariableTypeFunction rule : variableTypeRules) {
            String value = record.get(rule.getName());

            if (value != null && !value.isEmpty()) {
                try {
                    switch (rule.getType()) {
                        case "numeric":
                            if (!value.matches("-?\\d+(\\.\\d+)?")) {
                                typeValidationErrors.add("El campo " + rule.getName() + " debe ser numérico");
                            }
                            break;
                        case "string":
                            if (!value.matches("[\\p{L}\\p{N}\\p{P}\\p{S}\\s]+")) {
                                typeValidationErrors.add("El campo " + rule.getName() + " debe ser un texto válido");
                            }
                            break;
                        case "uuid":
                            if (!value.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
                                typeValidationErrors.add("El campo " + rule.getName() + " debe ser un UUID válido");
                            }
                            break;
                        case "binary":
                            if (!value.matches("[01]")) { // Validar solo "0" o "1"
                                typeValidationErrors.add("El campo " + rule.getName() + " debe ser binario (0 o 1)");
                            }
                            break;
                        case "true_false":
                            if (!value.equalsIgnoreCase("verdadero") && !value.equalsIgnoreCase("falso")) { // Validar "verdadero" o "falso"
                                typeValidationErrors.add("El campo " + rule.getName() + " debe ser 'verdadero' o 'falso'");
                            }
                            break;
                        case "yes_no":
                            if (!value.equalsIgnoreCase("si") && !value.equalsIgnoreCase("no")) { // Validar "si" o "no"
                                typeValidationErrors.add("El campo " + rule.getName() + " debe ser 'si' o 'no'");
                            }
                            break;
                        default:
                            typeValidationErrors.add("El campo " + rule.getName() + " tiene un tipo desconocido: " + rule.getType());
                            break;
                    }
                } catch (NumberFormatException e) {
                    log.error("Error en validateVariableType: {}, en ID: {}, record: {}", e.getMessage(), record.get("id"), record);
                    typeValidationErrors.add("El campo " + rule.getName() + " debe ser numérico");
                }
            }
        }

        // Si hay errores de validación de tipo, concatenarlos y añadirlos a la lista de errores
        if (!typeValidationErrors.isEmpty()) {
            errors.add(String.join("; ", typeValidationErrors));
        }
    }

    // 3 Longitud de cadena
    public static void validateSize(Map<String, String> record, List<SizeFunction> sizeRules, List<String> errors) {
        List<String> sizeErrors = new ArrayList<>();

        for (SizeFunction rule : sizeRules) {
            String value = record.get(rule.getName());
            if (value != null && value.length() > rule.getSize()) {
                sizeErrors.add("El campo " + rule.getName() + " excede la longitud maxima de " + rule.getSize() + " caracteres");
            }
        }

        if (!sizeErrors.isEmpty()) {
            errors.add(String.join("; ", sizeErrors));
        }
    }

    // 4 Duplicación
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
            errors.add(String.join("; ", duplicationErrors));
        }
    }

    // 5 Emparejamiento con Biblioteca


    // 6,1 Transformación númerica (área de polígono)


    // 6,2 Transformación númerica (Tipologia de altura)
    public static void validateRangeWithWord(Map<String, String> record, List<RangeWithWordModel> rangeRules, List<String> errors) {
        List<String> rangeTypeValidationErrors = new ArrayList<>();

        for (RangeWithWordModel rule : rangeRules) {
            String rangeValueStr = record.get(rule.getRangeColumn());
            String typeValue = record.get(rule.getTypeColumn());

            if (rangeValueStr != null && !rangeValueStr.isEmpty()) {
                try {
                    double rangeValue = Double.parseDouble(rangeValueStr);

                    // Validar si el valor está dentro del rango especificado en la regla
                    if (rangeValue >= rule.getMin() && rangeValue <= rule.getMax()) {
                        // Verificar que el valor de la columna de tipo coincida con el tipo esperado
                        if (typeValue == null || !typeValue.equals(rule.getExpectedType())) {
                            rangeTypeValidationErrors.add("El campo " + rule.getTypeColumn() +
                                    " debe ser '" + rule.getExpectedType() + "' cuando " +
                                    rule.getRangeColumn() + " está en el rango [" + rule.getMin() + ", " + rule.getMax() + "]");
                        }
                    }
                } catch (NumberFormatException e) {
                    log.error("Error en validateRangeWithType: {}, en ID: {}, record: {}", e.getMessage(), record.get("id"), record);
                    rangeTypeValidationErrors.add("El campo " + rule.getRangeColumn() + " debe ser numérico para aplicar la validación de rango");
                }
            }
        }

        // Si hay errores de validación de rango y tipo, concatenarlos y añadirlos a la lista de errores
        if (!rangeTypeValidationErrors.isEmpty()) {
            errors.add(String.join("; ", rangeTypeValidationErrors));
        }
    }

    // 7 Comparación con otra columana
    public static void validateComparisonsBetweenColumns(Map<String, String> record, List<ComparisonFunction> comparisonRules, List<String> errors) {
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
            errors.add(String.join("; ", comparisonErrors));
        }
    }

    // 8 Comparación númerica
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
            errors.add(String.join("; ", minMaxErrors));
        }
    }

    // 9 Conteo valores no nulos

    // 10 Orden cuantitativo
    public static void validateColumnOrder(Map<String, String> record, List<OrderColumn> orderColumn, List<String> errors) {
        List<String> orderValidationErrors = new ArrayList<>();

        for (OrderColumn rule : orderColumn) {
            List<String> columns = rule.getColumns();
            String orderType = rule.getOrderType();

            List<Double> values = new ArrayList<>();
            boolean allValuesParsed = true;

            // Extraer y convertir los valores de las columnas seleccionadas
            for (String column : columns) {
                String valueStr = record.get(column);

                try {
                    if (valueStr != null && !valueStr.isEmpty()) {
                        values.add(Double.parseDouble(valueStr));
                    } else {
                        values.add(null); // Añadir null si el valor está vacío
                    }
                } catch (NumberFormatException e) {
                    log.error("Error en validateColumnOrder: Valor no numérico en columna '{}', ID: {}, record: {}", column, record.get("id"), record);
                    orderValidationErrors.add("El campo " + column + " debe ser numérico para aplicar la validación de orden.");
                    allValuesParsed = false;
                }
            }

            // Continuar solo si todos los valores pudieron ser convertidos a numéricos
            if (allValuesParsed) {
                boolean isOrdered = true;

                // Verificar el orden dependiendo de 'ascendente' o 'descendente'
                for (int i = 1; i < values.size(); i++) {
                    Double prevValue = values.get(i - 1);
                    Double currValue = values.get(i);

                    if (prevValue != null && currValue != null) {
                        if ("ascendente".equalsIgnoreCase(orderType) && prevValue > currValue) {
                            isOrdered = false;
                            break;
                        } else if ("descendente".equalsIgnoreCase(orderType) && prevValue < currValue) {
                            isOrdered = false;
                            break;
                        }
                    }
                }

                // Si el orden es incorrecto, se añade un mensaje de error
                if (!isOrdered) {
                    orderValidationErrors.add("Las columnas " + columns + " deben estar en orden " + orderType + ".");
                }
            }
        }

        // Si hay errores de validación de orden, concatenarlos y añadirlos a la lista de errores
        if (!orderValidationErrors.isEmpty()) {
            errors.add(String.join("; ", orderValidationErrors));
        }
    }

    // 11 Comparación de fechas
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
            errors.add(String.join("; ", dateComparisonErrors));
        }
    }

    // 11,1 Rango de fechas
    public static void validateDatesInRange(Map<String, String> record, List<DateRangeRule> dateRangeRules, List<String> errors) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Ajusta el formato si es necesario
        List<String> dateValidationErrors = new ArrayList<>();

        for (DateRangeRule rule : dateRangeRules) {
            String startDateStr = record.get(rule.getStartDateColumn());
            String endDateStr = record.get(rule.getEndDateColumn());
            String referenceDateStr = record.get(rule.getReferenceDateColumn());

            // Verificar que las fechas no estén vacías
            if (startDateStr == null || endDateStr == null || referenceDateStr == null) {
                dateValidationErrors.add("Fechas en columnas " + rule.getStartDateColumn() + ", " + rule.getEndDateColumn() + " o " + rule.getReferenceDateColumn() + " están vacías.");
                continue;
            }

            try {
                LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
                LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);
                String[] referenceDates = referenceDateStr.split(" - "); // Se asume que las fechas de rango están separadas por un guion

                if (referenceDates.length == 2) {
                    LocalDate referenceStartDate = LocalDate.parse(referenceDates[0].trim(), dateFormatter);
                    LocalDate referenceEndDate = LocalDate.parse(referenceDates[1].trim(), dateFormatter);

                    // Validar que la fecha de inicio y la fecha final estén contenidas en el rango
                    if (startDate.isBefore(referenceStartDate) || endDate.isAfter(referenceEndDate)) {
                        dateValidationErrors.add("El rango de fechas (" + startDateStr + " - " + endDateStr + ") debe estar contenido dentro de " + referenceDateStr);
                    }
                } else {
                    dateValidationErrors.add("Formato de rango de fechas en la columna " + rule.getReferenceDateColumn() + " es incorrecto.");
                }

            } catch (DateTimeParseException e) {
                log.error("Error en validateDateRangeInReference: Formato de fecha inválido en columnas '{}', '{}', o '{}', ID: {}, record: {}", rule.getStartDateColumn(), rule.getEndDateColumn(), rule.getReferenceDateColumn(), record.get("id"), record);
                dateValidationErrors.add("Formato de fecha inválido en una de las columnas: " + rule.getStartDateColumn() + ", " + rule.getEndDateColumn() + ", " + rule.getReferenceDateColumn());
            }
        }

        // Si hay errores de validación de fechas, concatenarlos y añadirlos a la lista de errores
        if (!dateValidationErrors.isEmpty()) {
            errors.add(String.join("; ", dateValidationErrors));
        }
    }


    // 12 Asociación de columanas para no nulidad


    // 13 Condicional de no nulidad
    public static void validateConditionalNonNull(Map<String, String> record, List<ConditionalNonNullRules> rules,
                                                  List<String> errors) {

        // Lista para almacenar columnas que tienen valores nulos
        List<String> missingColumns = new ArrayList<>();
        // Recorre cada regla para aplicarla al registro
        for (ConditionalNonNullRules rule : rules) {

            // Obtiene el valor de la columna seleccionada en el registro actual
            String selectedColumnValue = record.get(rule.getSelectedColumn());

            // Verifica si el valor cumple la condición especificada en la regla
            if (selectedColumnValue != null && selectedColumnValue.equals(rule.getSelectedValue())) {

                for (String column : rule.getComparedColumns()) {
                    String comparedValue = record.get(column);
                    if (comparedValue == null || comparedValue.isEmpty()) {
                        missingColumns.add(column);
                    }
                }

            }
            if (!missingColumns.isEmpty()) {
                errors.add("Cuando " + rule.getSelectedColumn() + " es '" + rule.getSelectedValue() + "', "
                        + "las siguientes columnas no deben ser nulas: " + String.join(", ", missingColumns));
            }
        }
    }

}
