<template>
  <div class="container">
    <div>
      <div style="font-size: 32px;margin-block: 10px;font-weight: 800;">{{ strategy.title }}</div>
      <div class="user">
        <img :src="strategy.userAvatar" alt="" srcset="">
        <span>{{ strategy.userName }}</span>
      </div>
      <div class="info">
        <img :src="strategy.cover" alt="" srcset="">
        <span>{{ strategy.scenicName }}</span>
        <span>{{ strategy.createTime }}</span>
      </div>
      <div style="font-size: 14px;" v-html="strategy.content"></div>
      <div>
        <Evaluations :contentId="strategyId" contentType="STRATEGY"/>
      </div>
    </div>
  </div>
</template>
<script>
import Evaluations from "@/components/Evaluations"

export default {
  components: {Evaluations},
  name: "StrategyDetail",
  data() {
    return {
      strategy: {},
      strategyId: null
    }
  },
  created() {
    const strategyId = sessionStorage.getItem('strategyId');
    this.strategyId = Number(strategyId);
    this.fetchStrategy(strategyId);
  },
  methods: {
    // 通过攻略ID查询攻略信息
    fetchStrategy(strategyId) {
      this.$axios.post('/scenicStrategy/query', {id: strategyId}).then(res => {
        if (res.data.code === 200) {
          this.strategy = res.data.data[0];
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.container {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
}
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

.info {
  font-size: 12px;
  display: flex;
  flex-direction: column;
  /* 改为垂直布局 */
  align-items: center;
  /* 图片和文字居中 */
  margin-block: 20px;
  gap: 10px;

  img {
    width: 100%;
    /* 图片宽度占满父容器 */
    max-width: 600px;
    /* 限制图片最大宽度 */
    height: auto;
    /* 高度自适应 */
    border-radius: 8px;
    /* 添加圆角 */
    margin-bottom: 10px;
    /* 图片与文字之间的间距 */
  }

  span:nth-child(2) {
    display: inline-block;
    padding: 1px 3px;
    border-radius: 2px;
    background-color: rgb(237, 243, 249);
    color: rgb(136, 115, 233);
  }
}
</style>
