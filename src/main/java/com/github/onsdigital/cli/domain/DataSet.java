package com.github.onsdigital.cli.domain;

import java.util.Collection;

/**
 * Created by fawks on 10/01/2017.
 */
public class DataSet {
    private Collection collection;

    public DataSet(final Collection collection) {

        this.collection = collection;
    }

    public Collection getCollection() {
        return collection;
    }
}
