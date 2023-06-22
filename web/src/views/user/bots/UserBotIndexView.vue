<template>
    <ContentField>
        我的Bot
    </ContentField>
</template>

<script setup>
import ContentField from '@/components/ContentField'
import $ from 'jquery'
import { useStore } from 'vuex'

const store = useStore();
// $.ajax({
//     url: "http://127.0.0.1:3000/user/bot/add/",
//     type: "POST",
//     data: {
//         title: "Bot的标题",
//         description: "Bot的描述",
//         content: "Bot的代码"
//     },
//     headers: {
//         Authorization: "Bearer " + store.state.user.token
//     },
//     success (resp) {
//         console.log(resp.error_message);
//     },
//     error (resp) {
//         console.log(resp);
//     }
// })
// $.ajax({
//     url: "http://127.0.0.1:3000/user/bot/remove/",
//     type: "POST",
//     data: {
//         bot_id: 1
//     },
//     headers: {
//         Authorization: "Bearer " + store.state.user.token
//     },
//     success (resp) {
//         console.log(resp);
//     },
//     error (resp) {
//         console.log(resp);
//     }
// })
// $.ajax({
//     url: "http://127.0.0.1:3000/user/bot/update/",
//     type: "POST",
//     data: {
//         bot_id: 3,
//         title: "修改过的Bot标题",
//         description: "修改过的Bot描述",
//         content: "修改过的Bot代码"
//     },
//     headers: {
//         Authorization: "Bearer " + store.state.user.token
//     },
//     success (resp) {
//         console.log(resp);
//     },
//     error (resp) {
//         console.log(resp);
//     }
// })
$.ajax({
    url: "http://127.0.0.1:3000/user/bot/getlist/",
    type: "GET",
    headers: {
        Authorization: "Bearer " + store.state.user.token
    },
    success (resp) {
        console.log(resp);
    },
    error (resp) {
        console.log(resp);
    }
})
</script>

<style scoped>
</style>