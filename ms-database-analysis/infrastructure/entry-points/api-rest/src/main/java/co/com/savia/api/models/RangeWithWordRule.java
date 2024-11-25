package co.com.savia.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RangeWithWordRule {
    private double min;       // Valor mínimo del rango
    private double max;       // Valor máximo del rango
    private String expectedType; // Tipo esperado en la columna correspondiente
    private String rangeColumn;  // Nombre de la columna con el valor numérico
    private String typeColumn;   // Nombre de la columna que debe contener el tipo esperado

}
