<template>
  <div>
    <div class="top">
      <div class="top-left">
        <Logo sysName="旅游民宿" />
      </div>
      <div class="top-right">
        <ul>
          <li @click="route('/notice')">公告通知</li>
          <li @click="route('/scenic')">景点信息</li>
          <li @click="route('/hotel')">民宿信息</li>
          <li @click="route('/strategy')">攻略区</li>
        </ul>
        <el-dropdown :hide-on-click="false" size="mini" type="success">
          <span class="el-dropdown-link" style="cursor: pointer;">
            我的订单<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="route('/myScenicOrder')">景点订单</el-dropdown-item>
            <el-dropdown-item @click.native="route('/myHotelOrder')">民宿订单</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-dropdown v-if="vendors.length !== 0 && vendors[0].isAudit && vendors[0].status" :hide-on-click="false"
          size="mini" type="success">
          <span class="el-dropdown-link" style="cursor: pointer;">
            供应商订单<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="route('/ticketOrder')">门票订单</el-dropdown-item>
            <el-dropdown-item @click.native="route('/hotelOrder')">民宿订单</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-dropdown :hide-on-click="false" size="mini" type="success">
          <span class="el-dropdown-link" style="cursor: pointer;">
            内容中心<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="route('/publishStrategy')">发布攻略</el-dropdown-item>
            <el-dropdown-item @click.native="route('/contentCenter')">攻略详情</el-dropdown-item>
            <el-dropdown-item @click.native="route('/save')">我的收藏</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button round size="mini" type="primary" @click="route('/service')">服务中心</el-button>
        <el-dropdown class="user-dropdown" size="mini" type="success">
          <span class="el-dropdown-link" style="display: flex; align-items: center;cursor: pointer;">
            <el-avatar :size="30" :src="userInfo.userAvatar" style="margin-top: 0;"></el-avatar>
            <span class="user-name" style="margin-left: 5px;font-size: 14px;">{{ userInfo.userName }}</span>
            <i class="el-icon-arrow-down el-icon--right" style="margin-left: 5px;"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="route('/self')">个人资料</el-dropdown-item>
            <el-dropdown-item @click.native="route('/resetPwd')">修改密码</el-dropdown-item>
            <el-dropdown-item @click.native="loginOut">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <div class="router-view">
      <div class="item">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import Logo from "@/components/Logo"

export default {
  components: { Logo },
  name: "User",
  data() {
    return {
      key: '',
      defaultPath: '/scenic',
      userInfo: {},
      // 供应商信息
      vendors: [],
    }
  },
  created() {
    this.auth();
    console.log(this.data);
    // 仅在特定条件下默认跳转到默认路径 /scenic
    if (this.$route.path === '/') {
      this.route(this.defaultPath);
    }
  },
  methods: {
    // 路由跳转
    route(path) {
      if (this.$route.path !== path) {
        this.$router.push(path);
      }
    },
    // 退出登录
    async loginOut() {
      const confirmed = await this.$swalConfirm({
        title: '退出登录',
        text: `退出后需要重新登录哦？`,
        icon: 'warning',
      });
      if (confirmed) {
        sessionStorage.setItem('token', null);
        this.$router.push('/');
      }
    },
    // Token 检验
    async auth() {
      const { data } = await this.$axios.get('/user/auth');
      if (data.code !== 200) { // Token校验异常
        this.$router.push('/');
      } else {
        sessionStorage.setItem('userInfo', JSON.stringify(data.data));
        this.userInfo = data.data;
        // 获取供应商信息
        const { data: vendorData } = await this.$axios.post('/vendor/queryUser');
        if (vendorData.code === 200) {
          this.vendors = vendorData.data;
        }
      }
    },
  }
};
</script>
<style lang="scss" scoped>
.top {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 65px;
  line-height: 65px;
  padding: 20px 100px;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid rgb(231, 231, 231);
  // background-color: white;
  transition: all 0.3s;
  // 毛玻璃效果核心样式
  background-color: rgba(255, 255, 255, 0.3); // 略微透明背景
  backdrop-filter: blur(10px); // 毛玻璃模糊
  -webkit-backdrop-filter: blur(10px); // Safari 兼容性

  // 可选：添加阴影增强层次感
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

  .top-right {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;

    ul {
      list-style: none;

      li {
        cursor: pointer;
        float: left;
        margin-right: 6px;
        padding: 5px 10px;
        min-width: 50px;
        max-width: 100px;
        color: rgb(117, 59, 89);
        font-size: 14px;
        box-sizing: border-box;
        font-weight: 500;
      }

      li:hover {
        border-radius: 10px;
        background-color: #e6e9f0;
        color: rgb(100, 141, 144);
      }
    }
  }

  .top-left {
    display: flex;
    justify-content: center;
    align-items: center;
  }
}

.router-view {
  padding: 60px 30px;
  // box-sizing: border-box;
  background-image: linear-gradient(to top, #e6e9f0 0%, #eef1f5 100%);
  min-height: calc(100vh - 66px);
  padding-top: calc(65px + 40px); // 关键：留出顶部空间

  .item {
    padding: 0 50px;
    box-sizing: border-box;
  }
}
</style>