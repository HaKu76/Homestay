<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 5px;">
    <el-row style="padding: 10px;margin: 0 5px;">
      <el-row>
        <el-select v-model="vendorQueryDto.isAudit" placeholder="审核状态" size="small" @change="handleFilter">
          <el-option v-for="item in isAuditList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-select v-model="vendorQueryDto.status" placeholder="可用状态" size="small" style="margin-left: 5px;"
                   @change="handleFilter">
          <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-date-picker v-model="searchTime" end-placeholder="申请结束" range-separator="至" size="small"
                        start-placeholder="申请开始" style="width: 220px;margin-left: 5px;" type="daterange" @change="handleFilter">
        </el-date-picker>
        <el-input v-model="vendorQueryDto.name" clearable placeholder="供应商名"
                  size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
        <span style="float: right;">
          <el-button class="customer" size="small"
                     style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                     @click="add()"><i class="el-icon-plus"></i>新增供应商</el-button>
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="供应商名" prop="name"></el-table-column>
        <el-table-column label="负责人ID" prop="userId" sortable width="120"></el-table-column>
        <el-table-column label="负责人" prop="userName" width="70"></el-table-column>
        <el-table-column label="联系人" prop="contactPerson" width="100"></el-table-column>
        <el-table-column label="联系电话" prop="contactPhone" width="130"></el-table-column>
        <el-table-column label="工作地点" prop="workAddress" width="130"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" sortable width="168"></el-table-column>
        <el-table-column label="是否审核" prop="isWord" width="98">
          <template slot-scope="scope">
            <i v-if="!scope.row.isAudit" class="el-icon-warning" style="margin-right: 5px;"></i>
            <i v-else class="el-icon-success" style="margin-right: 5px;color: rgb(253, 199, 50);"></i>
            <el-tooltip v-if="scope.row.isAudit" class="item" content="审核通过后，供应商才可以发布服务或者商品"
                        effect="dark"
                        placement="bottom-end">
              <span style="text-decoration: underline;text-decoration-style: dashed;">已审核</span>
            </el-tooltip>
            <span v-else>未审核</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="isWord" width="108">
          <template slot-scope="scope">
            <i v-if="!scope.row.status" class="el-icon-warning" style="margin-right: 5px;"></i>
            <i v-else class="el-icon-success" style="margin-right: 5px;color: rgb(253, 199, 50);"></i>
            <el-tooltip v-if="scope.row.status" class="item" content="不可用状态，暂停供应商服务" effect="dark"
                        placement="bottom-end">
              <span style="text-decoration: underline;text-decoration-style: dashed;">可用</span>
            </el-tooltip>
            <span v-else>不可用</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110">
          <template slot-scope="scope">
            <span class="text-button" @click="handleEdit(scope.row)">编辑</span>
            <span class="text-button" @click="handleDelete(scope.row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[10, 20]" :total="totalItems"
                     layout="total, sizes, prev, pager, next, jumper" style="margin:10px 0;"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"></el-pagination>
    </el-row>
    <!-- 操作面板 -->
    <el-dialog :show-close="false" :visible.sync="dialogOperation" width="32%">
      <div slot="title">
        <p class="dialog-title">{{ !isOperation ? '新增供应商' : '修改供应商信息' }}</p>
      </div>
      <div style="padding:0 20px;">
        <el-row style="margin-bottom: 20px;">
          <span class="dialog-hover">供应商名称</span>
          <input v-model="data.name" class="dialog-input" placeholder="请输入"/>
          <span class="dialog-hover">联系人</span>
          <input v-model="data.contactPerson" class="dialog-input" placeholder="请输入"/>
          <span class="dialog-hover">联系电话</span>
          <input v-model="data.contactPhone" class="dialog-input" placeholder="请输入"/>
          <span class="dialog-hover">电子邮箱</span>
          <input v-model="data.email" class="dialog-input" placeholder="请输入"/>
          <span class="dialog-hover">产品类型</span>
          <input v-model="data.productType" class="dialog-input" placeholder="请输入"/>
          <span class="dialog-hover">办公地址</span>
          <input v-model="data.workAddress" class="dialog-input" placeholder="请输入"/>
          <div style="margin-block: 10px;">
            <span class="dialog-hover">是否审核</span>
            <el-switch v-model="data.isAudit" active-text="已审核" inactive-text="未审核">
            </el-switch>
          </div>
          <div>
            <span class="dialog-hover">可用状态</span>
            <el-switch v-model="data.status" active-text="可用" inactive-text="不可用">
            </el-switch>
          </div>
          <div style="margin: 10px 0;">
            <span class="dialog-hover">负责人</span>
            <el-select v-model="data.userId" placeholder="请选择负责人">
              <el-option v-for="user in users" :key="user.id" :label="user.userName" :value="user.id">
              </el-option>
            </el-select>
          </div>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button v-if="!isOperation" class="customer" size="small"
                   style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                   @click="addOperation()">新增</el-button>
        <el-button v-else class="customer" size="small"
                   style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                   @click="updateOperation()">修改</el-button>
        <el-button class="customer" size="small" style="background-color: rgb(211, 241, 241);border: none;"
                   @click="cancel()">取消</el-button>
      </span>
    </el-dialog>
  </el-row>
