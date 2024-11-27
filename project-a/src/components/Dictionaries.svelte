<script>
    import { rulesStore } from '../stores/rulesStores.js';
    import { headersStore } from '../stores/headersStore.js';
    import { ChevronDown, ChevronUp, Plus, X } from 'lucide-svelte';

    let selectedColumn = '';
    let dictionaryName = '';
    let isExpanded = false;

    $: headers = $headersStore;
    $: dictionaries = $rulesStore.rules.dictionaries || [];

    function addDictionary() {
        if (selectedColumn && dictionaryName) {
            rulesStore.update(store => {
                if (!store.rules.dictionaries) {
                    store.rules.dictionaries = [];
                }
                store.rules.dictionaries.push({
                    columnName: selectedColumn,
                    dictionaryName: dictionaryName
                });
                return store;
            });
            // Reset fields
            selectedColumn = '';
            dictionaryName = '';
        }
    }

    function removeDictionary(index) {
        rulesStore.update(store => {
            store.rules.dictionaries.splice(index, 1);
            return store;
        });
    }

    function toggleExpand() {
        isExpanded = !isExpanded;
    }
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
        <h2 class="text-lg font-bold text-white">Diccionarios</h2>
        <button on:click={toggleExpand} class="text-white hover:text-blue-300 transition-colors" aria-label={isExpanded ? "Contraer sección" : "Expandir sección"}>
            {#if isExpanded}
                <ChevronUp />
            {:else}
                <ChevronDown />
            {/if}
        </button>
    </div>

    {#if isExpanded}
        <div class="mt-4 space-y-4">
            <div class="space-y-2">
                <select 
                    bind:value={selectedColumn} 
                    class="w-full bg-zinc-700 text-white p-2 rounded-md"
                    aria-label="Seleccionar columna para el diccionario"
                >
                    <option value="">Seleccionar columna</option>
                    {#each headers as header}
                        <option value={header}>{header}</option>
                    {/each}
                </select>

                <input
                    type="text"
                    bind:value={dictionaryName}
                    placeholder="Nombre del diccionario"
                    class="w-full bg-zinc-700 text-white p-2 rounded-md"
                    aria-label="Nombre del diccionario"
                />

                <button 
                    on:click={addDictionary} 
                    disabled={!selectedColumn || !dictionaryName}
                    class="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    aria-label="Agregar diccionario"
                >
                    <Plus class="inline-block mr-2" />
                    Agregar Diccionario
                </button>
            </div>

            {#if dictionaries.length > 0}
                <div class="space-y-2">
                    {#each dictionaries as dictionary, index}
                        <div class="bg-zinc-700 p-3 rounded-md flex justify-between items-center">
                            <div>
                                <span class="text-white font-semibold">{dictionary.columnName}</span>
                                <span class="text-gray-400 ml-2">-</span>
                                <span class="text-gray-300 ml-2">{dictionary.dictionaryName}</span>
                            </div>
                            <button 
                                on:click={() => removeDictionary(index)}
                                class="text-red-400 hover:text-red-600 transition-colors"
                                aria-label={`Eliminar diccionario para ${dictionary.columnName}`}
                            >
                                <X />
                            </button>
                        </div>
                    {/each}
                </div>
            {/if}
        </div>
    {/if}
</div>