Vue.component('game-field', {
    store,
    template: `
        <div class="game-container" >
            <div class="points">Points: {{gameStatePresent ? points : '-'}}</div>
            <button
                :disabled="!gameStatePresent"
                @click="reconfigure"
                class="reconfigure button"
            >
                Reconfigure
            </button>
            <button :disabled="!gameStatePresent" @click="restart" class="restart button">
                Restart Game
            </button>
            <div class="game-field" >
                <div class="column" v-for="(col, colIndex) in field">
                    <div v-for="(marble, rowIndex) in col" 
                            class="marble-wrapper"
                            @mouseenter="marble ? startHover(colIndex, rowIndex) : null"
                            @mouseleave="marble ? endHover() : null"
                            @click="marble ? move(colIndex, rowIndex) : null"
                            :style="marbleWrapperStyle(marble)">
                        <div class="marble" :style="marbleStyle(marble)"/>
                    </div>
                </div>
            </div>
        </div>`,
    computed: {
        gameStatePresent: function () {
            return this.$store.getters.gameStatePresent;
        },
        points: function () {
            return this.$store.state.points;
        },
        field: function () {
            return this.$store.state.field;
        },
    },
    methods: {
        marbleStyle: function (marble) {
            return marble
                ? {
                    background: marble.color,
                    border: `4px solid ${
                        marble.highlight ? 'white' : 'transparent'
                    }`,
                }
                : {
                    background: 'rgba(100, 100, 100, 0.3)',
                    border: '4px solid transparent',
                };
        },
        marbleWrapperStyle: function (marble) {
            return marble
                ? {
                    cursor: 'pointer',
                }
                : {};
        },
        move: function (column, row) {
            this.$store.dispatch('move', {column, row});
        },
        startHover: function (column, row) {
            this.debounce('startHoverDebounce', () => {
                this.$store.dispatch('hover', {column, row});
            });
        },
        endHover: function () {
            this.debounce('endHoverDebounce', () => {
                this.$store.commit('removeHighlight');
            });
        },
        restart: function () {
            this.$store.dispatch('restart');
        },
        reconfigure: function () {
            this.$store.commit('showModal');
        },
        debounce: function (debounceFuncId, func, timeout = 150) {
            if (this[debounceFuncId]) {
                clearTimeout(this[debounceFuncId]);
            }
            this[debounceFuncId] = setTimeout(func, timeout);
        },
    },
});
