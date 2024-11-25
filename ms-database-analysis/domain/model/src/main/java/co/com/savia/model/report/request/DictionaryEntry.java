package co.com.savia.model.report.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DictionaryEntry {
    private String id;
    private String nombreCientifico;
    private String nombreComun;
    private String family;
    private String origen;
    private String reportsInColombia;
    private String habit;
    private String finish;

    public String getFormattedCientifico() {
        return id + ". " + nombreCientifico;
    }

    public String getFormattedComun() {
        return id + ". " + nombreComun;
    }
}
