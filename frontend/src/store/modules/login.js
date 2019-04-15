const state = {
    showLoginDialog: false
};

const getters = {

};

const actions = {

};

const mutations = {
    showLoginDialog (state) {
        state.showLoginDialog = true
    },
    hideLoginDialog (state) {
        state.showLoginDialog = false
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}
