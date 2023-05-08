<template>
  <div>
    <div>Bot昵称: {{ botName }}</div>
    <div>Bot战力: {{ botRating }} </div>
  </div>
  <router-view />
</template>

<script>
import $ from 'jquery'
import { ref } from 'vue'

export default {
  name: "App",
  setup () {
    let botName = ref("");
    let botRating = ref("");

    $.ajax({
      url: "http://127.0.0.1:3000/pk/getbotinfo",
      type: "GET",
      success: resp => {
        botName.value = resp.name;
        botRating.value = resp.rating;
        console.log(resp);
      }
    });

    return {
      botName, botRating
    }
  }
}
</script>

<style>
body {
  background-image: url("@/assets/background.webp");
  background-size: cover;
}
</style>
