/** @type {import('tailwindcss').Config} */
export default {
	content: [
		'./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}',
	],

	theme: {
		extend: {
			colors: {
				customLightGreen: '#dcebc9',
				customGreen: '#66ab2f',
			  },
		},
	},
	plugins: [],
}
