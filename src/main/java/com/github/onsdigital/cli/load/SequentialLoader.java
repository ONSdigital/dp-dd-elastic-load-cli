package com.github.onsdigital.cli.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.onsdigital.cli.builder.DataSetBuilder;
import com.github.onsdigital.cli.domain.Document;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by James Fawke on 09/01/2017.
 */
public class SequentialLoader implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SequentialLoader.class);
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT,
                                                                            true);
    private final TransportClient client;
    private final File logJsonDir;
    private final String index;
    List<DataSetBuilder> builders = new ArrayList<>();

    public SequentialLoader(final List<DataSetBuilder> builders,
                            final TransportClient client,
                            final String index,
                            final File logJsonDir) {

        this.builders = builders;
        this.client = client;
        this.index = index;
        this.logJsonDir = logJsonDir;

    }

    @Override
    public void run() {
        builders.parallelStream()
                .map(b -> {
                    Collection<Document> json = null;
                    try {
                        json = b.build();
                    }
                    catch (IOException e) {
                        LOGGER.error("run([]) : failing to process file");
                    }
                    return json;
                })
                .filter(j -> null != j)
                .forEach(jsons -> jsons.forEach(json -> {
                             logRequest(json);
                             checkForFailures(buildRequest(json));
                         })
                        );
        LOGGER.info("run([]) : committing requestBuilder");


    }

    private void logRequest(final Document document) {
        if (null != logJsonDir) {
            try {
                File tempFile = File.createTempFile("IndexSeq",
                                                    ".document",
                                                    logJsonDir);
                MAPPER.writeValue(tempFile,
                                  document);
            }
            catch (IOException e) {

                LOGGER.error("logRequest([document]) : failed to write file to log location {} ",
                             e.getMessage(),
                             e);
            }
        }
    }

    private IndexResponse buildRequest(final Document document) {
        return client.prepareIndex(index,
                                   UUID.randomUUID()
                                       .toString(),
                                   StringUtils.toString(document.getId()))
                     .setSource(document)
                     .get();
    }


    private void checkForFailures(final IndexResponse indexResponse) {

        LOGGER.error("run([]) : {} ",
                     indexResponse);

    }
}
