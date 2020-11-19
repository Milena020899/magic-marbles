Vue.component('settings', {
    mixins: [websocketMixin],
    data: function () {
        return {
            settings: {
                width: 0,
                height: 0,
                connectedMarbles: 0,
                remainingMarbleDeduction: 1,
            },
        };
    },
    props: ['title', 'closeable', 'button-text'],
    template: `<modal :title="title" :closeable="closeable" @close-modal="closeSettings" >
                <form id="settings" @submit="submitSettings">
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
        updateSettings: function (settings) {
            this.settings.width = settings.width;
            this.settings.height = settings.height;
            this.settings.connectedMarbles = settings.connectedMarbles;
            this.settings.remainingMarbleDeduction =
                settings.remainingMarbleDeduction;
        },
    },
    created: function () {
        this.onMessage('settings', this.updateSettings);
    },
});
