<template>
    <!-- 游戏背景组件 -->
    <div class="match-ground">
        <div class="row">
            <div class="col-6">
                <div class="user-photo">
                    <img :src="$store.state.user.photo"
                         alt="">
                </div>
                <div class="user-name">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-6">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo"
                         alt="">
                </div>
                <div class="user-name">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
            <div class="col-12"
                 style="text-align: center; margin-top: 12vh;">
                <div v-if="isMatch"
                     class="loader">
                    <span></span><span></span><span></span><span></span><span></span><span></span>
                    <h1>{{ props.message }}</h1>
                </div>
                <button type="button"
                        class="btn btn-primary btn-lg"
                        @click="handleMatch">{{ matchBtnInfo }}</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'
import { useStore } from 'vuex'

const props = defineProps({
    message: {
        type: String,
        required: true
    }
})

const store = useStore();

let isMatch = ref(false)
let matchBtnInfo = ref('开始匹配')

let handleMatch = () => {
    isMatch.value = !isMatch.value
    if (isMatch.value) {
        matchBtnInfo.value = "取消匹配"
        store.state.pk.socket.send(JSON.stringify({
            event: "start-matching"
        }))
    } else {
        matchBtnInfo.value = "开始匹配"
        store.state.pk.socket.send(JSON.stringify({
            event: "stop-matching"
        }))
    }
}
</script>

<style scoped>
.match-ground {
    width: 60vw;
    height: 70vh;
    margin: 40px auto;
    background-color: rgba(21, 57, 87, 0.5);
}

.row .col-6 {
    text-align: center;
}

.user-photo {
    margin-top: 12vh;
}

.user-name {
    padding-top: 30px;
    color: white;
    font-size: 24px;
}

.user-photo img {
    width: 20vh;
    border-radius: 50%;
    border: 3px solid white;
}

.loader {
    height: 40px;
    position: absolute;
    top: 30%;
    left: 50%;
    transform: translateX(-50%);
}

.loader span {
    height: 15px;
    width: 15px;
    display: inline-block;
    margin: 0 2px;
    border-radius: 50%;
    transition: all 0.5s;
    animation: animate 2s infinite;
}

.loader span:nth-child(1) {
    animation-delay: 0;
}

.loader span:nth-child(2) {
    animation-delay: 0.1s;
}

.loader span:nth-child(3) {
    animation-delay: 0.2s;
}

.loader span:nth-child(4) {
    animation-delay: 0.3s;
}

.loader span:nth-child(5) {
    animation-delay: 0.4s;
}

.loader span:nth-child(6) {
    animation-delay: 0.5s;
}

.loader h1 {
    font-family: Arial, "Helvetica Neue", Helvetica, sans-serif;
    color: #fff;
    margin: 0;
    padding: 0;
}

@keyframes animate {
    0% {
        border: 1px solid #fff;
        background: transparent;
        transform: translateY(0);
    }
    50% {
        border: 1px solid #fff;
        background: greenyellow;
        transform: translateY(-25px);
    }
    100% {
        border: 1px solid #fff;
        background-color: aqua;
        transform: translateY(0);
    }
}
</style>