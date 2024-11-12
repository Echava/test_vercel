// rulesStores.js
import { writable } from 'svelte/store';

// Definir la estructura inicial del store de reglas
const initialRules = {
  categories: {
    nullRules: [],
    notNullRules: [],
    variableTypeRules: [],
    sizeRules: [],
    minimumAndMaximumRules: [],
    duplicationRules: [],
    comparisonsWithOtherColumnRules: [],
    comparisonsWithDateRules: [],
    orderColumnsRules: [],
    rangeWithWordRules: [], // Aquí se almacenarán las reglas creadas en el componente rangeWithWordRules.svelte
    dateRangeRules: []
  }
};

// Crear el store utilizando Svelte writable
export const categoriesStore = writable(initialRules);
