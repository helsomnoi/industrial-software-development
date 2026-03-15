package bank.command;

import bank.facade.ExportFacade;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@AllArgsConstructor
public class ExportDataCommand extends BaseCommand {
    private final ExportFacade exportFacade;
    private final String format;


    @Override
    public void execute() {
        validateNotExecuted();
        switch (format.toLowerCase()) {
            case "json" -> exportFacade.exportToJson();
            case "yaml" -> exportFacade.exportToYaml();
            case "csv" -> exportFacade.exportToCsv();
            default -> throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
        }
        this.executed = true;
    }

    @Override
    public String getDescription() {
        return "Экспорт данных в формат " + format.toUpperCase();
    }

}
