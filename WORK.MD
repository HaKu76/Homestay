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

## 民宿房间床位管理

修正部分：床位状态为非空，而前端如果默认不可用传null会报错，需要传0这个状态值

```javascript
        addOperation() {
            const hotelRoomBed = {
                roomId: this.roomId,
                ...this.data,
                status: this.data.status ? 1 : 0, // 将布尔值转换为1或0
            }
            //新增status的状态
            修改同理也新增这一行代码
```

新增的时候roomId没传到后端

解决方法：在调用 `addOperation` 方法时，将 `this.roomId` 作为参数传递进去。

```javascript
roomId: this.roomId,
```

在模板中调用 `addOperation` 方法时，不需要传递参数，直接调用即可

## 景点门票管理

修复修改时不输入折扣应传值null，后端mapper.xml需要在修改新增一个if，discount为null的情况

还有发现但未修复的问题后续优化：打折边界值输入（case：0折）

注意门票描述的模糊查询`CONCAT('%', #{detail}, '%')`

## 民宿订单信息管理



## 景点门票订单管理



## 公告管理

> 调用Editor组件：
>
> - 先在script导入`import Editor from "@/components/Editor"`
>
> - 声明`components: { Editor },`然后调用↓
>
> - <div>
>   <Editor height="300px" :receiveContent="data.content" @on-receive="onReceiveContent" />
>   </div>

## 供应商申请及个人信息维护

user/Service.vue要在VendorServiceImpl新增添加代码

## 供应商维护景点信息功能

页面布局问题：脚手架的css样式有点问题，问题是出在了user/Main.vue这个文件，修改一下就好了

## 供应商维护民宿信息功能

之前遗漏了一个BUG：在HotelMapper.xml里面查询vendorId写错了后面写成了scenicId，害我找了半天的问题，前后端代码删删改改搞了半天。

## 供应商维护门票信息功能

优化了服务中心的路由点击，之前是在span标签下，我点了好几次才能跳转，优化到div标签下就舒服多了。

## 供应商维护民宿房间信息功能

只改动前端代码，进行后端接口的复用

## 供应商维护门票订单信息功能

新增一个供应商查询门票订单Dto类ScenicTicketQueryParamDto

## 供应商查看门票订单销售额折线图功能

新增一个金额Dto关联类MoneyDto，一个金额工具类MoneyUtils

前端折线图组件LineChart.vue

## 供应商维护民宿订单信息功能



## 供应商查看民宿订单销售额折线图功能

修复民宿房间新增无效的问题：在HotelRoomBedManage.vue组件中，监听roomId改为深度监听。

## 供应商查看公告列表及详情功能



## 内容中心发布景点攻略功能



## 内容中心展示及景点攻略编辑功能

修复了确认编辑后的路由跳转到scenic的逻辑问题：在Main.vue修改生命周期函数改为

```javascript
  created() {
    this.auth();
    // 仅在特定条件下默认跳转到默认路径 /scenic
    if (this.$route.path === '/') {
      this.route(this.defaultPath);
    }
  },
```

并且在确认编辑的方法这边修改路由的push改为`this.$router.push('/contentCenter');`

## 攻略区展示及评论功能

新增Text工具类TextUtils，新增项目依赖jsoup

Main.vue新增用户信息`sessionStorage.setItem('userInfo',JSON.stringify(data.data));`

## 景点信息列表

给景点分类查询新增了一个全部的选项，在前端添加代码

fetchFreshData函数添加代码

```javascript
        // 选择全部时，清除 categoryId
        if (params.categoryId === '') {
          delete params.categoryId; // 清除 categoryId
        }
```

fetchCategories获取景点分类的方法修改代码

```javascript
        if (res.data.code === 200) {
          // 插入“全部”选项
          this.categories = [
            { id: '', name: '全部' }, // id 设置为空字符串，表示不限制分类
            ...res.data.data // 原有的分类数据
          ];
        }
```

## 景点详情页面包含的三个功能模块

景点详情展示、评论、景点评分

## 景点浏览量与收藏功能

逻辑就是存储用户ID列表，使用,来切分

## 我的收藏展示功能



## 景点门票购买功能



## 我的订单门票详情及支付功能

支付功能只做了修改状态，另外修复了创建时间查询无效的bug，挂载一个`@change="handleFilter"`即可解决。

## 民宿信息列表、详情页展示、评论、房间预定功能



## 民宿订单详情功能



## 管理员首页可视化功能
