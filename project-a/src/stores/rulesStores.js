// rulesStores.js
import { writable } from 'svelte/store';

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
      dateRangeRules: [],
      specificValuesRules: [],
      conditionalNonNullRules: [],    
    },
    dictionaries: []
  },
  report: {
    headers: []
  }
};

export const rulesStore = writable(initialRules);


