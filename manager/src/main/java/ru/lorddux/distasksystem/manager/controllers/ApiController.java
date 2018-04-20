package ru.lorddux.distasksystem.manager.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.lorddux.distasksystem.manager.SerializeService;
import ru.lorddux.distasksystem.manager.db.NodeType;
import ru.lorddux.distasksystem.manager.db.State;
import ru.lorddux.distasksystem.manager.db.entity.StorageConfig;
import ru.lorddux.distasksystem.manager.db.entity.StorageLayer;
import ru.lorddux.distasksystem.manager.db.entity.Worker;
import ru.lorddux.distasksystem.manager.db.entity.WorkerConfig;
import ru.lorddux.distasksystem.manager.db.repositories.StorageConfigRepository;
import ru.lorddux.distasksystem.manager.db.repositories.StorageLayerRepository;
import ru.lorddux.distasksystem.manager.db.repositories.WorkerConfigRepository;
import ru.lorddux.distasksystem.manager.db.repositories.WorkerRepository;
import ru.lorddux.distasksystem.manager.structures.StorageConfigStructure;
import ru.lorddux.distasksystem.manager.structures.WorkerConfigStructure;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.rmi.server.UID;

@Controller
@RequestMapping(path="/api")
public class ApiController implements WebMvcConfigurer {
    private static final String EMPTY_CONFIG = "-";
    private static final Integer DEFAULT_STORAGE_PORT = 1515;
    private static final Integer DEFAULT_CAPACITY = 10;

    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private WorkerConfigRepository workerConfigRepository;
    @Autowired
    private StorageLayerRepository storageLayerRepository;
    @Autowired
    private StorageConfigRepository storageConfigRepository;
    @Autowired
    private  SerializeService serializer;

    @ResponseBody
    @PostMapping(value = "/config", produces = "application/json")
    public String getConfig(HttpServletRequest request) {
        Serializable result;
        String address = getAddress(request);
        NodeType type = getType(request);
        if (type == null) {
            return EMPTY_CONFIG;
        }
        switch (type){
            case WORKER:
                result = processWorker(address);
                break;
            case STORAGE:
                Integer port = request.getIntHeader("x-port");
                result = processStorage(address, port);
                break;
            default:
                return EMPTY_CONFIG;
        }
        try {
            return serializer.serialize(result);
        } catch (JsonProcessingException e) {
            return EMPTY_CONFIG;
        }
    }

    private Serializable processStorage(String address, @Nullable Integer port) {
        StorageLayer storage;
        if ((storage = storageLayerRepository.findByAddress(address)) == null) {
            storage = new StorageLayer();
            storage.setAuthorization(new UID().toString());
            storage.setAddress(address);
        }
        storage.setPort(port == null ? DEFAULT_STORAGE_PORT : port);
        storage.setState(State.STOP);

        StorageConfig config;
        if (storageConfigRepository.findById(0L).isPresent()) {
            config = storageConfigRepository.findById(0L).get();
        } else {
            return EMPTY_CONFIG;
        }
        storage.setConfig(config);
        storageLayerRepository.save(storage);
        return new StorageConfigStructure(
                storage.getPort(),
                config.getDriverAddress(),
                config.getDriverClass(),
                storage.getAuthorization(),
                config.getConnectionUrl(),
                config.getConnectionUserName(),
                config.getConnectionPassword(),
                config.getSqlStatement()
        );
    }

    private Serializable processWorker(String address) {
        Worker worker;
        if ((worker = workerRepository.findByAddress(address)) == null) {
            worker = new Worker();
            worker.setAuthorization(new UID().toString());
            worker.setCapacity(DEFAULT_CAPACITY);
            worker.setAddress(address);
        }
        worker.setState(State.STOP);

        WorkerConfig config;
        if (workerConfigRepository.findById(0L).isPresent()) {
            config = workerConfigRepository.findById(0L).get();
        } else {
            return EMPTY_CONFIG;
        }
        worker.setConfig(config);
        workerRepository.save(worker);
        return new WorkerConfigStructure(
                config.getCodeAddress(),
                config.getCodeMainFile(),
                config.getCodeCommand(),
                config.getStorageAddress(),
                config.getStoragePort(),
                config.getQueueConnectionString(),
                config.getQueueName(),
                worker.getAuthorization(),
                worker.getCapacity()
        );
    }

    @Nullable
    private NodeType getType(HttpServletRequest request) {
        String typeRaw = request.getHeader("x-type");
        if (typeRaw == null) {
            return null;
        }
        try {
            return NodeType.valueOf(typeRaw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String getAddress(HttpServletRequest request) {
        String address = request.getHeader("x-addr");
        if (address == null) {
            address = request.getHeader("X-FORWARDED-FOR");
            if (address == null) {
                address = request.getRemoteAddr();
            } else if (address.contains(",")) {
                address = address.split(",")[0];
            }
        }
        return address;
    }
}
