<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 10px;">
    <el-row style="padding: 10px 5px;margin: 0 5px;box-sizing: border-box;">
      <el-row>
        <span style="display: inline-block;font-size: 26px;font-weight: 800;padding-left: 15px;">景点信息</span>
        <span style="float: right;">
          <el-select v-model="scenicQueryDto.categoryId" placeholder="所属类别" size="small" style="margin-left: 5px;"
                     @change="fetchFreshData">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
          <el-date-picker v-model="searchTime" end-placeholder="创建结束" range-separator="至" size="small"
                          start-placeholder="创建开始" style="width: 220px;margin-left: 5px;" type="daterange"
                          @change="fetchFreshData">
          </el-date-picker>
          <el-input v-model="scenicQueryDto.name" clearable placeholder="景点名" size="small"
                    style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
            <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
          </el-input>
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-row v-if="tableData.length === 0">
        <el-empty description="暂无景点信息"></el-empty>
      </el-row>
      <el-row v-else class="scenic-item">
        <el-col v-for="(scenic, index) in tableData" :key="index" :span="6" @click.native="scenicClick(scenic)">
          <div class="item" style="box-sizing: border-box;">
            <img :src="scenic.cover" alt="" srcset="" style="height: 166px;">
            <div class="name">{{ scenic.name }}</div>
            <div>
              <span class="address">
                <i class="el-icon-location"></i>
                {{ scenic.address }}
              </span>
            </div>
            <div class="info">
              <span class="time">{{ timeAgo(scenic) }}</span>
              <span class="save">收藏{{ strDeal(scenic.saveIds) }}</span>
              <span class="view">浏览{{ strDeal(scenic.viewIds) }}</span>
            </div>
            <div class="extra-info">
              <span><i class="el-icon-school"></i>{{ scenic.vendorName }}</span>
              <span>{{ scenic.categoryName }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[8]" :total="totalItems"
                     layout="total, sizes, prev, pager, next, jumper" style="margin:10px 0;"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"></el-pagination>
    </el-row>
  </el-row>
</template>

<script>
import {timeAgo} from "@/utils/data"

export default {
  data() {
    return {
      data: {},
      filterText: '',
      currentPage: 1,
      pageSize: 8,
      totalItems: 0,
      tableData: [],
      scenicQueryDto: {}, // 搜索条件
      categories: [],
      searchTime: [],
    };
  },
  created() {
    this.fetchFreshData();
    this.fetchCategories();
  },
  methods: {
    scenicClick(scenic) {
      sessionStorage.setItem('scenicInfo', JSON.stringify(scenic));
      // 跳转至景点详情页
      this.$router.push('/scenicDetail');
    },
    // 收藏与浏览的处理方法
    strDeal(str) {
      if (str === null) {
        return '(0)'
      }
      const saveIdsAry = str.split(',');
      return '(' + saveIdsAry.length + ')';
    },
    timeAgo(scenic) {
      return timeAgo(scenic.createTime);
    },
    // 获取景点分类
    fetchCategories() {
      this.$axios.post('/scenicCategory/query', {}).then(res => {
        if (res.data.code === 200) {
          // 插入“全部”选项
          this.categories = [
            {id: null, name: '全部'}, // id 设置为空字符串，表示不限制分类
            ...res.data.data // 原有的分类数据
          ];
        }
      }).catch(error => {
        console.error("查询景点分类信息异常：", error);
      });
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
          ...this.scenicQueryDto
        };
        // 选择全部时，清除 categoryId
        if (params.categoryId === '') {
          delete params.categoryId; // 清除 categoryId
        }
        const response = await this.$axios.post('/scenic/query', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询景点信息异常:', error);
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
  },
};
</script>
<style lang="scss" scoped>
.scenic-item {
  .item {
    border: 1px solid rgba(255, 255, 255, 0);
  }

  .item:hover {
    border-radius: 10px;
    background-color: rgb(246, 246, 247);
    border-color: rgb(214, 214, 214);
  }

  .item {
    margin-block: 10px;
    padding: 5px 10px;
    border-radius: 6px;
    box-sizing: border-box;
    cursor: pointer;

    .extra-info {
      display: flex;
      justify-content: left;
      align-items: center;
      gap: 5px;
      font-size: 12px;

      span:last-child {
        display: inline-block;
        padding: 3px 6px;
        border-radius: 3px;
        background-color: aliceblue;
        color: rgb(84, 49, 223);
      }
    }

    img {
      width: 100%;
      min-height: 120px;
      max-height: 190px;
      border-radius: 10px;
    }

    .address {
      margin-top: 10px;
      display: inline-block;
      width: 100px;
      font-size: 12px;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }

    .name {
      font-size: 20px;
      font-weight: 800;
    }

    .info {
      display: flex;
      justify-content: left;
      gap: 5px;
      margin-block: 6px;

      span {
        font-size: 12px;
      }
    }
  }

}
</style>