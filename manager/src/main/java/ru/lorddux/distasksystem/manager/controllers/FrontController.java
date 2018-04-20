package ru.lorddux.distasksystem.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.lorddux.distasksystem.manager.db.repositories.*;


@Controller
public class FrontController implements WebMvcConfigurer {
    @Autowired
    private StorageConfigRepository storageConfigRepository;
    @Autowired
    private StorageLayerRepository storageLayerRepository;
    @Autowired
    private WorkerConfigRepository workerConfigRepository;
    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }


}