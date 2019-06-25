<template>
    <div class="preview" v-html="compileMarkdown"></div>
</template>

<script>
    import marked from "marked";

    export default {
        name: "Preview",
        props: ['content'],
        computed: {
            compileMarkdown: function() {
                return marked(this.content, { sanitize: true, breaks: true });
            }
        }
    };
</script>

<style scoped>
    .preview {
        border: 1px solid rgba(187, 187, 187, .6);
        border-radius: 4px;
        height: 96%;
        padding: 2%;
    }
</style>
