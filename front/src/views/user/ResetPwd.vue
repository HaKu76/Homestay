<template>
  <div class="container">
    <!-- 原始密码 -->
    <div>
      <p>*原始密码</p>
      <input v-model="oldPwd" class="dialog-input" placeholder="请输入" style="line-height: 40px;" type="password"/>
    </div>
    <!-- 新密码 -->
    <div>
      <p>*新密码</p>
      <input v-model="newPwd" class="dialog-input" placeholder="请输入" style="line-height: 40px;" type="password"/>
    </div>
    <!-- 确认密码 -->
    <div>
      <p>*确认密码</p>
      <input v-model="againPwd" class="dialog-input" placeholder="请输入" style="line-height: 40px;" type="password"/>
    </div>
    <div>
      <div style="margin-top: 20px;text-align: center;">
        <el-button class="customer" round size="mini" type="primary" @click="postInfo">立即修改</el-button>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "ResetPwd",
  data() {
    return {
      oldPwd: '',
      newPwd: '',
      againPwd: ''
    }
  },
  methods: {
    // 提交密码信息
    async postInfo() {
      const userUpdatePwdDTO = {
        oldPwd: this.$md5(this.$md5(this.oldPwd)),
        newPwd: this.$md5(this.$md5(this.newPwd)),
        againPwd: this.$md5(this.$md5(this.againPwd))
      }
      const {data} = await this.$axios.put('/user/updatePwd', userUpdatePwdDTO);
      this.$notify({
        duration: 1000,
        title: '修改密码',
        message: data.code === 200 ? '修改成功，请重新登录' : data.msg,
        type: data.code === 200 ? 'success' : 'error'
      });
      if (data.code === 200) {
        this.$router.push('/');
      }
    },
  }
};
</script>
<style lang="scss" scoped>
.container {
  width: 500px;
  margin: 0 auto;
}
</style>
