<template>
    <div></div>
</template>

<script setup>
import router from '@/router/index'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import $ from 'jquery'

const route = useRoute();
const store = useStore();

$.ajax({
    // url: "https://app6039.acapp.acwing.com.cn/api/user/account/third_party/web/receive_code",
    url: "http://127.0.0.1:3000/api/user/account/third_party/web/receive_code",
    type: "GET",
    data: {
        code: route.query.code,
        state: route.query.state,
    },
    success (resp) {
        if (resp.result === "success") {    // 用户同意授权
            localStorage.setItem("jwt_token", resp.jwt_token);
            store.commit("updateToken", resp.jwt_token);
            router.push({ name: "home" });
            store.commit("updatePollingInfo", false)
        } else {    // 用户拒绝授权
            router.push({ name: "user_account_login" });
        }
    }
})
</script>

<style>
</style>