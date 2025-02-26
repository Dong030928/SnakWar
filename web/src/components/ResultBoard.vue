<template>
    <div :class="isDraw ? 'result-board result-board-bg-draw' : 'result-board result-board-bg'">
        <div class="text"
             v-if="isDraw">
            DRAW!<p style="font-size: 20px">(天梯积分+0)</p>
        </div>
        <div class="text"
             v-else-if="isALose">
            LOSE!<p style="font-size: 20px">(天梯积分-2)</p>
        </div>
        <div class="text"
             v-else-if="isBLose">
            LOSE!<p style="font-size: 20px">(天梯积分-2)</p>
        </div>
        <div class="text"
             v-else>
            WIN!<p style="font-size: 20px">(天梯积分+5)</p>
        </div>
        <div class="result-board-btn">
            <button type="button"
                    class="btn btn-warning btn-lg"
                    @click="restart">
                再来一局
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useStore } from 'vuex'
import $ from 'jquery'

let store = useStore()
let isDraw = ref(store.state.pk.loser === 'all')
let isALose = ref(store.state.pk.loser === 'A' && store.state.pk.a_id === parseInt(store.state.user.id))
let isBLose = ref(store.state.pk.loser === 'B' && store.state.pk.b_id === parseInt(store.state.user.id))

const restart = () => {
    store.commit("updateStatus", "matching")
    store.commit("updateLoser", "none")
    store.commit("updateOpponent", {
        username: "",
        photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
        rating: "----"
    })
}

const updateUserRating = () => {
    $.ajax({
        // url: "https://app6039.acapp.acwing.com.cn/api/user/account/info/",
        url: "http://127.0.0.1:3000/api/user/account/info/",
        type: "GET",
        headers: {
            Authorization: "Bearer " + store.state.user.token,
        },
        success (resp) {
            store.state.user.rating = resp.rating;
            console.log(resp);
        }
    });
};

updateUserRating()
</script>

<style scoped>
.result-board {
    height: 30vh;
    width: 30vw;
    min-height: 200px;
    min-width: 400px;
    background-size: cover;
    background-color: rgba(50, 50, 50, 0.5);
    border-radius: 50px;
    position: absolute;
    top: 30vh;
    left: 35vw;
    box-shadow: 5px 5px 5px 3px;
}

.result-board-bg-draw {
    background-image: url("@/assets/images/result-draw-board.webp");
}

.result-board-bg {
    background-image: url("@/assets/images/res-bg.png");
}

.result-board .text {
    position: relative;
    text-align: center;
    text-shadow: 0 0 10px red, 0 0 20px orange, 0 0 30px yellow, 0 0 40px green,
        0 0 50px blue, 0 0 60px purple;
    color: antiquewhite;
    font-size: 50px;
    font-weight: 600;
    font-style: italic;
    padding-top: 5vh;
}

.result-board .result-board-btn {
    position: relative;
    text-align: center;
    margin-top: 3vh;
}
</style>