<template>
  <div class="text">
    <div style="font-size: 28px;margin-block: 20px;font-weight: 800;">{{ notice.title }}</div>
    <div style="font-size: 14px;">{{ notice.createTime }}</div>
    <div style="width: 60%;margin: 0 auto;">
      <div style="font-size: 14px;" v-html="notice.content"></div>
    </div>

  </div>
</template>
<script>
export default {
  name: "NoticeDetail",
  data() {
    return {
      notice: {}
    }
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      const jsonNotice = sessionStorage.getItem('noticeInfo');
      this.notice = JSON.parse(jsonNotice);
    },
  }
};
</script>
<style lang="scss" scoped>
.text{
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  width: 900px;
  margin: 0 auto;
}
</style>