</template>

<script>
export default {
  data() {
    return {
      data: {},
      filterText: '',//关键词
      currentPage: 1,//当前页
      pageSize: 10,//页面大小
      totalItems: 0,//总条数
      dialogOperation: false, // 开关
      isOperation: false, // 开关-标识新增或修改
      tableData: [],//列表数据
      selectedRows: [],
      vendorQueryDto: {}, // 搜索条件
      users: [],
      searchTime: [],
      isAuditList: [{value: null, label: '全部'}, {value: 0, label: '未审核'}, {value: 1, label: '已审核'}],
      statusList: [{value: null, label: '全部'}, {value: 0, label: '不可用'}, {value: 1, label: '可用'}],
    };
  },
  created() {
    this.fetchFreshData();
    // 拉取用户信息
    this.fetchUsers();
  },
  methods: {
    // 获取法人用户信息
    fetchUsers() {
      this.$axios.post('/user/query', {}).then(res => {
        if (res.data.code === 200) {
          this.users = res.data.data;
        }
      }).catch(error => {
        console.error("查询法人信息异常：", error);
      });
    },
    // 置位
    cancel() {
      this.data = {};
      this.dialogOperation = false;
      this.isOperation = false;
    },
    // 批量删除数据
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`未选中任何数据`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: '删除供应商数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/vendor/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: '删除提示',
              text: response.data.msg,
              icon: 'success',
              showConfirmButton: false,
              timer: 2000,
            });
            this.fetchFreshData();

          }
        } catch (e) {
          this.$swal.fire({
            title: '错误提示',
            text: e,
            icon: 'error',
            showConfirmButton: false,
            timer: 2000,
          });
          console.error(`供应商信息删除异常：`, e);
        }
      }
    },
    // 修改信息
    async updateOperation() {
      try {
        const response = await this.$axios.put('/vendor/update', this.data);
        this.clearFormData();
        this.$swal.fire({
          title: '供应商信息修改',
          text: response.data.msg,
          icon: response.data.code === 200 ? 'success' : 'error',
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.cancel();
          this.fetchFreshData();
        }
      } catch (error) {
        console.error('提交表单时出错:', error);
        this.$message.error('提交失败，请稍后再试！');
      }
    },
    // 信息新增
    async addOperation() {
      try {
        const response = await this.$axios.post('/vendor/save', this.data);
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.cancel();
          this.fetchFreshData();
        }
      } catch (error) {
        console.error('提交表单时出错:', error);
        this.$message.error('提交失败，请稍后再试！');
      }
    },
    clearFormData() {
      this.data = {};
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
          ...this.vendorQueryDto
        };
        const response = await this.$axios.post('/vendor/query', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询供应商信息异常:', error);
      }
    },
    add() {
      this.dialogOperation = true;
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
      this.dialogOperation = true;
      this.isOperation = true;
      this.data = {...row}
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    }
  },
};
</script>
<style lang="scss" scoped></style>