package com.example.springserver.service;
import com.example.springserver.dao.AssetDao;
import com.example.springserver.pojo.Asset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpringServerService {

    @Autowired
    private AssetDao assetDao;
    
    public Asset[] listAsset() {
        return assetDao.getAllAssets();
    }
    
}
