const state = {
    title: '',
    currentDocument: { repo: { owner: { id: -1, name: '' } } },
    editorKey: 0,
    otherCollaborators: []
};

const getters = {

};

const actions = {

};

const mutations = {
    setTitle: (state, newValue) => {
        state.title = newValue
    },
    setOtherCollaborators: (state, newValue) => {
        state.otherCollaborators = newValue
    },
    addCollaborator: (state, name) => {
        for (let i = 0; i < state.otherCollaborators.length; i++) {
            if (state.otherCollaborators[i].name === name) {
                return
            }
        }

        state.otherCollaborators = [...state.otherCollaborators, {id: String(state.otherCollaborators.length), name: name, imageUrl: ''}]
    },
    removeCollaborator: (state, name) => {
        for (let i = 0; i < state.otherCollaborators.length; i++) {
            if (state.otherCollaborators[i].name === name) {
                state.otherCollaborators.splice(i, 1)
                return
            }
        }
    },
    incEditorKey: (state) => {
        state.editorKey++
    },
    setCurrentDocument: (state, newValue) => {
        state.currentDocument = newValue
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}
