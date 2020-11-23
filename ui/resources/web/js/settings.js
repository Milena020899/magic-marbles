Vue.component('settings', {
    store,
    data: function () {
        return {
            settings: {...this.$store.settings},
        };
    },
    props: ['button-text'],
    template: `<form id="settings" @submit="submitSettings">
                    <div class="settings-input-group">
                        <label for="width">Field Width</label>
                        <input id="width" class="settings-input" v-model="settings.width" required type="number" min="0" value="10" name="width">
                    </div>
                    <div class="settings-input-group">
                        <label for="height">Field Height</label>
                        <input id="height" class="settings-input"  v-model="settings.height" required type="number" min="0" value="10" name="height">
                    </div>
                    <div class="settings-input-group">
                        <label for="minimumConnectedMarbles">Minimum of connected marbles</label>
                        <input id="minimumConnectedMarbles" class="settings-input" required v-model="settings.minimumConnectedMarbles" type="number" min="0" value="3"
                            name="connectedMarbles">
                    </div>
                    <div class="settings-input-group">
                        <label for="remainingMarblePenalty">Remaining marble point reduction</label>
                        <input id="remainingMarblePenalty" class="settings-input" required v-model="settings.remainingMarblePenalty" type="number" min="0"
                        value="50" name="remainingMarbleDeduction">
                    </div>
                    <div id="form-errors" v-if="settingsErrors">
                        <div class="settings-error" v-for="error in settingsErrors">{{error}}</div>
                    </div>
                    <button id="configure" class="button" type="submit" name="reconfigure">{{buttonText}}</button>
                </form>`,
    computed: {
        settingsErrors: function () {
            return this.$store.state.settingsErrors
        }
    },
    methods: {
        submitSettings: function (e) {
            e.preventDefault();
            this.$store.dispatch('startWithConfiguration', this.settings);
        },
    },
});
