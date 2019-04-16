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
        computed: {
            simplemde () {
                return this.$refs.markdownEditor.simplemde
            }
        },
        methods: {
            submit: function() {
                this.$emit('contentWasChanged', this.content);

                let cm = this.simplemde.codemirror;
                let startPoint = cm.getCursor('start');
                let endPoint = cm.getCursor('end');

                console.log(startPoint)
                console.log(endPoint)
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
