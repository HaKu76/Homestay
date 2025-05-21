<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 10px;">
    <el-row style="padding: 10px;margin-left: 5px;">
      <el-row style="display: flex;justify-content: left;gap: 6px;">
        <span class="edit-button" @click="add()">
          新增用户
        </span>
        <el-select v-model="userQueryDto.isLogin" placeholder="登录状态" size="small" style="width: 120px;"
                   @change="handleFilter">
          <el-option v-for="item in loginStatuList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-select v-model="userQueryDto.isWord" placeholder="禁言状态" size="small" style="width: 120px;"
                   @change="handleFilter">
          <el-option v-for="item in wordStatuList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-select v-model="userQueryDto.role" placeholder="用户角色" size="small" style="width: 120px;"
                   @change="handleFilter">
          <el-option v-for="item in rolesList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-date-picker v-model="searchTime" end-placeholder="注册结束" range-separator="至" size="small" start-placeholder="注册开始"
                        style="width: 216px;" type="daterange" @change="handleFilter">
        </el-date-picker>
        <el-input v-model="userQueryDto.userName" clearable placeholder="用户名" size="small" style="width: 166px;"
                  @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
      </el-row>
    </el-row>
    <el-row style="margin: 0 22px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" :stripe="true" style="width: 100%">
        <el-table-column label="头像" prop="userAvatar" width="68">
          <template slot-scope="scope">
            <el-avatar :size="25" :src="scope.row.userAvatar" style="margin-top: 10px;"></el-avatar>
          </template>
        </el-table-column>
        <el-table-column label="用户名" prop="userName" width="150">
        </el-table-column>
        <el-table-column label="基本信息" prop="userName" width="208">
          <template slot-scope="scope">
            <div class="text">账号：{{ scope.row.userAccount }}<i class="el-icon-document-copy"
                                                                 style="margin-left: 4px;"
                                                                 @click="copyAccount(scope.row.userAccount)"></i></div>
            <div class="text">邮箱：{{ scope.row.userEmail === null ? '用户未设置' : scope.row.userEmail }}</div>
            <div class="text">角色：{{ scope.row.userRole === 1 ? '管理员' : '用户' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="账号状态" prop="status" width="208">
          <template slot-scope="scope">
            <div class="text">
              禁言状态：
              <i v-if="scope.row.isWord" class="el-icon-warning" style="margin-right: 5px;"></i>
              <i v-else class="el-icon-success" style="margin-right: 5px;color: rgb(253, 199, 50);"></i>
              <el-tooltip v-if="scope.row.isWord" class="item" content="账号一经禁言，不可评论互动。经由管理员解禁后，方可评论"
                          effect="dark"
                          placement="bottom-end">
                <span style="text-decoration: underline;text-decoration-style: dashed;">已禁言</span>
              </el-tooltip>
              <span v-else>正常</span>
            </div>
            <div class="text">
              禁言状态：
              <i v-if="scope.row.isLogin" class="el-icon-warning" style="margin-right: 5px;"></i>
              <i v-else class="el-icon-success" style="margin-right: 5px;color: rgb(253, 199, 50);"></i>
              <el-tooltip v-if="scope.row.isLogin" class="item" content="账号一经封号，不可登录系统。经由管理员解禁后，方可登录"
                          effect="dark"
                          placement="bottom-end">
                <span style="text-decoration: underline;text-decoration-style: dashed;">已封号</span>
              </el-tooltip>
              <span v-else>正常</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column :sortable="true" label="注册于" prop="createTime" width="208"></el-table-column>
        <el-table-column fixed="right" label="操作" width="180">
          <template slot-scope="scope">
            <span class="text-button" @click="handleStatus(scope.row)">账号状态</span>
            <span class="text-button" @click="handleEdit(scope.row)">编辑</span>
            <span class="text-button" @click="handleDelete(scope.row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[20, 50]"
                     :total="totalItems" layout="total, sizes, prev, pager, next, jumper" style="margin:10px 0;"
                     @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    </el-row>
    <!-- 操作面板 -->
    <el-dialog :show-close="false" :visible.sync="dialogUserOperation" width="32%">
      <div style="padding:16px 20px;">
        <el-row>
          <p>用户头像</p>
          <el-upload :on-success="handleAvatarSuccess" :show-file-list="false"
                     action="http://localhost:21090/api/Homestay-sys/v1.0/file/upload" class="avatar-uploader">
            <img v-if="userAvatar" :src="userAvatar" class="dialog-avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row>
          <span class="dialog-hover">用户名</span>
          <input v-model="data.userName" class="dialog-input" placeholder="用户名"/>
          <span class="dialog-hover">账号</span>
          <input v-model="data.userAccount" class="dialog-input" placeholder="账号"/>
          <span class="dialog-hover">邮箱</span>
          <input v-model="data.userEmail" class="dialog-input" placeholder="邮箱"/>
          <span class="dialog-hover">密码</span>
          <input v-model="userPwd" class="dialog-input" placeholder="输入新密码" type="password"/>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer" style="margin-top: 10px;">
        <span class="channel-button" @click="cancel()">
          取消操作
        </span>
        <span v-if="!isOperation" class="edit-button" @click="addOperation()">
          确定新增
        </span>
        <span v-else class="edit-button" @click="updateOperation()">
          确定修改
        </span>
      </span>
    </el-dialog>
    <el-dialog :show-close="false" :visible.sync="dialogStatusOperation" width="25%">
      <div slot="title"
           style="background-color: rgba(34, 165, 241);border-top-left-radius: 5px;border-top-right-radius: 5px;">
        <p class="dialog-title" style="color: #FFFFFF;font-size: 14px;font-weight: 800;">
          账号状态
        </p>
      </div>
      <div style="padding:10px 20px;">
        <el-row>
          <p>*封号状态</p>
          <el-switch v-model="data.isLogin" active-text="封号" inactive-color="rgb(193, 193, 193)"
                     inactive-text="正常状态">
          </el-switch>
        </el-row>
        <el-row style="margin: 20px 0;">
          <p>*禁言状态</p>
          <el-switch v-model="data.isWord" active-text="禁言" inactive-color="rgb(193, 193, 193)"
                     inactive-text="正常状态">
          </el-switch>
        </el-row>
        <el-row style="margin: 20px 0;">
          <p>*是否设置为管理员</p>
          <el-switch v-model="isAdmin" active-text="管理员" inactive-color="rgb(193, 193, 193)"
                     inactive-text="普通用户">
          </el-switch>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button class="customer" size="small"
                   style="background-color: rgb(34, 165, 241);color: rgb(247,248,249);border: none;" type="info" @click="comfirmStatus">确认</el-button>
        <el-button class="customer" size="small" style="background-color: rgb(246,246,246);border: none;"
                   @click="cancel">取消</el-button>
      </span>
    </el-dialog>
  </el-row>
</template>

<script>
export default {
  data() {
    return {
      userPwd: '',
      userAvatar: '',
      data: {},
      filterText: '',
      isAdmin: false, // 是否是管理员标志，初始值为false
      currentPage: 1,
      pageSize: 20,
      totalItems: 0,
      dialogStatusOperation: false,
      dialogUserOperation: false, // 开关
      isOperation: false, // 开关-标识新增或修改
      tableData: [],
      searchTime: [],
      selectedRows: [],
      status: null,
      userQueryDto: {}, // 搜索条件
      loginStatuList: [{value: null, label: '全部'}, {value: 0, label: '正常'}, {value: 1, label: '封号'}],
      wordStatuList: [{value: null, label: '全部'}, {value: 0, label: '正常'}, {value: 1, label: '禁言'}],
      rolesList: [{value: null, label: '全部'}, {value: 2, label: '用户'}, {value: 1, label: '管理员'}]
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    async copyAccount(text) {
      try {
        // 创建一个临时的 textarea 元素
        const tempTextArea = document.createElement('textarea');
        tempTextArea.value = text;
        document.body.appendChild(tempTextArea);

        // 选中并复制内容到剪贴板
        tempTextArea.select();
        tempTextArea.setSelectionRange(0, 99999); // 适用于移动设备
        document.execCommand('copy');

        // 移除临时的 textarea 元素
        document.body.removeChild(tempTextArea);

        this.$notify({
          duration: 1000,
          title: '复制账号',
          message: '账号复制成功',
          type: 'success'
        });
      } catch (err) {
        console.error('无法复制账号: ', err);
      }
    },
    cancel() {
      this.data = {};
      this.userAvatar = '';
      this.userPwd = '';
      this.dialogUserOperation = false;
      this.dialogStatusOperation = false;
      this.isOperation = false;
    },
    comfirmStatus() {
      const userUpdateDto = {
        id: this.data.id,
        isLogin: this.data.isLogin,
        userRole: this.isAdmin ? 1 : 2,
        isWord: this.data.isWord
      }
      this.$axios.put(`/user/backUpdate`, userUpdateDto).then(res => {
        if (res.data.code === 200) {
          this.$notify({
            duration: 1500,
            title: '信息修改',
            message: '修改成功',
            type: 'success'
          });
          this.cancel();
          this.fetchFreshData();
        }
      }).catch(error => {
        console.log("修改状态异常：" + error);
      })
    },
    // 修改用户状态
    handleStatus(data) {
      // 设置用户角色
      this.isAdmin = data.userRole === 1;
      this.dialogStatusOperation = true;
      this.data = data;
    },
    // 头像上传回调函数
    handleAvatarSuccess(res, file) {
      this.$notify({
        duration: 1500,
        title: '头像上传',
        message: res.code === 200 ? '上传成功' : '上传失败',
        type: res.code === 200 ? 'success' : 'error'
      });
      // 上传成功则更新用户头像
      if (res.code === 200) {
        this.userAvatar = res.data;
      }
    },
    // 批量删除数据
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`未选中任何数据`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: '删除用户数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/user/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$notify({
              duration: 1000,
              title: '信息删除',
              message: '删除成功',
              type: 'success'
            });
            this.fetchFreshData();
            return;
          }
        } catch (error) {
          this.$message.error("用户信息删除异常：", error);
          console.error(`用户信息删除异常：`, error);
        }
      }
    },
    // 修改信息
    async updateOperation() {
      if (this.userPwd !== '') {
        const pwd = this.$md5(this.$md5(this.userPwd));
        this.data.userPwd = pwd;
      } else {
        this.data.userPwd = null;
      }
      this.data.userAvatar = this.userAvatar;
      try {
        const response = await this.$axios.put('/user/backUpdate', this.data);
        if (response.data.code === 200) {
          this.$notify({
            duration: 1000,
            title: '信息修改',
            message: '修改成功',
            type: 'success'
          });
          this.cancel();
          this.fetchFreshData();
        }
      } catch (error) {
        console.error('修改出错:', error);
      }
    },
    // 信息新增
    async addOperation() {
      //  密码处理
      if (this.userPwd !== '') {
        this.data.userPwd = this.$md5(this.$md5(this.userPwd));
      } else {
        this.data.userPwd = null;
      }
      this.data.userAvatar = this.userAvatar;
      try {
        const response = await this.$axios.post('/user/insert', this.data);
        if (response.data.code === 200) {
          this.$notify({
            duration: 1000,
            title: '信息新增',
            message: '新增成功',
            type: 'success'
          });
          this.cancel();
          this.fetchFreshData();
        }
      } catch (error) {
        console.error('信息新增出错:', error);
        this.$message.error('提交失败，请稍后再试！');
      }
    },
    async fetchFreshData() {
      try {
        let startTime = null;
        let endTime = null;
        if (this.searchTime != null && this.searchTime.length === 2) {
          const [startDate, endDate] = this.searchTime;
          // 转换为本地时间字符串，格式为 YYYY-MM-DDTHH:mm:ss
          startTime = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, '0')}-${String(startDate.getDate()).padStart(2, '0')}T00:00:00`;
          endTime = `${endDate.getFullYear()}-${String(endDate.getMonth() + 1).padStart(2, '0')}-${String(endDate.getDate()).padStart(2, '0')}T23:59:59`;
        }
        // 请求参数
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          key: this.filterText,
          startTime: startTime,
          endTime: endTime,
          ...this.userQueryDto
        };
        const response = await this.$axios.post('/user/query', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        this.$message.error("查询用户信息异常:", error);
        console.error('查询用户信息异常:', error);
      }
    },
    add() {
      this.dialogUserOperation = true;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.filterText = '';
      this.handleFilter();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchFreshData();
    },
    handleEdit(row) {
      this.dialogUserOperation = true;
      this.isOperation = true;
      row.userPwd = null;
      this.userAvatar = row.userAvatar;
      this.data = {...row}
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    }
  },
};
</script>
<style lang="scss" scoped>
.text {
  font-size: 12px;
  text-align: left;
  color: rgb(163, 164, 167);
}
</style>