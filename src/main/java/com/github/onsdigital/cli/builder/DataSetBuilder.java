package com.github.onsdigital.cli.builder;

import com.github.onsdigital.cli.domain.Document;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by fawks on 09/01/2017.
 */
public interface DataSetBuilder {
  DataSetEnum getType();

  List<Document> build() throws IOException;
}
