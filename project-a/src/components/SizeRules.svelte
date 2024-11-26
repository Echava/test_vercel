<script>
    import { headersStore } from '../stores/headersStore.js';
    import { rulesStore } from '../stores/rulesStores.js';
    import { ChevronDown, ChevronUp, X } from 'lucide-svelte';

    let selectedHeader = '';
    let newSize = '';
    let isExpanded = false;

    $: headers = $headersStore;
    $: sizeAssociations = $rulesStore.categories?.sizeRules || [];

    function toggleExpand() {
        isExpanded = !isExpanded;
    }

    function addSizeAssociation() {
        if (selectedHeader && newSize) {
            const updatedAssociations = [...sizeAssociations];
            if (!updatedAssociations.some(a => a.header === selectedHeader)) {
                updatedAssociations.push({ header: selectedHeader, size: parseInt(newSize, 10) });
                updateRulesStore(updatedAssociations);
            }
            selectedHeader = '';
            newSize = '';
        }
    }

    function removeSizeAssociation(header) {
        const updatedAssociations = sizeAssociations.filter(a => a.header !== header);
        updateRulesStore(updatedAssociations);
    }

    function updateRulesStore(updatedAssociations) {
        rulesStore.update(rules => ({
            ...rules,
            categories: {
                ...rules.categories,
                sizeRules: updatedAssociations
            }
        }));
    }
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
        <h2 class="text-lg font-bold text-white">Reglas de Tamaño</h2>
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

            <input
                type="number"
                bind:value={newSize}
                class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
                placeholder="Tamaño"
                aria-label="Ingresar tamaño"
            />

            <button
                on:click={addSizeAssociation}
                class="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors"
                disabled={!selectedHeader || !newSize}
            >
                Agregar
            </button>
        </div>

        <div class="space-y-2">
            {#each sizeAssociations as association (association.header)}
                <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
                    <span class="text-white">{association.header}: {association.size}</span>
                    <button
                        on:click={() => removeSizeAssociation(association.header)}
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
