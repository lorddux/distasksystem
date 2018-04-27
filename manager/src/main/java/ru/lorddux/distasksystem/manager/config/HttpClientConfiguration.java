package ru.lorddux.distasksystem.manager.config;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:/http.properties")
public class HttpClientConfiguration {

    @Value("${http.poolMaxTotal:6}")
    private Integer poolMaxTotal;

    @Value("${http.poolDefaultMaxPerRoute:2}")
    private Integer poolDefaultMaxPerRoute;

    @Value("${http.connectionRequestTimeout:25}")
    private Integer connectionRequestTimeout;

    @Value("${http.connectTimeout:2}")
    private Integer connectTimeout;

    @Value("${http.socketTimeout:10}")
    private Integer socketTimeout;

    @Value("${http.keepAliveTimeout:15}")
    private Integer keepAliveTimeout;

    private static int toMillis(int seconds) {
        return seconds * 1000;
    }

    private RequestConfig buildConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(toMillis(connectionRequestTimeout))
                .setConnectTimeout(toMillis(connectTimeout))
                .setSocketTimeout(toMillis(socketTimeout))
                .build();
    }

    private PoolingHttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(poolMaxTotal);
        cm.setDefaultMaxPerRoute(poolDefaultMaxPerRoute);
        return cm;
    }

    private ConnectionKeepAliveStrategy buildKeepAliveStrategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return toMillis(Integer.parseInt(value));
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
            return toMillis(keepAliveTimeout);
        };
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(buildConfig());
        builder.setConnectionManager(buildConnectionManager());
        builder.setKeepAliveStrategy(buildKeepAliveStrategy());
        return builder.build();
    }
}