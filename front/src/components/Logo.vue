<template>
    <span class="logo">
        <el-image fit="fill" src="/logo.png" style="width: 35px; height: 35px"></el-image>
        <div v-if="!flag">
            <span :style="{ color: bag, display: 'block' }">{{ sysName }}</span>
        </div>
    </span>
</template>
<script>
export default {
  name: "Logo",
  data() {
    return {}
  },
  props: {
    sysName: {
      type: String,
      required: true,
      default: '旅游民宿'
    },
    flag: {
      type: Boolean,
      required: false
    },
    bag: {
      type: String,
      default: '#1c1c1c'
    },
  },
  created() {

  },
  methods: {}
};
</script>
<style lang="scss" scoped>
.logo {
  color: rgb(8, 24, 16) !important;
  font-weight: bold;
  font-size: 22px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  user-select: none;
  font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;

  span {
    margin-left: 8px;
    color: #666;
  }
}
</style>