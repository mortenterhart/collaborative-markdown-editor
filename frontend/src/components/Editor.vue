<template>
    <markdown-editor
            v-model="content"
            ref="markdownEditor"
            id="foo"
            :configs="configs"
    ></markdown-editor>
</template>

<script>
    import debounce from 'debounce';
    import markdownEditor from 'vue-simplemde/src/markdown-editor';

    export default {
        name: "SimpleMDE",
        data: function() {
            return {
                content: '',
                currentDocState: -1,
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
            simplemde() {
                return this.$refs.markdownEditor.simplemde;
            }
        },
        methods: {
            sendContentDiffAfterTyping: debounce(e => {
                e.reduceContentChanges();
                e.sendWebSocketMessages();
                e.contentChanges = [];
            }, 400),
            reduceContentChanges: function() {
                for (let i = 0; i < this.contentChanges.length - 1; i++) {
                    if (this.contentChanges[i].type === this.contentChanges[i + 1].type) {
                        if (this.contentChanges[i].type === 'Delete') {
                            this.contentChanges[i].pos = this.contentChanges[i + 1].pos;
                        }
                        this.contentChanges[i].msg += this.contentChanges[i + 1].msg;
                        this.contentChanges.splice(i + 1, 1);
                        i -= 1;
                    }
                }
            },
            sendWebSocketMessages: function() {
                for (let i = 0; i < this.contentChanges.length; i++) {
                    const msg = JSON.stringify({
                        "userId": this.$store.state.login.user.id,
                        "docId": Number(this.$route.params.id),
                        "cursorPos": this.contentChanges[i].pos,
                        "docState": this.currentDocState,
                        "msg": this.contentChanges[i].msg,
                        "messageType": this.contentChanges[i].type
                    });
                    this.$emit('sendWebSocketMessage', msg);
                }
            },
            handleEditorWebSocketEvents(data) {
                switch (data.messageType) {
                    case "DocumentTitle": {
                        this.$store.commit('app/setTitle', data.msg);
                        this.setHTMLTitle(data.msg);
                        break;
                    }
                    case "ContentInit": {
                        this.content = data.msg;
                        this.currentDocState = data.docState;
                        this.$emit('contentWasChanged', this.content);
                        break;
                    }
                    case "UsersInit": {
                        this.$store.commit('app/setOtherCollaborators', JSON.parse(data.msg));
                        break;
                    }
                    case "Insert": {
                        this.currentDocState = data.docState;
                        const insertPos = this.getCursorFromTotalCursorPos(this.content, data.cursorPos);
                        this.simplemde.codemirror.getDoc().replaceRange(data.msg, insertPos);
                        if (this.contentChanges.length > 0) {
                            this.adjustContentChangesArray(data, insertPos);
                        }
                        this.$emit('contentWasChanged', this.content);
                        break;
                    }
                    case "Delete": {
                        this.currentDocState = data.docState;
                        const deletePos = this.getCursorFromTotalCursorPos(this.content, data.cursorPos);
                        this.simplemde.codemirror.getDoc().replaceRange("", deletePos, this.getCursorFromTotalCursorPos(this.content, data.cursorPos + data.msg.length));
                        if (this.contentChanges.length > 0) {
                            this.adjustContentChangesArray(data, deletePos);
                        }
                        this.$emit('contentWasChanged', this.content);
                        break;
                    }
                    case "UserJoined": {
                        this.$store.commit('app/addCollaborator', JSON.parse(data.msg));
                        this.$snotify.info(
                            JSON.parse(data.msg).name + ' joined the document',
                            'Info'
                        );
                        break;
                    }
                    case "UserLeft": {
                        this.$store.commit('app/removeCollaborator', JSON.parse(data.msg));
                        this.$snotify.info(
                            JSON.parse(data.msg).name + ' left the document',
                            'Info'
                        );
                        break;
                    }
                }
            },
            adjustContentChangesArray(data, pos) {
                for (let i = 0; i < this.contentChanges.length; i++) {
                    if (this.contentChanges[i].pos > pos) {
                        continue;
                    }
                    if (this.contentChanges[i].type === 'Insert') {
                        this.contentChanges[i].pos += data.msg.length;
                    } else {
                        this.contentChanges[i].pos -= data.msg.length;
                    }
                }
            },
            getCurrentCursorPos() {
                return this.getTotalCursorPos(this.content, this.simplemde.codemirror.getCursor('start').line, this.simplemde.codemirror.getCursor('start').ch);
            },
            getTotalCursorPos(content, line, cursorPos) {
                let totalCursorPos = 0;
                const lines = content.split('\n');
                for (let i = 0; i < lines.length - 1; i++) {
                    if (line === i) {
                        return totalCursorPos + cursorPos;
                    }
                    totalCursorPos += lines[i].length + 1;
                }
                return totalCursorPos + cursorPos;
            },
            getCursorFromTotalCursorPos(content, totalCursorPos) {
                let currPos = totalCursorPos;
                const lines = content.split('\n');
                for (let i = 0; i < lines.length; i++) {
                    if (currPos <= lines[i].length) {
                        return {
                            line: i,
                            ch: currPos
                        };
                    }
                    currPos -= lines[i].length + 1;
                }
            },
            buildMessageStringFromArray(array) {
                let msg = "";
                for (let i = 0; i < array.length; i++) {
                    if (i !== 0) {
                        msg += '\n';
                    }
                    msg += array[i];
                }
                return msg;
            },
            setHTMLTitle(documentName) {
                document.title = documentName + ' | Collaborative Markdown Editor';
            }
        },
        mounted() {
            this.$emit('contentWasChanged', this.content);

            let vm = this;
            this.simplemde.codemirror.on("change", function(editor, changeObj) {
                if (!changeObj.origin || changeObj.origin === "setValue") {
                    return;
                }

                const deleteMessage = vm.buildMessageStringFromArray(changeObj.removed);
                if (deleteMessage.length > 0) {
                    vm.contentChanges.push({
                        type: 'Delete',
                        msg: deleteMessage,
                        pos: vm.getTotalCursorPos(vm.content, changeObj.from.line, changeObj.from.ch)
                    });
                }

                const insertMessage = vm.buildMessageStringFromArray(changeObj.text);
                if (insertMessage.length > 0) {
                    vm.contentChanges.push({
                        type: 'Insert',
                        msg: insertMessage,
                        pos: vm.getTotalCursorPos(vm.content, changeObj.from.line, changeObj.from.ch)
                    });
                }

                vm.sendContentDiffAfterTyping(vm);
                vm.$emit('contentWasChanged', vm.content);
            })
        }
    };
</script>

<style>
    @import '~simplemde/dist/simplemde.min.css';

    .markdown-editor {
        height: 95% !important;
    }

    .CodeMirror {
        height: 95%;
    }
</style>
