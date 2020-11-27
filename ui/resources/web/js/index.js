const app = new Vue({
    store,
    el: '#app',
    data: {
        websocket: null,
    },
    computed: {
        showModal: function () {
            return this.$store.state.showModal;
        },
        gameStatePresent: function () {
            return this.$store.getters.gameStatePresent;
        },
        gameOver: function () {
            return this.$store.state.over;
        },
        settingsModalConfig: function () {
            return {
                closeable: this.gameStatePresent,
                title: this.gameStatePresent
                    ? 'Reconfigure'
                    : 'Create Game',
                buttonText: 'Restart Game',
            };
        },
        blurStyle: function () {
            if ((this.showModal && this.gameStatePresent) || this.gameOver)
                return {filter: 'blur(1.0rem)'};
            else return {};
        },
    },
    created: function () {
        this.$store.dispatch('syncCall');
    },
});
