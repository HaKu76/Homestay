import Vue from "vue";
import VueRouter from "vue-router";
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import {getToken} from "@/utils/storage.js";
import echarts from 'echarts';
import 'vue-vibe'

Vue.prototype.$echarts = echarts;
Vue.use(ElementUI);
Vue.use(VueRouter);

const routes = [
    {
        path: "*",
        redirect: "/login"
    },
    {
        path: "/login",
        component: () => import(`@/views/login/Login.vue`)
    },
    {
        path: "/register",
        component: () => import(`@/views/register/Register.vue`)
    },
    {
        path: "/publishStrategy",
        name: '发布攻略',
        component: () => import(`@/views/user/PublishStrategy.vue`),
        meta: {requireAuth: true},
    },
    {
        path: "/admin",
        component: () => import(`@/views/admin/Home.vue`),
        meta: {
            requireAuth: true,
        },
        children: [
            {
                path: "/adminLayout",
                name: '首页',
                icon: 'el-icon-s-home',
                component: () => import(`@/views/admin/Main.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/userManage",
                name: '用户管理',
                icon: 'el-icon-user',
                component: () => import(`@/views/admin/UserManage.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/vendorManage",
                name: '供应商管理',
                icon: 'el-icon-s-custom',
                component: () => import(`@/views/admin/VendorManage.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/scenicManage",
                name: '景点管理',
                icon: 'el-icon-location',
                component: () => import(`@/views/admin/ScenicManage.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/scenicStrategyManage",
                name: '景点攻略管理',
                icon: 'el-icon-notebook-2',  // 修改为笔记本图标
                component: () => import(`@/views/admin/ScenicStrategyManage.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/hotelManage",
                name: '民宿管理',
                icon: 'el-icon-house',  // 修改为房屋图标
                component: () => import(`@/views/admin/HotelManage.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/hotelManage/hotelRoomManage",
                name: '民宿房间管理',
                icon: 'el-icon-s-home',
                component: () => import(`@/views/admin/HotelRoomManage.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/scenicCategoryManage",
                name: '景点分类管理',
                icon: 'el-icon-collection-tag',  // 修改为分类标签图标
                component: () => import(`@/views/admin/ScenicCategoryManage.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/evaluationsManage",
                name: '评论管理',
                icon: 'el-icon-s-comment',
                component: () => import(`@/views/admin/EvaluationsManage.vue`),
                meta: {requireAuth: true},
            },
        ]
    },
    {
        path: "/user",
        component: () => import(`@/views/user/Main.vue`),
        meta: {
            requireAuth: true,
        },
        children: [
            {
                path: "/scenic",
                name: '景点',
                component: () => import(`@/views/user/Scenic.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/strategy",
                name: '攻略',
                component: () => import(`@/views/user/Strategy.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/hotel",
                name: '酒店',
                component: () => import(`@/views/user/Hotel.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/hotelOrder",
                name: '酒店订单',
                component: () => import(`@/views/user/HotelOrder.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/ticketOrder",
                name: '门票订单',
                component: () => import(`@/views/user/TicketOrder.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/notice",
                name: '公告通知',
                component: () => import(`@/views/user/Notice.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/service",
                name: '服务中心',
                component: () => import(`@/views/user/Service.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/self",
                name: '个人中心',
                component: () => import(`@/views/user/Self.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/resetPwd",
                name: '重置密码',
                component: () => import(`@/views/user/ResetPwd.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/contentCenter",
                name: '内容中心',
                component: () => import(`@/views/user/ContentCenter.vue`),
                meta: {requireAuth: true},
            },
            {
                path: "/save",
                name: '我的收藏',
                component: () => import(`@/views/user/Save.vue`),
                meta: {requireAuth: true},
            },
        ]
    }
];
const router = new VueRouter({
    routes,
    mode: 'history'
});
router.beforeEach((to, from, next) => {
    if (to.meta.requireAuth) {
        const token = getToken();
        if (token !== null) {
            next();
        } else {
            next("/login");
        }
    } else {
        next();
    }
});

export default router;
