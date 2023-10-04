<template>
    <div class="game-body">
        <MenuView v-if="$store.state.router.router_name === 'menu'" />
        <PKIndexView v-else-if="$store.state.router.router_name === 'pk'" />
        <RecordIndexView v-else-if="$store.state.router.router_name === 'record'" />
        <RecordContentView v-else-if="$store.state.router.router_name === 'record_content'" />
        <RanklistIndexView v-else-if="$store.state.router.router_name === 'ranklist'" />
        <UserBotIndexView v-else-if="$store.state.router.router_name === 'user_bot'" />
    </div>
</template>

<script setup>
import { useStore } from 'vuex';
import MenuView from './views/MenuView.vue'
import PKIndexView from './views/pk/PKIndexView.vue'
import RecordIndexView from './views/record/RecordIndexView.vue'
import RecordContentView from './views/record/RecordContentView.vue'
import RanklistIndexView from "@/views/ranklist/RanklistIndexView.vue"
import UserBotIndexView from './views/user/bots/UserBotIndexView.vue'
import $ from 'jquery'

const store = useStore()

$.ajax({
    url: "https://app6039.acapp.acwing.com.cn/api/user/account/third_party/app/apply_code",
    type: "GET",
    success (resp) {
        if (resp.result === "success") {
            store.state.user.AcWingOS.api.oauth2.authorize(
                resp.appid,
                resp.redirect_uri,
                resp.scope,
                resp.state,
                resp => {   // 此回调函数的参数为 receiveCode 函数接口所返回JSON格式里各项的参数，即 result，若成功就有 jwt_token，否则没有
                    if (resp.result === "success") {
                        store.commit("updateToken", resp.jwt_token)
                        store.dispatch("getInfo", {
                            success () {
                                store.commit("updatePollingInfo", false)
                            },
                            error () {
                                store.commit("updatePollingInfo", false)
                            }
                        })
                    } else {
                        store.state.user.AcWingOS.api.window.close()
                    }
                });
        } else {
            store.state.user.AcWingOS.api.window.close()
        }
    }
})
</script>

<style scoped>
* {
    margin: 0;
}

div.window {
    width: 100vw;
    height: 100vh;
}

div.game-body {
    background-image: url("@/assets/images/background.webp");
    background-size: cover;
    width: 100%;
    height: 100%;
}
</style>
