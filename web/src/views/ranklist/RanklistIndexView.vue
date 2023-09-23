<template>
    <ContentField>
        <table class="table table-success table-striped">
            <thead>
                <tr>
                    <th scope="col">玩家</th>
                    <th scope="col">天梯积分</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(user, index) in users"
                    :key="index">
                    <td class="user-info">
                        <img :src="user.photo"
                             alt=""
                             class="record-user-photo">
                        <span class="record-user-username">{{ user.username }}</span>
                    </td>

                    <td>{{ user.rating }}</td>
                </tr>
            </tbody>
        </table>

        <!-- 页码组件 -->

        <div class="box">
            <p class="pagesize">共 {{ parseInt(Math.ceil(totalUsers / 10)) }} 页</p>
            <nav>
                <ul class="pagination"
                    style="float: right">
                    <li class="page-item"
                        @click="changePage(-2)">
                        <a class="page-link"
                           href="#"
                           aria-label="Previous">
                            <span aria-hidden="true">&laquo;</span>
                        </a>
                    </li>
                    <li :class="'page-item ' + item.isActive"
                        v-for="(item, index) in pages"
                        :key="index"
                        @click="changePage(item.number)">
                        <a class="page-link"
                           href="#">{{ item.number }}</a>
                    </li>
                    <li class="page-item"
                        @click="changePage(-1)">
                        <a class="page-link"
                           href="#"
                           aria-label="Next">
                            <span aria-hidden="true">&raquo;</span>
                        </a>
                    </li>
                </ul>
            </nav>
        </div>

    </ContentField>
</template>

<script setup>
import ContentField from '@/components/ContentField';
import { useStore } from 'vuex';
import $ from 'jquery';
import { onMounted, ref } from 'vue';

let store = useStore();
let currentPage = 1;
let currentPageSize = ref(10);
let users = ref([]);
let totalUsers = ref(0);
let pages = ref([]);

let changePage = (page) => {
    if (page === -2) page = currentPage - 1;
    else if (page === -1) page = currentPage + 1;

    let maxPages = parseInt(Math.ceil(totalUsers.value / 10));
    if (page >= 1 && page <= maxPages) {
        pullPage(page)
    }
}

let pullPage = (page) => {
    currentPage = page
    $.ajax({
        url: "http://127.0.0.1:3000/rank/get/list",
        type: "GET",
        data: {
            page,
        },
        headers: {
            Authorization: "Bearer " + store.state.user.token
        },
        success (resp) {
            console.log(resp);
            users.value = resp.users
            totalUsers.value = resp.users_count
            currentPageSize.value = users.value.length
            updatePages()
        },
        error (resp) {
            console.log(resp);
        }
    })
}

// 确定页码当前展示多少个
let updatePages = () => {
    let maxPages = parseInt(Math.ceil(totalUsers.value / 10));
    let newPages = [];
    for (let i = currentPage - 2; i <= currentPage + 2; i++) {
        if (i > 0 && i <= maxPages) {
            newPages.push({
                number: i,
                isActive: i === currentPage ? "active" : ""
            });
        }
    }
    pages.value = newPages;
}

onMounted(() => {
    pullPage(currentPage)
})
</script>

<style scoped>
* {
    text-align: center;
    vertical-align: center;
}

.record-user-photo {
    width: 4vh;
    border-radius: 50%;
    margin-right: 10px;
}

.user-info {
    display: flex;
    justify-content: center;
    align-items: center;
}

.box {
    display: flex;
    justify-content: center;
    align-items: center;
}

.pagesize {
    margin-right: 20px;
    font-size: 13px;
    color: rgb(153, 162, 170);
}
</style>