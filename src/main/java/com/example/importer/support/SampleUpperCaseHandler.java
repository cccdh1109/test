package com.example.importer.support;

import com.example.importer.domain.ImportJobConfig;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SampleUpperCaseHandler extends DefaultFileImportHandler {

    @Override
    public String getHandlerType() {
        return "upper_case_demo";
    }

    @Override
    public Map<String, Object> convert(ImportJobConfig config, int lineNumber, String[] headers, String[] values) {
        Map<String, Object> document = super.convert(config, lineNumber, headers, values);
        document.replaceAll((key, value) -> value instanceof String ? ((String) value).toUpperCase() : value);
        return document;
    }
}
