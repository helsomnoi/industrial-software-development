package bank.command;

import bank.facade.ExportFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExportDataCommandTest {

    @Mock
    private ExportFacade exportFacade;

    @Test
    void execute_Json_ShouldCallExportToJson() {
        ExportDataCommand command = new ExportDataCommand(exportFacade, "json");
        command.execute();
        verify(exportFacade).exportToJson();
    }

    @Test
    void execute_Yaml_ShouldCallExportToYaml() {
        ExportDataCommand command = new ExportDataCommand(exportFacade, "yaml");
        command.execute();
        verify(exportFacade).exportToYaml();
    }

    @Test
    void execute_Csv_ShouldCallExportToCsv() {
        ExportDataCommand command = new ExportDataCommand(exportFacade, "csv");
        command.execute();
        verify(exportFacade).exportToCsv();
    }

    @Test
    void execute_UnsupportedFormat_ShouldThrow() {
        ExportDataCommand command = new ExportDataCommand(exportFacade, "xml");
        assertThrows(IllegalArgumentException.class, command::execute);
    }
}