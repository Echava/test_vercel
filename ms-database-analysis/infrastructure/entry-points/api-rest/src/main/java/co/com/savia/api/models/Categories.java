package co.com.savia.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Categories {
    private List<String> nullRules;
    private List<String> notNullRules;
    private List<VariableTypeFunction> variableTypeRules;
    private List<SizeFunction> sizeRules;
    private List<MinMaxFunction> minimumAndMaximumRules;
    private List<String> duplicationRules;
    private List<ComparisonFunction> comparisonsWithOtherColumnRules;
    private List<DateComparisonFunction> comparisonsWithDateRules;
    private List<RangeWithWordModel> rangeWithWordRules;
    private List<OrderColumn> orderColumnsRules;
    private List<DateRangeRule> dateRangeRules;
    private List<ConditionalNonNullRules> conditionalNonNullRules;
}
