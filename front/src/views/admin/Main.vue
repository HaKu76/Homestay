<template>
  <div style="box-sizing: border-box;overflow-y: hidden;overflow-x: hidden;">
    <el-row>
      <el-col :span="16">
        <div>
          <div>
            <el-row style="margin-block: 20px;">
              <el-col v-for="(staticItem, index) in staticData" :key="index" :span="4">
                <div class="static-item">
                  <el-statistic :precision="0" :title="staticItem.name" :value="staticItem.count"
                                group-separator=","></el-statistic>
                </div>
              </el-col>
            </el-row>
          </div>
          <div>
            <div class="line">
              <LineChart :date="ticketMoneyDates" :values="ticketMoneyValues" height="280px"
                         tag="全站景点门票销售额"
                         @on-selected="ticketSelected"/>
            </div>
            <div class="line">
              <LineChart :date="hotelRoomMoneyDates" :values="hotelRoomMoneyValues" height="280px"
                         tag="全站民宿销售额"
                         @on-selected="hotelOrderSelected"/>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="buttom">
          <p>最新攻略</p>
          <div v-for="(strategy, index) in scenicStrategies" :key="index" class="strategy-item"
               @click="readStrategy(strategy)">
            <div class="left">
              <img :alt="strategy.title" :src="strategy.cover" srcset="">
            </div>
            <div class="right">
              <div class="user">
                <img :src="strategy.userAvatar" alt="" srcset="">
                <span>{{ strategy.userName }}</span>
              </div>
              <div class="title">{{ strategy.title }}</div>
              <div class="info">
                <span>{{ strategy.scenicName }}</span>
                <span>{{ timeOut(strategy.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import LineChart from "@/components/LineChart"
import {timeAgo} from "@/utils/data"

export default {
  components: {LineChart},
  data() {
    return {
      staticData: [],
      scenicStrategies: [],
      ticketMoneyValues: [],
      ticketMoneyDates: [],
      hotelRoomMoneyValues: [],
      hotelRoomMoneyDates: [],
      defaultQueryDays: 365, // 由于数据太少，直接查一年来测试
    }
  },
  created() {
    this.fentchStaticCount();
    this.fentchStrategy();
    this.ticketSelected(this.defaultQueryDays);
    this.hotelOrderSelected(this.defaultQueryDays);
  },
  methods: {
    timeOut(time) {
      return timeAgo(time);
    },
    // 查询指定时间内的门票销售额记录
    hotelOrderSelected(day) {
      this.$axios.get(`/hotelOrderInfo/daysQueryMoney/${day}`).then(res => {
        if (res.data.code === 200) {
          // 只取出里面的金额，自成一个数组
          this.hotelRoomMoneyValues = res.data.data.map(money => money.count);
          // 只取出里面的日期，自成一个数组
          this.hotelRoomMoneyDates = res.data.data.map(money => money.name);
        }
      });
    },
    // 查询指定时间内的门票销售额记录
    ticketSelected(day) {
      this.$axios.get(`/scenicTicketOrder/daysQueryMoney/${day}`).then(res => {
        if (res.data.code === 200) {
          // 只取出里面的金额，自成一个数组
          this.ticketMoneyValues = res.data.data.map(money => money.count);
          // 只取出里面的日期，自成一个数组
          this.ticketMoneyDates = res.data.data.map(money => money.name);
        }
      });
    },
    readStrategy(strategy) {
      sessionStorage.setItem('strategyId', strategy.id);
      this.$router.push('/seeStrategyDetail');
    },
    // 查询最新发布的攻略数据
    fentchStrategy() {
      const strategyQuery = {
        current: 1,
        size: 5
      }
      this.$axios.post('/scenicStrategy/queryList', strategyQuery).then(res => {
        if (res.data.code === 200) {
          this.scenicStrategies = res.data.data;
        }
      });
    },
    // 查询静态数据
    fentchStaticCount() {
      this.$axios.get('/views/staticControls').then(res => {
        if (res.data.code === 200) {
          this.staticData = res.data.data;
        }
      });
    },
  },
};
</script>
<style lang="scss" scoped>
// 折线图
.line {
  padding: 20px 60px;
  margin-top: 5px;
  box-sizing: border-box;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 10px;
}

.buttom {
  border-radius: 10px;
  padding: 5px 20px;
  box-sizing: border-box;

  .strategy-item {
    display: flex;
    justify-content: left;
    padding: 10px 0;
    cursor: pointer;

    .left {
      padding: 5px;
      box-sizing: border-box;

      img {
        width: 138px;
        height: 80px;
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
        width: 100px;
        font-weight: 800;
        padding-bottom: 6px;
        white-space: nowrap;
        /* 防止文本换行 */
        overflow: hidden;
        /* 隐藏超出容器的文本 */
        text-overflow: ellipsis;
        /* 使用省略号表示被截断的文本 */
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
}
</style>