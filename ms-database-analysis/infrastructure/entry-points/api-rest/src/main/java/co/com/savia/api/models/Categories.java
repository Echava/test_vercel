package co.com.savia.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Categories {
    private List<String> nulidadFunction;
    private List<VariableTypeFunction> variableTypeFunction;
    private List<SizeFunction> sizeFunction;
    private List<MinMaxFunction> minimiumAndMaximunFunction;
    private List<String> duplicationFunction;
    private List<ComparisonFunction> comparisonsWithOtherColumnFunction;
    private List<DateComparisonFunction> comparisonsWithDateFunction;
}
