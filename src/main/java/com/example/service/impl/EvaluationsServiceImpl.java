package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.EvaluationsMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.PageResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.EvaluationsQueryDto;
import com.example.pojo.entity.Evaluations;
import com.example.pojo.entity.User;
import com.example.pojo.vo.CommentChildVO;
import com.example.pojo.vo.CommentParentVO;
import com.example.pojo.vo.EvaluationsVO;
import com.example.service.EvaluationsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 评论服务实现类
 */
@Service
public class EvaluationsServiceImpl implements EvaluationsService {

    @Resource
    private EvaluationsMapper evaluationsMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 创建并保存用户评论
     *
     * @param evaluations 评论数据对象，包含评论内容、关联ID等字段信息
     * @return Result<Object> 操作结果对象，成功时包含"评论成功"提示，失败时包含错误原因
     */
    @Override
    public Result<Object> insert(Evaluations evaluations) {
        // 设置评论者ID为当前登录用户
        evaluations.setCommenterId(LocalThreadHolder.getUserId());

        // 构建用户查询条件并获取完整用户信息
        User queryConditionEntity = User.builder().id(LocalThreadHolder.getUserId()).build();
        User user = userMapper.getByActive(queryConditionEntity);

        // 检查用户禁言状态
        if (user.getIsWord()) {
            return ApiResult.error("账户已被禁言");
        }

        // TODO 需要发通知！

        // 设置评论创建时间并持久化到数据库
        evaluations.setCreateTime(LocalDateTime.now());
        evaluationsMapper.save(evaluations);

        return ApiResult.success("评论成功");
    }

    /**
     * 根据内容ID和类型查询所有父级评论，并统计评论总数
     *
     * @param contentId   目标内容ID（如文章/视频ID），用于指定查询的具体内容主体
     * @param contentType 内容类型标识，用于区分不同种类的内容（如ARTICLE/VIDEO等）
     * @return Result<Object> 包含操作结果的通用响应对象，数据部分包含：
     * - totalCount: 该内容下所有评论的总数
     * - parentComments: 包含子评论层级结构的父级评论列表（已设置点赞状态标识）
     */
    @Override
    public Result<Object> list(Integer contentId, String contentType) {
        // 获取父级评论及其子评论层级结构
        List<CommentParentVO> parentComments = evaluationsMapper.getParentComments(contentId, contentType);

        // 为当前登录用户设置点赞状态标识
        setUpvoteFlag(parentComments);

        // 统计该内容下的评论总数
        Integer count = evaluationsMapper.totalCount(contentId, contentType);

        return ApiResult.success(new EvaluationsVO(count, parentComments));
    }

    /**
     * 设置评论点赞标识及统计点赞数量
     * 
     * 遍历父评论列表及子评论列表，进行以下操作：
     * 1. 检查当前用户是否在点赞列表中，设置 upvoteFlag 标识
     * 2. 统计对应评论的总点赞数，更新 upvoteCount
     *
     * @param parentComments 父级评论视图对象集合，需要设置点赞状态的评论列表
     */
    private void setUpvoteFlag(List<CommentParentVO> parentComments) {
        // 预先获取当前线程中的用户ID（在整个处理过程中保持唯一值）
        String userId = LocalThreadHolder.getUserId().toString();

        parentComments.forEach(parentComment -> {
            // 处理父级评论的点赞状态和计数
            parentComment.setUpvoteFlag(isUserUpvote(parentComment.getUpvoteList(), userId));
            parentComment.setUpvoteCount(countVotes(parentComment.getUpvoteList()));

            // 处理子级评论的点赞状态和计数
            Optional.ofNullable(parentComment.getCommentChildVOS())
                    .orElse(Collections.emptyList())
                    .forEach(child -> {
                        // 设置子评论的点赞标识和计数（逻辑与父评论处理一致）
                        child.setUpvoteFlag(isUserUpvote(child.getUpvoteList(), userId));
                        child.setUpvoteCount(countVotes(child.getUpvoteList()));
                    });
        });
    }


