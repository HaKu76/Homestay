package com.example.mapper;

import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.entity.Vendor;
import com.example.pojo.vo.VendorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商持久化接口
 */
@Mapper
public interface VendorMapper {
    void save(Vendor vendor);

    void update(Vendor vendor);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<VendorVO> query(VendorQueryDto dto);

    // 分页页数
    Integer queryCount(VendorQueryDto dto);
}
