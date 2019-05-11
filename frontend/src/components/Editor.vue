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
                content: '',
                contentChanges: [],
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
            sendContentDiffAfterTyping: debounce(e => {
                e.reduceContentChanges()
                e.sendWebSocketMessages()
                e.contentChanges = []
            }, 400),
            reduceContentChanges: function() {
                for (let i = 0; i < this.contentChanges.length - 1; i++) {
                    if (this.contentChanges[i].type === this.contentChanges[i + 1].type) {
                        if (this.contentChanges[i].type === 'Delete') {
                            this.contentChanges[i].pos = this.contentChanges[i + 1].pos
                        }
                        this.contentChanges[i].msg += this.contentChanges[i + 1].msg
                        this.contentChanges.splice(i + 1, 1)
                        i -= 1
                    }
                }
            },
            sendWebSocketMessages: function() {
                for (let i = 0; i < this.contentChanges.length; i++) {
                    const msg = JSON.stringify({
                        "userId": this.$store.state.login.user.id,
                        "docId": Number(this.$route.params.id),
                        "cursorPos": this.contentChanges[i].pos,
                        "msg": this.contentChanges[i].msg,
                        "messageType": this.contentChanges[i].type
                    })
                    this.socket.send(msg);
                    console.log(msg)
                }
            },
            handleWebSocketEvents(data) {
                switch (data.messageType) {
                    case "DocumentTitle":
                        this.$store.commit('app/setTitle', data.msg)
                        break;
                    case "ContentInit":
                        this.content = data.msg
                        this.$emit('contentWasChanged', this.content);
                        break;
                    case "UsersInit":
                        let users = JSON.parse(data.msg)
                        let participants = []
                        for (let i = 0; i < users.length; i++) {
                            participants.push({id: String(i), name: users[i], imageUrl: ''})
                        }
                        this.$store.commit('app/setOtherCollaborators', participants)
                        break;
                    case "Insert":
                        this.simplemde.codemirror.getDoc().replaceRange(data.msg, this.getCursorFromTotalCursorPos(this.content, data.cursorPos))
                        this.$emit('contentWasChanged', this.content);
                        break;
                    case "Delete":
                        this.simplemde.codemirror.getDoc().replaceRange("", this.getCursorFromTotalCursorPos(this.content, data.cursorPos), this.getCursorFromTotalCursorPos(this.content, data.cursorPos + data.msg.length))
                        this.$emit('contentWasChanged', this.content);
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
                console.log(`ws://${location.hostname}:${location.port}/CMD/ws/${this.$route.params.id}/${this.$store.state.login.user.name}/${this.$store.state.login.user.id}`)
                return `ws://${location.hostname}:${location.port}/CMD/ws/${this.$route.params.id}/${this.$store.state.login.user.name}/${this.$store.state.login.user.id}`
            }
        },
        mounted() {
            this.$emit('contentWasChanged', this.content);
            if (this.socket) this.socket.close();
            this.socket = new WebSocket(this.getWebSocketURL());

            let vm = this;
            this.socket.onmessage = function (event) {
                vm.handleWebSocketEvents(JSON.parse(event.data.toString()));
            };

            this.simplemde.codemirror.on("change", function(editor, changeObj) {
                if (!changeObj.origin || changeObj.origin === "setValue") {
                    return
                }

                const deleteMessage = vm.buildMessageStringFromArray(changeObj.removed)
                if (deleteMessage.length > 0) {
                    vm.contentChanges = [...vm.contentChanges, {type: 'Delete', msg: deleteMessage, pos: vm.getTotalCursorPos(vm.content, changeObj.from.line, changeObj.from.ch)}]
                }

                const insertMessage = vm.buildMessageStringFromArray(changeObj.text)
                if (insertMessage.length > 0) {
                    vm.contentChanges = [...vm.contentChanges, {type: 'Insert', msg: insertMessage, pos: vm.getTotalCursorPos(vm.content, changeObj.from.line, changeObj.from.ch)}]
                }

                vm.sendContentDiffAfterTyping(vm)
                vm.$emit('contentWasChanged', vm.content);
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
