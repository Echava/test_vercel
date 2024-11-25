// rulesStores.js
import { writable } from 'svelte/store';

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
    rangeWithWordRules: [],
    dateRangeRules: []
  },
  report: {
    headers: []
  }
};

export const rulesStore = writable(initialRules);


