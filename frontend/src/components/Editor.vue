<template>
    <markdown-editor
            v-model="content"
            @input="submit"
            ref="markdownEditor"
            id="foo"
            :configs="configs"
    ></markdown-editor>
</template>

<script>
    import markdownEditor from 'vue-simplemde/src/markdown-editor'

    export default {
        name: "SimpleMDE",
        data: function() {
            return {
                socket: null,
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
                /*
                this.socket.send(JSON.stringify({
                    "msg": this.content,
                })); */
                //let cm = this.simplemde.codemirror;
                //let startPoint = cm.getCursor('start');
                //let endPoint = cm.getCursor('end');
                //console.log(startPoint)

                // document.querySelector("#foo").selectionEnd
                //console.log(this.simplemde.element.selectionEnd)
            },
            handle(data) {
                console.log(data)
                switch (data.messageType) {
                    case "DocumentTitle":
                        this.$store.commit('app/setTitle', data.msg)
                        break;
                    case "ContentInit":
                        this.content = data.msg
                        break;
                }
            },
            getWebSocketURL() {
                return `ws://localhost:8080/CMD/ws/${this.$route.params.id}/${this.$store.state.login.username}`;
            }
        },
        mounted() {
            this.content = '';
            this.submit()
            if (this.socket) this.socket.close();
            this.socket = new WebSocket(this.getWebSocketURL());

            let vm = this;
            this.socket.onmessage = function (event) {
                vm.handle(JSON.parse(event.data.toString()));
            };
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
