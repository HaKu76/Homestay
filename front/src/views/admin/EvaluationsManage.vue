<template>
  <el-row style="background-color: #FFFFFF;padding: 10px 0;border-radius: 5px;">
    <el-row style="padding: 10px;margin: 0 10px;">
      <el-row>
        <el-date-picker v-model="searchTime" end-placeholder="评论结束" range-separator="至" size="small"
          start-placeholder="评论开始" style="width: 220px;" type="daterange" @change="handleFilter">
        </el-date-picker>
        <el-input v-model="evalustionsQueryDto.content" clearable placeholder="评论内容" size="small"
          style="width: 188px;margin-left: 5px;margin-right: 6px;" @clear="handleFilterClear">
          <el-button slot="append" icon="el-icon-search" @click="handleFilter"></el-button>
        </el-input>
      </el-row>
    </el-row>
    <el-row style="margin: 0 20px;border-top: 1px solid rgb(245,245,245);">
      <el-table :data="tableData" row-key="id" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column label="文本" prop="content">
          <template slot-scope="scope">
            <el-tooltip :content="scope.row.content" class="item" effect="dark" placement="bottom">
              <div class="cell-name">{{ scope.row.content }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="点赞" prop="upvoteList" width="60">
          <template slot-scope="scope">
            <span v-if="scope.row.upvoteList !== null" style="font-size: 16px;font-weight: bolder;">{{
              scope.row.upvoteList.split(',').length
            }}</span>
            <span v-else style="font-size: 16px;font-weight: bolder;">0</span>
          </template>
        </el-table-column>
        <el-table-column label="挂载源" prop="contentType" width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.contentType }}</span>
          </template>
        </el-table-column>
        <el-table-column label="评论于" prop="createTime" width="168">
          <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="评论者" prop="userName" width="120">
          <template slot-scope="scope">
            <span v-html="highlightKeyword(scope.row.userName)"></span>
          </template>
        </el-table-column>
        <el-table-column label="被评论" prop="replierName" width="120">
          <template slot-scope="scope">
            <span v-html="highlightKeyword(scope.row.replierName)"></span>
          </template>
        </el-table-column>
        <el-table-column label="层级" prop="parentId" width="120">
          <template slot-scope="scope">
            <i v-if="scope.row.parentId === null" class="el-icon-warning" style="margin-right: 5px;"></i>
            <i v-else class="el-icon-success" style="margin-right: 5px;color: rgb(253, 199, 50);"></i>
            <span v-if="scope.row.parentId === null"
              style="text-decoration: underline;text-decoration-style: dashed;">父级</span>
            <span v-else>子级</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="数据操作" width="100px">
          <template slot-scope="scope">
            <span class="text-button" @click="handleDelete(scope.row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[20, 50]" :total="totalItems"
        layout="total, sizes, prev, pager, next, jumper" style="margin: 10px 0;" @size-change="handleSizeChange"
        @current-change="handleCurrentChange"></el-pagination>
    </el-row>
    <!-- 举报面板 -->
    <el-dialog :show-close="false" :visible.sync="reportDialog" title="" width="35%">
      <div slot="title" style="padding: 25px 0 0 20px;">
        <span style="font-size: 18px;font-weight: 800;">举报详情</span>
      </div>
      <el-row style="padding: 10px 20px 20px 20px;">
        <el-col :span="12">
          <PieChart :types="types" :values="values" />
        </el-col>
        <el-col :span="12">
          <el-row class="main">
            <div v-if="!reportsDate.length">
              <span class="count">暂无数据</span>
            </div>
            <el-col v-for="(entity, index) in reportsDate" :key="index" :span="6" class="item">
              <div class="count" style="margin-top: 20px;">
                <span>
                  {{ entity.count }}
                </span>
              </div>
              <div>{{ entity.name }}</div>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button class="customer" size="small" style="background-color: rgb(241, 241, 241);border: none;"
          @click="reportDialog = false">取 消</el-button>
      </span>
    </el-dialog>
  </el-row>
</template>

<script>
import Editor from "@/components/Editor";
import PieChart from "@/components/PieChart"

