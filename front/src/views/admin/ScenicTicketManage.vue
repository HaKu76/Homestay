<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 10px;">
    <el-row style="padding: 10px 5px;margin: 0 5px;">
      <el-row>
        <el-select v-model="scenicTicketQueryDto.useStatus" placeholder="可用状态" size="small"
                   style="margin-left: 5px;"
                   @change="handleFilter">
          <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-date-picker v-model="searchTime" end-placeholder="创建结束" range-separator="至" size="small"
                        start-placeholder="创建开始" style="width: 220px;margin-left: 5px;" type="daterange"
                        @change="handleFilter">
        </el-date-picker>
        <el-input v-model="scenicTicketQueryDto.detail" clearable placeholder="门票介绍" size="small"
                  style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
        <span style="float: right;">
          <el-button class="customer" size="small"
                     style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                     @click="add()"><i class="el-icon-plus"></i>新增景点门票</el-button>
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="景点ID" prop="scenicId" sortable width="100"></el-table-column>
        <el-table-column label="关联景点" prop="scenicName"></el-table-column>
        <el-table-column label="门票介绍" prop="detail" width="200"></el-table-column>
        <el-table-column label="数量" prop="number" width="100"></el-table-column>
        <el-table-column label="价格" prop="price" width="100"></el-table-column>
        <el-table-column label="折扣" prop="discount" width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.discount === null ? '无折扣' : scope.row.discount + '折' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" sortable width="168"></el-table-column>
        <el-table-column label="是否可用" prop="useStatus" width="98">
          <template slot-scope="scope">
            <i v-if="!scope.row.useStatus" class="el-icon-warning" style="margin-right: 5px;"></i>
            <i v-else class="el-icon-success" style="margin-right: 5px;color: rgb(253, 199, 50);"></i>
            <el-tooltip v-if="scope.row.useStatus" class="item" content="门票可用时，用户方可购买" effect="dark"
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
        <p class="dialog-title">{{ !isOperation ? '新增景点门票' : '修改景点门票信息' }}</p>
      </div>
      <div style="padding:0 20px;">
        <el-row style="margin-bottom: 20px;">
          <span class="dialog-hover">门票数量</span>
          <input v-model="data.number" class="dialog-input" placeholder="请输入"/>
          <span class="dialog-hover">价格</span>
          <input v-model="data.price" class="dialog-input" placeholder="请输入"/>
          <span class="dialog-hover">折扣</span>
          <input v-model="data.discount" class="dialog-input" placeholder="请输入"/>
          <div>
            <div>
              <span class="dialog-hover">介绍</span>
            </div>
            <el-input v-model="data.detail" :rows="2" placeholder="请输入内容" style="width: 100%;" type="textarea">
            </el-input>
          </div>
          <div style="margin: 10px 0;">
            <span class="dialog-hover">景点</span>
            <el-select v-model="data.scenicId" placeholder="关联景点">
              <el-option v-for="scenic in scenics" :key="scenic.id" :label="scenic.name" :value="scenic.id">
              </el-option>
            </el-select>
          </div>
          <div>
            <span class="dialog-hover">可用状态</span>
            <el-switch v-model="data.useStatus" active-text="可用" inactive-text="不可用">
            </el-switch>
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
      filterText: '',
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogOperation: false, // 开关
      isOperation: false, // 开关-标识新增或修改
      tableData: [],
      selectedRows: [],
      scenicTicketQueryDto: {}, // 搜索条件
      scenics: [],
      searchTime: [],
      statusList: [{value: null, label: '全部'}, {value: 0, label: '不可用'}, {value: 1, label: '可用'}],
    };
  },
  created() {
    this.fetchFreshData();
    this.fetchScenics();
  },
  methods: {
    // 获取法人用户信息
    fetchScenics() {
      this.$axios.post('/scenic/query', {}).then(res => {
        if (res.data.code === 200) {
          this.scenics = res.data.data;
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
        title: '删除景点门票数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/scenicTicket/batchDelete`, ids);
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
          console.error(`景点门票信息删除异常：`, e);
        }
      }
    },
    // 修改信息
    async updateOperation() {
      try {
        const response = await this.$axios.put('/scenicTicket/update', this.data);
        this.clearFormData();
        this.$swal.fire({
          title: '景点门票信息修改',
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
        const response = await this.$axios.post('/scenicTicket/save', this.data);
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
          ...this.scenicTicketQueryDto
        };
        const response = await this.$axios.post('/scenicTicket/query', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询景点门票信息异常:', error);
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