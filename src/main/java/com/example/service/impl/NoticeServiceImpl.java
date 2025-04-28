package com.example.service.impl;

import com.example.mapper.NoticeMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.NoticeQueryDto;
import com.example.pojo.entity.Notice;
import com.example.service.NoticeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告的业务逻辑实现类
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    /**
     * 新增系统公告
     * <p>
     * 该方法会自动设置公告创建时间为当前系统时间，并将公告实体持久化到数据库。
     * 注意：公告标题、内容等核心字段应在调用本方法前通过notice参数对象设置完成。
     *
     * @param notice 公告实体对象，包含以下必要字段：
     *               - title: 公告标题（需非空校验）
     *               - content: 公告正文内容
     *               - 其他业务字段根据具体需求设定
     *               注意：createTime字段将由系统自动生成，无需手动设置
     * @return 统一响应结果，成功时返回：
     * - code: 200（操作成功状态码）
     * - message: "操作成功"
     * 失败时将返回对应错误码和提示信息
     */
    @Override
    public Result<Void> save(Notice notice) {
        // 自动生成创建时间（数据库不设置默认值时使用）
        notice.setCreateTime(LocalDateTime.now());

        // 执行数据库持久化操作
        noticeMapper.save(notice);

        return ApiResult.success();
    }

    /**
     * 修改公告
     * <p>
     * 根据提供的公告实体对象更新数据库中对应的公告信息。调用noticeMapper执行更新操作，并返回操作结果
     *
     * @param notice 要更新的公告实体对象，必须包含有效的公告ID和其他需要更新的字段，不可为null
     * @return 返回操作结果，包含成功状态及提示信息，数据部分为Void类型
     */
    @Override
    public Result<Void> update(Notice notice) {
        noticeMapper.update(notice);
        return ApiResult.success();
    }

    /**
     * 批量删除公告信息
     *
     * @param ids 要删除的公告ID集合，不可为空集合或包含无效ID
     * @return Result<Void> 统一响应结果对象，包含操作成功状态码和消息
     * 当所有指定ID的公告被成功删除时返回成功响应
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        // 执行批量删除操作（底层通过MyBatis动态SQL实现）
        noticeMapper.batchDelete(ids);

        // 构造标准成功响应（包含预设的成功状态码和消息）
        return ApiResult.success();
    }

    /**
     * 查询公告列表（带分页功能）
     *
     * @param dto 查询条件实体，包含分页参数(pageNum/pageSize)及过滤条件
     * @return Result<List < Notice>> 包含公告列表和总记录数的响应对象，
     * 结构为：{data: 公告数据列表, total: 符合条件的总记录数}
     */
    @Override
    public Result<List<Notice>> query(NoticeQueryDto dto) {
        // 查询符合条件的总记录数（用于分页计算）
        Integer totalCount = noticeMapper.queryCount(dto);

        // 查询分页结果列表（根据dto中的pageNum/pageSize参数）
        List<Notice> result = noticeMapper.query(dto);

        // 组合分页数据和总记录数返回
        return ApiResult.success(result, totalCount);
    }
}
