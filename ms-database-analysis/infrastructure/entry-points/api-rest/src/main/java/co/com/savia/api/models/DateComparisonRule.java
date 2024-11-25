package co.com.savia.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DateComparisonRule {
    private String comparetorOne;
    private String comparetorTwo;
    private String operator;  // "greater_than", "less_than", etc.
}

