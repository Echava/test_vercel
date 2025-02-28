package co.com.savia.model.report.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ComparisonWithOtherColumnRule {
    private String comparetorOne;
    private String comparetorTwo;
    private String operator;  // Puede ser "greater_than", "less_than", etc.
}
