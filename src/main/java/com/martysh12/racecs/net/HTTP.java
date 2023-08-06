package com.martysh12.racecs.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HTTP {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static class Request {
        private HttpRequest.Builder builder;

        private Request(String method, String uri) {
            try {
                builder = HttpRequest.newBuilder(new URI(uri)).method(method, HttpRequest.BodyPublishers.noBody());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        public static Request get(String uri) {
            return new Request("GET", uri);
        }

        public Request header(String key, String value) {
            builder.header(key, value);
            return this;
        }

        public HttpResponse<String> send() {
            builder.header("Accept", "*/*");

            try {
                return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
