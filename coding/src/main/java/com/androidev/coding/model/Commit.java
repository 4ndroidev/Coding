package com.androidev.coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commit {

    public String url;
    public String sha;
    public String html_url;
    public String comments_url;
    public CommitInfo commit;
    public AuthorX author;
    public CommitterX committer;
    public List<Parents> parents;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitInfo {
        public String url;
        public Author author;
        public Committer committer;
        public String message;
        public Tree tree;
        public int comment_count;
        public Verification verification;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Author {
            public String name;
            public String email;
            public String date;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Committer {
            public String name;
            public String email;
            public String date;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Tree {
            public String url;
            public String sha;
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
        public String login;
        public int id;
        public String avatar_url;
        public String gravatar_id;
        public String url;
        public String html_url;
        public String followers_url;
        public String following_url;
        public String gists_url;
        public String starred_url;
        public String subscriptions_url;
        public String organizations_url;
        public String repos_url;
        public String events_url;
        public String received_events_url;
        public String type;
        public boolean site_admin;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitterX {
        public String login;
        public int id;
        public String avatar_url;
        public String gravatar_id;
        public String url;
        public String html_url;
        public String followers_url;
        public String following_url;
        public String gists_url;
        public String starred_url;
        public String subscriptions_url;
        public String organizations_url;
        public String repos_url;
        public String events_url;
        public String received_events_url;
        public String type;
        public boolean site_admin;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Parents {
        public String url;
        public String sha;
    }
}
