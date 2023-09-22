export default {
    state: {
        is_record: false,
        a_steps: [],
        b_steps: [],
        record_loser: "",
        is_playback_ends: false
    },
    getters: {},
    mutations: {
        updateIsRecord (state, is_record) {
            state.is_record = is_record;
        },
        updateSteps (state, data) {
            state.a_steps = data.a_steps;
            state.b_steps = data.b_steps;
        },
        updateRecordLoser (state, record_loser) {
            state.record_loser = record_loser
        },
        updateIsPlaybackEnds (state, is_playback_ends) {
            state.is_playback_ends = is_playback_ends
        }
    },
    actions: {
    },
    modules: {},
}
