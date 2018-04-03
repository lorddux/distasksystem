package ru.hse.lorddux.http;

import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;

public interface RequestService<T> {
    HttpUriRequest createRequest(T requestData) throws URISyntaxException;
}