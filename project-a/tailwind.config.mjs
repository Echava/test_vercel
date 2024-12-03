/** @type {import('tailwindcss').Config} */
export default {
	content: [
		'./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}',
	],

	theme: {
		extend: {
			colors: {
				customLightGreen: '#A8D5BA',
				customGreen: '#6FA582',
			  },
		},
	},
	plugins: [],
}
