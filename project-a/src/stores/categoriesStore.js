import { writable } from 'svelte/store';

// Definir las categorías iniciales
const initialCategories = [
  { name: 'notNull' },
  { name: 'numeric' },
  { name: 'string' },
  { name: 'uuid' },
  { name: 'binary' },
  { name: 'boolean' }
];

// Crear el store con las categorías iniciales
export const categoriesStore = writable(initialCategories);
