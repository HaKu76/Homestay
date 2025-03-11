package com.example.controller;

import com.example.aop.Pager;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.ScenicTicketQueryDto;
import com.example.pojo.entity.ScenicTicket;
import com.example.pojo.vo.ScenicTicketVO;
import com.example.service.ScenicTicketService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 景点门票控制器
 */
@RestController
@RequestMapping("/scenicTicket")
public class ScenicTicketController {
    //增删改查
    //JSON--->实体类（可用直接用）

    @Resource
    private ScenicTicketService scenicTicketService;

    /**
     * 景点门票新增
     *
     * @param scenicTicket 景点门票实体
     * @return Result<Void>
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<Void> save(@RequestBody ScenicTicket scenicTicket) {
        return scenicTicketService.save(scenicTicket);
    }

    /**
     * 景点门票修改
     *
     * @param scenicTicket 景点门票实体
     * @return Result<Void>
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Void> update(@RequestBody ScenicTicket scenicTicket) {
        return scenicTicketService.update(scenicTicket);
    }

    /**
     * 景点门票批量删除
     *
     * @param ids 景点门票ID列表
     * @return Result<Void>
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Void> update(@RequestBody List<Integer> ids) {
        return scenicTicketService.batchDelete(ids);
    }

    /**
     * 查询供应商管理的景点门票信息
     *
     * @return Result<Void>
     */
    @PostMapping(value = "/queryVendorTicket")
    @ResponseBody
    public Result<List<ScenicTicketVO>> queryVendorTicket() {
        return scenicTicketService.queryVendorTicket();
    }

    /**
     * 查询景点门票
     *
     * @return Result<Void>
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<ScenicTicketVO>> query(@RequestBody ScenicTicketQueryDto dto) {
        return scenicTicketService.query(dto);
    }
}
