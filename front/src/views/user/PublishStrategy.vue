<template>
  <div style="background-color: rgb(249,249,249);">
    <div
        style="background-color: rgb(255,255,255);min-width: 700px;max-width: 900px;margin: 0 auto;padding: 20px 10px;">
      <div>
        <p>*标题</p>
        <input v-model="data.title" class="dialog-input" placeholder="攻略标题" style="line-height: 45px;"/>
      </div>
      <div>
        <p>*关联景点</p>
        <el-select v-model="data.scenicId" placeholder="关联景点" size="small">
          <el-option v-for="item in scenics" :key="item.id" :label="item.name" :value="item.id">
          </el-option>
        </el-select>
      </div>
      <div style="margin-block: 10px;">
        <p>*攻略封面</p>
        <el-upload :on-success="handleCoverSuccess" :show-file-list="false" action="/api/Homestay-sys/v1.0/file/upload"
                   class="avatar-uploader">
          <img v-if="cover" :src="cover" style="width: 200px;height: 140px;">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </div>
      <div>
        <p>*攻略详情</p>
        <Editor :receiveContent="receiveContent" height="400px" @on-receive="onReceive"/>
      </div>
      <div style="margin-top: 20px;text-align: center;">
        <el-button class="customer" round size="mini" type="primary" @click="postInfo">立即发布</el-button>
        <el-button class="customer" round size="mini" @click="cancelPublish">取消发布</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import Editor from "@/components/Editor"

export default {
  components: {Editor},
  name: "PublishStrategy",
  data() {
    return {
      data: {},
      cover: '',
      scenics: [],
      receiveContent: ''
    }
  },
  created() {
    this.fetchScenics();
  },
  methods: {
    // 攻略发布
    async postInfo() {
      try {
        this.data.cover = this.cover;
        const {data} = await this.$axios.post('/scenicStrategy/save', this.data);
        if (data.code === 200) {
          this.$message.success({
            message: '发布成功，请等待审核',
            duration: 1000 // 设置显示时间为 1000 毫秒（1 秒）
          });
          this.$router.go(-1); // 返回上一个页面
        }
      } catch (error) {
        this.$message.error(error);
      }
    },
    // 取消发布
    cancelPublish() {
      this.$router.go(-1); // 返回上一级路由
    },
    // 富文本编辑器内容回调函数
    onReceive(content) {
      this.data.content = content;
    },
    // 查询全部的景点信息
    async fetchScenics() {
      try {
        const {data} = await this.$axios.get('/scenic/querySelectedScenic');
        if (data.code === 200) {
          this.scenics = data.data;
        }
      } catch (error) {
        this.$message.error(error);
      }
    },
    // 封面上传成功时回调函数
    handleCoverSuccess(res, file) {
      this.$notify({
        duration: 1500,
        title: '封面上传',
        message: res.code === 200 ? '上传成功' : '上传失败',
        type: res.code === 200 ? 'success' : 'error'
      });
      // 上传成功则更新封面
      if (res.code === 200) {
        this.cover = res.data;
      }
    },
  }
};
</script>

<style lang="scss" scoped></style>