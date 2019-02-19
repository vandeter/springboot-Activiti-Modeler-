package com.example.springbootdemo.service;

import com.example.springbootdemo.model.Item;

public interface ItemService {
    public Item selectByPrimaryKey(int id) throws Exception;
}
