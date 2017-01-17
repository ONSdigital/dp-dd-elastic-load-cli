package com.github.onsdigital.cli.load;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IndexLoaderTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexLoaderTest.class);


  @Test
  public void testLoad() throws IOException {
    IndexLoader instance = IndexLoader.getInstance();
    assertNotNull(instance);
    assertEquals("localhost",
                 instance.getHost());
    assertEquals((Integer)9300,
                 instance.getPort());
    assertEquals("test_individual_index",
                 instance.getIndex());
  }

}