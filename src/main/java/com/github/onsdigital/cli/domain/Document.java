package com.github.onsdigital.cli.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by fawks on 11/01/2017.
 */
public class Document {
    private MetaData metaData;
    private DataSet dataSet;

    @JsonIgnore
    private String id;

    @JsonIgnore
    private String index;

    @JsonIgnore
    private String docType;


    public String getId() {
        return id;
    }

    public Document setId(final String id) {
        this.id = id;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public Document setIndex(final String index) {
        this.index = index;
        return this;
    }

    public String getDocType() {
        return docType;
    }

    public Document setDocType(final String docType) {
        this.docType = docType;
        return this;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public Document setDataSet(final DataSet dataSet) {
        this.dataSet = dataSet;
        return this;

    }

    public MetaData getMetaData() {
        return metaData;
    }

    public Document setMetaData(final MetaData metaData) {
        this.metaData = metaData;
        return this;
    }
}
