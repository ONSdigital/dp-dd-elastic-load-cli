package com.github.onsdigital.cli.load;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.onsdigital.cli.builder.DataSetBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by fawks on 09/01/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexLoader {
  private static final Logger LOGGER = LoggerFactory.getLogger(IndexLoader.class);
  private String host = "localhost";
  private Integer port = 9300;
  private String index;

  private Integer threadPool = 10;
  private Integer bulkingSize = 20;
  private File logMessageLocation = new File("/tmp/");

  private ExecutorService es;

  public static IndexLoader getInstance() throws IOException {


    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    String pathname = "file:config/ElasticLoader.yml";
    File file = new DefaultResourceLoader().getResource(pathname)
                                           .getFile();

    return mapper.readValue(file,
                            IndexLoader.class)
                 .init();

  }

  private IndexLoader init() {
    es = Executors.newFixedThreadPool(getThreadPool());
    return this;
  }

  public void build(List<DataSetBuilder> builders) throws UnknownHostException {
    try {
      LOGGER.info("build([builders]) : indexing {} documents", builders.size());
      InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(getHost()),
                                                                                   getPort());
      final TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(transportAddress);

      Lists.partition(builders,
                      getBulkingSize())
           .forEach(schedule(client));
    }
    catch (UnknownHostException e) {
      LOGGER.error("build([builders]) : {} ",
                   e.getMessage(),
                   e);
      throw e;
    }
    es.shutdown();

  }

  private Consumer<List<DataSetBuilder>> schedule(final TransportClient client) {
    LOGGER.info("schedule([client]) : Submitting Requests");
    return b -> {
      BulkRequestBuilder bulkRequest = client.prepareBulk();

      es.execute(new BulkLoader(b,
                                bulkRequest,
                                getIndex(),
                                getLogMessageLocation()));
    };
  }

  private Consumer<List<DataSetBuilder>> run(final TransportClient client) {
    LOGGER.info("schedule([client]) : Submitting Requests");
    return b -> {

      new SequentialLoader(b,
                           client,
                           getIndex(),
                           getLogMessageLocation()).run();
    };
  }

  public String getHost() {
    return host;
  }

  public IndexLoader setHost(final String host) {
    this.host = host;
    return this;
  }

  public Integer getPort() {
    return port;
  }

  public IndexLoader setPort(final Integer port) {
    this.port = port;
    return this;
  }

  public String getIndex() {
    return index;
  }

  public IndexLoader setIndex(final String index) {
    this.index = index;
    return this;
  }


  public Integer getBulkingSize() {
    return bulkingSize;
  }

  public IndexLoader setBulkingSize(final Integer bulkingSize) {
    this.bulkingSize = bulkingSize;
    return this;
  }

  public Integer getThreadPool() {
    return threadPool;
  }

  public void setThreadPool(final Integer threadPool) {
    this.threadPool = threadPool;
  }

  public File getLogMessageLocation() {
    return logMessageLocation;
  }

  public IndexLoader setLogMessageLocation(final String logMessageLocation) {
    if (StringUtils.isNotBlank(logMessageLocation)) {
      this.logMessageLocation = new File(logMessageLocation);
    }
    return this;
  }
}