export default {
  components: {
    Editor,
    PieChart
  },
  data() {
    return {
      data: { cover: '' },
      reportsDate: [],
      filterText: '',
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogOperation: false,
      isOperation: false,
      searchTime: [],
      selectedRows: [],
      html: {},
      fileList: [],
      dynamicTags: ['健康', '养生'],
      inputVisible: false,
      inputValue: '',
      coverDialog: false,
      commentDialog: false,
      evaluationsList: [],
      selectedRows: [],
      count: 0,
      evaluationsDelete: false,
      idToDelete: null,
      contentDialog: false,
      reportDialog: false,
      types: [],
      values: [],
      evalustionsQueryDto: {},
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    keySearch() {
      this.fetchFreshData();
    },
    reportsList(row) {
      this.reportDialog = true;
      this.reportList(row.id);
    },
    async reportList(id) {
      const response = await this.$axios(`/evaluations-reports/reportCount/${id}`);
      const { data } = response;
      this.reportsDate = data.data;
      this.types = this.reportsDate.map(entity => entity.name);
      this.values = this.reportsDate.map(entity => entity.count);
    },
    showContent(news) {
      this.data = news;
      this.contentDialog = true;
    },
    async removeEvaluations(id) {
      this.evaluationsDelete = true;
      this.idToDelete = id;
    },
    confirmDelete() {
      this.$axios.delete(`/evaluations/delete/${this.idToDelete}`).then((response) => {
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.evaluationsDelete = false;
          this.loadEvaluationsList();
        }
      }).catch((error) => {
        console.log(error);
      });
    },
    handleEvaluationsClose() {
      this.commentDialog = false;
    },
    // 查看评论列表
    commentList(news) {
      this.data = news;
      this.commentDialog = true;
      this.loadEvaluationsList();
    },
    // 加载评论数据列表
    async loadEvaluationsList() {
      try {
        const response = await this.$axios.get(`/evaluations/list/${this.data.id}/evaluations`);
        this.evaluationsList = response.data.data.data;
        this.count = response.data.data.count;
      } catch (error) {
        console.error(`加载评论列表异常：`, error);
      }
    },
    // 查看封面图
    showPic(news) {
      this.data = news;
      this.coverDialog = true;
    },
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
    },
    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.dynamicTags.push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = '';
    },
    goBack() {
      this.data = {};
      this.dialogOperation = false;
    },
    goBackPage() {
      this.data = {};
      this.commentDialog = false;
    },
    // 多选框选中
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    // 批量删除数据
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message(`未选中任何数据`);
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: '删除评论数据',
        text: `删除后不可恢复，是否继续？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          let ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/evaluations/batchDelete`, ids);
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
          console.error(`评论信息删除异常：`, e);
        }
      }
    },
    // 关键词高亮显示
    highlightKeyword(text) {
      if (text == null) {
        return;
      }
      if (this.filterText != '') {
        const regex = new RegExp(this.filterText, 'gi');
        return text.replace(regex, '<span class="highlight" style="background-color: #bec936;color:#373834;">$&</span>');
      }
      return text;
    },
    resetQueryCondition() {
      this.filterText = '';
      this.searchTime = '';
      this.fetchFreshData();
    },
    async updateOperation() {
      try {
        const response = await this.$axios.put('/evaluations/update', this.data);
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error('提交表单时出错:', error);
        this.$message.error('提交失败，请稍后再试！');
      }
    },
    async addOperation() {
      this.data.tag = this.dynamicTags.join(',');
      try {
        const response = await this.$axios.post('/evaluations/save', this.data);
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error('提交表单时出错:', error);
        this.$message.error('提交失败，请稍后再试！');
      }
    },
    // 关闭表单
    closeDialog() {
      this.dialogOperation = false;
    },
    // 清除表单数据
    clearFormData() {
      this.data = {};
      this.html = '';
      this.fileList = [];
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
          startTime: startTime,
          endTime: endTime,
          ...this.evalustionsQueryDto,
        };
        // 使用await等待请求完成
        let response = await this.$axios.post('/evaluations/query', params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
      } catch (error) {
        // 错误处理
        console.error('Fetch data failed:', error);
      }
    },
    add() {
      this.dialogOperation = true;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
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
      this.dialogOperation = true;
      this.isOperation = true;
      this.data = row;
      console.log(this.data);
    },
    async handleDelete(row) {
      this.selectedRows.push(row);
      this.batchDelete();
    }
  },
};
</script>
<style lang="scss" scoped>
.main {
  padding: 50px 0;
  border-radius: 3px;
  display: flex;
  justify-content: center;

  .item:last-child {
    border-right: none;
  }

  .count,
  .tag {
    text-align: center;
  }

  .count {
    height: 30px;
    line-height: 30px;
    font-size: 26px;
    font-weight: bold;
    color: rgb(120 102 102);
  }

  .tag {
    height: 30px;
    line-height: 30px;
    font-size: 12px;
    color: rgb(120 102 102);
  }
}

.tag {
  color: #1E80FF;
  background-color: #EAF2FF;
  padding: 3px 4px;
  border-radius: 3px;
  font-size: 10px;
  margin-right: 5px;
}

.tag1 {
  color: #1c1c1c;
  background-color: #d7d8b8;
  padding: 3px 4px;
  border-radius: 3px;
  font-size: 10px;
  margin-right: 5px;
}
</style>