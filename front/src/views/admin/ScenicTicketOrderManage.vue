<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 10px;">
    <el-row style="padding: 10px 5px;margin: 0 5px;">
      <el-row>
        <el-select v-model="scenicTicketOrderQueryDto.payStatus" placeholder="支付状态" size="small"
                   style="margin-left: 5px;" @change="handleFilter">
          <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-date-picker v-model="searchTime" end-placeholder="创建结束" range-separator="至" size="small"
                        start-placeholder="创建开始" style="width: 220px;margin-left: 5px;" type="daterange"
                        @change="handleFilter">
        </el-date-picker>
        <el-input v-model="scenicTicketOrderQueryDto.userId" clearable placeholder="用户ID" size="small"
                  style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="景点ID" prop="scenicId" width="100"></el-table-column>
        <el-table-column label="景点名称" prop="scenicName" sortable width="110"></el-table-column>
        <el-table-column label="用户ID" prop="userId" width="80"></el-table-column>
        <el-table-column label="用户名" prop="userName" width="80"></el-table-column>
        <el-table-column label="联系人" prop="contactPerson" width="120"></el-table-column>
        <el-table-column label="联系电话" prop="contactPhone" width="120"></el-table-column>
        <el-table-column label="金额" prop="amount" sortable width="80"></el-table-column>
        <el-table-column label="支付时间" prop="payTime" sortable width="168"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" sortable width="168"></el-table-column>
        <el-table-column label="支付状态" prop="useStatus" width="98">
          <template slot-scope="scope">
            <i v-if="!scope.row.payStatus" class="el-icon-warning" style="margin-right: 5px;"></i>
            <i v-else class="el-icon-success" style="margin-right: 5px;color: rgb(253, 199, 50);"></i>
            <el-tooltip v-if="!scope.row.payStatus" class="item" content="未支付，不能重新下单" effect="dark"
                        placement="bottom-end">
              <span style="text-decoration: underline;text-decoration-style: dashed;">未支付</span>
            </el-tooltip>
            <span v-else>已支付</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <span class="text-button" @click="handleDelete(scope.row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[10, 20]" :total="totalItems"
                     layout="total, sizes, prev, pager, next, jumper" style="margin:10px 0;"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"></el-pagination>
    </el-row>
  </el-row>
</template>

<script>
export default {
  data() {
    return {
      filterText: '',
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      tableData: [],
      selectedRows: [],
      scenicTicketOrderQueryDto: {}, // 搜索条件
      searchTime: [],
      statusList: [{value: null, label: '全部'}, {value: 0, label: '未支付'}, {value: 1, label: '已支付'}],
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
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
        title: '删除景点门票订单数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/scenicTicketOrder/batchDelete`, ids);
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
          console.error(`景点门票订单信息删除异常：`, e);
        }
      }
    },
    // 修改信息
    async updateOperation() {
      try {
        const response = await this.$axios.put('/scenicTicketOrder/update', this.data);
        this.clearFormData();
        this.$swal.fire({
          title: '景点门票订单信息修改',
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
        const response = await this.$axios.post('/scenicTicketOrder/save', this.data);
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
          ...this.scenicTicketOrderQueryDto
        };
        console.log(params);
        const response = await this.$axios.post('/scenicTicketOrder/query', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询景点门票订单信息异常:', error);
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