package com.androidev.coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commit {

    public String sha;
    public CommitInfo commit;
    public String url;
    public String html_url;
    public String comments_url;
    public AuthorX author;
    public CommitterX committer;
    public Stats stats;
    public List<Parents> parents;
    public List<File> files;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitInfo {
        public Author author;
        public Committer committer;
        public String message;
        public Tree tree;
        public String url;
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
            public String sha;
            public String url;
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
    public static class Stats {
        public int total;
        public int additions;
        public int deletions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Parents {
        public String sha;
        public String url;
        public String html_url;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class File {
        public String sha;
        public String filename;
        public String status;
        public int additions;
        public int deletions;
        public int changes;
        public String blob_url;
        public String raw_url;
        public String contents_url;
        public String patch;
    }
}
