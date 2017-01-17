package com.github.onsdigital.cli.builder;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.onsdigital.cli.domain.DataSet;
import com.github.onsdigital.cli.domain.MetaData;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by James Fawke on 09/01/2017.
 * Builds a DataSet with the MetaData and only the header values of the dataset (excludes the Text)
 */
public class DataSetHeaderOnlyBuilder extends AbstractDataSetBuilder<Map> {


  public DataSetHeaderOnlyBuilder(final File dataDirectory) {
    super(dataDirectory);


  }


  @Override
  public final Collection<DataSet> loadDataSet(final File csvFile) throws IOException {
    CsvMapper mapper = new CsvMapper();
    CsvSchema schema = CsvSchema.emptySchema()
                                .withoutHeader(); // load  first row as data as we only want the header
    //Just load the first row
    return Lists.newArrayList(new DataSet(mapper.readerFor(List.class)
                                                .with(schema)
                                                .readValue(csvFile)));
  }

  @Override
  public final MetaData loadMetaData(final File metaDataFile) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return new MetaData(mapper.readValue(metaDataFile,
                                         Map.class));

  }


  @Override
  public DataSetEnum getType() {
    return DataSetEnum.HEADERS;
  }
}
