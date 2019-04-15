<template>
    <markdown-editor
            v-model="content"
            @input="submit"
            ref="markdownEditor"
            :configs="configs"
    ></markdown-editor>
</template>

<script>
    import markdownEditor from 'vue-simplemde/src/markdown-editor'

    export default {
        name: "SimpleMDE",
        data: function() {
            return {
                content: '',
                configs: {
                    spellChecker: false
                }
            }
        },
        components: {
            markdownEditor
        },
        methods: {
            submit: function() {
                this.$emit('contentWasChanged', this.content);
            }
        }
    }
</script>

<style>
    @import '~simplemde/dist/simplemde.min.css';

    .markdown-editor  {
        height: 95% !important;
    }

    .CodeMirror {
        height: 95%;
    }
</style>
