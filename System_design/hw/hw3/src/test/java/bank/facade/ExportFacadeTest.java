package bank.facade;

import bank.data_operations.exporter.CsvDataExporter;
import bank.data_operations.exporter.JsonDataExporter;
import bank.data_operations.exporter.YamlDataExporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExportFacadeTest {

    @Mock
    private JsonDataExporter jsonExporter;

    @Mock
    private YamlDataExporter yamlExporter;

    @Mock
    private CsvDataExporter csvExporter;

    @InjectMocks
    private ExportFacade exportFacade;

    @Test
    void exportToJson_ShouldCallJsonExporter() {
        exportFacade.exportToJson();
        verify(jsonExporter).exportData();
    }

    @Test
    void exportToYaml_ShouldCallYamlExporter() {
        exportFacade.exportToYaml();
        verify(yamlExporter).exportData();
    }

    @Test
    void exportToCsv_ShouldCallCsvExporter() {
        exportFacade.exportToCsv();
        verify(csvExporter).exportData();
    }
}