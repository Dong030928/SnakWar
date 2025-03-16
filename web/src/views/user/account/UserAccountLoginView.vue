<template>
    <ContentField v-if="!$store.state.user.pulling_info">
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username"
                               class="form-label">用户名</label>
                        <input v-model="username"
                               type="text"
                               class="form-control"
                               id="username"
                               placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password"
                               class="form-label">密码</label>
                        <input v-model="password"
                               type="password"
                               class="form-control"
                               id="password"
                               placeholder="请输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit"
                            class="btn btn-primary">登&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;录</button>

                    <!-- <div class="divider">
                        <div class="text">
                            <span style="">其他方式登录</span>
                        </div>
                    </div>

                    <div class="third-party-logo">
                        <div class="acwing-logo"
                             @click="AcWingLogin">
                            <img src="@/assets/images/acwing_logo.png"
                                 title="AcWing一键登录">
                        </div>
                        <div class="qq-logo"
                             @click="QQLogin">
                            <img src="@/assets/images/qq_logo.png"
                                 title="QQ一键登录">
                        </div>
                    </div> -->
                </form>

            </div>
        </div>
    </ContentField>
</template>

<script setup>
import ContentField from '../../../components/ContentField.vue'
import { useStore } from 'vuex'
import { ref } from 'vue'
import router from '../../../router/index'
// import $ from 'jquery'

const store = useStore();
let username = ref('');
let password = ref('');
let error_message = ref('');

const jwtToken = localStorage.getItem("jwt_token");
if (jwtToken) {
    store.commit("updateToken", jwtToken);
    store.dispatch("getInfo", {
        success () {
            router.push({ name: "home" })
            store.commit("updatePollingInfo", false)
        },
        error () {
            store.commit("updatePollingInfo", false)
        }
    })
} else {
    store.commit("updatePollingInfo", false)
}

const login = () => {
    error_message.value = "";
    store.dispatch("login", {
        username: username.value,
        password: password.value,
        success () {
            store.dispatch("getInfo", {
                success () {
                    router.push({ name: 'home' });
                    console.log(store.state.user);
                }
            })
        },
        error () {
            error_message.value = "用户名或密码错误";
        }
    })
}

// const AcWingLogin = () => {
//     $.ajax({
//         url: "https://cloudwebsite/api/user/account/third_party/web/apply_code",
//         type: "GET",
//         success (resp) {
//             if (resp.result === "success") {
//                 window.location.replace(resp.apply_code_url);
//             }
//         },
//         error (resp) {
//             console.log(resp);
//         }
//     })
// }

// const QQLogin = () => {
//     console.log(1);
//     $.ajax({
//         url: "https://cloudwebsite/api/user/account/third_party/web/apply_code",
//         type: "GET",
//         success (resp) {
//             console.log(resp.apply_code_url)
//         },
//         error (resp) {
//             console.log(resp);
//         }
//     })
// }

</script>

<style scoped>
button {
    width: 100%;
}

.error-message {
    color: red;
}

.divider {
    position: relative;
    display: block;
    height: 1px;
    width: 100%;
    margin: 35px 0 20px 0;
    border-top: 1px;
    background-color: #e0e0e0;
}

.divider .text {
    left: 50%;
    transform: translateX(-50%) translateY(-50%);
    position: absolute;
    background-color: #fff;
    padding: 0 20px;
    font-weight: 500;
    font-size: 14px;
}

.third-party-logo {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    width: 100%;
    height: 100%;
    margin-top: 20px;
}

.acwing-logo,
.qq-logo {
    cursor: pointer;
    user-select: none;
}

.third-party-logo .acwing-logo > img,
.third-party-logo .qq-logo > img {
    width: 30px;
}
</style>