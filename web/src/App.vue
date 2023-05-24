<template>
    <NavBar />
    <router-view />
</template>

<script>
import NavBar from '@/components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
// import $ from "jquery"

export default {
    components: {
        NavBar
    },
    setup () {
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/account/info/",
        //     type: "get",
        //     headers: {
        //         Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlZGY0OTI5NjhhMDE0OWU1YTMxZjUyMzNkNmI2NzUwMCIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY4NDM3MzcyNiwiZXhwIjoxNjg1NTgzMzI2fQ.fqaowTbAMc-3FrMz51JzVlXb8LxBQ5xcwZIvUNJ1-10"
        //     },
        //     success (resp) {
        //         console.log(resp);
        //     },
        //     error (resp) {
        //         console.log(resp);
        //     }
        // });
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/account/register/",
        //     type: "post",
        //     data: {
        //         username: "dxz",
        //         password: "123456",
        //         confirmedPassword: "123456"
        //     },
        //     success (resp) {
        //         console.log(resp);
        //     },
        //     error (resp) {
        //         console.log(resp);
        //     }
        // });
    }
}
</script>

<style>
body {
    background-image: url("@/assets/images/background.webp");
    background-size: cover;
}
</style>
