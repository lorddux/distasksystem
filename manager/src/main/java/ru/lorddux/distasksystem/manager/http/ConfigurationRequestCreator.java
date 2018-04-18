package ru.lorddux.distasksystem.manager.http;

import com.google.gson.Gson;
import ru.lorddux.distasksystem.manager.db.entity.WorkerConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

import java.net.URISyntaxException;

public class ConfigurationRequestCreator implements RequestCreator<WorkerConfig> {
    private static final String PATH = "/config";
    private URIBuilder uriBuilder;
    private String authorization;

    public ConfigurationRequestCreator(String host, String authorization) {
        uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPath(PATH);
        this.authorization = authorization;
    }

    public HttpPost createRequest(WorkerConfig configurationEntity) throws URISyntaxException {
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.setHeader("authorization", authorization);
        String json = new Gson().toJson(configurationEntity);
        StringEntity entity = new StringEntity(json, "UTF-8");
        httpPost.setEntity(entity);
        return httpPost;
    }
}
