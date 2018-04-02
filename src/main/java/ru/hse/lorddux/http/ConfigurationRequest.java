package ru.hse.lorddux.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import ru.hse.lorddux.config.Configuration;
import ru.hse.lorddux.structures.request.InitRequestData;
import ru.hse.lorddux.structures.response.ConfigurationResponseData;

import java.net.URISyntaxException;

public class ConfigurationRequest extends Request<Configuration, ConfigurationResponseData>{
    InitRequestData requestData;

    public ConfigurationRequest(InitRequestData requestData, String host, String path) {
        super(host, path, ConfigurationResponseData.class);
        this.requestData = requestData;
    }

    protected HttpGet createRequest(URIBuilder uriBuilder) throws URISyntaxException {
        uriBuilder.setParameter("cpu", this.requestData.getCpu().toString());
        uriBuilder.setParameter("ram", this.requestData.getRam().toString());
        uriBuilder.setParameter("hdd", this.requestData.getHdd().toString());
        uriBuilder.setParameter("ssd", this.requestData.getSsd().toString());
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        return httpGet;
    }


}
