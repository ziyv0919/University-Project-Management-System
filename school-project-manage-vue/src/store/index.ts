import { PiniaCustomStateProperties, defineStore } from 'pinia';

const useTestStore = defineStore<
	string,
	{
		count: number;
	},
	{
		doubleCount: (state: { count: number } & PiniaCustomStateProperties<{ count: number }>) => number;
	},
	{
		changeCount: (count: number) => void;
	}
>('testStore', {
	state: () => ({
		count: 1,
	}),
	getters: {
		doubleCount: (state) => state.count * 2,
	},
	actions: {
		changeCount(count) {
			this.count = count;
		},
	},
});

export default useTestStore;
