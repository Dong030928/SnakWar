<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-else-if="$store.state.pk.status === 'matching'"
                 :message="matchingInfo" />
    <ResultBoard v-if="$store.state.pk.loser != 'none'" />
</template>

<script>
import PlayGround from '@/components/PlayGround'
import MatchGround from '@/components/MatchGround'
import ResultBoard from '@/components/ResultBoard'
import { useStore } from 'vuex';
import { onMounted, onUnmounted, ref } from 'vue';

export default {
    components: {
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup () {
        const store = useStore()

        store.commit("updateIsRecord", false)

        let socket = null;
        let socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}`

        let matchingInfo = ref("正在匹配")

        onMounted(() => {
            store.commit("updateOpponent", {
                username: "",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
                rating: "----"
            })

            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                console.log("socket connected!");
                store.commit("updateSocket", socket)
            }

            socket.onmessage = (msg) => {
                const data = JSON.parse(msg.data);

                if (data.event === "match success") {
                    console.log("匹配成功！");

                    matchingInfo.value = "匹配成功！"

                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                        rating: data.opponent_rating
                    });

                    setTimeout(() => {  // 延时太长会传空参
                        // store.state.pk.status = "playing"
                        store.commit("updateStatus", "playing")
                    }, 500)

                    store.commit("updateGame", data.game)
                } else if (data.event === "move") {
                    console.log(data)

                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;

                    snake0.setDirection(data.a_direction);
                    snake1.setDirection(data.b_direction);
                } else if (data.event === "result") {
                    console.log(data)

                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;

                    if (data.loser === "all" || data.loser === "A") {
                        snake0.status = "die"
                    }

                    if (data.loser === "all" || data.loser === "B") {
                        snake1.status = "die"
                    }

                    store.commit("updateLoser", data.loser)


                    matchingInfo.value = "正在匹配"
                }
            }

            socket.onclose = () => {
                console.log("socket disconnected!");
            }
        });

        onUnmounted(() => {
            socket.close();
            store.commit("updateStatus", "matching");   // 当从PK页面切出去后，判输
            store.commit("updateLoser", "none")
        })

        return {
            matchingInfo
        }
    }
}
</script>

<style scoped>
</style>