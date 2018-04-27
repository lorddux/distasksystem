package ru.lorddux.distasksystem.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SerializeService {

    private final Logger logger = LogManager.getLogger();
    private final ObjectMapper mapper;

    public <T> T deserialize(String object, Class<T> objectClazz) throws IOException {
        logger.trace("deserialize " + object);
        return mapper.readValue(object, objectClazz);
    }

    public <T> String serialize(T object) throws JsonProcessingException {
        String serializedObject = mapper.writeValueAsString(object);
        logger.trace("serialize " + serializedObject);
        return serializedObject;
    }
}
