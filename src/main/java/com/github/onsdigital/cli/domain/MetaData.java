package com.github.onsdigital.cli.domain;

import java.util.List;
import java.util.Map;

/**
 * Created by fawks on 11/01/2017.
 */
public class MetaData {
  private Map metadata;
  private List dataSetHeaders;

  public MetaData(final Map metadata) {
    this.metadata = metadata;
  }

  public Map getMetadata() {
    return metadata;
  }

  public List getDataSetHeaders() {
    return dataSetHeaders;
  }

  public MetaData setDataSetHeaders(final List dataSetHeaders) {
    this.dataSetHeaders = dataSetHeaders;
    return this;
  }
}
