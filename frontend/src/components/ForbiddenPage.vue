<template>
    <div class="centering-text">
        <v-icon size="120" color="#4e8cff">error</v-icon>
        <h1>403 Forbidden</h1>
        <p>You are not permitted to access this document!<br/>
            Click <a @click="returnToHomepage">here</a> to return to the homepage.</p>
    </div>
</template>

<script>
    import router from '../router/index';

    export default {
        name: "ForbiddenPage",
        methods: {
            returnToHomepage: function() {
                router.push('/');
            }
        }
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
