package com.example.springserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Asset {
    private String id;
    private String name;
    private String count;
    private String owner;
}
