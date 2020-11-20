Vue.component('game-field', {
    template: `
        <div class="game-container" >
            <div class="points">
                Points: {{gameRunning ? points : '-'}}
            </div>
            <button
                :disabled="!gameRunning"
                @click="reconfigure"
                class="reconfigure button"
            >
                Reconfigure
            </button>
            <button :disabled="!gameRunning" @click="restart" class="restart button">
                Restart Game
            </button>
            <div class="game-field" >
                <div class="column" v-for="(col, colIndex) in field">
                    <div
                        class="marble"
                        @mouseenter="marble ? onHover(colIndex, rowIndex) : null"
                        @mouseleave="marble ? endHover() : null"
                        @click="marble ? move(colIndex, rowIndex) : null"
                        v-for="(marble, rowIndex) in col"
                        :style="marbleStyle(marble)"
                    ></div>
                </div>
            </div>
        </div>`,
    methods: {
        marbleStyle: function (marble) {
            return marble
                ? {
                      cursor: 'pointer',
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
        onHover: function (column, row) {
            this.debounce('startHoverDebounce', () =>
                this.sendToSocket('hover', { column, row })
            );
        },
        endHover: function () {
            this.debounce('endHoverDebounce', () => {
                this.field.forEach((column) =>
                    column.forEach((marble) => {
                        if (marble) {
                            marble.highlight = false;
                        }
                    })
                );
            });
        },
        updateHover: function ({ marbles }) {
            marbles.forEach((coord) => {
                this.field[coord.column][coord.row].highlight = true;
            });
        },
        updateState: function ({ field, points }) {
            this.field = field.map((col) =>
                col.map((marble) =>
                    marble ? { ...marble, highlight: false } : null
                )
            );
            this.points = points;
            this.gameRunning = true;
        },
        move: function (column, row) {
            this.sendToSocket('move', { column, row });
        },
        reconfigure: function () {
            this.$emit('reconfigure');
        },
        restart: function () {
            this.$emit('restart');
        },
        debounce: function (debounceFuncId, func, timeout = 250) {
            if (this[debounceFuncId]) {
                clearTimeout(this[debounceFuncId]);
            }
            this[debounceFuncId] = setTimeout(func, timeout);
        },
    },
});
