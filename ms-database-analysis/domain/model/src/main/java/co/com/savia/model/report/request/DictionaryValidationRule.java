package co.com.savia.model.report.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DictionaryValidationRule {
    private String fieldName; // Campo en el registro a validar
    private boolean validateCientifico; // true si se valida nombre científico, false si se valida nombre común

}
