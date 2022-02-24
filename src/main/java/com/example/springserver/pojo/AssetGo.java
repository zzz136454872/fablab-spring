package com.example.springserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetGo {
    private String ID;
    private String Name;
    private String Count;
    private String Owner;

    public Asset toAsset() {
        return new Asset(ID,Name,Count,Owner);
    }
}
