Vue.component('settings', {
    props: ['button-text'],
    template: `<form id="settings" @submit="submitSettings">
                    <div class="settings-input-group">
                        <label for="width">Field Width</label>
                        <input id="width" class="settings-input" v-model="settings.width" type="number" min="0" value="10" name="width">
                    </div>
                    <div class="settings-input-group">
                        <label for="height">Field Height</label>
                        <input id="height" class="settings-input"  v-model="settings.height" type="number" min="0" value="10" name="height">
                    </div>
                    <div class="settings-input-group">
                        <label for="connectedMarbles">Minimum of connected marbles</label>
                        <input id="connectedMarbles" class="settings-input"  v-model="settings.connectedMarbles" type="number" min="0" value="3"
                            name="connectedMarbles">
                    </div>
                    <div class="settings-input-group">
                        <label for="remainingMarbleDeduction">Remaining marble point reduction</label>
                        <input id="remainingMarbleDeduction" class="settings-input"  v-model="settings.remainingMarbleDeduction" type="number" min="0"
                        value="50" name="remainingMarbleDeduction">
                    </div>
                    <button id="configure" class="button" type="submit" name="reconfigure">{{buttonText}}</button>
                </form>`,
    methods: {
        submitSettings: function (e) {
            e.preventDefault();
            this.$emit('settings-submit', this.settings);
        },
    },
});
