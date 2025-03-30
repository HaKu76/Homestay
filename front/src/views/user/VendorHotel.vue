<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 5px;">
    <h1 style="margin-inline: 20px;">供应商民宿信息发布管理</h1>
    <el-row style="padding: 10px 5px;margin: 0 5px;">
      <el-row>
        <el-date-picker v-model="searchTime" end-placeholder="创建结束" range-separator="至"
                        size="small" start-placeholder="创建开始" style="width: 220px;margin-left: 5px;" type="daterange" @change="handleFilter">
        </el-date-picker>
        <el-input v-model="hotelQueryDto.name" clearable
                  placeholder="民宿名称" size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
        <span style="float: right;">
                    <el-button class="customer"
                               size="small"
                               style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info" @click="add()"><i
                        class="el-icon-plus"></i>新增民宿</el-button>
                </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="民宿图" prop="cover" width="120px">
          <template slot-scope="scope">
            <img :src="scope.row.cover" style="width: 88px;height: 55px;border-radius: 5px;"/>
          </template>
        </el-table-column>
        <el-table-column label="民宿名" prop="name"></el-table-column>
        <el-table-column label="民宿所在地" prop="address" width="200px"></el-table-column>
        <el-table-column label="联系电话" prop="contactPhone" width="150px"></el-table-column>
        <el-table-column label="供应商ID" prop="vendorId" sortable width="120px"></el-table-column>
        <el-table-column label="供应商名称" prop="vendorName" width="150px"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" sortable width="168"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <span class="text-button" @click="handleEdit(scope.row)">编辑</span>
            <span class="text-button" @click="handleDelete(scope.row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[10, 20]"
                     :total="totalItems" layout="total, sizes, prev, pager, next, jumper" style="margin:10px 0;"
                     @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    </el-row>
    <!-- 操作面板 -->
    <el-dialog :show-close="false" :visible.sync="dialogOperation" width="25%">
      <div slot="title">
        <p class="dialog-title">{{ !isOperation ? '新增民宿' : '修改民宿信息' }}</p>
      </div>
      <div style="padding:0 20px;">
        <el-row>
          <div>
            <span class="dialog-hover">民宿封面图</span>
          </div>
          <el-upload :on-success="handleCoverSuccess" :show-file-list="false"
                     action="/api/Homestay-sys/v1.0/file/upload" class="avatar-uploader">
            <img v-if="cover" :src="cover" style="width: 218px;height: 130px;">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row>
          <span class="dialog-hover">民宿名</span>
          <input v-model="data.name" class="dialog-input" placeholder="民宿名" style="line-height: 45px;"/>
          <span class="dialog-hover">地址</span>
          <input v-model="data.address" class="dialog-input" placeholder="请输入" style="line-height: 45px;"/>
          <span class="dialog-hover">联系电话</span>
          <input v-model="data.contactPhone" class="dialog-input" placeholder="请输入"
                 style="line-height: 45px;"/>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
                <el-button v-if="!isOperation" class="customer"
                           size="small"
                           style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;"
                           type="info" @click="addOperation()">新增</el-button>
                <el-button v-else class="customer"
                           size="small"
                           style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;"
                           type="info" @click="updateOperation()">修改</el-button>
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
      cover: '',
      data: {},
      filterText: '',
      cover: '',
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      tableData: [],
      selectedRows: [],
      hotelQueryDto: {}, // 搜索条件
      searchTime: [],
      dialogOperation: false, // 控制弹窗民宿的开关
      isOperation: false
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    handleCoverSuccess(res, file) {
      this.$notify({
        duration: 1500,
        title: '封面上传',
        message: res.code === 200 ? '上传成功' : '上传失败',
        type: res.code === 200 ? 'success' : 'error'
      });
      // 上传成功则更新用户头像
      if (res.code === 200) {
        this.cover = res.data;
      }
    },
    // 新增民宿信息
    async addOperation() {
      this.data.cover = this.cover;
      const {data} = await this.$axios.post('/hotel/save', this.data);
      if (data.code === 200) {
        // 通知结果
        this.$notify({
          duration: 1500,
          title: '民宿新增',
          message: '新增成功',
          type: 'success'
        });
        this.cancel();
        this.fetchFreshData();
      }
    },
    cancel() {
      this.dialogOperation = false;
      this.isOperation = false;
      this.data = {};
      this.cover = '';
    },
    // 修改民宿信息
    async updateOperation() {
      this.data.cover = this.cover;
      const {data} = await this.$axios.put('/hotel/update', this.data);
      if (data.code === 200) {
        // 通知结果
        this.$notify({
          duration: 1500,
          title: '民宿修改',
          message: '修改成功',
          type: 'success'
        });
        this.cancel();
        this.fetchFreshData();
      }
    },
    add() {
      this.dialogOperation = true;
    },
    // 批量删除数据
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`未选中任何数据`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: '删除民宿数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/hotel/batchDelete`, ids);
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
          console.error(`民宿信息删除异常：`, e);
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
          ...this.hotelQueryDto
        };
        const response = await this.$axios.post('/hotel/queryVendorHotel', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询民宿信息异常:', error);
      }
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
      this.cover = row.cover;
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