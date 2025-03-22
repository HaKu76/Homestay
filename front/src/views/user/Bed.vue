<template>
  <div>
    <div v-if="bedOperation" style="padding: 20px;">
      <div>
        <span class="dialog-hover">床位号</span>
        <input v-model="data.number" class="dialog-input" placeholder="输入"/>
        <el-row style="margin: 20px 0;">
          <div>
            <span class="dialog-hover">床位状态</span>
          </div>
          <el-switch v-model="data.status" active-color="#13ce66" active-text="可用" inactive-color="#ff4949"
                     inactive-text="不可用">
          </el-switch>
        </el-row>
      </div>
      <div style="display: flex;justify-content: center;align-items: center;">
        <el-button v-if="!isOperation" class="customer"
                   size="small" style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;"
                   type="info" @click="addOperation()">新增
        </el-button>
        <el-button v-else class="customer"
                   size="small" style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;"
                   type="info" @click="updateOperation()">修改
        </el-button>
      </div>
    </div>
    <div style="display: flex;justify-content: center;align-items: center;margin: 10px;">
      <el-button v-if="beds.length !== 0" size="mini" style="width: 100%;" type="info"
                 @click="createBed">创建床位信息
      </el-button>
    </div>

    <div v-if="beds.length === 0">
      <el-empty>
        <el-button type="info" @click="createBed">创建床位信息</el-button>
      </el-empty>
    </div>
    <div v-else style="padding: 10px;">
      <div v-for="(bed, index) in beds" :key="index" class="bed-item">
        <div style="font-size: 18px;">
                    <span style="margin-right: 5px;">
                        <i class="el-icon-table-lamp"></i>
                        床位号：{{ bed.number }}
                    </span>
          <span>{{ bed.status ? '状态可用' : '状态不可用' }}</span>
        </div>
        <div style="margin: 8px 6px;font-size: 12px;">
          创建时间：{{ bed.createTime }}
        </div>
        <div>
          <el-button circle icon="el-icon-edit" size="mini" style="margin-right: 10px;"
                     type="primary" @click="updateBedInfo(bed)"></el-button>
          <el-popconfirm title="确定删除床位信息？" @confirm="deleteBed(bed)">
            <el-button slot="reference" circle icon="el-icon-delete" size="mini" type="danger"></el-button>
          </el-popconfirm>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    roomId: {
      type: Number,
      default: ''
    },
  },
  watch: {
    roomId(v1, v2) {
      console.log("房间ID：", v1);
      this.fetchBeds(v1);
    },
  },
  data() {
    return {
      data: {},
      beds: [],
      bedOperation: false, // 控制床位新增/编辑的开关变量
      isOperation: false, // false: 新增；true： 修改
    }
  },
  created() {
    this.fetchBeds();
  },
  methods: {
    async deleteBed(bed) {
      let ids = [bed.id]
      const response = await this.$axios.post(`/hotelRoomBed/batchDelete`, ids);
      if (response.data.code === 200) {
        this.cannel();
        this.fetchBeds();

      }
    },
    createBed() {
      this.data = {};
      this.bedOperation = true;
    },
    updateBedInfo(bed) {
      this.data = bed;
      this.bedOperation = true;
      this.isOperation = true;
    },
    // 床位新增
    addOperation() {
      const hotelRoomBed = {
        roomId: this.roomId,
        ...this.data
      }
      this.$axios.post('/hotelRoomBed/save', hotelRoomBed).then(res => {
        if (res.data.code === 200) {
          // 关闭操作区域
          this.bedOperation = false;
          this.$message.success('床位新增成功');
          this.fetchBeds();
        }
      }).catch(error => {
        console.error("查询床位信息异常:", error);

      })
    },
    // 床位修改
    updateOperation() {
      this.$axios.put('/hotelRoomBed/update', this.data).then(res => {
        if (res.data.code === 200) {
          // 关闭操作区域
          this.bedOperation = false;
          this.isOperation = false;
          this.$message.success('床位修改成功');
          this.fetchBeds();
        }
      }).catch(error => {
        console.error("查询床位信息异常:", error);

      })
    },
    // 取消事件
    cannel() {
      this.data = {};
      this.bedOperation = false;
      this.isOperation = false;
    },
    // 加载对应的床位信息
    fetchBeds() {
      const hotelRoomBedQueryDto = {
        roomId: this.roomId
      }
      this.$axios.post('/hotelRoomBed/query', hotelRoomBedQueryDto).then(res => {
        if (res.data.code === 200) {
          this.beds = res.data.data;
        }
      }).catch(error => {
        console.error("查询床位信息异常:", error);

      })
    },
  },
};
</script>
<style lang="scss" scoped>
.bed-item {
  background-color: rgb(248, 248, 248);
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 4px;
}
</style>