package com.github.onsdigital.cli.load;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.onsdigital.cli.builder.DataSetBuilder;
import com.github.onsdigital.cli.domain.Document;
import com.google.common.collect.Lists;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Fawke on 09/01/2017.
 */
public class BulkLoader implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkLoader.class);
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT,
                                                                            true);
    private final BulkRequestBuilder bulkRequest;
    private final File logJsonDir;
    private final String index;
    List<DataSetBuilder> builders = new ArrayList<>();

    public BulkLoader(final List<DataSetBuilder> builders,
                      final BulkRequestBuilder bulkRequest,
                      final String index,
                      final File logJsonDir) {

        this.builders = builders;
        this.bulkRequest = bulkRequest;
        this.index = index;
        this.logJsonDir = logJsonDir;

    }

    @Override
    public void run() {


        builders.stream()
                .map(b -> {
                    List<Document> documents = null;
                    try {
                        LOGGER.info("run([]) : building ");
                        documents = b.build();
                    }
                    catch (IOException e) {
                        LOGGER.error("run([]) : failing to process file {} with", e.getMessage(), e);
                    }
                    return documents;
                })
                .filter(j -> null != j)
                .forEach(jsons -> {
                             Lists.partition(jsons, 20000)
                                  .forEach(sSubSet -> {
                                      sSubSet.forEach(document -> {
                                          logRequest((Document) document);
                                          buildRequest((Document) document);
                                      });
                                      LOGGER.info("run([]) : committing bulkRequest");
                                      BulkResponse bulkItemResponses = bulkRequest.get();

                                      checkForFailures(bulkItemResponses);
                                  });
                         }
                        );


    }

    private void logRequest(final Document document) {
        if (null != logJsonDir) {
            try {
                File tempFile = File.createTempFile("BulkApiLoad",
                                                    ".json",
                                                    logJsonDir);
//        LOGGER.info("logRequest([document]) : writing log file {}", tempFile.getAbsolutePath());
//        MAPPER.writeValue(tempFile,
//                          document);
            }
            catch (IOException e) {

                LOGGER.error("logRequest([document]) : failed to write file to log location {} ",
                             e.getMessage(),
                             e);
            }
        }
    }

    private BulkRequestBuilder buildRequest(final Document document) {
        byte[] page = null;
        try {
            LOGGER.info("buildRequest([document]) : persisting {}", document.getId());
            page = MAPPER.writeValueAsBytes(document);
        }
        catch (JsonProcessingException e) {
            LOGGER.error("buildRequest([document]) : failed to convert fail {} with {} ",
                         document.getId(),
                         e.getMessage(),
                         e);
            return null;
        }

        return bulkRequest.add(new IndexRequest(document.getIndex(),
                                                document.getDocType())
                                       .id(StringUtils.toString(document.getId()))
                                       .source(page));
    }

    private void checkForFailures(final BulkResponse bulkItemResponses) {
        if (bulkItemResponses.hasFailures()) {
            bulkItemResponses.forEach(r -> {
                if (r.isFailed()) {
                    LOGGER.error("run([]) : {} : {}",
                                 r.getFailureMessage(),
                                 r.getFailure());
                }
            });
        }
    }
}
