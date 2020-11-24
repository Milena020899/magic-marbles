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
        gameRunning: function () {
            return this.$store.state.gameRunning;
        },
        gameOver: function () {
            return this.$store.state.over;
        },
        settingsModalConfig: function () {
            return {
                closeable: this.$store.state.gameRunning,
                title: this.$store.state.gameRunning
                    ? 'Reconfigure'
                    : 'Create Game',
                buttonText: 'Restart Game',
            };
        },
        blurStyle: function () {
            if (this.showModal && this.gameRunning || this.gameOver)
                return {filter: 'blur(1.0rem)'};
            else return {};
        },
    },
    created: function () {
        this.$store.dispatch('syncCall')
    }
});
