package com.androidev.coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Branch {

    public String name;
    public CommitX commit;
    public Links _links;
    @JsonProperty("protected")
    public boolean protectedX;
    public String protection_url;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitX {
        public String sha;
        public Commit commit;
        public AuthorX author;
        public String url;
        public CommitterX committer;
        public List<Parents> parents;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Commit {
            public Author author;
            public String url;
            public String message;
            public Tree tree;
            public Committer committer;
            public Verification verification;

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Author {
                public String name;
                public String date;
                public String email;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Tree {
                public String sha;
                public String url;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Committer {
                public String name;
                public String date;
                public String email;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Verification {
                public boolean verified;
                public String reason;
                public Object signature;
                public Object payload;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class AuthorX {
            public String gravatar_id;
            public String avatar_url;
            public String url;
            public int id;
            public String login;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CommitterX {
            public String gravatar_id;
            public String avatar_url;
            public String url;
            public int id;
            public String login;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Parents {
            public String sha;
            public String url;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {
        public String html;
        public String self;
    }
}
