const app = new Vue({
    el: '#app',
    mixins: [websocketMixin],
    data: {
        gameRunning: true,
        settingsVisible: false,
    },
    computed: {
        settingsConfig: function () {
            return {
                closeable: this.gameRunning,
                title: this.gameRunning ? 'Reconfigure' : 'Create Game',
                buttonText: 'Restart Game',
            };
        },
    },
    methods: {
        submitSettings: function (e) {
            e.preventDefault();
            this.sendToSocket('reconfigure', { ...this.settings });
        },
        reconfigure: function () {
            this.settingsVisible = true;
        },
        restart: function () {},
        showGameOver: function (points) {},
        settingsClose: function () {
            this.settingsVisible = false;
        },
    },
});
