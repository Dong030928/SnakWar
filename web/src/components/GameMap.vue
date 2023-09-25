<template>
    <!-- 游戏地图组件 -->
    <div ref="parent"
         class="gamemap">
        <!-- 左下 -->
        <div class="left-bottom">
            <span v-if="parseInt($store.state.user.id) === $store.state.pk.a_id"
                  class="my-color">自己</span>
            <span v-else
                  class="opponent-color">对手</span>
        </div>

        <canvas ref="canvas"
                tabindex="0"></canvas>
        <!-- 右下 -->
        <div class="right-top">
            <span v-if="parseInt($store.state.user.id) === $store.state.pk.b_id"
                  class="my-color">自己</span>
            <span v-else
                  class="opponent-color">对手</span>
        </div>

    </div>
    <div class="box">
        <button v-if="$store.state.record.is_record"
                class="btn btn-secondary btn-lg"
                @click="backToRankList">返回对战列表</button>
    </div>

</template>

<script setup>
import { GameMap } from '@/assets/scripts/GameMap'
import router from '@/router';
import { onMounted, ref } from 'vue'
import { useStore } from 'vuex'

const store = useStore();
let parent = ref(null)
let canvas = ref(null)

console.log(store.state.user.id, store.state.pk.a_id, store.state.pk.b_id);

const backToRankList = () => {
    router.push({
        path: "/record"
    })
}

onMounted(() => {
    store.commit(
        "updateGameObject",
        new GameMap(canvas.value.getContext('2d'), parent.value, store)
    )

})


</script>

<style scoped>
.gamemap {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
}

.box {
    position: relative;
}

button {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, 50%);
}

.left-bottom {
    font-size: 30px;
    font-weight: bold;
    position: absolute;
    left: 15vh;
    bottom: 0vh;
}

.right-top {
    font-size: 30px;
    font-weight: bold;

    position: absolute;
    right: 15vh;
    top: 0vh;
}

.my-color {
    color: antiquewhite;
    text-shadow: 0 0 10px red, 0 0 20px orange, 0 0 30px yellow, 0 0 40px green,
        0 0 50px blue, 0 0 60px purple;
}

.opponent-color {
    color: rgb(0, 0, 0);
    text-shadow: 0 0 10px red, 0 0 20px orange, 0 0 30px yellow, 0 0 40px green,
        0 0 50px blue, 0 0 60px purple;
}
</style>