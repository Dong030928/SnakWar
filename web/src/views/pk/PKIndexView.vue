<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-else-if="$store.state.pk.status === 'matching'"
                 :message="matchingInfo" />
</template>

<script>
import PlayGround from '@/components/PlayGround'
import MatchGround from '@/components/MatchGround'
import { useStore } from 'vuex';
import { onMounted, onUnmounted, ref } from 'vue';

export default {
    components: {
        PlayGround,
        MatchGround
    },
    setup () {
        const store = useStore();
        let socket = null;
        let socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}`;

        let matchingInfo = ref("正在匹配")

        onMounted(() => {
            store.commit("updateOpponent", {
                username: "",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            })

            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                console.log("socket connected!");
                store.commit("updateSocket", socket)
            }

            socket.onmessage = (msg) => {
                let data = JSON.parse(msg.data);
                if (data.event === "match success") {
                    matchingInfo.value = "匹配成功！"

                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo
                    });

                    setTimeout(() => {
                        // store.state.pk.status = "playing"
                        store.commit("updateStatus", "playing")
                    }, 2000)

                    store.commit("updateGameMap", data.game_map)
                }

                // console.log(data);
            }

            socket.onclose = () => {
                console.log("socket disconnected!");
            }
        });

        onUnmounted(() => {
            socket.close();
            store.commit("updateStatus", "matching");   // 当从PK页面切出去后，判输
        })

        return {
            matchingInfo
        }
    }
}
</script>

<style scoped>
</style>