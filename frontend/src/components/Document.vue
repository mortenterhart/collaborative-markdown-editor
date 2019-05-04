<template>
    <v-container fluid fill-height>
        <v-layout row>
            <v-flex xs6 pr-2>
                <MDE @contentWasChanged="content = $event"/>
            </v-flex>
            <v-flex xs6 pl-2>
                <Preview :content="content"/>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import MDE from './Editor'
    import Preview from './Preview'

    export default {
        name: "Document",
        data: () => ({
            content: ''
         }),
        components: {
            MDE,
            Preview
        },
        created() {
            // TODO: check if user is logged in and if user has access to the doc
        }
    }
</script>

<style scoped>

</style>