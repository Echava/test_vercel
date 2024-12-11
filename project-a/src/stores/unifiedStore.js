import { writable, derived, get } from 'svelte/store';
import { headersStore } from './headersStore';
import { excelFileStore } from './excelFileStore';
import { rulesStore } from './rulesStores';

// Función para verificar si estamos en el navegador
const isBrowser = typeof window !== 'undefined' && typeof window.document !== 'undefined';

// Store unificado
export const unifiedStore = derived(
  [headersStore, excelFileStore, rulesStore],
  ([$headers, $excelFile, $rules]) => ({
    headers: $headers,
    excelFile: $excelFile,
    rules: $rules.rules,
    report: $rules.report
  })
);

// Persistencia
if (isBrowser) {
  unifiedStore.subscribe(state => {
    const persistedState = {
      ...state,
      excelFile: state.excelFile ? state.excelFile.name : null
    };
    localStorage.setItem('appState', JSON.stringify(persistedState));
  });
}

// Carga inicial
const loadInitialState = () => {
  if (isBrowser) {
    const savedState = JSON.parse(localStorage.getItem('appState') || '{}');
    if (savedState.headers) headersStore.set(savedState.headers);
    if (savedState.rules) rulesStore.set({ rules: savedState.rules, report: savedState.report });
    if (savedState.excelFile) excelFileStore.set(null);
  }
};

if (isBrowser) {
  loadInitialState();
}

// Funciones auxiliares
export const getHeaders = () => get(headersStore);
export const getExcelFile = () => get(excelFileStore);
export const getRules = () => get(rulesStore).rules;
export const getReport = () => get(rulesStore).report;

export const setHeaders = (headers) => headersStore.set(headers);
export const setExcelFile = (file) => excelFileStore.set(file);
export const setRules = (rules) => rulesStore.update(store => ({ ...store, rules }));
export const setReport = (report) => rulesStore.update(store => ({ ...store, report }));

// Función para generar el JSON final
export const generateFinalJSON = () => {
  const state = get(unifiedStore);
  return JSON.stringify({
    rules: state.rules,
    report: state.report
  });
};

// Función para verificar si los datos necesarios están disponibles
export const isDataReady = () => {
  const state = get(unifiedStore);
  return state.headers.length > 0 && state.excelFile !== null && Object.keys(state.rules.categories).length > 0;
};

export default unifiedStore;