<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 30px;border-radius: 10px;">
    <el-row>
      <h1 style="margin-inline: 20px;">民宿订单详情</h1>
      <el-row style="padding: 10px 5px;margin: 0 5px;">
        <el-row>
          <el-select v-model="scenicTicketOrderQueryDto.payStatus" placeholder="支付状态" size="small"
                     style="margin-left: 5px;" @change="handleFilter">
            <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
          <el-date-picker v-model="searchTime" end-placeholder="创建结束" range-separator="至"
                          size="small" start-placeholder="创建开始" style="width: 220px;margin-left: 5px;" type="daterange"
                          @change="handleFilter">
          </el-date-picker>
        </el-row>
      </el-row>
      <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
        <el-table :data="tableData" style="width: 100%">
          <el-table-column label="民宿名称" prop="hotelName" width="150"></el-table-column>
          <el-table-column label="房型" prop="roomName" width="120"></el-table-column>
          <el-table-column label="联系人" prop="contactPerson" width="120"></el-table-column>
          <el-table-column label="联系电话" prop="contactPhone" width="120"></el-table-column>
          <el-table-column label="金额" prop="amount" sortable width="120"></el-table-column>
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
                            <span v-if="!scope.row.payStatus" class="text-button"
                                  @click="handlePay(scope.row)">支付</span>
              <span class="text-button" @click="handleDelete(scope.row)">删除</span>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination :current-page="currentPage" :page-size="pageSize"
                       :page-sizes="[10, 20]" :total="totalItems" layout="total, sizes, prev, pager, next, jumper"
                       style="margin:10px 0;" @size-change="handleSizeChange"
                       @current-change="handleCurrentChange"></el-pagination>
      </el-row>
      <el-row>
        <LineChart :date="dates" :values="values" height="420px" tag="消费记录（元）" @on-selected="selected"/>
      </el-row>
    </el-row>
  </el-row>
</template>

<script>
import LineChart from "@/components/LineChart"

export default {
  components: {LineChart},
  data() {
    return {
      filterText: '',
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      queryDays: 365,
      tableData: [],
      selectedRows: [],
      scenicTicketOrderQueryDto: {}, // 搜索条件
      searchTime: [],
      values: [],
      dates: [],
      statusList: [{value: null, label: '全部'}, {value: 0, label: '未支付'}, {value: 1, label: '已支付'}],
    };
  },
  created() {
    this.fetchFreshData();
    // 数据太少，默认查365天
    this.selected(this.queryDays);
  },
  methods: {
    // 请求后端的金额成交数据
    selected(time) {
      this.$axios.get(`/hotelOrderInfo/daysQuery/${time}`).then(response => {
        const {data} = response;
        if (data.code === 200) {
          this.values = data.data.map(entity => entity.count);
          this.dates = data.data.map(entity => entity.name);
        }
      })
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
        title: '删除民宿订单数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/hotelOrderInfo/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: '删除提示',
              text: response.data.msg,
              icon: 'success',
              showConfirmButton: false,
              timer: 2000,
            });
            this.fetchFreshData();
            return;
          }
        } catch (e) {
          this.$swal.fire({
            title: '错误提示',
            text: e,
            icon: 'error',
            showConfirmButton: false,
            timer: 2000,
          });
          console.error(`民宿订单信息删除异常：`, e);
        }
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
          ...this.scenicTicketOrderQueryDto
        };
        const response = await this.$axios.post('/hotelOrderInfo/queryUser', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询民宿订单信息异常:', error);
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
    // 处理支付的方法
    handlePay(row) {
      const hotelOrderInfo = {id: row.id}
      this.$axios.post('/hotelOrderInfo/pay', hotelOrderInfo).then(res => {
        if (res.data.code === 200) {
          this.$message.success('支付成功');
          this.fetchFreshData();
          this.selected(this.queryDays);
        }
      }).catch(error => {
        console.error("支付错误：", error);
      })
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    }
  },
};
</script>
<style lang="scss" scoped></style>