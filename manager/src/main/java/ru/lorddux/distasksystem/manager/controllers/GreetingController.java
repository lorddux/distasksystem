package ru.lorddux.distasksystem.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.lorddux.distasksystem.manager.db.repositories.*;

@Controller
public class GreetingController implements WebMvcConfigurer {

    @Autowired
    NodeStateRepository nodeRepository;

    @Autowired
    StorageConfigRepository storageConfigRepository;

    @Autowired
    StorageLayerRepository storageLayerRepository;

    @Autowired
    WorkerConfigRepository workerConfigRepository;

    @Autowired
    WorkerRepository workerRepository;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }

    @ResponseBody
    @GetMapping(value = "/conf", produces = "application/json")
    public String getConfig() {
//        TODO
        return null;
    }

}