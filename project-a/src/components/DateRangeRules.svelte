<script>
    import { rulesStore } from '../stores/rulesStores.js';
    import { ChevronDown, ChevronUp, X } from 'lucide-svelte';
    import { headersStore } from '../stores/headersStore.js';

    let isExpanded = false;
    let errorMessage = '';

    let startDate = '';
    let endDate = '';
    let selectedReferenceColumn = '';

    $: headers = $headersStore;
    $: dateRanges = $rulesStore.rules.categories?.dateRangeRules || [];

    function addDateRange() {
        if (!startDate || !endDate || !selectedReferenceColumn) {
            errorMessage = 'Ingrese fechas válidas y seleccione una columna de referencia.';
            return;
        }

        const newRange = { startDateColumn: startDate, endDateColumn: endDate, referenceDateColumn: selectedReferenceColumn };
        updateRulesStore([...dateRanges, newRange]);

        startDate = endDate = '';
        selectedReferenceColumn = '';
        errorMessage = '';
    }

    function removeDateRange(index) {
        updateRulesStore(dateRanges.filter((_, i) => i !== index));
    }

    function updateRulesStore(updatedRanges) {
        rulesStore.update(store => ({
            ...store,
            rules: {
                ...store.rules,
                categories: {
                    ...store.rules.categories,
                    dateRangeRules: updatedRanges
                }
            }
        }));
    }

    const toggleExpand = () => {
        isExpanded = !isExpanded;
    };
</script>

<div class="bg-customLightGreen p-4 rounded-lg">
    <div class="flex items-center justify-between">
        <h2 class="text-lg font-bold text-black">Rango de Fechas</h2>
        <button on:click={toggleExpand} class="text-black hover:text-blue-300 transition-colors">
            {#if isExpanded}
                <ChevronUp />
            {:else}
                <ChevronDown />
            {/if}
        </button>
    </div>

    {#if isExpanded}
        <div class="flex flex-col gap-4 mb-4 mt-4">
            <div class="flex items-center gap-2">
                <label for="start-date" class="text-white w-32">Fecha Inicial:</label>
                <input id="start-date" type="date" bind:value={startDate} class="bg-zinc-600 text-white p-2 rounded-md w-full" />
            </div>

            <div class="flex items-center gap-2">
                <label for="end-date" class="text-white w-32">Fecha Final:</label>
                <input id="end-date" type="date" bind:value={endDate} class="bg-zinc-600 text-white p-2 rounded-md w-full" />
            </div>

            <div class="mb-4">
                <label for="reference-column" class="block text-white mb-2">Columna de Referencia:</label>
                <select
                    id="reference-column"
                    bind:value={selectedReferenceColumn}
                    class="w-full bg-zinc-600 text-white p-2 rounded-md"
                >
                    <option value="">Seleccionar columna</option>
                    {#each headers as header}
                        <option value={header}>{header}</option>
                    {/each}
                </select>
            </div>
        </div>

        <button
            on:click={addDateRange}
            class="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors mb-4"
        >
            Agregar Rango de Fechas
        </button>

        {#if errorMessage}
            <p class="text-red-500 mb-4">{errorMessage}</p>
        {/if}

        <div class="space-y-2">
            {#each dateRanges as range, index}
                <div class="flex items-center bg-zinc-600/50 p-2 rounded-md">
                    <span class="text-white flex-grow">
                        {range.startDateColumn} - {range.endDateColumn} (Referencia: {range.referenceDateColumn})
                    </span>
                    <button
                        on:click={() => removeDateRange(index)}
                        class="text-red-400 hover:text-red-600 transition-colors"
                        aria-label={`Eliminar rango de fechas ${index}`}
                    >
                        <X size={20} />
                    </button>
                </div>
            {/each}
        </div>
    {/if}
</div>


