<template>
    <div class="result-board">
        <div class="text"
             v-if="$store.state.pk.loser === 'all'">
            DRAW!
        </div>
        <div class="text"
             v-else-if="$store.state.pk.loser === 'A' && $store.state.pk.a_id === parseInt($store.state.user.id)">
            LOSE!
        </div>
        <div class="text"
             v-else-if="$store.state.pk.loser === 'B' && $store.state.pk.b_id === parseInt($store.state.user.id)">
            LOSE!
        </div>
        <div class="text"
             v-else>
            WIN!
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
import { useStore } from 'vuex'

let store = useStore()

const restart = () => {
    store.commit("updateStatus", "matching")
    store.commit("updateLoser", "none")
    store.commit("updateOpponent", {
        username: "",
        photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
    })
}
</script>

<style scoped>
.result-board {
    height: 30vh;
    width: 30vw;
    min-height: 200px;
    min-width: 400px;
    background-image: url("@/assets/images/res-bg.png");
    background-size: cover;
    background-color: rgba(50, 50, 50, 0.5);
    border-radius: 50px;
    position: absolute;
    top: 30vh;
    left: 35vw;
    box-shadow: 5px 5px 5px 3px;
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
    padding-top: 7vh;
}
</style>