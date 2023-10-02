<template>
    <div class="menu-field">
        <div class="menu">
            <div class="menu-item"
                 @click="$store.state.router.router_name='pk'">对战</div>
            <div class="menu-item"
                 @click="$store.state.router.router_name='record'">对局列表</div>
            <div class="menu-item"
                 @click="$store.state.router.router_name='ranklist'">排行榜</div>
            <div class="menu-item"
                 @click="$store.state.router.router_name='user_bot'">我的Bot</div>
        </div>
    </div>
</template>

<script setup>

</script>

<style scoped>
.menu-field {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
}

.menu {
    width: 25vw;
    height: 30vh;
    background-color: rgba(0, 0, 0, 0.2);
}

.menu-item {
    height: 7.5vh;
    width: 100%;
    font-size: 24px;
    line-height: 7.5vh;
    color: white;
    text-align: center;
    font-style: italic;
    font-weight: 600;
    cursor: pointer;
    user-select: none;
}

.menu-item:hover {
    transform: scale(1.2);
    transition: 200ms;
}
</style>