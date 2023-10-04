<template>
    <!-- 游戏背景组件 -->
    <div class="field">
        <div class="match-ground">
            <div class="match-ground-head">
                <div>
                    <div class="user-photo">
                        <p class="rating">天梯积分：<i style="color: orange;">{{ $store.state.user.rating }}</i></p>
                        <img :src="$store.state.user.photo"
                             alt="">
                    </div>
                    <div class="user-name">
                        {{ $store.state.user.username }}
                    </div>
                </div>
                <div class="user-select-bot">
                    <div class="user-select-bot">
                        <select v-model="selectBot"
                                class="form-select">
                            <option value="-1"
                                    selected>本人出战</option>
                            <option v-for="bot in bots"
                                    :key="bot.id"
                                    :value="bot.id">{{ bot.title }}</option>
                        </select>
                    </div>
                </div>
                <div>
                    <div class="user-photo">
                        <p class="rating">天梯积分：<i style="color: orange;">{{ $store.state.pk.opponent_rating }}</i></p>
                        <img :src="$store.state.pk.opponent_photo"
                             alt="">
                    </div>
                    <div class="user-name">
                        {{ $store.state.pk.opponent_username }}
                    </div>
                </div>
            </div>

            <div class="match-board"
                 style="text-align: center; margin-top: 10vh;">
                <div :class="isMatch ? 'loader-board' : ''">
                    <div v-if="isMatch"
                         class="loader">
                        <span></span><span></span><span></span><span></span><span></span><span></span>
                        <h1>{{ props.message }}</h1>
                    </div>
                </div>
            </div>

            <div class="start-match-button">
                <button type="button"
                        @click="handleMatch">{{ matchBtnInfo }}</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'
import { useStore } from 'vuex'
import $ from 'jquery'

const props = defineProps({
    message: {
        type: String,
        required: true
    }
})

const store = useStore();

let isMatch = ref(false)
let matchBtnInfo = ref('开始匹配')
let bots = ref([]);
let selectBot = ref(-1);

let handleMatch = () => {
    isMatch.value = !isMatch.value
    if (isMatch.value) {
        matchBtnInfo.value = "取消匹配"
        console.log(store.state.pk.socket);
        store.state.pk.socket.send(JSON.stringify({
            event: "start-matching",
            bot_id: selectBot.value,
        }))
    } else {
        matchBtnInfo.value = "开始匹配"
        store.state.pk.socket.send(JSON.stringify({
            event: "stop-matching"
        }))
    }
}

const refreshBots = () => {
    $.ajax({
        url: "https://app6039.acapp.acwing.com.cn/api/user/bot/getlist/",
        type: "GET",
        headers: {
            Authorization: "Bearer " + store.state.user.token,
        },
        success (resp) {
            bots.value = resp;
        }
    });
};

refreshBots()
</script>

<style scoped>
* {
    margin: 0;
}

.field {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
}

.match-ground {
    width: 60%;
    height: 60%;
    background-color: rgba(50, 50, 50, 0.5);
    display: flex;
    flex-direction: column;
    justify-content: space-around;
}

.match-ground-head {
    display: flex;
    justify-content: space-evenly;
}

.row .col-6 {
    text-align: center;
}

.user-photo {
    text-align: center;
    margin-top: 100px;
}

.user-name {
    padding-top: 2vh;
    color: white;
    font-size: 24px;
    font-weight: 600;
    text-align: center;
}

.user-photo > img {
    width: 10vh;
    border-radius: 50%;
    border: 3px solid white;
}

.user-select-bot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 15vw;
    text-align: center;
}

.user-select-bot > select {
    margin: 0 auto;
    width: 10vw;
    font-size: 20px;
    border-radius: 5px;
    height: 4.5vh;
}

.start-match-button {
    text-align: center;
    margin-bottom: 20px;
}

.start-match-button > button {
    font-size: 20px;
    border-radius: 5px;
    background-color: #ffc310;
    padding: 8px 12px;
    border: none;
    cursor: pointer;
}

.match-board {
    display: flex;
    justify-content: center; /*主轴对齐方式,水平居中*/
    align-items: center; /*垂直对齐方式,居中*/
    width: 100%;
    height: 100%;
}

/* 匹配动画CSS */
.loader-board {
    position: relative;
    margin-top: -250px;
    height: 250px;
    width: 444.4px;
    background-image: url("@/assets/images/match-ground-bg.jpg");
    background-size: cover;
    border-radius: 15%;
    box-shadow: 5px 5px 5px 3px;
}

.loader {
    position: absolute;
    top: 125px;
    left: 222.2px;
    transform: translateX(-50%) translateY(-50%);
    font-style: italic;
    text-shadow: 0 0 10px red, 0 0 20px orange, 0 0 30px yellow, 0 0 40px green,
        0 0 50px blue, 0 0 60px purple;
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

.rating {
    font-size: 20px;
    font-weight: 600;
}
</style>