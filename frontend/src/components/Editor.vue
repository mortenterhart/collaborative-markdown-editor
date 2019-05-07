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
    import debounce from 'debounce'
    import markdownEditor from 'vue-simplemde/src/markdown-editor'

    export default {
        name: "SimpleMDE",
        data: function() {
            return {
                socket: null,
                cursorPosition: 0,
                lastReceivedContent: '',
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
                this.cursorPosition = this.simplemde.codemirror.getCursor('start').ch
                this.sendContentDiff(this)
            },
            sendContentDiff: debounce(e => {
                console.log(e)

                const pos = e.cursorPosition - (e.content.length - e.lastReceivedContent.length)
                const msg = JSON.stringify({
                    "userId": 0,
                    "docId": Number(e.$route.params.id),
                    "cursorPos": pos,
                    "msg": e.content.substring(pos, e.cursorPosition),
                    "messageType": "Insert"
                })
                console.log(msg)
                e.socket.send(msg);

                e.lastReceivedContent = e.content // funktioniert hier nicht
            }, 400),
            handle(data) {
                console.log(data)
                switch (data.messageType) {
                    case "DocumentTitle":
                        this.$store.commit('app/setTitle', data.msg)
                        break;
                    case "ContentInit":
                        this.content = data.msg
                        this.lastReceivedContent = this.content
                        break;
                }
            },
            getWebSocketURL() {
                return `ws://localhost:8080/CMD/ws/${this.$route.params.id}/${this.$store.state.login.username}`;
            }
        },
        mounted() {
            this.content = '';
            this.$emit('contentWasChanged', this.content);
            if (this.socket) this.socket.close();
            this.socket = new WebSocket(this.getWebSocketURL());

            let vm = this;
            this.socket.onmessage = function (event) {
                vm.handle(JSON.parse(event.data.toString()));
            };
        },
        destroyed() {
            if (this.socket) this.socket.close();
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
