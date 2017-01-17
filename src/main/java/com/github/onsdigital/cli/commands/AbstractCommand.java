package com.github.onsdigital.cli.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.onsdigital.cli.domain.Document;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Created by fawks on 17/01/2017.
 */
public abstract class AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommand.class);
    protected void buildFile(final Collection<Document> build) throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String strJson = mapper.writeValueAsString(build);
        LOGGER.info("execute([]) : CSV as JSON : {}",
                                       strJson);
        FileUtils.writeStringToFile(new File(getOutputFile()),
                                    strJson,
                                    Charset.forName(CsvToStringCommand.UTF_8));
    }

    protected abstract String getOutputFile();
}
