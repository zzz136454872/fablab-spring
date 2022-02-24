package com.example.springserver.controller;

import com.example.springserver.pojo.Asset;
import com.example.springserver.service.SpringServerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private SpringServerService springServerService;

    @GetMapping("hello")
    public String hello() {
        return "hello world";
    }

    @GetMapping("list")
    public Asset[] listAsset() {
        return springServerService.listAsset();
    }
}