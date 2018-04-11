package ru.lorddux.distasksystem.worker.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import ru.lorddux.distasksystem.worker.data.request.PCParametersData;

import java.net.URISyntaxException;

public class ConfigurationRequestCreator implements RequestCreator<PCParametersData> {
    PCParametersData requestData;
    URIBuilder uriBuilder;

    public ConfigurationRequestCreator(PCParametersData requestData, String host, String path) {
        uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPath(path);
        this.requestData = requestData;
    }

    public HttpGet createRequest(PCParametersData requestData) throws URISyntaxException {
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
