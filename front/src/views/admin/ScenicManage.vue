<template>
  <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 10px;">
    <el-row style="padding: 10px 5px;margin: 0 5px;">
      <el-row>
        <el-select v-model="scenicQueryDto.categoryId" placeholder="所属类别" size="small" style="margin-left: 5px;"
                   @change="handleFilter">
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id">
          </el-option>
        </el-select>
        <el-select v-model="scenicQueryDto.status" placeholder="景点状态" size="small" style="margin-left: 5px;"
                   @change="handleFilter">
          <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
        <el-date-picker v-model="searchTime" end-placeholder="创建结束" range-separator="至" size="small"
                        start-placeholder="创建开始" style="width: 220px;margin-left: 5px;" type="daterange"
                        @change="handleFilter">
        </el-date-picker>
        <el-input v-model="scenicQueryDto.name" clearable placeholder="景点名" size="small"
                  style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
        <span style="float: right;">
          <el-button class="customer" size="small"
                     style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                     @click="add()"><i class="el-icon-plus"></i>新增景点</el-button>
        </span>
      </el-row>
    </el-row>
    <el-row style="margin: 0 15px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="景点封面" prop="cover" width="120px">
          <template slot-scope="scope">
            <img :src="scope.row.cover" style="width: 88px;height: 55px;border-radius: 10px;"/>
          </template>
        </el-table-column>
        <el-table-column label="景点名" prop="name"></el-table-column>
        <el-table-column label="所在地" prop="address" width="160px"></el-table-column>
        <el-table-column label="所属分类" prop="categoryName" width="120px"></el-table-column>
        <el-table-column label="供应商" prop="vendorName" width="120px"></el-table-column>
        <el-table-column label="评分" prop="ratingScore" width="60px">
          <template slot-scope="scope">
            <span>{{ scope.row.ratingScore === null ? 0 : scope.row.ratingScore }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" sortable width="168"></el-table-column>
        <el-table-column label="状态" prop="isWord" width="108">
          <template slot-scope="scope">
            <i v-if="!scope.row.status" class="el-icon-warning" style="margin-right: 5px;"></i>
            <i v-else class="el-icon-success" style="margin-right: 5px;color: rgb(253, 199, 50);"></i>
            <el-tooltip v-if="scope.row.status" class="item" content="如果是不可用状态，暂停景点服务" effect="dark"
                        placement="bottom-end">
              <span style="text-decoration: underline;text-decoration-style: dashed;">可用</span>
            </el-tooltip>
            <span v-else>不可用</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <span class="text-button" @click="scenicLineHandle(scope.row)">景点路线</span>
            <span class="text-button" @click="handleEdit(scope.row)">编辑</span>
            <span class="text-button" @click="handleDelete(scope.row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[10, 20]" :total="totalItems"
                     layout="total, sizes, prev, pager, next, jumper" style="margin:10px 0;"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"></el-pagination>
    </el-row>
    <!-- 新增、修改操作面板 -->
    <el-dialog :show-close="false" :visible.sync="dialogOperation" width="32%">
      <div slot="title">
        <p class="dialog-title">{{ !isOperation ? '新增景点' : '修改景点信息' }}</p>
      </div>
      <div style="padding:0 20px;">
        <el-row>
          <span class="dialog-hover">景点封面</span>
          <el-upload :on-success="handleCoverSuccess" :show-file-list="false"
                     action="/api/Homestay-sys/v1.0/file/upload" class="avatar-uploader">
            <img v-if="cover" :src="cover" style="width: 250px;height: 150px;">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row style="margin-bottom: 20px;">
          <span class="dialog-hover">景点名称</span>
          <input v-model="data.name" class="dialog-input" placeholder="请输入"/>
          <span class="dialog-hover">景点所在地址</span>
          <input v-model="data.address" class="dialog-input" placeholder="请输入"/>
          <div>
            <span class="dialog-hover">景点状态</span>
            <el-switch v-model="data.status" active-text="可用" inactive-text="不可用">
            </el-switch>
          </div>
          <div style="margin: 10px 0;">
            <span class="dialog-hover">景点分类</span>
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
        <el-button v-if="!isOperation" class="customer" size="small"
                   style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                   @click="addOperation()">新增</el-button>
        <el-button v-else class="customer" size="small"
                   style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                   @click="updateOperation()">修改</el-button>
        <el-button class="customer" size="small" style="background-color: rgb(211, 241, 241);border: none;"
                   @click="cancel()">取消</el-button>
      </span>
    </el-dialog>
    <el-dialog :show-close="false" :visible.sync="dialogLineOperation" width="28%">
      <div slot="title">
        <p class="dialog-title">{{ !isLineOperation ? '新增景点路线' : '修改景点路线信息' }}</p>
      </div>
      <div style="padding:0 20px;">
        <el-row>
          <span class="dialog-hover">路线封面</span>
          <el-upload :on-success="handleCoverSuccess" :show-file-list="false"
                     action="/api/Homestay-sys/v1.0/file/upload" class="avatar-uploader">
            <img v-if="cover" :src="cover" style="width: 250px;height: 150px;">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-row>
        <el-row style="margin-bottom: 20px;">
          <div>
            <span class="dialog-hover">景点简介</span>
          </div>

          <el-input v-model="scenicLine.detail" :rows="2" placeholder="请输入内容" style="width: 100%;" type="textarea">
          </el-input>
          <div>
            <span class="dialog-hover">优先级</span>
          </div>
          <el-slider v-model="scenicLine.level" :max="20" show-input/>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button v-if="!isLineOperation" class="customer" size="small"
                   style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                   @click="addLineOperation()">新增路线</el-button>
        <el-button v-else class="customer" size="small"
                   style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;" type="info"
                   @click="updateLineOperation()">修改路线</el-button>
        <el-button class="customer" size="small" style="background-color: rgb(211, 241, 241);border: none;"
                   @click="cancelLine()">取消</el-button>
      </span>
    </el-dialog>
    <el-drawer :direction="direction" :visible.sync="drawer" title="景点路线" width="35%">
      <div class="add-line">
        <span style="cursor: pointer;font-size: 14px" @click="addLine">
          <i class="el-icon-plus"></i>
          添加路线
        </span>
      </div>
      <div v-if="scenicLines.length === 0">
        <el-empty description="暂无路线信息"></el-empty>
      </div>
      <div v-else>
        <div>
          <el-timeline>
            <el-timeline-item v-for="(scenicLine, index) in scenicLines" :key="index" :timestamp="scenicLine.createTime"
                              placement="top">
              <el-card>
                <div>
                  <img :src="scenicLine.cover" alt="路线封面" style="width: 100%;height: 200px;border-radius: 10px;">
                </div>
                <p style="margin-bottom: 8px;font-size: 14px;color: rgb(97, 97, 97);">{{
                    scenicLine.detail
                  }}
                </p>
                <div>
                  <el-tooltip class="item" content="修改景点路线" effect="dark" placement="bottom">
                    <el-button circle icon="el-icon-edit" type="primary" @click="updateLine(scenicLine)"></el-button>
                  </el-tooltip>
                  <el-tooltip class="item" content="删除景点路线" effect="dark" placement="bottom">
                    <el-button circle icon="el-icon-delete" type="danger" @click="deleteLine(scenicLine)"></el-button>
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
      scenicLine: {},
      data: {},
      filterText: '',
      cover: '',
      currentPage: 1,
      drawer: false, // 控制景点路线的抽屉开关
      direction: 'rtl', // right to left
      pageSize: 10,
      totalItems: 0,
      dialogLineOperation: false, // 景点路线弹窗确认
      isLineOperation: false, // 景点路线操作时显示文本开关
      dialogOperation: false, // 开关
      isOperation: false, // 开关-标识新增或修改
      tableData: [],
      selectedRows: [],
      scenicQueryDto: {}, // 搜索条件
      searchTime: [],
      scenicLines: [],
      categoryList: [],
      vendorList: [],
      isAuditList: [{value: null, label: '全部'}, {value: 0, label: '未审核'}, {value: 1, label: '已审核'}],
      statusList: [{value: null, label: '全部'}, {value: 0, label: '不可用'}, {value: 1, label: '可用'}],
    };
  },
  created() {
    this.fetchFreshData();
    this.fetchVendorList();
    this.fetchCategoryList();
  },
  methods: {
    // 修改景点信息
    updateLine(scenicLine) {
      this.dialogLineOperation = true;
      this.isLineOperation = true;
      this.cover = scenicLine.cover;
      this.scenicLine = scenicLine;
    },
    // 删除景点信息
    deleteLine(scenicLine) {
      this.$confirm('此操作将删除该景点路线, 是否继续?', '操作提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let ids = [scenicLine.id];
        this.$axios.post(`/scenicLine/batchDelete`, ids).then(res => {
          if (res.data.code === 200) {
            this.$message.success('景点路线删除成功');
            this.fetchScenicLines(this.data.id);
          }
        })
      }).catch(() => {
        console.log("操作取消");
      });
    },
    cancelLine() {
      this.scenicLine = {};
      this.dialogLineOperation = false;
      this.isLineOperation = false;
      this.cover = '';
    },
    addLine() {
      this.dialogLineOperation = true;
    },
    scenicLineHandle(scenic) {
      this.data = scenic;
      this.scenicLine.scenicId = scenic.id;
      this.fetchScenicLines(scenic.id);
    },
    // 请求景点路线信息
    fetchScenicLines(scenicId) {
      const scenicLineQueryDto = {scenicId: scenicId}
      this.$axios.post('/scenicLine/query', scenicLineQueryDto).then(res => {
        if (res.data.code === 200) {
          this.scenicLines = res.data.data;
          this.drawer = true;
        }
      }).catch(error => {
        this.$message.error('查询景点路线信息异常');
        console.error("查询景点路线信息异常：", error);
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
    // 获取景点分类
    fetchCategoryList() {
      this.$axios.post('/scenicCategory/query', {}).then(res => {
        if (res.data.code === 200) {
          // 插入“全部”选项
          this.categoryList = [
            {id: null, name: '全部'}, // id 设置为空字符串，表示不限制分类
            ...res.data.data // 原有的分类数据
          ];
        }
      }).catch(error => {
        console.error("查询景点分类信息异常：", error);
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
    cancel() {
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
        title: '删除景点数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/scenic/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: '删除提示',
              text: response.data.msg,
              icon: 'success',
              showConfirmButton: false,
              timer: 2000,
            });
            this.fetchFreshData();

          }
        } catch (e) {
          this.$swal.fire({
            title: '错误提示',
            text: e,
            icon: 'error',
            showConfirmButton: false,
            timer: 2000,
          });
          console.error(`景点信息删除异常：`, e);
        }
      }
    },
    // 修改路线信息
    async updateLineOperation() {
      try {
        this.scenicLine.cover = this.cover;
        const response = await this.$axios.put('/scenicLine/update', this.scenicLine);
        this.$swal.fire({
          title: '景点路线信息修改',
          text: response.data.msg,
          icon: response.data.code === 200 ? 'success' : 'error',
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.fetchScenicLines(this.data.id);
          this.cancelLine();
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
        const response = await this.$axios.put('/scenic/update', this.data);
        this.clearFormData();
        this.$swal.fire({
          title: '景点信息修改',
          text: response.data.msg,
          icon: response.data.code === 200 ? 'success' : 'error',
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.cancel();
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
        this.scenicLine.scenicId = this.data.id;
        this.scenicLine.cover = this.cover;
        const response = await this.$axios.post('/scenicLine/save', this.scenicLine);
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.cancelLine();
          this.fetchScenicLines(this.data.id);
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
        const response = await this.$axios.post('/scenic/save', this.data);
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.cancel();
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
        // 选择全部时，清除 categoryId
        if (params.categoryId === '') {
          delete params.categoryId; // 清除 categoryId
        }
        const response = await this.$axios.post('/scenic/query', params);
        const {data} = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        console.error('查询景点信息异常:', error);
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
      this.data = {...row}
    },
    handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    }
  },
};
</script>
<style lang="scss" scoped>
.add-line {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgb(64, 158, 255);
  padding: 10px 20px;
  margin: 10px 20px;
  color: rgb(245, 245, 245);
  border-radius: 10px;
}
</style>