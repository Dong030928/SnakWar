<template>
    <div class="window">
        <div class="game-body">
            <MenuView v-if="$store.state.router.router_name === 'menu'" />
            <PKIndexView v-else-if="$store.state.router.router_name === 'pk'" />
            <RecordIndexView v-else-if="$store.state.router.router_name === 'record'" />
            <RecordContentView v-else-if="$store.state.router.router_name === 'record_content'" />
            <RanklistIndexView v-else-if="$store.state.router.router_name === 'ranklist'" />
            <UserBotIndexView v-else-if="$store.state.router.router_name === 'user_bot'" />
        </div>
    </div>
    <div class="back-btn"
         v-if="$store.state.router.router_name != 'menu'"
         @click="backToMenu">返回</div>
</template>

<script setup>
import { useStore } from 'vuex';
import MenuView from './views/MenuView.vue'
import PKIndexView from './views/pk/PKIndexView.vue'
import RecordIndexView from './views/record/RecordIndexView.vue'
import RecordContentView from './views/record/RecordContentView.vue'
import RanklistIndexView from "@/views/ranklist/RanklistIndexView.vue"
import UserBotIndexView from './views/user/bots/UserBotIndexView.vue'

const store = useStore()

const jwtToken = localStorage.getItem("jwt_token");

if (jwtToken) {
    store.commit("updateToken", jwtToken);
    store.dispatch("getInfo", {
        success () {
            store.commit("updatePollingInfo", false)
        },
        error () {
            store.commit("updatePollingInfo", false)
        }
    })
} else {
    store.commit("updatePollingInfo", false)
}

const backToMenu = () => {
    store.commit("updateRouterName", "menu")
}

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

.back-btn {
    position: absolute;
    width: 80px;
    height: 40px;
    font-size: 24px;
    font-style: italic;
    font-weight: 600;
    color: white;
    border: 3px solid white;
    text-align: center;
    right: 2vh;
    bottom: 2vh;
    cursor: pointer;
    user-select: none;
}

.back-btn:hover {
    transform: scale(1.2);
    transition: 200ms;
}
</style>
