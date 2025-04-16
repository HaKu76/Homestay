<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 10px;">
    <el-row style="padding: 10px 5px;margin: 0 5px;">
      <el-row>
        <span style="display: inline-block;font-size: 26px;font-weight: 800;padding-left: 15px;">民宿信息</span>
        <span style="float: right;">
          <el-date-picker v-model="searchTime" end-placeholder="创建结束" range-separator="至" size="small"
                          start-placeholder="创建开始" style="width: 220px;margin-left: 5px;" type="daterange">
          </el-date-picker>
          <el-input v-model="scenicQueryDto.name" clearable placeholder="民宿名"
                    size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
            <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
          </el-input>
        </span>

      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-row v-if="tableData.length === 0">
        <el-empty description="暂无民宿信息"></el-empty>
      </el-row>
      <el-row v-else class="scenic-item">
        <el-col v-for="(hotel, index) in tableData" :key="index" :span="6" @click.native="hotelClick(hotel)">
          <div class="item">
            <img :src="hotel.cover" alt="" srcset="">
            <div class="name">{{ hotel.name }}</div>
            <div>
              <span class="address">
                <i class="el-icon-location"></i>
                {{ hotel.address }}
              </span>
            </div>
            <div class="info">
              <span class="time">{{ timeAgo(hotel) }}</span>
              <span>联系电话&nbsp;{{ hotel.contactPhone }}</span>
            </div>
            <div class="extra-info">
              <span>{{ hotel.vendorName }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[8, 16]"
                     :total="totalItems" layout="total, sizes, prev, pager, next, jumper" style="margin:10px 0;"
                     @size-change="handleSizeChange" @current-change="handleCurrentChange"></el-pagination>
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
      searchTime: [],
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    hotelClick(hotel) {
      sessionStorage.setItem('hotelInfo', JSON.stringify(hotel));
      // 跳转至民宿详情页
      this.$router.push('/hotelDetail');
    },
    timeAgo(scenic) {
      return timeAgo(scenic.createTime);
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
        const response = await this.$axios.post('/hotel/query', params);
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
  },
};
</script>
<style lang="scss" scoped>
.scenic-item {
  .item:hover {
    border-radius: 10px;
    background-color: rgb(246, 246, 247);
    border-color: rgb(214, 214, 214);
  }

  .item {
    border: 1px solid rgba(255, 255, 255, 0);
    margin-block: 10px;
    padding: 10px 10px;
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
      height: 190px;
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