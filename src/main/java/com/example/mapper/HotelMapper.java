package com.example.mapper;


import com.example.pojo.dto.query.extend.HotelQueryDto;
import com.example.pojo.entity.Hotel;
import com.example.pojo.vo.HotelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 民宿持久化接口
 */
@Mapper
public interface HotelMapper {
    void save(Hotel hotel);

    void update(Hotel hotel);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<HotelVO> query(HotelQueryDto dto);

    // 分页页数
    Integer queryCount(HotelQueryDto dto);
}
