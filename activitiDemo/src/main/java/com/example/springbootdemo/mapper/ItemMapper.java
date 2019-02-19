package com.example.springbootdemo.mapper;

import com.example.springbootdemo.model.Item;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper {
    public Item selectByPrimaryKey(int id) throws Exception;
}
