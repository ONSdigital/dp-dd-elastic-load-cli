package com.github.onsdigital.cli.builder;

import java.io.File;

/**
 * Created by James Fawke on 09/01/2017.
 */
public class BuilderFactory {
  public static DataSetBuilder getInstance(DataSetEnum dataSetEnum, File file) {
    DataSetBuilder builder = null;
    switch (dataSetEnum) {
      case HEADERS:
        builder = new DataSetHeaderOnlyBuilder(file);
        break;
      case JSON:
        builder = new JsonDataSetBuilder(file);
        break;

      case ROWS:
        builder = new RowbasedDataSetBuilder(file);
        break;
      case INDIVIDUAL:
        builder = new IndividualDataSetBuilder(file);
        break;
    }
    return builder;
  }
}
