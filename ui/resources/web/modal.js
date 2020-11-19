Vue.component('modal', {
    data: function () {
        return { visible: true };
    },
    props: ['title', 'closeable'],
    template: `<div v-if="visible" class="modal-wrapper">
                    <div id="modal">
                        <div id="modal-header">
                            <h1 id="modal-title">{{title}}</h1>
                            <div @click="closeModal"  v-if="closeable" id="modal-close">X</div>
                        </div>
                       <slot></slot>
                    </div>
                </div>`,
    methods: {
        closeModal: function () {
            this.$emit('close-modal');
        },
    },
});
