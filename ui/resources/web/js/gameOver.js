Vue.component('game-over', {
    store,
    template:
        `<div id="game-over-wrapper">
            <div id="game-over-message">You achieved {{points}} Points</div>
            <button id="game-over-restart-btn" class="button" @click="restart">Restart</button>
            <button class="button" @click="toInitial">Reconfigure and Restart</button>
        </div>`,
    computed: {
        points: function () {
            return this.$store.state.points
        }
    },
    methods: {
        restart: function () {
            this.$store.dispatch('restart');
        },
        toInitial: function () {
            this.$store.dispatch('toInitialState');
        }
    }
})