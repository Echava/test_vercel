<script>
    import { headersStore } from '../stores/headersStore.js';
    import { rulesStore } from '../stores/rulesStores.js';
    import { ChevronDown, ChevronUp, X } from 'lucide-svelte';

    let selectedHeader = '';
    let minValue = '';
    let maxValue = '';
    let isExpanded = false;

    $: headers = $headersStore;
    $: minMaxAssociations = $rulesStore.categories?.minimumAndMaximumRules || [];

    function toggleExpand() {
        isExpanded = !isExpanded;
    }

    function addMinMaxAssociation() {
        if (selectedHeader && minValue && maxValue) {
            const updatedAssociations = [...minMaxAssociations];
            if (!updatedAssociations.some(a => a.header === selectedHeader)) {
                updatedAssociations.push({ 
                    header: selectedHeader, 
                    minValue: parseFloat(minValue), 
                    maxValue: parseFloat(maxValue) 
                });
                updateRulesStore(updatedAssociations);
            }
            selectedHeader = '';
            minValue = '';
            maxValue = '';
        }
    }

    function removeMinMaxAssociation(header) {
        const updatedAssociations = minMaxAssociations.filter(a => a.header !== header);
        updateRulesStore(updatedAssociations);
    }

    function updateRulesStore(updatedAssociations) {
        rulesStore.update(rules => ({
            ...rules,
            categories: {
                ...rules.categories,
                minimumAndMaximumRules: updatedAssociations
            }
        }));
    }
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
        <h2 class="text-lg font-bold text-white">Reglas de Mínimo y Máximo</h2>
        <button on:click={toggleExpand} class="text-white hover:text-blue-300 transition-colors">
            {#if isExpanded}
                <ChevronUp />
            {:else}
                <ChevronDown />
            {/if}
        </button>
    </div>

    {#if isExpanded}
        <div class="flex flex-col gap-4 mb-4 mt-4">
            <div class="flex flex-wrap gap-4">
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
                    bind:value={minValue}
                    class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
                    placeholder="Valor Mínimo"
                    aria-label="Ingresar valor mínimo"
                />

                <input
                    type="number"
                    bind:value={maxValue}
                    class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
                    placeholder="Valor Máximo"
                    aria-label="Ingresar valor máximo"
                />
            </div>

            <button
                on:click={addMinMaxAssociation}
                class="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors w-full"
                disabled={!selectedHeader || !minValue || !maxValue}
            >
                Agregar
            </button>
        </div>

        <div class="space-y-2">
            {#each minMaxAssociations as association (association.header)}
                <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
                    <span class="text-white">{association.header}: Mínimo {association.minValue}, Máximo {association.maxValue}</span>
                    <button
                        on:click={() => removeMinMaxAssociation(association.header)}
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


