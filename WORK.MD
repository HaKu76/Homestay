## 项目开发环境

### 1.1 开发工具

#### 1.1.1 前端

VSCode

Node.js版本：v14.21.3

```cmd
node -v
```

npm版本：6.14.18

```cmd
npm -v
```

#### 1.1.2 后端

IDEA

JDK版本：1.8

```cmd
java -version
```

Maven版本：3.9.6

```cmd
mvn -v
```

springboot版本：2.7.6

### 1.1.3 数据库

连接工具Navicat

## 项目创建

项目结构中，项目SDK选择1.8，语言级别选择8，模块的语言级别选择8，依赖也选择1.8

前端初始构建指令

```
拉取依赖项
npm i
前端运行
npm run dev
```

### 脚手架说明

#### 后端

assets.picture这是图片资源，数据库引用的url就是该文件

aop

> Pager分页自动化注解
>
> PagerAspect是Pager对应的aop切面类
>
> 这两个是分页相关的接口，处理传过来的一些页面参数
>

>
> Protector是接口保护注解
>
> ProtectorAspect是Protector对应的aop切面类
>
> 这两个是实现接口的访问权限控制和用户身份解析

config

> InterceptorConfig拦截器配置类
>
> WebConfig前后端跨域配置类

context

> LocalThreadHolder用户访问时取得用户身份ID

controller

> 相关接口

Interceptor

> 校验类，实现用户认证

mapper

> 持久化接口，与数据库交互的方法

pojo

> 各种项目实体类，dto类，通用的返回封装类，实体类，vo类

service

> 项目相关业务逻辑

utils

> 项目工具

HomestayApplication项目启动类

resources.mapper是项目的SQL文件存放

resources.application.yml是配置文件

#### 前端

node_modules:依赖存放

public:更换logo

router/index.js:路由配置

utils:工具类

components:封装组件

view:功能页面，要在router/index.js进行对应的路由注册

#### 数据库的创建

连接名随意，主机名localhost，端口3306，用户名密码自定义。

新建数据库，名称homestay（见名知意），字符集utf8mb4（最多使用四个字节），排序规则utf8mb4-bin

一共15张表

| 表名                  | 备注        |
|---------------------|-----------|
| user                | 用户信息表     |
| evaluations         | 评论信息表     |
| scenic_category     | 景点分类表     |
| vendor              | 供应商信息表    |
| scenic              | 景点信息表     |
| scenic_line         | 景点路线表     |
| scenic_rating       | 景点评分表     |
| scenic_strategy     | 景点攻略表     |
| hotel               | 民宿信息表     |
| hotel_room          | 民宿房间信息表   |
| hotel_room_bed      | 民宿房间床位信息表 |
| hotel_order_info    | 民宿预约订单信息表 |
| notice              | 公告表       |
| scenic_ticket       | 景点门票信息表   |
| scenic_ticket_order | 景点订票信息    |

##### 优化

给部分字段加上非null的属性，防止字段为空

### 实体类建设

都在pojo/entity下

民宿实体类Hotel

民宿订单信息实体类HotelOrderInfo

民宿房间实体类HotelRoom

民宿房间床位信息实体类HotelRoomBed

公告信息实体类Notice

景点信息实体类Scenic

景点分类实体类ScenicCategory

景点路线实体类ScenicLine

景点评分实体类ScenicRating

景点攻略实体类ScenicStrategy

景点门票实体类ScenicTicket

景点门票订单实体类ScenicTicketOrder

供应商信息实体类Vendor

## 景点分类

#### 后端

基础**查询类**QueryDto

> **接口（controller）和持久化数据库（mapper）中间包含业务逻辑层（service）**
>
> controller ---- service ---- mapper

[pojo/dto/query/extend下]景点分类查询Dto业务ScenicCategoryQueryDto继承基础查询

业务逻辑层service包含接口+实现类impl（Alt+Enter快速实现方法）

持久化层接口mapper->写SQL->回到impl继续更新逻辑->回到接口继续更新

**NEW：安装MyBatisX插件，可用快速定位mapper和SQL**

通用返回封装类ApiResult

#### 前端

**文件前端控制器（头像上传等等）FileController**

路由配置src/router下新增一条景点分类管理scenicCategoryManage

注意path开头小写，组件开头大写

功能页面：views/admin新增ScenicCategoryManage.vue

**分页查询接口注意加Pager注解（对分页参数进行处理）**

## 供应商管理

关联信息时添加VO类

注意修改映射为VO时牵一发动全身，所有查询相关及映射都需要将实体类改为vo类

## 景点管理

前端部分要注意拉取列表的命名规则

## 景点路线管理



## 景点评分管理

只有新增和查询

不包含VO

使用子查询语句

## 景点攻略管理



## 民宿管理

**所有的时间查询修复**

问题描述：时间查询和数据库的不匹配，假如我查询起始时间为3月5日，那么他会查询到3月4日开始的信息

问题原因：问题出在 `searchTime` 的时间处理逻辑上。当前代码将 `searchTime` 转换为 ISO 字符串时，直接使用了 `toISOString()`，这会返回 UTC 时间，可能与本地时间存在时区差异，导致查询结果不准确。

解决方法：修改 `fetchFreshData` 方法中的时间处理逻辑

```javascript
try {
    let startTime = null;
    let endTime = null;
    if (this.searchTime != null && this.searchTime.length === 2) {
      const [startDate, endDate] = this.searchTime;
      // 转换为本地时间字符串，格式为 YYYY-MM-DDTHH:mm:ss
      startTime = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, '0')}-${String(startDate.getDate()).padStart(2, '0')}T00:00:00`;
      endTime = `${endDate.getFullYear()}-${String(endDate.getMonth() + 1).padStart(2, '0')}-${String(endDate.getDate()).padStart(2, '0')}T23:59:59`;
    }
```

## 民宿房间管理

Bug修复：脚手架里的Operaion是打错的单词，全部替换Operation

细节注意：关于图片的上传，要注意在新增、修改、data等相关参数里添加相关的代码。
