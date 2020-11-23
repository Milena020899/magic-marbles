const store = new Vuex.Store({
    state: {
        field: [],
        settings: {},
        settingsErrors: null,
        gameRunning: false,
        showModal: false,
        highlightedMarbles: [],
        points: 0,
    },
    actions: {
        async startWithConfiguration({commit, dispatch}, settings) {
            commit('clearSettingsErrors')
            try {
                let response = await fetch(`${window.location.href}/startWithConfiguration`, {
                    ...defaultFetchConfig,
                    body: JSON.stringify(settings)
                })

                console.log(response.status)

                if (response.status === 200) {
                    dispatch('startGame', await response.json())
                }

                if (response.status === 400) {
                    let {errors} = await response.json()
                    commit('setSettingsErrors', errors)
                }
            } catch (e) {
            }
        },
        async restart({dispatch}) {
            let response = await fetch(`${window.location.href}/restart`, {
                ...defaultFetchConfig,
            })

            if (response.status === 200) {
                dispatch('updateGameState', await response.json())
            }

            if (response.status === 404) {
                dispatch('toInitialState')
            }
        },
        async move({commit, dispatch}, coordinates) {
            let response = await fetch(`${window.location.href}/move`, {
                ...defaultFetchConfig,
                body: JSON.stringify(coordinates)
            })

            if (response.status === 200) {
                dispatch('updateGameState', await response.json())
            }

            if (response.status === 404) {
                dispatch('toInitialState')
            }

            if (response.status === 400) {
                // TODO implement error display
            }
        },
        async hover({commit, dispatch}, coordinates) {
            let response = await fetch(`${window.location.href}/hover`, {
                ...defaultFetchConfig,
                body: JSON.stringify(coordinates)
            })

            if (response.status === 200) {
                let {marbles} = await response.json()
                commit('highlightMarbles', marbles)
            }

            if (response.status === 404) {
                dispatch('toInitialState')
            }
        },
        toInitialState({commit}) {
            commit('showModal')
            commit('setNoGameRunning')
        },
        startGame({commit, dispatch}, gameState) {
            dispatch('updateGameState', gameState)
            commit('setGameRunning')
            commit('hideModal')
        },
        updateGameState({commit}, {field, points}) {
            commit('setField', field);
            commit('setPoints', points);
        },
    },
    mutations: {
        setGameRunning(state) {
            state.gameRunning = true;
        },
        setNoGameRunning(state) {
            state.gameRunning = false;
        },
        showModal(state) {
            state.showModal = true;
        },
        hideModal(state) {
            state.showModal = false;
        },
        setSettings(state, settings) {
            state.settings = settings;
        },
        setSettingsErrors(state, errors) {
            state.settingsErrors = errors
        },
        clearSettingsErrors(state) {
            state.settingsErrors = null
        },
        setField(state, newField) {
            state.field = newField.map((col) =>
                col.map((marble) =>
                    marble ? {...marble, highlight: false} : null
                )
            );
        },
        setPoints(state, points) {
            state.points = points;
        },
        highlightMarbles(state, marbles) {
            marbles.forEach((coord) => {
                state.field[coord.column][coord.row].highlight = true;
            });
        },
        removeHighlight(state) {
            state.field.forEach((column) =>
                column.forEach((marble) => {
                    if (marble) {
                        marble.highlight = false;
                    }
                })
            );
        },
    },
});
