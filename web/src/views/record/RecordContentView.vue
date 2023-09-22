<template>
    <PlayGround />
</template>

<script setup>
import PlayGround from '@/components/PlayGround'
import { onUnmounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

onUnmounted(() => {
    store.commit("updateIsPlaybackEnds", false)
})
</script>

<style scoped>
</style>
