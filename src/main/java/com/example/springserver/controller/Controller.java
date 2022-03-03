package com.example.springserver.controller;

import com.example.springserver.pojo.Asset;
import com.example.springserver.service.SpringServerService;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Void> createAsset(@RequestBody Asset asset) throws Exception {
        //return springServerService.createAsset(asset);
        if(springServerService.createAsset(asset)) 
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/read/{assetId}")
    public ResponseEntity<Asset> readAsset(@PathVariable("assetId") String id) throws Exception {
        Asset asset=springServerService.readAsset(id);
        if(asset!=null) 
            return new ResponseEntity<Asset>(asset,HttpStatus.OK);
        else
            return new ResponseEntity<Asset>(asset,HttpStatus.NOT_FOUND);
    }

    @PostMapping("update")
    public ResponseEntity<Void> updateAsset(@RequestBody Asset asset) throws Exception {
        if(springServerService.updateAsset(asset)) 
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/delete/{assetId}")
    public ResponseEntity<Void> deleteAsset(@PathVariable("assetId") String id) throws Exception {
        if (springServerService.deleteAsset(id))
            return new ResponseEntity<Void>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}