<template>
  <div class="container">
    <div class="item">
      <div class="name">{{ scenic.name }}</div>
      <img :src="scenic.cover" alt="" srcset="">
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
      <div style="margin-top: 20px;">
        <span v-if="!saveBtnFlag" class="save-btn" @click="saveOperation"><i class="el-icon-star-off"></i>立即收藏</span>
        <span v-else class="cancel-save-btn" @click="saveOperation"><i class="el-icon-star-on"></i>取消收藏</span>
      </div>
    </div>
    <div>
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="景点介绍详情" name="first">
          <div v-if="scenic.detail === null">
            <el-empty description="该景点暂无介绍"></el-empty>
          </div>
          <div v-else v-html="scenic.detail"></div>
        </el-tab-pane>
        <el-tab-pane label="评论" name="second">
          <Evaluations :contentId="scenic.id" contentType="SCENIC" />
        </el-tab-pane>
        <el-tab-pane label="景点路线" name="five">
          <div v-if="scenicLineList.length === 0">
            <el-empty description="暂未收录路线"></el-empty>
          </div>
          <div v-else>
            <el-carousel height="450px">
              <el-carousel-item v-for="(item, index) in scenicLineList" :key="index">
                <img :src="item.cover" alt="" srcset="" style="border-radius: 5px;height: 100%;width: 100%;">
                <h3 style="padding: 20px;font-size: 12px;color: aliceblue;position: fixed;bottom: 0;">{{ item.detail }}
                </h3>
              </el-carousel-item>
            </el-carousel>
          </div>
        </el-tab-pane>
        <el-tab-pane label="景区评分" name="third">
          <div>
            <div class="rating-info">
              <div class="total-value">
                <div v-if="ratingFlag">
                  <el-result icon="success" subTitle="" title="评分">
                    <template slot="extra">
                      <span>我的评分&nbsp;{{ myScore }}分</span>
                    </template>
                  </el-result>
                </div>
                <div v-else>
                  <div>点此处进行评分</div>
                  <el-rate v-model="scenicRating.score" show-text @change="ratingChange">
                  </el-rate>
                </div>
              </div>
              <div class="total-value">
                <el-rate v-model="scenic.ratingScore" disabled score-template="平均{value}分" show-score
                  text-color="#ff9900">
                </el-rate>
              </div>
            </div>
            <div v-for="(rating, index) in scenicRatingList" :key="index" class="rating-item">
              <div>
                <div class="info">
                  <el-avatar :size="30" :src="rating.userAvatar"></el-avatar>
                  <span>{{ rating.userName }}</span>
                </div>
                <div class="score">
                  <el-rate v-model="rating.score" disabled score-template="{value}分" show-score text-color="#ff9900">
                  </el-rate>
                </div>
                <div class="time">
                  <span>评分于&nbsp;{{ rating.createTime }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="门票列表" name="four">
          <div v-if="scenicTicketList.length === 0">
            <el-empty description="暂无门票出售"></el-empty>
          </div>
          <div v-else>
            <div v-for="(ticket, index) in scenicTicketList" :key="index" class="ticket-item">
              <div class="detail">
                {{ ticket.detail }}
              </div>
              <div class="info">
                <span class="price">￥{{ ticket.price }}</span>
                <span>数量{{ ticket.number }}张</span>
                <span class="discount">{{
                  ticket.discount === null ? '无折扣' : ticket.discount + '折'
                }}</span>
              </div>
              <div class="operation">
                <span>出票于{{ timeAgo(ticket) }}</span>
                <el-button round size="mini" @click="buyTicketDialog(ticket)">购票</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <el-dialog :show-close="false" :visible.sync="dialogTicketOperation" width="25%">
      <div slot="title">
        <p class="dialog-title">在线购票</p>
      </div>
      <div style="padding:0 20px;">
        <el-row>
          <div class="ticket-item">
            <p>*门票相关信息</p>
            <div class="detail">
              {{ ticket.detail }}
            </div>
            <div class="info">
              <span class="price">￥{{ ticket.price }}</span>
              <span>数量{{ ticket.number }}张</span>
              <span class="discount">{{ ticket.discount === null ? '无折扣' : ticket.discount + '折' }}</span>
            </div>
            <div class="operation">
              <span>出票于{{ timeAgo(ticket) }}</span>
            </div>
          </div>
          <div>
            <div>
              <span class="dialog-hover">联系人</span>
              <input v-model="data.contactPerson" class="dialog-input" placeholder="输入" />
            </div>
            <div>
              <span class="dialog-hover">联系电话</span>
              <input v-model="data.contactPhone" class="dialog-input" placeholder="输入" />
            </div>
            <div>
              <p>*购票数量</p>
              <el-input-number v-model="data.buyNumber" :max="ticket.number" :min="1" label="描述文字"></el-input-number>
            </div>
            <div class="ticket-item" style="margin-block: 20px;">
              <div class="info">
                <span>共选中{{ data.buyNumber }}张票，金额<span class="price">{{
                  moneyCount(data)
                }}元</span></span>
              </div>
            </div>
          </div>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button class="customer" size="small"
          style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
          @click="addOperation()">确认购票</el-button>
        <el-button class="customer" size="small" style="background-color: rgb(211, 241, 241);border: none;"
          @click="cancel()">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { timeAgo } from "@/utils/data"
import Evaluations from "@/components/Evaluations"

export default {
  components: { Evaluations },
  name: "ScenicDetail",
  data() {
    return {
      data: { buyNumber: 1 }, // 存储购票相关的信息
      scenic: {},
      ticket: {},
      scenicRating: {},
      myScore: 0,
      scenicRatingList: [],
      scenicTicketList: [],
      scenicLineList: [],
      dialogTicketOperation: false,
      activeName: 'first',
      ratingFlag: false,
      saveBtnFlag: false,
    }
  },
  created() {
    this.loadInfo();
  },
  methods: {
    // 查询景点路线
    queryLine(id) {
      this.$axios.post('/scenicLine/query', { scenicId: id }).then(res => {
        if (res.data.code === 200) {
          this.scenicLineList = res.data.data;
        }
      })
    },
    // 购票
    addOperation() {
      this.data.ticketId = this.ticket.id;
      this.$axios.post('/scenicTicketOrder/save', this.data).then(res => {
        if (res.data.code === 200) { // 下单成功
          this.$message.success(res.data.msg);
          this.dialogTicketOperation = false;
          // 跳到对应的门票订单页
          // console.log("下单了。跳转订单页");
          this.$router.push('/myScenicOrder');
        } else {
          this.$message.error(res.data.msg);
        }
      })
    },
    // 统计金额
    moneyCount(data) {
      // 可能没有折扣
      const discountTotal = this.ticket.discount === null ? 1 : (this.ticket.discount / 10);
      const total = data.buyNumber * this.ticket.price * discountTotal;
      // 使用 Math.ceil 取整，然后保留两位小数
      return Math.ceil(total * 100) / 100;
    },
    // 取消购票
    cancel() {
      // 关闭购票弹窗
      this.dialogTicketOperation = false;
      this.data = {};
    },
    buyTicketDialog(ticket) {
      this.ticket = ticket;
      this.dialogTicketOperation = true;
    },
    // 收藏操作
    saveOperation() {
      this.$axios.post(`/scenic/saveOperation/${this.scenic.id}`).then(res => {
        if (res.data.code === 200) {
          this.$message.success({
            message: res.data.msg,
            duration: 5000,
            showClose: true
          });
          this.saveStatus();
        }
      })
    },
    // 浏览指标的处理
    viewOperation() {
      this.$axios.post(`/scenic/viewOperation/${this.scenic.id}`).then(res => {
        if (res.data.code === 200) {
          console.log("浏览指标已经记录");
        }
      })
    },
    // 查询用户是否已经收藏了这个景点
    saveStatus() {
      this.$axios.post(`/scenic/saveStatus/${this.scenic.id}`).then(res => {
        if (res.data.code === 200) {
          this.saveBtnFlag = Boolean(res.data.data);
        }
      })
    },
    // 评分
    ratingChange(score) {
      this.$axios.post('/scenicRating/save', { scenicId: this.scenic.id, score }).then(res => {
        if (res.data.code === 200) {
          this.$message.success(res.data.msg);
        } else {
          this.$message(res.data.msg);
        }
        this.queryRatingStatus();
        this.fetchSceniRating(this.scenic.id);
      })
    },
    // 开始的时候，先查询是否已经评分，如果没有评分，显示评分的组件
    queryRatingStatus() {
      this.$axios.post('/scenicRating/queryUser', { scenicId: this.scenic.id }).then(res => {
        if (res.data.code === 200) {
          this.ratingFlag = res.data.data.length !== 0;
          // 如果评过分，存储对应的分数，做显示
          if (this.ratingFlag) {
            this.myScore = res.data.data[0].score;
          }
        }
      })
    },
    handleClick(tab, event) {
      if (tab.index === '3') {
        this.fetchSceniRating(this.scenic.id);
        this.queryRatingStatus();
      } else if (tab.index === '4') {
        this.fetchScenicTicket(this.scenic.id);
      } else if (tab.index === '2') {
        this.queryLine(this.scenic.id);
      }
    },
    // 记载景区下面的门票信息
    fetchScenicTicket(scenicId) {
      this.$axios.post('/scenicTicket/query', { scenicId: scenicId }).then(res => {
        if (res.data.code === 200) {
          // 过滤掉 useStatus 为不可用的门票
          this.scenicTicketList = res.data.data.filter(ticket => ticket.useStatus == 1);
        }
      })
    },
    // 查询景点下面的评分信息
    fetchSceniRating(scenicId) {
      this.$axios.post('/scenicRating/query', { scenicId: scenicId }).then(res => {
        if (res.data.code === 200) {
          this.scenicRatingList = res.data.data;
        }
      })
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
    loadInfo() {
      const jsonScenicInfo = sessionStorage.getItem('scenicInfo');
      this.scenic = JSON.parse(jsonScenicInfo);
      this.viewOperation();
      this.saveStatus();
    },
  }
};
</script>
<style lang="scss" scoped>
.container {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  width: 900px;
  margin: 0 auto;

  .save-btn,
  .cancel-save-btn {
    display: inline-block;
    font-size: 12px;
    padding: 6px 10px;
    border-radius: 5px;
  }

  .save-btn {
    background-color: rgb(51, 51, 51);
    color: rgb(250, 250, 250);
  }

  .save-btn:hover {
    background-color: rgb(31, 31, 31);
  }

  .cancel-save-btn:hover {
    background-color: rgb(235, 138, 138);
  }

  .cancel-save-btn {
    background-color: rgb(237, 157, 157);
    color: rgb(250, 250, 250);
  }

  .rating-info {
    min-height: 200px;
    display: flex;
    justify-content: space-around;
    background-color: rgb(246, 246, 246);

    .total-value {
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }

  .rating-item {
    margin-block: 20px;

    .info {
      display: flex;
      justify-content: left;
      align-items: center;
      gap: 5px;

      span {
        font-size: 14px;
      }
    }

    .score,
    .time {
      padding: 6px 30px;
    }

    .time {
      font-size: 12px;
    }

  }

  .ticket-item {
    background-color: rgb(246, 246, 246);
    border-radius: 5px;
    padding: 10px;
    box-sizing: border-box;
    margin-bottom: 2px;

    .detail {
      font-size: 20px;
      font-weight: 800;
      margin-block: 4px;
    }

    .info {
      font-size: 12px;
      margin-block: 10px;
      display: flex;
      justify-content: left;
      align-items: center;
      gap: 8px;
      color: rgb(83, 82, 82);

      .price {
        font-size: 18px;
        font-weight: 800;
        color: rgb(236, 118, 54);
      }
    }

    .operation {
      display: flex;
      justify-content: left;
      align-items: center;
      gap: 8px;

      span {
        font-size: 12px;
      }
    }
  }

  .item {
    margin-block: 20px;
    padding: 30px 10px;
    box-sizing: border-box;
    cursor: pointer;
    background-color: rgb(225, 245, 245);
    border-radius: 5px;

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
      width: 300px;
      min-height: 120px;
      max-height: 140px;
      border-radius: 5px;
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
      font-size: 32px;
      font-weight: 800;
      margin-bottom: 20px;
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