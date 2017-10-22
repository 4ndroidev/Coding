package com.androidev.coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth {

    public String access_token;
    public String scope;
    public String token_type;
}
