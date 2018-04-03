package ru.hse.lorddux.config;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AllArgsConstructor
public class ConfigurationClient {
    private static final Logger log_ = LogManager.getLogger(ConfigurationClient.class);

    private long sleepTime;

}
