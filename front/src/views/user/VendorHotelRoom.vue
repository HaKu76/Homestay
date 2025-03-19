<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 5px;">
    <el-row style="padding: 10px;margin: 0 5px;">
      <el-row>
        <el-select v-model="hotelRoomQueryDto.hotelId" placeholder="从属民宿" size="small" @change="fetchFreshData">
          <el-option v-for="item in hotels" :key="item.id" :label="item.name" :value="item.id">
          </el-option>
        </el-select>
        <el-date-picker v-model="searchTime" end-placeholder="创建结束" range-separator="至"
                        size="small" start-placeholder="创建开始" style="width: 220px;margin-left: 5px;" type="daterange">
        </el-date-picker>
        <el-input v-model="hotelRoomQueryDto.name" clearable
                  placeholder="民宿房间名" size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
        <span style="float: right;">
                    <el-button class="customer"
                               size="small"
                               style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info" @click="add()"><i
                        class="el-icon-plus"></i>新增民宿房间</el-button>
                </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="房间封面" prop="cover" width="120px">
          <template slot-scope="scope">
            <img :src="scope.row.cover" style="width: 88px;height: 55px;border-radius: 5px;"/>
          </template>
        </el-table-column>
        <el-table-column label="房间名" prop="name"></el-table-column>
        <el-table-column label="民宿ID" prop="hotelId" sortable width="120px"></el-table-column>
        <el-table-column label="民宿名称" prop="hotelName" sortable width="120px"></el-table-column>
        <el-table-column label="价格" prop="price" sortable width="80px"></el-table-column>
        <el-table-column label="折扣" prop="discount" sortable width="120px"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" sortable width="168"></el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <span class="text-button" @click="roomManage(scope.row)">床位管理</span>
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
    <el-dialog :show-close="false" :visible.sync="dialogOperation" width="28%">
      <div slot="title">
        <p class="dialog-title">{{ !isOperation ? '新增民宿房间' : '修改民宿房间信息' }}</p>
      </div>
      <div style="padding:0 20px;">
        <span class="dialog-hover">民宿房间封面</span>
        <el-row>
          <el-upload :on-success="handleCoverSuccess" :show-file-list="false"
                     action="/api/Homestay-sys/v1.0/file/upload" class="avatar-uploader">
            <img v-if="cover" :src="cover" style="width: 200px;height: 120px;">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row style="margin-bottom: 20px;">
          <span class="dialog-hover">民宿房间名称</span>
          <input v-model="data.name" class="dialog-input" placeholder="请输入"/>
          <div>
            <div>
              <span class="dialog-hover">介绍</span>
            </div>
            <el-input v-model="data.detail" :rows="2" placeholder="请输入" style="width: 100%;"
                      type="textarea">
            </el-input>
          </div>
          <div>
            <div>
              <span class="dialog-hover">价格</span>
            </div>
            <el-input v-model="data.price" placeholder="请输入"></el-input>
          </div>
          <div>
            <div>
              <span class="dialog-hover">折扣</span>
            </div>
            <el-input v-model="data.discount" placeholder="请输入"></el-input>
          </div>
          <div style="margin: 10px 0;">
            <div>
              <span class="dialog-hover">民宿列表</span>
            </div>
            <el-select v-model="data.hotelId" placeholder="请选择">
              <el-option v-for="hotel in hotels" :key="hotel.id" :label="hotel.name" :value="hotel.id">
              </el-option>
            </el-select>
          </div>
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
                           @click="cannel()">取消</el-button>
            </span>
    </el-dialog>

    <el-drawer :append-to-body="true" :direction="direction" :visible.sync="drawer" title="房间管理" width="35%">
      <BedManage :roomId="roomId"/>
    </el-drawer>
  </el-row>
</template>

<script>
import BedManage from '@/views/user/Bed'

export default {
  components: {BedManage},
  data() {
    return {
      cover: '',
      roomId: null,
      data: {},
      filterText: '',
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogOperation: false, // 开关
      isOperation: false, // 开关-标识新增或修改
      tableData: [],
      selectedRows: [],
      hotelRoomQueryDto: {}, // 搜索条件
      hotels: [],
      searchTime: [],
      drawer: false,
      direction: 'rtl', // right to left
    };
  },
  created() {
    this.fetchFreshData();
    this.fetchUsers();
  },
  methods: {
    // 房间管理
    roomManage(hotelRoom) {
      this.roomId = hotelRoom.id;
      this.drawer = true;
    },
    // 封面上传回调
    handleCoverSuccess(res, file) {
      this.$notify({
        duration: 1500,
        title: '封面上传',
        message: res.code === 200 ? '上传成功' : '上传失败',
        type: res.code === 200 ? 'success' : 'error'
      });
      // 上传成功则更新封面
      if (res.code === 200) {
        this.cover = res.data;
      }
    },
    // 获取民宿信息
    fetchUsers() {
      this.$axios.post('/hotel/queryVendorHotel', {}).then(res => {
        if (res.data.code === 200) {
          this.hotels = res.data.data;
        }
      }).catch(error => {
        console.error("查询民宿信息异常：", error);
      });
    },
    // 置位
    cannel() {
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
        title: '删除民宿房间数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/hotelRoom/batchDelete`, ids);
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
          console.error(`民宿房间信息删除异常：`, e);
        }
      }
    },
    // 修改信息
    async updateOperation() {
      try {
        this.data.cover = this.cover;
        const response = await this.$axios.put('/hotelRoom/update', this.data);
        this.clearFormData();
        this.$swal.fire({
          title: '民宿房间信息修改',
          text: response.data.msg,
          icon: response.data.code === 200 ? 'success' : 'error',
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.cannel();
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
        this.data.cover = this.cover;
        const response = await this.$axios.post('/hotelRoom/save', this.data);
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.cannel();
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
          ...this.hotelRoomQueryDto
        };
        const response = await this.$axios.post('/hotelRoom/query', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询民宿房间信息异常:', error);
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