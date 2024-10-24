package co.com.savia.api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BusinessRuleLoader {

    private final JsonNode rules;

    public BusinessRuleLoader(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        rules = mapper.readTree(new File(filePath));
    }

    public Map<String, JsonNode> getValidations() {
        Map<String, JsonNode> validationsMap = new HashMap<>();

        rules.get("validations").fields().forEachRemaining(entry -> validationsMap.put(entry.getKey(), entry.getValue()));
        return validationsMap;
    }

    public JsonNode getValidationForField(String field) {
        return rules.get("validations").get(field);
    }
}
