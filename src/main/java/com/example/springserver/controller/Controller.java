package com.example.springserver.controller;

import com.example.springserver.pojo.Asset;
import com.example.springserver.service.SpringServerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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

    @PostMapping("create")
    public ResponseEntity<String> createAsset(@RequestBody Asset asset) throws Exception {
        //return springServerService.createAsset(asset);
        return new ResponseEntity<String>(springServerService.createAsset(asset),HttpStatus.OK);
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> createAsset(@PathVariable("assetId") String id) throws Exception {
        return new ResponseEntity<Asset>(springServerService.readAsset(id),HttpStatus.OK);
    }

    @PostMapping("update")
    public ResponseEntity<Void> updateAsset(@RequestBody Asset asset) throws Exception {
        springServerService.updateAsset(asset);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> deleteAsset(@PathVariable("assetId") String id) throws Exception {
        springServerService.deleteAsset(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}