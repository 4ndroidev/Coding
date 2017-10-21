package com.androidev.coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Patch {

    public List<Line> lines = new ArrayList<>();

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Line {
        public int index;
        public int leftNo;
        public int rightNo;
        public String prefix;
        public String text;

        public Line(int index, int leftNo, int rightNo, String prefix, String text) {
            this.index = index;
            this.leftNo = leftNo;
            this.rightNo = rightNo;
            this.prefix = prefix;
            this.text = text;
        }
    }
}
