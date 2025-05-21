package com.example.service.impl;

import com.example.context.LocalThreadHolder;
import com.example.mapper.UserMapper;
import com.example.pojo.api.ApiResult;
import com.example.pojo.api.PageResult;
import com.example.pojo.api.Result;
import com.example.pojo.dto.query.base.QueryDto;
import com.example.pojo.dto.query.extend.UserQueryDto;
import com.example.pojo.dto.update.UserLoginDTO;
import com.example.pojo.dto.update.UserRegisterDTO;
import com.example.pojo.dto.update.UserUpdateDTO;
import com.example.pojo.em.LoginStatusEnum;
import com.example.pojo.em.RoleEnum;
import com.example.pojo.em.WordStatusEnum;
import com.example.pojo.entity.User;
import com.example.pojo.vo.ChartVO;
import com.example.pojo.vo.UserVO;
import com.example.service.UserService;
import com.example.utils.DateUtil;
import com.example.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册接口
     * 
     * 实现用户注册逻辑，包含用户名查重、账号查重、新用户数据组装及持久化操作
     *
     * @param userRegisterDTO 注册请求参数，包含 userName（用户名）、userAccount（用户账号）、
     *                        userPwd（密码）、userAvatar（头像）、userEmail（邮箱）等字段
     * @return Result<String> 统一响应结果，成功返回"注册成功"，失败返回具体错误原因：
     * - 用户名已被使用
     * - 账号不可用
     */
    @Override
    public Result<String> register(UserRegisterDTO userRegisterDTO) {
        // 用户名查重：检查用户名是否已被激活用户使用
        User user = userMapper.getByActive(
                User.builder().userName(userRegisterDTO.getUserName()).build()
        );
        if (Objects.nonNull(user)) {
            return ApiResult.error("用户名已经被使用，请换一个");
        }

        // 账号查重：检查账号是否已被激活用户使用
        User entity = userMapper.getByActive(
                User.builder().userAccount(userRegisterDTO.getUserAccount()).build()
        );
        if (Objects.nonNull(entity)) {
            return ApiResult.error("账号不可用");
        }

        // 构建新用户实体：设置默认角色、状态及注册时间
        User saveEntity = User.builder()
                .userRole(RoleEnum.USER.getRole())
                .userName(userRegisterDTO.getUserName())
                .userAccount(userRegisterDTO.getUserAccount())
                .userAvatar(userRegisterDTO.getUserAvatar())
                .userPwd(userRegisterDTO.getUserPwd())
                .userEmail(userRegisterDTO.getUserEmail())
                .createTime(LocalDateTime.now())
                .isLogin(LoginStatusEnum.USE.getFlag())
                .isWord(WordStatusEnum.USE.getFlag()).build();

        // 持久化用户数据
        userMapper.insert(saveEntity);
        return ApiResult.success("注册成功");
    }

    /**
     * 用户登录认证处理
     * 
     * 执行账号密码验证，生成JWT令牌，并返回登录结果。包含以下业务逻辑：
     * 1. 根据活跃账号查询用户信息
     * 2. 密码匹配验证
     * 3. 重复登录状态检查
     * 4. JWT令牌生成及响应数据封装
     *
     * @param userLoginDTO 包含用户账号(userAccount)和密码(userPwd)的登录凭证对象
     * @return Result<Object> 响应对象，成功时data字段包含token和role的键值对，失败时返回错误信息
     */
    @Override
    public Result<Object> login(UserLoginDTO userLoginDTO) {
        // 根据活跃状态查询用户（仅匹配userAccount字段）
        User user = userMapper.getByActive(
                User.builder().userAccount(userLoginDTO.getUserAccount()).build()
        );

        // 账号不存在校验
        if (!Objects.nonNull(user)) {
            return ApiResult.error("账号不存在");
        }

        // 密码正确性校验（实际生产环境应使用加密验证）
        if (!Objects.equals(userLoginDTO.getUserPwd(), user.getUserPwd())) {
            return ApiResult.error("密码错误");
        }

        // 防止重复登录检查
        if (user.getIsLogin()) {
            return ApiResult.error("登录状态异常");
        }

        // 生成JWT令牌（包含用户ID和角色信息）
        String token = JwtUtil.generateToken(user.getId(), user.getUserRole());

        // 构造响应数据结构
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("role", user.getUserRole());
        return ApiResult.success("登录成功", map);
    }

    /**
     * 令牌检验及用户信息获取 -- 认证成功返回用户详细信息
     * 
     * 实现逻辑：
     * 1. 从线程本地存储获取已认证的用户ID
     * 2. 根据用户ID构建查询条件
     * 3. 通过MyBatis查询处于激活状态的用户实体
     * 4. 将领域对象转换为视图对象
     *
     * @return Result<UserVO> 包含用户视图对象的响应结果，结构：
     * - success: 始终返回成功状态（前置条件要求必须通过令牌验证）
     * - data: 用户核心信息视图对象，包含id/username/email等字段
     */
    @Override
    public Result<UserVO> auth() {
        // 从线程上下文获取认证阶段存储的用户标识
        Integer userId = LocalThreadHolder.getUserId();

        // 构建MyBatis查询条件对象
        User queryEntity = User.builder().id(userId).build();

        // 查询处于激活状态（active=1）的用户记录
        User user = userMapper.getByActive(queryEntity);

        // 实体对象到视图对象的属性拷贝
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 包装标准化成功响应
        return ApiResult.success(userVO);
    }

    /**
     * 分页查询用户数据
     *
     * @param userQueryDto 用户查询参数对象，包含以下字段：
     *                     - page：当前页码
     *                     - size：每页记录数
     *                     - [其他查询条件字段]（根据实际DTO结构补充）
     * @return Result<List < User>> 包含分页数据的响应对象，其中：
     * - data：当前页用户数据列表
     * - total：符合条件的数据总数
     * - code：响应状态码
     * - message：响应消息
     */
    @Override
    public Result<List<User>> query(UserQueryDto userQueryDto) {
        // 执行分页查询获取当前页数据
        List<User> users = userMapper.query(userQueryDto);

        // 获取符合查询条件的总记录数（不考虑分页）
        Integer count = userMapper.queryCount(userQueryDto);

        // 构建包含分页信息的响应对象
        return PageResult.success(users, count);
    }

    /**
     * 用户信息修改接口
     *
     * @param userUpdateDTO 用户信息更新数据传输对象，包含需要更新的用户字段（如用户名、邮箱等）。
     *                      应包含有效的用户ID标识和至少一个可修改字段
     * @return Result<String> 操作结果响应，包含成功状态码和消息。
     * 成功时返回状态码200和"操作成功"消息，失败时返回对应错误码和错误信息
     */
    @Override
    public Result<String> update(UserUpdateDTO userUpdateDTO) {
        // 构建更新实体：从线程上下文获取当前用户ID作为更新条件
        User updateEntity = User.builder().id(LocalThreadHolder.getUserId()).build();

        // 属性拷贝：将DTO中的非空属性复制到实体对象
        BeanUtils.copyProperties(userUpdateDTO, updateEntity);

        // 执行数据库更新操作（根据ID进行条件更新）
        userMapper.update(updateEntity);

        return ApiResult.success();
    }

    /**
     * 批量删除用户信息
     *
     * @param ids 需要删除的用户ID列表，列表不能为空且应包含有效用户ID
     * @return 返回操作结果对象，包含操作状态信息。成功时返回状态码为200的Result对象
     * @apiNote 该方法直接调用Mapper层批量删除接口，不包含事务控制逻辑，
     * 需要确保调用方已处理事务边界
     */
    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        // 执行批量删除操作（底层基于MyBatis的批量SQL执行）
        userMapper.batchDelete(ids);
        // 返回标准成功响应（默认携带200状态码）
        return ApiResult.success();
    }

    /**
     * 用户信息修改密码
     * 处理密码修改业务逻辑，包含原始密码验证、新密码一致性校验、数据库更新操作
     *
     * @param map 修改信息入参，需包含以下键值：
     *            oldPwd - 原始密码明文
     *            newPwd - 新密码明文
     *            againPwd - 确认新密码明文
     * @return Result<String> 响应结果，包含以下状态：
     * 成功状态：密码更新成功
     * 失败状态：原始密码错误/新密码不一致
     */
    @Override
    public Result<String> updatePwd(Map<String, String> map) {
        // 从请求参数中提取密码相关字段
        String oldPwd = map.get("oldPwd");
        String newPwd = map.get("newPwd");
        String againPwd = map.get("againPwd");

        // 获取当前登录用户完整信息
        User user = userMapper.getByActive(
                User.builder().id(LocalThreadHolder.getUserId()).build()
        );

        // 原始密码匹配验证
        if (!user.getUserPwd().equals(oldPwd)) {
            return ApiResult.error("原始密码验证失败");
        }

        // 新密码一致性校验
        if (!newPwd.equals(againPwd)) {
            return ApiResult.error("前后密码输入校验不一致");
        }

        // 执行密码更新操作
        user.setUserPwd(newPwd);
        userMapper.update(user);

        return ApiResult.success();
    }

    /**
     * 通过ID查询用户信息
     * 
     * 根据用户ID查询处于激活状态的用户实体，将其转换为视图对象后封装为统一响应结果
     *
     * @param id 用户ID（需对应已存在的用户记录）
     * @return 包含用户视图对象的标准化响应结果，当用户不存在或非活跃状态时返回空数据
     */
    @Override
    public Result<UserVO> getById(Integer id) {
        // 构造带ID的查询条件对象，查询活跃状态用户
        User user = userMapper.getByActive(User.builder().id(id).build());

        // 转换实体为视图对象（自动过滤实体中的敏感字段）
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 封装标准化成功响应（包含数据载荷）
        return ApiResult.success(userVO);
    }

    /**
     * 后台新增用户
     *
     * @param userRegisterDTO 包含用户注册信息的传输对象，需包含用户名、密码等必填字段
     * @return {@code Result<String>} 操作结果响应，成功时包含注册成功信息，失败时包含错误提示
     */
    @Override
    public Result<String> insert(UserRegisterDTO userRegisterDTO) {
        return register(userRegisterDTO);
    }

    /**
     * 后台用户信息修改
     *
     * @param user 包含待更新用户信息的实体对象，需包含用户ID及需要修改的字段值
     * @return Result<String> 通用响应结果，data字段为空字符串，
     * 成功时code为200，失败时包含错误状态码和信息
     */
    @Override
    public Result<String> backUpdate(User user) {
        // 执行数据库更新操作
        userMapper.update(user);
        return ApiResult.success();
    }

    /**
     * 统计指定时间段内的用户存量数据并生成图表数据
     *
     * @param day 统计天数范围（例如：7表示最近7天）
     * @return 包含图表数据的结果对象，数据按天分组统计
     * Result.data 中 List<ChartVO> 的每个元素表示单日的统计结果
     */
    @Override
    public Result<List<ChartVO>> daysQuery(Integer day) {
        // 通过时间工具类获取查询时间范围
        QueryDto queryDto = DateUtil.startAndEndTime(day);

        // 构建用户查询条件
        UserQueryDto userQueryDto = new UserQueryDto();
        userQueryDto.setStartTime(queryDto.getStartTime());
        userQueryDto.setEndTime(queryDto.getEndTime());

        // 执行数据库查询获取原始用户数据
        List<User> userList = userMapper.query(userQueryDto);

        // 提取用户创建时间集合
        List<LocalDateTime> localDateTimes = userList.stream()
                .map(User::getCreateTime)
                .collect(Collectors.toList());

        // 使用时间工具类生成日期分布统计结果
        List<ChartVO> chartVOS = DateUtil.countDatesWithinRange(day, localDateTimes);

        return ApiResult.success(chartVOS);
    }
}
