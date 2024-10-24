package co.com.savia.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RecordValidator {

    private final BusinessRuleLoader ruleLoader;
    private final Set<String> uniqueIdSet = new HashSet<>();

    public RecordValidator(BusinessRuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
    }

    public void validateRecord(Map<String, String> record) throws Exception {
        for (Map.Entry<String, String> field : record.entrySet()) {
            JsonNode validation = ruleLoader.getValidationForField(field.getKey());

            if (validation != null) {
                // Perform required validation
                if (validation.has("required") && validation.get("required").asBoolean() && (field.getValue() == null || field.getValue().isEmpty())) {
                    throw new Exception(field.getKey() + " is required.");
                }

                // Perform UUID validation
                if (validation.has("type") && validation.get("type").asText().equals("uuid")) {
                    if (!isValidUUID(field.getValue())) {
                        throw new Exception(field.getKey() + " must be a valid UUID.");
                    }
                }

                // Perform unique validation
                if (validation.has("unique") && validation.get("unique").asBoolean()) {
                    if (!uniqueIdSet.add(field.getValue())) {
                        throw new Exception(field.getKey() + " must be unique.");
                    }
                }

                // Perform min validation
                if (validation.has("min")) {
                    double value = Double.parseDouble(field.getValue());
                    double min = validation.get("min").asDouble();
                    if (value < min) {
                        throw new Exception(field.getKey() + " must be greater than " + min);
                    }
                }

                // Perform length validation
                if (validation.has("length") && field.getValue().length() != validation.get("length").asInt()) {
                    throw new Exception(field.getKey() + " must be " + validation.get("length").asInt() + " characters long.");
                }
            }
        }
    }

    private boolean isValidUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}