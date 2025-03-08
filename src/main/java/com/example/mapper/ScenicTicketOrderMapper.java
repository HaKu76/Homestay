package com.example.mapper;

import com.example.pojo.dto.query.extend.ScenicTicketOrderQueryDto;
import com.example.pojo.entity.ScenicTicketOrder;
import com.example.pojo.vo.ScenicTicketOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点门票订单持久化接口
 */
@Mapper
public interface ScenicTicketOrderMapper {
    void save(ScenicTicketOrder scenicTicketOrder);

    void update(ScenicTicketOrder scenicTicketOrder);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<ScenicTicketOrderVO> query(ScenicTicketOrderQueryDto dto);

    // 分页页数
    Integer queryCount(ScenicTicketOrderQueryDto dto);
}
