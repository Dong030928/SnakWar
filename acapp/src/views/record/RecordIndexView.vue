<template>
    <ContentField>
        <div class="game-table">
            <div>
                <table>
                    <thead>
                        <tr>
                            <th scope="col">玩家A</th>
                            <th scope="col">玩家B</th>
                            <th scope="col">对战结果</th>
                            <th scope="col">对战时间</th>
                            <th scope="col">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(item, index) in records"
                            :key="index">
                            <td class="game-table-username">
                                <img :src="item.a_photo"
                                     alt=""
                                     class="record-user-photo">
                                <span class="record-user-username">{{ item.a_username }}</span>
                            </td>
                            <td>
                                <img :src="item.b_photo"
                                     alt=""
                                     class="record-user-photo">
                                <span class="record-user-username">{{ item.b_username }}</span>
                            </td>
                            <td>{{ item.result }}</td>
                            <td>{{ item.record.createTime }}</td>
                            <td>
                                <button @click="openRecordContent(item.record.id)"
                                        class="btn btn-danger btn-sm">查看回放</button>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <!-- 页码组件 -->

                <div class="box">
                    <p class="pagesize">共 {{ parseInt(Math.ceil(totalRecords / 10)) }} 页</p>
                    <nav>
                        <ul class="pagination"
                            style="float: right; padding: 0">
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
            </div>
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
let records = ref([]);
let totalRecords = ref(0);
let pages = ref([]);

let changePage = (page) => {
    if (page === -2) page = currentPage - 1;
    else if (page === -1) page = currentPage + 1;

    let maxPages = parseInt(Math.ceil(totalRecords.value / 10));
    if (page >= 1 && page <= maxPages) {
        pullPage(page)
    }
}

let pullPage = (page) => {
    currentPage = page
    $.ajax({
        url: "https://app6039.acapp.acwing.com.cn/api/record/get/list",
        type: "GET",
        data: {
            page,
        },
        headers: {
            Authorization: "Bearer " + store.state.user.token
        },
        success (resp) {
            console.log(resp);
            records.value = resp.records
            totalRecords.value = resp.records_count
            currentPageSize.value = records.value.length
            updatePages()
        },
        error (resp) {
            console.log(resp);
        }
    })
}

// 确定页码当前展示多少个
let updatePages = () => {
    let maxPages = parseInt(Math.ceil(totalRecords.value / 10));
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


const get2DMap = (map) => {
    let g = []

    for (let i = 0, k = 0; i < 13; i++) {
        let row = []
        for (let j = 0; j < 14; j++, k++) {
            map[k] === "0" ? row.push(0) : row.push(1)
        }
        g.push(row)
    }

    return g
}

let openRecordContent = (id) => {
    for (const record of records.value) {
        if (id === record.record.id) {
            console.log(id);
            store.commit("updateIsRecord", true)
            store.commit("updateGame", {
                map: get2DMap(record.record.map),
                a_id: record.record.aid,
                a_sx: record.record.asx,
                a_sy: record.record.asy,
                b_id: record.record.bid,
                b_sx: record.record.bsx,
                b_sy: record.record.bsy,
            })
            store.commit("updateSteps", {
                a_steps: record.record.asteps,
                b_steps: record.record.bsteps,
            })
            store.commit("updateRecordLoser", record.record.loser)
            store.commit("updateRouterName", "record_content")
            break;
        }
    }
}
</script>

<style scoped>
img.record-user-photo {
    width: 4vh;
    border-radius: 50%;
}
div.game-table {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
}
div.game-table table {
    background-color: rgba(255, 255, 255, 0.5);
    border-radius: 5px;
}
.game-table-username {
    text-align: left;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 7.5vw;
}
td {
    width: 7.5vw;
}
th {
    text-align: center;
}
.page-item {
    display: inline-block;
    padding: 8px 12px;
    background-color: white;
    border: 1px solid #dee2e6;
    cursor: pointer;
    user-select: none;
}
.page-item:hover {
    background-color: #e9ecef;
}
.page-item.active {
    background-color: #0d6efd;
}
.page-item.active > a {
    color: white;
}
.page-link {
    color: #0d6efd;
    text-decoration: none;
}
nav {
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
    margin-right: 30px;
    font-size: 13px;
    color: rgb(153, 162, 170);
}
</style>