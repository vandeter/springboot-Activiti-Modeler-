package com.example.springbootdemo.service.impl;

import com.example.springbootdemo.mapper.ItemMapper;
import com.example.springbootdemo.model.Item;
import com.example.springbootdemo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemMapper itemMapper;
    @Override
    public Item selectByPrimaryKey(int id) throws Exception {
        return itemMapper.selectByPrimaryKey(id);
    }
}
