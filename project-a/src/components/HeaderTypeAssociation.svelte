<script>
    import { headersStore } from '../stores/headersStore.js';
    import { categoriesStore } from '../stores/categoriesStore.js';
    import { rulesStore } from '../stores/rulesStores.js';
    import { writable } from 'svelte/store';
    import { ChevronDown, ChevronUp } from 'lucide-svelte';

    let selectedHeader = '';
    let selectedType = '';
    const associations = writable([]);
    let isExpanded = false;

    $: headers = $headersStore;
    $: variableTypes = $categoriesStore.variableTypeRules; // Corregido: accedemos directamente a variableTypeRules

    function addAssociation() {
        if (selectedHeader && selectedType) {
            associations.update(assocs => {
                if (!assocs.some(a => a.header === selectedHeader)) {
                    return [...assocs, { header: selectedHeader, type: selectedType }];
                }
                return assocs;
            });
            selectedHeader = '';
            selectedType = '';
            updateRulesStore();
        }
    }

    function removeAssociation(header) {
        associations.update(assocs => assocs.filter(a => a.header !== header));
        updateRulesStore();
    }

    function updateRulesStore() {
        rulesStore.update(rules => ({
            ...rules,
            categories: {
                ...rules.categories,
                variableTypeRules: $associations
            }
        }));
    }

    function toggleExpand() {
        isExpanded = !isExpanded;
    }
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
        <h2 class="text-lg font-bold text-white">Tipos de Variable</h2>
        <button on:click={toggleExpand} class="text-white hover:text-blue-300 transition-colors">
            {#if isExpanded}
                <ChevronUp />
            {:else}
                <ChevronDown />
            {/if}
        </button>
    </div>

    {#if isExpanded}
        <div class="flex flex-col sm:flex-row gap-4 mb-4 mt-4">
            <select
                bind:value={selectedHeader}
                class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
                aria-label="Seleccionar cabecera"
            >
                <option value="">Seleccionar cabecera</option>
                {#each headers as header}
                    <option value={header}>{header}</option>
                {/each}
            </select>

            <select
                bind:value={selectedType}
                class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
                aria-label="Seleccionar tipo de variable"
            >
                <option value="">Seleccionar tipo</option>
                {#each variableTypes as type}
                    <option value={type.name}>{type.name}</option>
                {/each}
            </select>

            <button
                on:click={addAssociation}
                class="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors"
                disabled={!selectedHeader || !selectedType}
            >
                Agregar
            </button>
        </div>

        <div class="space-y-2">
            {#each $associations as association (association.header)}
                <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
                    <span class="text-white">{association.header}: {association.type}</span>
                    <button
                        on:click={() => removeAssociation(association.header)}
                        class="bg-red-500 text-white p-1 rounded-md hover:bg-red-600 transition-colors"
                        aria-label={`Eliminar asociación para ${association.header}`}
                    >
                        Eliminar
                    </button>
                </div>
            {/each}
        </div>
    {/if}
</div>