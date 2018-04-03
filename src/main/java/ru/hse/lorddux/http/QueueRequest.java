package ru.hse.lorddux.http;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import ru.hse.lorddux.structures.response.ResponseData;

import java.net.URISyntaxException;

public abstract class QueueRequest<Result, RequestResponseData extends ResponseData<Result>> extends Request<Result, RequestResponseData> {
    public QueueRequest(String host, String path, Class<RequestResponseData> responseDataClass) {
        super(host, path, responseDataClass);
    }

    protected HttpUriRequest createRequest(URIBuilder uriBuilder) throws URISyntaxException {
        return null;
    }
}
