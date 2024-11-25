// rulesStore.js
import { writable } from 'svelte/store';

// Definir la estructura inicial del store de reglas
const initialRules = {
  rules: {
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
      rangeWithWordRules: [],
      dateRangeRules: []
    }
  }
};

// Crear el store utilizando Svelte writable
export const rulesStore = writable(initialRules);


