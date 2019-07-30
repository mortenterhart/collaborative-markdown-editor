<template>
    <div class="centering-text">
        <v-icon size="120" color="#4e8cff">error</v-icon>
        <h1>404 Not Found</h1>
        <p>The resource {{ resourcePath }} you requested was not found on this server!<br>
            Click <a @click="returnToHomepage">here</a> to return to the homepage.</p>
    </div>
</template>

<script>
    import router from '../router/index';

    export default {
        name: "ErrorPage",
        methods: {
            returnToHomepage: function() {
                router.push('/');
            }
        },
        computed: {
            resourcePath: function() {
                return location.hash.replace(/^#/, "");
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
