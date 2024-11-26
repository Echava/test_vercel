<script>
    import { headersStore } from '../stores/headersStore.js';
    import { categoriesStore } from '../stores/categoriesStore.js';
    import { rulesStore } from '../stores/rulesStores.js';
    import { writable } from 'svelte/store';
    import { ChevronDown, ChevronUp, X } from 'lucide-svelte';

    let selectedHeader = '';
    let selectedType = '';
    let isExpanded = false;

    $: headers = $headersStore;
    $: variableTypes = $categoriesStore.variableTypeRules;
    $: associations = $rulesStore.categories?.variableTypeRules || [];

    function addAssociation() {
        if (selectedHeader && selectedType) {
            rulesStore.update(rules => {
                const updatedAssociations = rules.categories?.variableTypeRules || [];
                if (!updatedAssociations.some(a => a.header === selectedHeader)) {
                    updatedAssociations.push({ header: selectedHeader, type: selectedType });
                }
                return {
                    ...rules,
                    categories: {
                        ...rules.categories,
                        variableTypeRules: updatedAssociations
                    }
                };
            });
            selectedHeader = '';
            selectedType = '';
        }
    }

    function removeAssociation(header) {
        rulesStore.update(rules => ({
            ...rules,
            categories: {
                ...rules.categories,
                variableTypeRules: rules.categories?.variableTypeRules.filter(a => a.header !== header) || []
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
            {#each associations as association (association.header)}
                <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
                    <span class="text-white">{association.header}: {association.type}</span>
                    <button
                        on:click={() => removeAssociation(association.header)}
                        class="text-red-400 hover:text-red-600 transition-colors"
                        aria-label={`Eliminar asociación para ${association.header}`}
                    >
                        <X size={20} />
                    </button>
                </div>
            {/each}
        </div>
    {/if}
</div>