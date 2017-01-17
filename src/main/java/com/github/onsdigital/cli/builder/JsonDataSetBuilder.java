package com.github.onsdigital.cli.builder;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.onsdigital.cli.domain.DataSet;
import com.github.onsdigital.cli.domain.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by James Fawke on 09/01/2017.
 * Builds a Json Dataset with the Json of the CSV where the Headers are the field names in the JSON
 */
public class JsonDataSetBuilder extends AbstractDataSetBuilder<Map> {
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonDataSetBuilder.class);

  public JsonDataSetBuilder(final File dataDirectory) {
    super(dataDirectory);


  }


  @Override
  public final Collection<DataSet> loadDataSet(final File csvFile) throws IOException {
    CsvMapper mapper = new CsvMapper();
    CsvSchema schema = CsvSchema.emptySchema()
                                .withHeader(); // use first row as header; otherwise defaults are fine

    return Lists.newArrayList(new DataSet(mapper.readerFor(Map.class)
                                                .with(schema)
                                              .<Map>readValues(csvFile).readAll()));

  }

  @Override
  public final MetaData loadMetaData(final File metaDataFile) throws IOException {

    final ObjectMapper JsonMapper = new ObjectMapper();
    final MetaData metaData = new MetaData(JsonMapper.readValue(metaDataFile, Map.class));


    CsvMapper csvMapper = new CsvMapper();
    CsvSchema schema = CsvSchema.emptySchema()
                                .withoutHeader(); // use first row as header; otherwise defaults are fine

    final List headers = csvMapper.readerFor(List.class)
                                  .with(schema)
                                  .readValue(getCsvDataSet());

    metaData.setDataSetHeaders(headers);
    return metaData;
  }

  @Override
  public DataSetEnum getType() {
    return DataSetEnum.JSON;
  }

}
