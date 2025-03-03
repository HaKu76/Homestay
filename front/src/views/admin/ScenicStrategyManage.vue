<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 5px;">
    <el-row style="padding: 10px 5px;margin: 0 5px;">
      <el-row>
        <el-select @change="fetchFreshData" size="small" v-model="scenicStrategyQueryDto.categoryId" style="margin-left: 5px;"
          placeholder="所属类别">
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id">
          </el-option>
        </el-select>
        <el-select @change="fetchFreshData" size="small" v-model="scenicStrategyQueryDto.status" style="margin-left: 5px;"
          placeholder="攻略状态">
          <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-date-picker size="small" style="width: 220px;margin-left: 5px;" v-model="searchTime" type="daterange"
          range-separator="至" start-placeholder="创建开始" end-placeholder="创建结束">
        </el-date-picker>
        <el-input size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;" v-model="scenicStrategyQueryDto.name"
          placeholder="攻略名" clearable @clear="handleFilterClear">
          <el-button slot="append" @click="handleFilter" icon="el-icon-search"></el-button>
        </el-input>
        <span style="float: right;">
          <el-button size="small" style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;"
            class="customer" type="info" @click="add()"><i class="el-icon-plus"></i>新增攻略</el-button>
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="cover" label="攻略封面" width="120px">
          <template slot-scope="scope">
            <img :src="scope.row.cover" style="width: 88px;height: 55px;border-radius: 5px;" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="攻略名"></el-table-column>
        <el-table-column prop="address" label="所在地" width="160px"></el-table-column>
        <el-table-column prop="categoryName" label="所属分类" width="120px"></el-table-column>
        <el-table-column prop="vendorName" label="供应商" width="120px"></el-table-column>
        <el-table-column prop="rating" label="评分" width="60px">
          <template slot-scope="scope">
            <span>{{ scope.row.rating === null ? 0 : scope.row.rating }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" sortable width="168" label="创建时间"></el-table-column>
        <el-table-column prop="isWord" width="108" label="状态">
          <template slot-scope="scope">
            <i v-if="!scope.row.status" style="margin-right: 5px;" class="el-icon-warning"></i>
            <i v-else style="margin-right: 5px;color: rgb(253, 199, 50);" class="el-icon-success"></i>
            <el-tooltip v-if="scope.row.status" class="item" effect="dark" content="如果是不可用状态，暂停攻略服务"
              placement="bottom-end">
              <span style="text-decoration: underline;text-decoration-style: dashed;">可用</span>
            </el-tooltip>
            <span v-else>不可用</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <span class="text-button" @click="scenicStrategyHandle(scope.row)">攻略路线</span>
            <span class="text-button" @click="handleEdit(scope.row)">编辑</span>
            <span class="text-button" @click="handleDelete(scope.row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin:10px 0;" @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="currentPage" :page-sizes="[10, 20]" :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="totalItems"></el-pagination>
    </el-row>
    <!-- 新增、修改操作面板 -->
    <el-dialog :show-close="false" :visible.sync="dialogOperation" width="32%">
      <div slot="title">
        <p class="dialog-title">{{ !isOperation ? '新增攻略' : '修改攻略信息' }}</p>
      </div>
      <div style="padding:0 20px;">
        <el-row>
          <span class="dialog-hover">攻略封面</span>
          <el-upload class="avatar-uploader" action="/api/Homestay-sys/v1.0/file/upload" :show-file-list="false"
            :on-success="handleCoverSuccess">
            <img v-if="cover" :src="cover" style="width: 250px;height: 150px;">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row style="margin-bottom: 20px;">
          <span class="dialog-hover">攻略名称</span>
          <input class="dialog-input" v-model="data.name" placeholder="请输入" />
          <span class="dialog-hover">攻略所在地址</span>
          <input class="dialog-input" v-model="data.address" placeholder="请输入" />
          <div>
            <span class="dialog-hover">攻略状态</span>
            <el-switch v-model="data.status" active-text="可用" inactive-text="不可用">
            </el-switch>
          </div>
          <div style="margin: 10px 0;">
            <span class="dialog-hover">攻略分类</span>
            <el-select v-model="data.categoryId" placeholder="请选择分类">
              <el-option v-for="category in categoryList" :key="category.id" :label="category.name"
                :value="category.id"></el-option>
            </el-select>
          </div>
          <div style="margin: 10px 0;">
            <span class="dialog-hover">供应商</span>
            <el-select v-model="data.vendorId" placeholder="请选择">
              <el-option v-for="vendor in vendorList" :key="vendor.id" :label="vendor.name"
                :value="vendor.id"></el-option>
            </el-select>
          </div>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" v-if="!isOperation"
          style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" class="customer" type="info"
          @click="addOperation()">新增</el-button>
        <el-button size="small" v-else style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;"
          class="customer" type="info" @click="updateOperation()">修改</el-button>
        <el-button class="customer" size="small" style="background-color: rgb(211, 241, 241);border: none;"
          @click="cannel()">取消</el-button>
      </span>
    </el-dialog>
    <el-dialog :show-close="false" :visible.sync="dialogLineOperation" width="28%">
      <div slot="title">
        <p class="dialog-title">{{ !isLineOperation ? '新增攻略路线' : '修改攻略路线信息' }}</p>
      </div>
      <div style="padding:0 20px;">
        <el-row>
          <span class="dialog-hover">路线封面</span>
          <el-upload class="avatar-uploader" action="/api/Homestay-sys/v1.0/file/upload" :show-file-list="false"
            :on-success="handleCoverSuccess">
            <img v-if="cover" :src="cover" style="width: 250px;height: 150px;">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row style="margin-bottom: 20px;">
          <div>
            <span class="dialog-hover">攻略简介</span>
          </div>

          <el-input style="width: 100%;" type="textarea" :rows="2" placeholder="请输入内容" v-model="scenicStrategy.detail">
          </el-input>
          <div>
            <span class="dialog-hover">优先级</span>
          </div>

          <el-slider v-model="scenicStrategy.level"></el-slider>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" v-if="!isLineOperation"
          style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" class="customer" type="info"
          @click="addLineOperation()">新增路线</el-button>
        <el-button size="small" v-else style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;"
          class="customer" type="info" @click="updateLineOperation()">修改路线</el-button>
        <el-button class="customer" size="small" style="background-color: rgb(211, 241, 241);border: none;"
          @click="cannelLine()">取消</el-button>
      </span>
    </el-dialog>
    <el-drawer title="攻略路线" width="35%" :visible.sync="drawer" :direction="direction">
      <div class="add-line">
        <span @click="addLine" style="cursor: pointer;font-size: 14px">
          <i class="el-icon-plus"></i>
          添加路线
        </span>
      </div>
      <div v-if="scenicStrategys.length === 0">
        <el-empty description="暂无路线信息"></el-empty>
      </div>
      <div v-else>
        <div>
          <el-timeline>
            <el-timeline-item v-for="(scenicStrategy, index) in scenicStrategys" :key="index" :timestamp="scenicStrategy.createTime"
              placement="top">
              <el-card>
                <div>
                  <img style="width: 100%;height: 200px;border-radius: 5px;" :src="scenicStrategy.cover" alt="路线封面">
                </div>
                <p style="margin-bottom: 8px;font-size: 14px;color: rgb(97, 97, 97);">{{
                  scenicStrategy.detail }}
                </p>
                <div>
                  <el-tooltip class="item" effect="dark" content="修改攻略路线" placement="bottom">
                    <el-button @click="updateLine(scenicStrategy)" type="primary" icon="el-icon-edit" circle></el-button>
                  </el-tooltip>
                  <el-tooltip class="item" effect="dark" content="删除攻略路线" placement="bottom">
                    <el-button @click="deleteLine(scenicStrategy)" type="danger" icon="el-icon-delete" circle></el-button>
                  </el-tooltip>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-drawer>
  </el-row>
</template>

<script>
export default {
  data() {
    return {
      scenicStrategy: {},
      data: {},
      filterText: '',
      cover: '',
      currentPage: 1,
      drawer: false, // 控制攻略路线的抽屉开关
      direction: 'rtl', // right to left
      pageSize: 10,
      totalItems: 0,
      dialogLineOperation: false, // 攻略路线弹窗确认
      isLineOperation: false, // 攻略路线操作时显示文本开关
      dialogOperation: false, // 开关
      isOperation: false, // 开关-标识新增或修改
      tableData: [],
      selectedRows: [],
      scenicStrategyQueryDto: {}, // 搜索条件
      searchTime: [],
      scenicStrategys: [],
      categoryList: [],
      vendorList: [],
      isAuditList: [{ value: null, label: '全部' }, { value: 0, label: '未审核' }, { value: 1, label: '已审核' }],
      statusList: [{ value: null, label: '全部' }, { value: 0, label: '不可用' }, { value: 1, label: '可用' }],
    };
  },
  created() {
    this.fetchFreshData();
    this.fetchVendorList();
    this.fetchCategoryList();
  },
  methods: {
    // 修改攻略信息
    updateLine(scenicStrategy) {
      this.dialogLineOperation = true;
      this.isLineOperation = true;
      this.cover = scenicStrategy.cover;
      this.scenicStrategy = scenicStrategy;
    },
    // 删除攻略信息
    deleteLine(scenicStrategy) {
      this.$confirm('此操作将删除该景区路线, 是否继续?', '操作提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let ids = [scenicStrategy.id];
        this.$axios.post(`/scenicStrategy/batchDelete`, ids).then(res => {
          if (res.data.code === 200) {
            this.$message.success('攻略路线删除成功');
            this.fetchscenicStrategys(this.data.id);
          }
        })
      }).catch(() => {
        console.log("操作取消");
      });
    },
    cannelLine() {
      this.scenicStrategy = {};
      this.dialogLineOperation = false;
      this.isLineOperation = false;
      this.cover = '';
    },
    addLine() {
      this.dialogLineOperation = true;
    },
    scenicStrategyHandle(scenic) {
      this.data = scenic;
      this.scenicStrategy.scenicId = scenic.id;
      this.fetchscenicStrategys(scenic.id);
    },
    // 请求攻略路线信息
    fetchscenicStrategys(scenicId) {
      const scenicStrategyQueryDto = { scenicId: scenicId }
      this.$axios.post('/scenicStrategy/query', scenicStrategyQueryDto).then(res => {
        if (res.data.code === 200) {
          this.scenicStrategys = res.data.data;
          this.drawer = true;
        }
      }).catch(error => {
        this.$message.error('查询攻略路线信息异常');
        console.error("查询攻略路线信息异常：", error);
      });
    },
    // 封面上传
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
    // 获取攻略分类
    fetchCategoryList() {
      this.$axios.post('/scenicCategory/query', {}).then(res => {
        if (res.data.code === 200) {
          this.categoryList = res.data.data;
        }
      }).catch(error => {
        console.error("查询攻略分类信息异常：", error);
      });
    },
    // 获取供应商信息
    fetchVendorList() {
      this.$axios.post('/vendor/query', {}).then(res => {
        if (res.data.code === 200) {
          this.vendorList = res.data.data;
        }
      }).catch(error => {
        console.error("查询供应商信息异常：", error);
      });
    },
    // 置位
    cannel() {
      this.data = {};
      this.cover = '';
      this.dialogOperation = false;
      this.isOperation = false;
    },
    // 批量删除数据
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`未选中任何数据`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: '删除攻略数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/scenicStrategy/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: '删除提示',
              text: response.data.msg,
              icon: 'success',
              showConfirmButton: false,
              timer: 2000,
            });
            this.fetchFreshData();
            return;
          }
        } catch (e) {
          this.$swal.fire({
            title: '错误提示',
            text: e,
            icon: 'error',
            showConfirmButton: false,
            timer: 2000,
          });
          console.error(`攻略信息删除异常：`, e);
        }
      }
    },
    // 修改路线信息
    async updateLineOperation() {
      try {
        this.scenicStrategy.cover = this.cover;
        const response = await this.$axios.put('/scenicStrategy/update', this.scenicStrategy);
        this.$swal.fire({
          title: '攻略路线信息修改',
          text: response.data.msg,
          icon: response.data.code === 200 ? 'success' : 'error',
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.fetchscenicStrategys(this.data.id);
          this.cannelLine();
        }
      } catch (error) {
        console.error('提交表单时出错:', error);
        this.$message.error('提交失败，请稍后再试！');
      }
    },
    // 修改信息
    async updateOperation() {
      try {
        this.data.cover = this.cover;
        const response = await this.$axios.put('/scenicStrategy/update', this.data);
        this.clearFormData();
        this.$swal.fire({
          title: '攻略信息修改',
          text: response.data.msg,
          icon: response.data.code === 200 ? 'success' : 'error',
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.cannel();
          this.fetchFreshData(this.data);
        }
      } catch (error) {
        console.error('提交表单时出错:', error);
        this.$message.error('提交失败，请稍后再试！');
      }
    },
    // 信息新增
    async addLineOperation() {
      try {
        this.scenicStrategy.scenicId = this.data.id;
        this.scenicStrategy.cover = this.cover;
        const response = await this.$axios.post('/scenicStrategy/save', this.scenicStrategy);
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.cannelLine();
          this.fetchscenicStrategys(this.data.id);
        }
      } catch (error) {
        console.error('提交表单时出错:', error);
        this.$message.error('提交失败，请稍后再试！');
      }
    },
    // 信息新增
    async addOperation() {
      try {
        this.data.cover = this.cover;
        const response = await this.$axios.post('/scenicStrategy/save', this.data);
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.cannel();
          this.fetchFreshData();
        }
      } catch (error) {
        console.error('提交表单时出错:', error);
        this.$message.error('提交失败，请稍后再试！');
      }
    },
    clearFormData() {
      this.data = {};
    },
    async fetchFreshData() {
      try {
        let startTime = null;
        let endTime = null;
        if (this.searchTime != null && this.searchTime.length === 2) {
          const [startDate, endDate] = await Promise.all(this.searchTime.map(date => date.toISOString()));
          startTime = `${startDate.split('T')[0]}T00:00:00`;
          endTime = `${endDate.split('T')[0]}T23:59:59`;
        }
        // 请求参数
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          key: this.filterText,
          startTime: startTime,
          endTime: endTime,
          ...this.scenicStrategyQueryDto
        };
        const response = await this.$axios.post('/scenicStrategy/query', params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询攻略信息异常:', error);
      }
    },
    add() {
      this.dialogOperation = true;
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
    handleEdit(row) {
      this.cover = row.cover;
      this.dialogOperation = true;
      this.isOperation = true;
      this.data = { ...row }
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    }
  },
};
</script>
<style scoped lang="scss">
.add-line {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgb(64, 158, 255);
  padding: 10px 20px;
  margin: 10px 20px;
  color: rgb(245, 245, 245);
  border-radius: 5px;
}
</style>