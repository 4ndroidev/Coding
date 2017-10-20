package com.androidev.coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Blob {

    public String sha;
    public int size;
    public String url;
    public String content;
    public String encoding;
}
