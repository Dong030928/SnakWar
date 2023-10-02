<template>
    <div class="container content-field">
        <div class="card">
            <div class="card-body">
                <slot></slot>
            </div>
        </div>
    </div>
    <div class="back-btn"
         @click="backToMenu">返回</div>
</template>

<script setup>
import { useStore } from 'vuex'
const store = useStore()

const backToMenu = () => {
    store.commit("updateRouterName", "menu")
}
</script>

<style scoped>
.content-field {
    margin-top: 20px;
}

.back-btn {
    position: absolute;
    width: 80px;
    height: 40px;
    font-size: 24px;
    font-style: italic;
    font-weight: 600;
    color: white;
    background-color: grey;
    border: 3px solid white;
    text-align: center;
    right: 2vh;
    bottom: 2vh;
    cursor: pointer;
    user-select: none;
}

.back-btn:hover {
    transform: scale(1.2);
    transition: 200ms;
}
</style>