<template>
    <!-- 游戏地图组件 -->
    <div ref="parent"
         class="gamemap">
        <canvas ref="canvas"
                tabindex="0"></canvas>

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
</style>