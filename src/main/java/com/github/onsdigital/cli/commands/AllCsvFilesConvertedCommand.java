package com.github.onsdigital.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.onsdigital.cli.builder.BuilderFactory;
import com.github.onsdigital.cli.builder.DataSetBuilder;
import com.github.onsdigital.cli.builder.DataSetEnum;
import com.github.onsdigital.cli.load.IndexLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by James Fawke on 09/01/2017.
 */

@Parameters(commandNames = AllCsvFilesConvertedCommand.ACTION,
            commandDescription = "Convert all CSV File to Json String in and under the directory")
public class AllCsvFilesConvertedCommand implements Command {
  public static final String ACTION = "convertAllCsvFiles";
  public static final String UTF_8 = "UTF-8";
  private static final Logger LOGGER = LoggerFactory.getLogger(AllCsvFilesConvertedCommand.class);
  @Parameter(names = "--help",
             help = true)
  boolean help = false;

  @Parameter(names = {"--csvDataDirectory", "-c"},
             required = true,
             description = "The CSV File to be converted into a Json Structure")
  private String csvDataDir;


  @Parameter(names = {"--structure", "-s"},
             required = false,
             description = "The  CSV File to be converted into which Json Structure")
  private DataSetEnum dataSetEnum = DataSetEnum.JSON;


  public void execute() throws IOException {
    File file = new File(getCsvDataDir());
    final Collection<File> files = FileUtils.listFiles(file,
                                                       new NameFileFilter("data.csv"),
                                                       new NotFileFilter(new NameFileFilter("previous")));

    List<DataSetBuilder> dataSetBuilder = new ArrayList();

    files.forEach(f -> dataSetBuilder.add(BuilderFactory.getInstance(dataSetEnum,
                                                                     f.getParentFile())));

    IndexLoader.getInstance()
               .build(dataSetBuilder);
  }

  public String getCsvDataDir() {
    return csvDataDir;
  }


}
