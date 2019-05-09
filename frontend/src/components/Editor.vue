<template>
    <markdown-editor
            v-model="content"
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
            sendContentDiff: debounce(e => {
                if (e.content.length === e.lastReceivedContent.length) {
                    return
                }

                let pos, message, messageType
                if (e.content.length > e.lastReceivedContent.length ) {
                    pos = e.cursorPosition - (e.content.length - e.lastReceivedContent.length)
                    message = e.content.substring(pos, e.cursorPosition)
                    messageType = "Insert"
                } else if (e.content.length < e.lastReceivedContent.length) {
                    pos = e.getCurrentCursorPos()
                    message = e.lastReceivedContent.substring(pos, pos + (e.lastReceivedContent.length - e.content.length))
                    messageType = "Delete"
                }

                const msg = JSON.stringify({
                    "userId": e.$store.state.login.user.id,
                    "docId": Number(e.$route.params.id),
                    "cursorPos": pos,
                    "msg": message,
                    "messageType": messageType
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
                        this.$emit('contentWasChanged', this.content);
                        break;
                    case "UsersInit":
                        this.$store.commit('app/setOtherCollaborators', JSON.parse(data.msg))
                        break;
                    case "Insert":
                        this.lastReceivedContent = this.content.substring(0, data.cursorPos) + data.msg + this.content.substring(data.cursorPos)
                        this.simplemde.codemirror.getDoc().replaceRange(data.msg, this.getCursorFromTotalCursorPos(this.content, data.cursorPos))
                        break;
                    case "Delete":
                        this.lastReceivedContent = this.content.substring(0, data.cursorPos) + this.content.substring(data.cursorPos + data.msg.length)
                        this.simplemde.codemirror.getDoc().replaceRange("", this.getCursorFromTotalCursorPos(this.content, data.cursorPos), this.getCursorFromTotalCursorPos(this.content, data.cursorPos + data.msg.length))
                        break;
                    case "UserJoined":
                        this.$store.commit('app/addCollaborator', data.msg)
                        this.$snotify.info(
                            data.msg + ' joined the document',
                            'Info'
                        );
                        break;
                    case "UserLeft":
                        this.$store.commit('app/removeCollaborator', data.msg)
                        this.$snotify.info(
                            data.msg + ' left the document',
                            'Info'
                        );
                        break;
                }
            },
            getCurrentCursorPos() {
                return this.getTotalCursorPos(this.content, this.simplemde.codemirror.getCursor('start').line, this.simplemde.codemirror.getCursor('start').ch)
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
                }
            },
            buildMessageStringFromArray(array) {
                let msg = ""
                for (let i = 0; i < array.length; i++) {
                    if (i !== 0)
                        msg += '\n'
                    msg += array[i]
                }
                return msg
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

            this.simplemde.codemirror.on("change", function(editor, changeObj) {
                if (changeObj.origin === "setValue")
                    return

                if (changeObj.origin !== "paste" || (changeObj.removed.length === 1 && changeObj.removed[0].length === 0)) {
                    // Normal insert or delete event
                    vm.$emit('contentWasChanged', vm.content);
                    if (vm.content.length !== vm.lastReceivedContent.length) {
                        vm.sendContentDiff(vm)
                        vm.cursorPosition = vm.getCurrentCursorPos()
                    }
                    return
                }

                // The remaining function covers the handling of replace operations
                const deleteMsg = JSON.stringify({
                    "userId": vm.$store.state.login.user.id,
                    "docId": Number(vm.$route.params.id),
                    "cursorPos": vm.getTotalCursorPos(vm.content, changeObj.from.line, changeObj.from.ch),
                    "msg": vm.buildMessageStringFromArray(changeObj.removed),
                    "messageType": "Delete"
                })
                vm.socket.send(deleteMsg);

                const insertMsg = JSON.stringify({
                    "userId": vm.$store.state.login.user.id,
                    "docId": Number(vm.$route.params.id),
                    "cursorPos": vm.getTotalCursorPos(vm.content, changeObj.from.line, changeObj.from.ch),
                    "msg": vm.buildMessageStringFromArray(changeObj.text),
                    "messageType": "Insert"
                })
                vm.socket.send(insertMsg);

                vm.lastReceivedContent = vm.content
            })
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
