<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 5px;">
    <el-row style="padding: 10px 5px;margin: 0 5px;">
      <el-row>
        <el-select v-model="scenicStrategyQueryDto.isAudit" placeholder="审核状态" size="small"
                   style="margin-left: 5px;" @change="fetchFreshData">
          <el-option v-for="item in isAuditList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-date-picker v-model="searchTime" end-placeholder="创建结束" range-separator="至" size="small"
                        start-placeholder="创建开始" style="width: 220px;margin-left: 5px;" type="daterange">
        </el-date-picker>
        <el-input v-model="scenicStrategyQueryDto.title" clearable
                  placeholder="攻略标题" size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;"
                  @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="攻略图" prop="cover" width="120px">
          <template slot-scope="scope">
            <img :src="scope.row.cover" style="width: 88px;height: 55px;border-radius: 5px;"/>
          </template>
        </el-table-column>
        <el-table-column label="攻略标题" prop="title"></el-table-column>
        <el-table-column label="发表者ID" prop="userId" sortable width="110px"></el-table-column>
        <el-table-column label="发表者" prop="userName" width="110px"></el-table-column>
        <el-table-column label="景点ID" prop="scenicId" sortable width="90px"></el-table-column>
        <el-table-column label="关联景点" prop="scenicName" width="120px"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" sortable width="168"></el-table-column>
        <el-table-column label="审核状态" prop="isWord" width="108">
          <template slot-scope="scope">
            <i v-if="!scope.row.isAudit" class="el-icon-warning" style="margin-right: 5px;"></i>
            <i v-else class="el-icon-success" style="margin-right: 5px;color: rgb(253, 199, 50);"></i>
            <el-tooltip v-if="!scope.row.isAudit" class="item" content="如果未审核，不显示于用户端" effect="dark"
                        placement="bottom-end">
              <span style="text-decoration: underline;text-decoration-style: dashed;">未审核</span>
            </el-tooltip>
            <span v-else>已审核</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <span v-if="!scope.row.isAudit" class="text-button" @click="auditStrategy(scope.row)">审核</span>
            <span class="text-button" @click="viewStrategyContent(scope.row)">查看攻略</span>
            <span class="text-button" @click="handleDelete(scope.row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[10, 20]"
                     :total="totalItems" layout="total, sizes, prev, pager, next, jumper" style="margin:10px 0;"
                     @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
    </el-row>
    <el-drawer :direction="direction" :visible.sync="drawer" title="查看内容" width="35%">
      <div style="margin: 20px;" v-html="data.content"></div>
    </el-drawer>
  </el-row>
</template>

<script>
export default {
  data() {
    return {
      data: {},
      filterText: '',
      cover: '',
      currentPage: 1,
      drawer: false, // 控制攻略路线的抽屉开关
      direction: 'rtl', // right to left
      pageSize: 10,
      totalItems: 0,
      tableData: [],
      selectedRows: [],
      scenicStrategyQueryDto: {}, // 搜索条件
      searchTime: [],
      isAuditList: [{value: null, label: '全部'}, {value: 0, label: '未审核'}, {value: 1, label: '已审核'}],
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    viewStrategyContent(scenicStrategy) {
      this.data = scenicStrategy;
      this.drawer = true;
    },
    // 审核策略
    async auditStrategy(scenicStrategy) {
      const confirmed = await this.$swalConfirm({
        title: '攻略审核',
        text: `是否审核通过【${scenicStrategy.title}】攻略`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          const response = await this.$axios.post(`/scenicStrategy/audit/${scenicStrategy.id}`, {});
          if (response.data.code === 200) {
            this.$swal.fire({
              title: '审核结果',
              text: response.data.msg,
              icon: 'success',
              showConfirmButton: false,
              timer: 1000,
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
          console.error(`攻略审核异常：`, e);
        }
      }
    },
    // 删除攻略信息
    deleteLine(scenicLine) {
      this.$confirm('此操作将删除该景点路线, 是否继续?', '操作提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let ids = [scenicLine.id];
        this.$axios.post(`/scenicStrategyStrategy/batchDelete`, ids).then(res => {
          if (res.data.code === 200) {
            this.$message.success('攻略路线删除成功');
            this.fetchScenicLines(this.data.id);
          }
        })
      }).catch(() => {
        console.log("操作取消");
      });
    },
    // 批量删除数据
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`未选中任何数据`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: '删除攻略数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/scenicStrategy/batchDelete`, ids);
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
          console.error(`攻略信息删除异常：`, e);
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
          ...this.scenicStrategyQueryDto
        };
        const response = await this.$axios.post('/scenicStrategy/query', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询攻略信息异常:', error);
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