<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 5px;">
    <el-row style="padding: 10px;margin-left: 10px;">
      <el-row>
        <el-input v-model="scenicCategoryQueryDto.name" clearable placeholder="景点分类名" size="small"
                  style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
        <span style="float: right;">
          <el-button class="customer" size="small"
                     style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                     @click="add()"><i class="el-icon-plus"></i>新增景点分类</el-button>
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 25px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="景点分类名" prop="name"></el-table-column>
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
    <el-dialog :show-close="false" :visible.sync="dialogOperation" width="25%">
      <div slot="title">
        <p class="dialog-title">{{ !isOperation ? '新增景点分类' : '修改景点分类信息' }}</p>
      </div>
      <div style="padding:0 20px;">
        <el-row style="margin-bottom: 20px;">
          <span class="dialog-hover">景点分类名</span>
          <input v-model="data.name" class="dialog-input" placeholder="景点分类名"/>
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
      scenicCategoryQueryDto: {}, // 搜索条件
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
        title: '删除景点分类数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/scenicCategory/batchDelete`, ids);
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
          console.error(`景点分类信息删除异常：`, e);
        }
      }
    },
    // 修改信息
    async updateOperation() {
      try {
        const response = await this.$axios.put('/scenicCategory/update', this.data);
        this.clearFormData();
        this.$swal.fire({
          title: '景点分类信息修改',
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
        const response = await this.$axios.post('/scenicCategory/save', this.data);
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
        // 请求参数
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          key: this.filterText,
          ...this.scenicCategoryQueryDto
        };
        const response = await this.$axios.post('/scenicCategory/query', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询景点分类信息异常:', error);
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