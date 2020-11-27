const store = new Vuex.Store({
    state: {
        settings: {},
        settingsErrors: null,
        showModal: false,
        field: null,
        highlightedMarbles: [],
        points: 0,
        over: false,
        stateId: 0,
    },
    getters: {
        gameStatePresent: state => state.field != null
    },
    actions: {
        async syncCall({dispatch}) {
            let response = await fetch(`${window.location.href}/sync`, {
                ...defaultFetchConfig,
            });
            dispatch('syncState', await response.json());
        },
        syncState({commit, dispatch}, {settings, gameState}) {
            commit('setSettings', settings);
            if (gameState === null || gameState === undefined) {
                dispatch('toInitialState');
            } else {
                dispatch('startGame', gameState);
            }
        },
        async startWithConfiguration({commit, dispatch}, settings) {
            commit('clearSettingsErrors');
            try {
                let response = await fetch(
                    `${window.location.href}/startWithConfiguration`,
                    {
                        ...defaultFetchConfig,
                        body: JSON.stringify(settings),
                    }
                );

                if (response.status === 200) {
                    commit('setSettings', settings);
                    dispatch('startGame', await response.json());
                }

                if (response.status === 400) {
                    let errResponse = await response.json();

                    if (errResponse.errors) {
                        commit('setSettingsErrors', errResponse.errors);
                    } else {
                        commit('setSettingsErrors', [errResponse.error]);
                    }
                }
            } catch (e) {
            }
        },
        async restart({dispatch}) {
            let response = await fetch(`${window.location.href}/restart`, {
                ...defaultFetchConfig,
            });

            if (response.status === 200) {
                dispatch('updateGameState', await response.json());
            }

            if (response.status === 404) {
                dispatch('toInitialState');
            }
        },
        async move({state, commit, dispatch}, coordinates) {
            let response = await fetch(`${window.location.href}/move`, {
                ...defaultFetchConfig,
                body: JSON.stringify({stateId: state.stateId, coordinates}),
            });

            if (response.status === 200) {
                dispatch('updateGameState', await response.json());
            }

            if (response.status === 404) {
                dispatch('toInitialState');
            }

            if (response.status === 409) {
                dispatch('syncState', await response.json());
            }
        },
        async hover({state, commit, dispatch}, coordinates) {
            let response = await fetch(`${window.location.href}/hover`, {
                ...defaultFetchConfig,
                body: JSON.stringify({stateId: state.stateId, coordinates}),
            });

            if (response.status === 200) {
                let {marbles} = await response.json();
                commit('highlightMarbles', marbles);
            }

            if (response.status === 404) {
                dispatch('toInitialState');
            }

            if (response.status === 409) {
                dispatch('syncState', await response.json());
            }
        },
        toInitialState({commit, dispatch}) {
            commit('hideModal');
            commit('clearSettingsErrors');
            dispatch('updateGameState', {field: null, points: 0, over: false, stateId: null})
        },
        startGame({commit, dispatch}, gameState) {
            dispatch('updateGameState', gameState);
            commit('hideModal');
        },
        updateGameState({commit}, {field, points, over, stateId}) {
            commit('setField', field);
            commit('setPoints', points);
            commit('setGameOver', over);
            commit('setStateId', stateId);
        },
    },
    mutations: {
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
            state.settingsErrors = errors;
        },
        clearSettingsErrors(state) {
            state.settingsErrors = null;
        },
        setField(state, newField) {
            state.field = newField?.map((col) =>
                col.map((marble) =>
                    marble ? {...marble, highlight: false} : null
                )
            );
        },
        setPoints(state, points) {
            state.points = points;
        },
        setGameOver(state, gameOverState) {
            state.over = gameOverState;
        },
        setStateId(state, stateId) {
            state.stateId = stateId;
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
