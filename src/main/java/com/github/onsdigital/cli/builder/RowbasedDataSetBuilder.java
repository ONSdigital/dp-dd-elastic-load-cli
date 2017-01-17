package com.github.onsdigital.cli.builder;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.onsdigital.cli.domain.DataSet;
import com.github.onsdigital.cli.domain.MetaData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by James Fawke on 09/01/2017.
 * Builds the Rows up into Strings for Json - so both the Key and the values are searchable
 */
public class RowbasedDataSetBuilder extends AbstractDataSetBuilder<String> {


  public RowbasedDataSetBuilder(final File dataDirectory) {
    super(dataDirectory);


  }


  @Override
  public final Collection<DataSet> loadDataSet(final File csvFile) throws IOException {
    CsvMapper mapper = new CsvMapper();
    ObjectMapper objMapper = new ObjectMapper();
    CsvSchema schema = CsvSchema.emptySchema()
                                .withHeader(); // use first row as header; otherwise defaults are fine
    MappingIterator<Object> it = mapper.readerFor(Map.class)
                                       .with(schema)
                                       .readValues(csvFile);
    List<String> collection = new ArrayList<String>();
    while (it.hasNext()) {
      collection.add(objMapper.writeValueAsString(it.next()));
    }
    return Lists.newArrayList(new DataSet(collection));

  }

  @Override
  public final MetaData loadMetaData(final File metaDataFile) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return new MetaData(mapper.readValue(metaDataFile,
                                         Map.class));

  }


  @Override
  public DataSetEnum getType() {
    return DataSetEnum.ROWS;
  }
}
