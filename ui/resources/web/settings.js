Vue.component('settings', {
    data: function () {
        return {
            settings: this.initialSettings,
        };
    },
    props: ['initialSettings', 'title', 'closeable', 'button-text'],
    template: `<modal :title="title" :closeable="closeable" @close-modal="closeSettings" >
                <form id="settings" @submit="submitSettings">
                    <label for="width">Field Width</label>
                    <input id="width" v-model="settings.width" type="number" min="0" value="10" name="width">
                    <label for="height">Field Height</label>
                    <input id="height" v-model="settings.height" type="number" min="0" value="10" name="height">
                    <label for="connectedMarbles">Minimum of connected marbles to remove</label>
                    <input id="connectedMarbles" v-model="settings.connectedMarbles" type="number" min="0" value="3"
                        name="connectedMarbles">
                    <label for="remainingMarbleDeduction">Remaining marble point reduction</label>
                    <input id="remainingMarbleDeduction" v-model="settings.remainingMarbleDeduction" type="number" min="0"
                        value="50" name="remainingMarbleDeduction">
                    <button id="configure" type="submit" name="reconfigure">{{buttonText}}</button>
                </form>
            </modal>`,
    methods: {
        submitSettings: function (e) {
            e.preventDefault();
            this.$emit('settings-submit', this.settings);
        },
        closeSettings: function () {
            this.$emit('close-settings');
        },
    },
});
