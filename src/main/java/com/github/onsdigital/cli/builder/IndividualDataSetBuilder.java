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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by James Fawke on 09/01/2017.
 * Builds a Json Dataset with the Json of the CSV where the Headers are the field names in the JSON
 */
public class IndividualDataSetBuilder extends AbstractDataSetBuilder<Map> {
  private static final Logger LOGGER = LoggerFactory.getLogger(IndividualDataSetBuilder.class);

  public IndividualDataSetBuilder(final File dataDirectory) {
    super(dataDirectory);

  }


  @Override
  public final Collection<DataSet> loadDataSet(final File csvFile) throws IOException {
    LOGGER.info("loadDataSet([csvFile]) : loading {}", csvFile.getAbsolutePath());
    CsvMapper mapper = new CsvMapper();
    CsvSchema schema = CsvSchema.emptySchema()
                                .withHeader(); // use first row as header; otherwise defaults are fine
    List ds = new ArrayList();

    try {
      mapper.readerFor(Map.class)
            .with(schema)
          .<Map>readValues(csvFile).readAll()
                                   .forEach(row -> ds.add(new DataSet(Lists.newArrayList(row))));
    }
    catch (IOException e) {
      LOGGER.error("loadDataSet([csvFile]) : failed to process file {} due to {} ",
                   csvFile.getAbsolutePath(),
                   e.getMessage(),
                   e);
      throw e;
    }

    return ds;

  }


  @Override
  public final MetaData loadMetaData(final File metaDataFile) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return new MetaData(mapper.readValue(metaDataFile,
                                         Map.class));

  }


  @Override
  public DataSetEnum getType() {
    return DataSetEnum.INDIVIDUAL;
  }
}
