package co.com.savia.model.report.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DateRangeRule {
    private String startDateColumn;     // Nombre de la columna para la fecha de inicio
    private String endDateColumn;       // Nombre de la columna para la fecha final
    private String referenceDateColumn; // Columna de referencia que contiene el rango de fechas
}

