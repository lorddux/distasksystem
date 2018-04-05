package ru.hse.lorddux.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import ru.hse.lorddux.data.request.PCPropertiesData;

import java.net.URISyntaxException;
import java.util.Objects;

public class ConfigurationRequestService implements RequestService<PCPropertiesData> {
    PCPropertiesData requestData;
    URIBuilder uriBuilder;

    public ConfigurationRequestService(PCPropertiesData requestData, String host, String path) {
        uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPath(path);
        this.requestData = requestData;
    }

    public HttpGet createRequest(PCPropertiesData requestData) throws URISyntaxException {
        setParameterIfNotNull(uriBuilder, "cpu", this.requestData.getCpu());
        setParameterIfNotNull(uriBuilder, "ram", this.requestData.getRam());
        setParameterIfNotNull(uriBuilder, "hdd", this.requestData.getHdd());
        setParameterIfNotNull(uriBuilder, "ssd", this.requestData.getSsd());
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        return httpGet;
    }

    private void setParameterIfNotNull(URIBuilder uriBuilder, String paramName, Object object) {
        if (object != null) {
            uriBuilder.setParameter(paramName, object.toString());
        }
    }
}
