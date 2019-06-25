<template>
    <div class="centering-text">
        <v-icon size="120" color="#1976d2">error</v-icon>
        <h1>403 Forbidden</h1>
        <p>You are not permitted to access this document!</p>
    </div>
</template>

<script>
    export default {
        name: "ForbiddenPage"
    };
</script>

<style scoped>
    div.centering-text {
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        text-align: center;
    }

    div p {
        font-size: 12pt;
    }
</style>
