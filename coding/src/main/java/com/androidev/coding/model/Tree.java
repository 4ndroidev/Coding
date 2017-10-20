package com.androidev.coding.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tree {
    public String sha;
    public String url;
    public boolean truncated;
    public List<Node> tree;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Node {
        public String path;
        public String mode;
        public String type;
        public String sha;
        public int size;
        public String url;
    }
}
