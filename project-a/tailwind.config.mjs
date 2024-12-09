/** @type {import('tailwindcss').Config} */
export default {
	content: [
		'./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}',
	],

	theme: {
		extend: {
			colors: {
				customLightGreen: '#A8D5BA',
				customGreen: '#ecffee',
				customTextGreen: '#066810',
			  },
		},
	},
	plugins: [],
}
