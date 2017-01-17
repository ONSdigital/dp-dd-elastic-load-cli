package com.github.onsdigital.cli.builder;

import com.github.onsdigital.cli.domain.DataSet;
import com.github.onsdigital.cli.domain.Document;
import com.github.onsdigital.cli.domain.MetaData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by James Fawke on 09/01/2017.
 */
public abstract class AbstractDataSetBuilder<T> implements DataSetBuilder {
  protected static final String DATA_CSV = "data.csv";
  protected static final String DATA_JSON = "data.json";
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataSetBuilder.class);
  public static final String SITE_IDX_PREFIX = "site.";
  protected File dataDirectory;
  protected File csvDataSet;
  protected File jsonMetaData;

  public AbstractDataSetBuilder(final File dataDirectory) {

    if (!dataDirectory.exists()) {
      throw new IllegalArgumentException("Directory provided does not exist: " + dataDirectory);

    }
    this.dataDirectory = dataDirectory;
    this.csvDataSet = new File(getDataDirectory(),
                               DATA_CSV);

    this.jsonMetaData = new File(getDataDirectory(),
                                 DATA_JSON);
  }

  public abstract Collection<DataSet> loadDataSet(File csvFile) throws IOException;

  public abstract MetaData loadMetaData(File metaDataFile) throws IOException;

  @Override
  public List<Document> build() throws IOException {

    MetaData metaData = buildMetaData();
    Collection<DataSet> collection = buildDataSet();

    List<Document> documentCollection = new ArrayList();
    int i = 0;


    for (DataSet d : collection) {
      Document document = new Document();
      document.setIndex(SITE_IDX_PREFIX + getType().name()
                                                   .toLowerCase());
      document.setDocType(csvDataSet.getPath()
                                    .replace(".", "_")
                                    .toLowerCase());
      document.setId(csvDataSet.getName() + "_" + i++);

      if (null != metaData) {
        document.setMetaData(metaData);
      }

      Collection c = d.getCollection();
      if (null != c) {
        patchFields(c);
        document.setDataSet(d);
      }
      documentCollection.add(document);
    }


    return documentCollection;
  }


  protected Collection patchFields(final Collection collection) {
    collection.forEach(obj -> {
      if (obj instanceof Map) {
        Map metaData = (Map) obj;
        int i = 0;
        Map replacementMap = new HashMap();
        for (Iterator<Map.Entry> it = metaData.entrySet()
                                              .iterator(); it.hasNext(); ) {
          i++;
          final Map.Entry entry = it.next();

          String key = (String) entry.getKey();
          if (StringUtils.contains(key, "..")) {
            String newKey = StringUtils.replace(key, "..", ".");
            replacementMap.put(newKey,
                               entry.getValue());
            it.remove();
            LOGGER.info("patchFields([metaData]) : remove entry key replacing with {}",
                        key,
                        newKey);

          }
        }
        metaData.putAll(replacementMap);
      }
    });
    return collection;
  }

  private MetaData buildMetaData() throws IOException {
    MetaData metaData = null;

    if (getJsonMetaData().exists()) {
      metaData = loadMetaData(getJsonMetaData());
    }


    return metaData;
  }

  private Collection<DataSet> buildDataSet() throws IOException {
    Collection<DataSet> set = null;

    if (getCsvDataSet().exists()) {
      set = loadDataSet(getCsvDataSet());
    }

    return set;
  }

  public File getDataDirectory() {
    return dataDirectory;
  }

  public File getCsvDataSet() {
    return csvDataSet;
  }

  public File getJsonMetaData() {
    return jsonMetaData;
  }
}
