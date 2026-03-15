package bank.facade;

import bank.data_operations.exporter.JsonDataExporter;
import bank.data_operations.exporter.YamlDataExporter;
import bank.data_operations.exporter.CsvDataExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExportFacade {
    private final JsonDataExporter jsonExporter;
    private final YamlDataExporter yamlExporter;
    private final CsvDataExporter csvExporter;

    public void exportToJson() {
        jsonExporter.exportData();
    }

    public void exportToYaml() {
        yamlExporter.exportData();
    }

    public void exportToCsv() {
        csvExporter.exportData();
    }
}