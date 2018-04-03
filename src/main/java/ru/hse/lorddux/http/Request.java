package ru.hse.lorddux.http;

import org.apache.http.impl.client.HttpClients;
import ru.hse.lorddux.exception.*;
import java.io.IOException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import ru.hse.lorddux.structures.response.ResponseData;

import java.net.URISyntaxException;

abstract class Request<Result, RequestResponseData extends ResponseData<Result>>
{
    private final URIBuilder uriBuilder;
    private final Class<RequestResponseData> responseDataClass;
    private static final String DEFAULT_HOST = "";

    protected Request(String host, String path, Class<RequestResponseData> responseDataClass) {
        if (host == null) {
            host = DEFAULT_HOST;
        }
        uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPath(path);
        this.responseDataClass = responseDataClass;
    }

    protected abstract HttpUriRequest createRequest(URIBuilder uriBuilder) throws URISyntaxException;

    public final Result execute() throws BaseException, IOException, URISyntaxException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(createRequest(this.uriBuilder))) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                RequestResponseData responseData = new Gson().fromJson(content, this.responseDataClass);
                if (!responseData.getSuccessFeature()) {
                    throw new BaseException(responseData.getErrorCode(), responseData.getDescription());
                }
                return responseData.getResult();
            }
        }
    }

}