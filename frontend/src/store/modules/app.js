const state = {
    title: '',
    editorKey: 0
};

const getters = {

};

const actions = {

};

const mutations = {
    setTitle: (state, newValue) => {
        state.title = newValue
    },
    incEditorKey: (state) => {
        state.editorKey++
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}
