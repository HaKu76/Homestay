<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 10px;">
    <el-row style="padding: 10px 5px;margin: 0 5px;">
      <el-row>
        <span style="display: inline-block;font-size: 26px;font-weight: 800;padding-left: 15px;">攻略大全</span>
        <span style="float: right;">
          <el-input v-model="scenicStrategyQueryDto.title" clearable
                    placeholder="攻略标题" size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;"
                    @clear="handleFilterClear">
            <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
          </el-input>
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">

      <div>
        <div v-for="(strategy, index) in tableData" :key="index" class="strategy-item" @click="readStrategy(strategy)">
          <div class="left">
            <img :alt="strategy.title" :src="strategy.cover" srcset="">
          </div>
          <div class="right">
            <div class="user">
              <img :src="strategy.userAvatar" alt="" srcset="">
              <span>{{ strategy.userName }}</span>
            </div>
            <div class="title">{{ strategy.title }}</div>
            <div class="detail">{{ strategy.detail }}</div>
            <div class="info">
              <span>{{ strategy.scenicName }}</span>
              <span>{{ strategy.createTime }}</span>
            </div>
          </div>
        </div>
      </div>

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
    readStrategy(strategy) {
      sessionStorage.setItem('strategyId', strategy.id);
      this.$router.push('/strategyDetail');
    },
    viewStrategyContent(scenicStrategy) {
      this.data = scenicStrategy;
      this.drawer = true;
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
        const response = await this.$axios.post('/scenicStrategy/queryList', params);
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
      // 将当前对象的信息存储起来
      sessionStorage.setItem('strategyInfo', JSON.stringify(row));
      this.$router.push('/editStrategy');
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    }
  },
};
</script>
<style lang="scss" scoped>
.strategy-item {
  display: flex;
  justify-content: left;
  padding: 10px 0;
  cursor: pointer;

  .left {
    padding: 5px;
    box-sizing: border-box;

    img {
      width: 238px;
      height: 160px;
      border-radius: 10px;
    }
  }

  .right {
    padding: 5px;
    box-sizing: border-box;

    .user {
      display: flex;
      justify-content: left;
      align-items: center;
      margin-block: 4px;

      img {
        width: 20px;
        height: 20px;
        border-radius: 10px;
      }

      span {
        margin-left: 4px;
        font-size: 14px;
      }
    }

    .title {
      font-size: 20px;
      font-weight: 800;
      padding-bottom: 6px;
    }

    .title:hover {
      text-decoration: underline;
      text-decoration-style: dashed;
    }

    .detail {
      font-size: 14px;
      color: rgb(131, 130, 130);
    }

    .info {
      font-size: 12px;
      margin-top: 10px;
      display: flex;
      justify-content: left;
      gap: 10px;

      span:first-child {
        display: inline-block;
        padding: 1px 3px;
        border-radius: 2px;
        background-color: rgb(237, 243, 249);
        color: rgb(136, 115, 233);
      }
    }
  }

}
</style>