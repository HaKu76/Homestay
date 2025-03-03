<template>
  <div style="box-sizing: border-box;overflow-y: hidden;overflow-x: hidden;">
    <div style="padding: 5px;box-sizing: border-box;">
      <LineChart :date="userDates" :values="userValues" height="280px" tag="用户存量数据"
                 @on-selected="userDatesSelected"/>
    </div>
  </div>
</template>
<script>
import LineChart from "@/components/LineChart"

export default {
  components: {LineChart},
  data() {
    return {
      userValues: [],
      userDates: [],
      modelDates: [],
      modelValues: [],
      pieValues: [],
      pieTypes: [],
      messageList: []
    }
  },
  created() {
    // 数据较少，默认查365天
    this.userDatesSelected(365);
    // 数据较少，默认查365天
    this.modelDatesSelected(365);
    this.loadPieCharts();
    this.loadMessage();
  },
  methods: {
    // 加载消息数据
    loadMessage() {
      const messageQueryDto = {
        current: 1,
        size: 2
      }
      this.$axios.post(`/message/query`, messageQueryDto).then(response => {
        const {data} = response;
        if (data.code === 200) {
          this.messageList = data.data;
        }
      })
    },
    loadPieCharts() {
      this.$axios.get(`/views/staticControls`).then(response => {
        const {data} = response;
        if (data.code === 200) {
          this.pieValues = data.data.map(entity => entity.count);
          this.pieTypes = data.data.map(entity => entity.name);
        }
      })
    },
    modelDatesSelected(time) {
      this.$axios.get(`/user-health/daysQuery/${time}`).then(response => {
        const {data} = response;
        if (data.code === 200) {
          this.modelValues = data.data.map(entity => entity.count);
          this.modelDates = data.data.map(entity => entity.name);
        }
      })
    },
    userDatesSelected(time) {
      this.$axios.get(`/user/daysQuery/${time}`).then(response => {
        const {data} = response;
        if (data.code === 200) {
          this.userValues = data.data.map(entity => entity.count);
          this.userDates = data.data.map(entity => entity.name);
        }
      })
    },
  },
};
</script>
<style lang="scss" scoped>
.status-success {
  display: inline-block;
  padding: 1px 5px;
  border-radius: 2px;
  background-color: rgb(201, 237, 249);
  color: rgb(111, 106, 196);
  font-size: 12px;
}

.status-error {
  display: inline-block;
  padding: 1px 5px;
  border-radius: 2px;
  background-color: rgb(233, 226, 134);
  color: rgb(131, 138, 142);
  color: rgb(111, 106, 196);
  font-size: 12px;
}
</style>