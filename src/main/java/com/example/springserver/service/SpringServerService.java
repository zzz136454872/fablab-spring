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
    
    public String createAsset(Asset asset) {
        return assetDao.createNewAsset(asset);
    }
    
    public Asset readAsset(String id) {
        return assetDao.readAssetById(id);
    }

    public void updateAsset(Asset asset) {
        assetDao.updateNonExistentAsset(asset);
    }

    public void deleteAsset(String id) {
        assetDao.deleteAssetById(id);
    }
}
