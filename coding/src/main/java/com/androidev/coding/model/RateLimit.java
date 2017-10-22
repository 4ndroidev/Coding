package com.androidev.coding.model;


public class RateLimit {

    public final static int MAX_LIMIT = 5000;

    public int limit;
    public int remaining;

    public RateLimit() {
        this(-1, -1);
    }

    public RateLimit(int limit, int remaining) {
        this.limit = limit;
        this.remaining = remaining;
    }

}
