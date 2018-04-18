package ru.lorddux.distasksystem.manager.http;

import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;

public interface RequestCreator<T> {
    HttpUriRequest createRequest(T requestData) throws URISyntaxException;
}