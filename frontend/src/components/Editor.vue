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
                if (this.content.length !== this.lastReceivedContent.length) {
                    this.sendContentDiff(this)
                    this.cursorPosition = this.getTotalCursorPos(this.content, this.simplemde.codemirror.getCursor('start').line, this.simplemde.codemirror.getCursor('start').ch)
                }
            },
            sendContentDiff: debounce(e => {
                const pos = e.cursorPosition - (e.content.length - e.lastReceivedContent.length)
                const msg = JSON.stringify({
                    "userId": e.$store.state.login.user.id,
                    "docId": Number(e.$route.params.id),
                    "cursorPos": pos,
                    "msg": e.content.substring(pos, e.cursorPosition),
                    "messageType": "Insert"
                })
                console.log(msg)
                e.socket.send(msg);

                e.lastReceivedContent = e.content
            }, 400),
            handle(data) {
                console.log(data)
                switch (data.messageType) {
                    case "DocumentTitle":
                        this.$store.commit('app/setTitle', data.msg)
                        break;
                    case "ContentInit":
                        this.lastReceivedContent = data.msg
                        this.content = data.msg
                        break;
                    case "Insert":
                        const cursor = this.getCursorFromTotalCursorPos(this.content, data.cursorPos)
                        this.lastReceivedContent = this.content.substring(0, data.cursorPos) + data.msg + this.content.substring(data.cursorPos)
                        this.simplemde.codemirror.getDoc().replaceRange(data.msg, cursor)
                        break;
                }
            },
            getTotalCursorPos(content, line, cursorPos) {
                let totalCursorPos = 0
                const lines = content.split('\n')
                for (let i = 0; i < lines.length - 1; i++) {
                    if (line === i)
                        return totalCursorPos + cursorPos
                    totalCursorPos += lines[i].length + 1
                }
                return totalCursorPos + cursorPos
            },
            getCursorFromTotalCursorPos(content, totalCursorPos) {
                let currPos = totalCursorPos
                const lines = content.split('\n')
                for (let i = 0; i < lines.length; i++) {
                    if (currPos <= lines[i].length) {
                        return {
                            line: i,
                            ch: currPos
                        }
                    }
                    currPos -= lines[i].length + 1
                    console.log(currPos)
                }
            },
            getWebSocketURL() {
                return `ws://localhost:8080/CMD/ws/${this.$route.params.id}/${this.$store.state.login.user.name}`;
            }
        },
        mounted() {
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
