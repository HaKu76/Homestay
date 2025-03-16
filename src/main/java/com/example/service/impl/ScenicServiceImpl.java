package com.example.service.impl;


import com.example.context.LocalThreadHolder;
import com.example.mapper.ScenicMapper;
import com.example.mapper.VendorMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicQueryDto;
import com.example.pojo.dto.query.extend.VendorQueryDto;
import com.example.pojo.em.RoleEnum;
import com.example.pojo.entity.Scenic;
import com.example.pojo.vo.ScenicVO;
import com.example.pojo.vo.SelectedVO;
import com.example.pojo.vo.VendorVO;
import com.example.service.ScenicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 景点的业务逻辑实现类
 */
@Service
public class ScenicServiceImpl implements ScenicService {

    @Resource
    private ScenicMapper scenicMapper;
    @Resource
    private VendorMapper vendorMapper;

    /**
     * 景点新增
     *
     * @param scenic 实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(Scenic scenic) {
        // 景点初始可用状态
        scenic.setStatus(true);
        // 设置初始时间
        scenic.setCreateTime(LocalDateTime.now());
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        // 如果是管理员，直接新增，供应商以及其他信息是已经传进来的
        if (Objects.equals(LocalThreadHolder.getRoleId(), RoleEnum.ADMIN.getRole())) {
            scenicMapper.save(scenic);
            return ApiResult.success();
        }
        // 这是用户自己新增的
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);
        // 1. 要么真的没有 2. 有的话，只有一项
        if (vendorVOS.isEmpty()) {
            return ApiResult.error("供应商身份异常");
        }
        scenic.setVendorId(vendorVOS.get(0).getId());
        scenicMapper.save(scenic);
        return ApiResult.success();
    }

    /**
     * 景点修改
     *
     * @param scenic 景点实体
     * @return
     */
    @Override
    public Result<Void> update(Scenic scenic) {
        scenicMapper.update(scenic);
        return ApiResult.success();
    }

    /**
     * 景点删除
     *
     * @param ids 景点ID列表
     * @return
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        scenicMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 景点查询
     *
     * @param dto 查询实体
     * @return Result<List < Scenic>>
     */
    @Override
    public Result<List<ScenicVO>> query(ScenicQueryDto dto) {
        Integer totalCount = scenicMapper.queryCount(dto);
        List<ScenicVO> result = scenicMapper.query(dto);
        return ApiResult.success(result, totalCount);
    }

    /**
     * 供应商自己名下的景点查询
     *
     * @param dto 查询实体
     * @return Result<List < ScenicVO>>
     */
    @Override
    public Result<List<ScenicVO>> queryVendorScenic(ScenicQueryDto dto) {
        VendorQueryDto vendorQueryDto = new VendorQueryDto();
        vendorQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<VendorVO> vendorVOS = vendorMapper.query(vendorQueryDto);
        // 1. 要么真的没有 2. 有的话，只有一项
        if (vendorVOS.isEmpty()) {
            return ApiResult.error("供应商身份异常");
        }
        dto.setVendorId(vendorVOS.get(0).getId());
        return query(dto);
    }

    /**
     * 关联景点下拉选择器
     *
     * @return Result<List < SelectedVO>>
     */
    @Override
    public Result<List<SelectedVO>> querySelectedScenic() {
        ScenicQueryDto scenicQueryDto = new ScenicQueryDto();
        // 只能查可用状态的景点信息
        scenicQueryDto.setStatus(true);
        List<ScenicVO> scenicVOS = scenicMapper.query(scenicQueryDto);
        // 再次封装
        List<SelectedVO> selectedScenicList = scenicVOS.stream()
                .map(scenicVO -> new SelectedVO(
                        scenicVO.getId(),
                        scenicVO.getName()
                )).collect(Collectors.toList());
        return ApiResult.success(selectedScenicList);
    }
}