    /**
     * 判断指定用户是否在点赞用户列表中
     * 
     * 该方法通过处理逗号分隔的用户ID字符串，验证目标用户是否已存在于列表中。
     * 完整处理流程：
     * 1. 使用Optional包装可能为null的点赞用户字符串
     * 2. 将字符串按逗号分割为List集合（空值将返回空集合）
     * 3. 检查集合中是否包含目标用户ID
     *
     * @param voteStr 点赞用户ID字符串，格式为逗号分隔的用户ID（如："user1,user2"），允许为null
     * @param userId  需要验证的目标用户ID，要求非空字符串
     * @return 当且仅当userId存在于voteStr包含的非空用户ID中时返回true；当voteStr为null/空字符串，或userId不存在于列表中时返回false
     */
    private boolean isUserUpvote(String voteStr, String userId) {
        // 使用Optional安全处理null值，分割字符串后检查用户ID存在性
        return Optional.ofNullable(voteStr)
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(Collections.emptyList())
                .contains(userId);
    }


    /**
     * 计算点赞数
     * 
     * 通过解析逗号分隔的用户ID字符串进行计数，自动处理空值场景。
     * 当输入为null时会返回0，空字符串会视为0个有效点赞（如""返回0）
     *
     * @param voteStr 点赞用户ID字符串（逗号分隔），接受null和空字符串输入
     * @return 解析出的有效点赞数量，返回值最小为0
     */
    private int countVotes(String voteStr) {
        // 使用Optional进行空安全处理：先判空，再分割字符串计算元素数量
        return Optional.ofNullable(voteStr)
                .map(s -> s.split(",").length)
                .orElse(0);
    }


    /**
     * 分页查询评论数据
     *
     * @param evaluationsQueryDto 查询参数DTO对象，包含分页参数（pageNum/pageSize）和查询条件
     *                            - 分页参数：当前页码和每页记录数
     *                            - 查询条件：可包含过滤评论的各种条件字段
     * @return Result<Object> 包含分页数据的响应结果对象
     * - data字段包含分页数据列表和总记录数
     * - 数据结构：{list: CommentChildVO[], total: Integer}
     * - 成功时code为200，失败时包含错误信息
     */
    @Override
    public Result<Object> query(EvaluationsQueryDto evaluationsQueryDto) {
        // 执行分页查询获取当前页数据列表
        List<CommentChildVO> list = evaluationsMapper.query(evaluationsQueryDto);

        // 获取符合条件的总记录数（不考虑分页）
        Integer totalPage = evaluationsMapper.queryCount(evaluationsQueryDto);

        // 封装分页结果对象（包含数据列表和总数）
        return PageResult.success(list, totalPage);
    }


    /**
     * 批量删除指定评论数据
     * 
     * 根据传入的评论ID列表执行批量删除操作，通过数据访问层调用对应的SQL映射语句实现数据库记录删除。
     * 该方法要求传入有效的非空ID列表，执行后会立即更新数据库记录。
     *
     * @param ids 需要删除的评论ID集合，应包含至少一个有效整数ID（不允许null或空集合）
     * @return 统一响应结果对象，包含操作状态标识。成功时data字段为null，失败时包含错误信息
     */
    @Override
    public Result<Object> batchDelete(List<Integer> ids) {
        evaluationsMapper.batchDelete(ids);
        return ApiResult.success();
    }


    /**
     * 批量删除指定评论数据
     * 通过单个评论ID构造批量删除参数，调用底层批量删除方法实现单条删除功能
     *
     * @param id 要删除的评论唯一标识符，应符合数据库主键约束
     * @return 包含操作结果的响应对象，成功时携带空数据及成功状态码，
     * 失败时返回对应错误码和提示信息（根据ApiResult的实现规范）
     */
    @Override
    public Result<String> delete(Integer id) {
        // 构造批量删除参数集合（适配需要批量操作的Mapper接口）
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(id);

        // 调用持久层批量删除方法
        evaluationsMapper.batchDelete(ids);

        return ApiResult.success();
    }


    /**
     * 修改评论信息
     *
     * @param evaluations 包含更新信息的评论实体对象，需携带有效主键ID
     *                    字段说明：
     *                    - 若字段值为null则不会更新对应数据库字段
     *                    - 仅允许修改评论内容、评分等可编辑字段
     * @return 操作结果对象，包含以下情况：
     * - 成功时返回带有成功状态码的Result对象
     * - 失败时返回包含错误信息的Result对象
     */
    @Override
    public Result<Void> update(Evaluations evaluations) {
        // TODO 点赞需要做通知（需实现消息队列通知和防重复提交机制）
        evaluationsMapper.update(evaluations);
        return ApiResult.success();
    }
}
