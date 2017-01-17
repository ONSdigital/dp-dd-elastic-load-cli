package com.github.onsdigital.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.onsdigital.cli.builder.RowbasedDataSetBuilder;
import com.github.onsdigital.cli.domain.Document;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Created by James Fawke on 09/01/2017.
 */

@Parameters(commandNames = CsvToStringCommand.ACTION,
            commandDescription = "Convert CSV File to Json String")
public class CsvToStringCommand implements Command {
  public static final String ACTION = "convertCSVToStringRows";
  public static final String UTF_8 = "UTF-8";
  private static final Logger LOGGER = LoggerFactory.getLogger(CsvToStringCommand.class);
  @Parameter(names = "--help",
             help = true)
  boolean help = false;

  @Parameter(names = {"--csvDataDirectory", "-c"},
             required = true,
             description = "The CSV File to be converted into a Json Structure")
  private String csvDataDir;

  @Parameter(names = {"-f", "--outputFile"},
             required = false,
             description = "File to write the Json out to")
  private String outputFile;

  public void execute() throws IOException {
    File file = new File(getCsvDataDir());

    Collection<Document> csv = new RowbasedDataSetBuilder(file).build();
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    String strJson = mapper.writeValueAsString(csv);
    LOGGER.info("execute([]) : CSV as JSON : {}",
                strJson);
    FileUtils.writeStringToFile(new File(outputFile),
                                strJson,
                                Charset.forName(UTF_8));

  }

  public String getCsvDataDir() {
    return csvDataDir;
  }

  public void setCsvDataDir(String csvDataDir) {
    this.csvDataDir = csvDataDir;
  }

  public String getAction() {
    return ACTION;
  }


}
