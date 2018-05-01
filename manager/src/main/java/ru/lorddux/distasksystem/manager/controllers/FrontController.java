package ru.lorddux.distasksystem.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.lorddux.distasksystem.manager.db.entity.StorageConfig;
import ru.lorddux.distasksystem.manager.db.entity.WorkerConfig;
import ru.lorddux.distasksystem.manager.db.repositories.*;

import javax.validation.Valid;

@Controller
public class FrontController implements WebMvcConfigurer {
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

    @GetMapping("/setworker")
    public String setWorkerConfig(WorkerConfig workerConfig) {
        return "workerform";
    }

    @PostMapping("/setworker")
    public String setWorkerConfig(@Valid WorkerConfig workerConfig, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "workerform";
        }
        workerConfig.setId(0L);
        if (workerConfigRepository.findById(0L).isPresent()) {
            WorkerConfig tmpConfig = workerConfigRepository.findById(0L).get();
            tmpConfig.setValues(workerConfig);
            workerConfigRepository.save(tmpConfig);
        } else {
            workerConfigRepository.save(workerConfig);
        }
        return "redirect:workerconf";
    }

    @PostMapping("/nodes")
    public String controlNodes() {

        return "";
    }

    @GetMapping("/setstorage")
    public String setStorageConfig(StorageConfig storageConfig) {
        return "storageform";
    }

    @PostMapping("/setstorage")
    public String setStorageConfig(@Valid StorageConfig storageConfig, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "storageform";
        }
        storageConfig.setId(0L);
        if (storageConfigRepository.findById(0L).isPresent()) {
            StorageConfig tmpConfig = storageConfigRepository.findById(0L).get();
            tmpConfig.setValues(storageConfig);
            storageConfigRepository.save(tmpConfig);
        } else {
            storageConfigRepository.save(storageConfig);
        }
        return "redirect:storageconf";
    }

    @GetMapping("/workerconf")
    public String getWorkerConfig(Model model) {
        if (workerConfigRepository.findById(0L).isPresent()) {
            WorkerConfig workerConfig = workerConfigRepository.findById(0L).get();
            model.addAttribute("codeAddress", workerConfig.getCodeAddress());
            model.addAttribute("codeMainFile", workerConfig.getCodeMainFile());
            model.addAttribute("codeCommand", workerConfig.getCodeCommand());
            model.addAttribute("storageAddress", workerConfig.getStorageAddress());
            model.addAttribute("storagePort", workerConfig.getStoragePort());
            model.addAttribute("queueConnectionString", workerConfig.getQueueConnectionString());
            model.addAttribute("queueName", workerConfig.getQueueName());
        }
        return "workerconf";
    }

    @GetMapping("/storageconf")
    public String getStorageConfig(Model model) {
        if (storageConfigRepository.findById(0L).isPresent()) {
            StorageConfig storageConfig = storageConfigRepository.findById(0L).get();
            model.addAttribute("driverAddress", storageConfig.getDriverAddress());
            model.addAttribute("driverClass", storageConfig.getDriverClass());
            model.addAttribute("connectionUrl", storageConfig.getConnectionUrl());
            model.addAttribute("connectionUserName", storageConfig.getConnectionUserName());
            model.addAttribute("connectionPassword", storageConfig.getConnectionPassword());
            model.addAttribute("sqlStatement", storageConfig.getSqlStatement());
        }
        return "storageconf";
    }

}