<script>
    import { writable } from 'svelte/store';
    import { rulesStore } from '../stores/rulesStores.js';
    import { ChevronDown, ChevronUp, X } from 'lucide-svelte';
    import { headersStore } from '../stores/headersStore.js';

    let isExpanded = false; // Estado de expansión del componente
    let errorMessage = '';

    // Valores para la fecha inicial y final
    let startYear = '';
    let startMonth = '';
    let startDay = '';
    let endYear = '';
    let endMonth = '';
    let endDay = '';
    let selectedReferenceColumn = ''; // Columna de referencia seleccionada

    $: headers = $headersStore;

    // Store local para manejar el rango de fechas
    const dateRanges = writable([]);

    // Función para agregar un nuevo rango de fechas
    function addDateRange() {
        const startDate = `${startYear}-${startMonth}-${startDay}`;
        const endDate = `${endYear}-${endMonth}-${endDay}`;

        // Validaciones para asegurarse de que las fechas sean válidas
        if (!isValidDate(startYear, startMonth, startDay) || !isValidDate(endYear, endMonth, endDay) || !selectedReferenceColumn) {
            errorMessage = 'Ingrese fechas válidas en el formato año-mes-día y seleccione una columna de referencia.';
            return;
        }

        dateRanges.set([{ startDateColumn: startDate, endDateColumn: endDate, referenceDateColumn: selectedReferenceColumn }]);

        // Limpiar los campos y el mensaje de error
        startYear = startMonth = startDay = endYear = endMonth = endDay = '';
        selectedReferenceColumn = '';
        errorMessage = '';

        updateRulesStore();
    }

    // Validar si el año, mes y día proporcionados son válidos
    function isValidDate(year, month, day) {
        const date = new Date(`${year}-${month}-${day}`);
        return !isNaN(date.getTime()) && year && month && day;
    }

    // Función para actualizar el rulesStore
    function updateRulesStore() {
        if ($dateRanges.length > 0) {
            rulesStore.update(rules => ({
                ...rules,
                rules: {
                    ...rules.rules,
                    categories: {
                        ...rules.rules.categories,
                        dateRangeRules: $dateRanges
                    }
                }
            }));
        }
    }

    // Alternar el estado de expansión del componente
    const toggleExpand = () => {
        isExpanded = !isExpanded;
    };
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
        <h2 class="text-lg font-bold text-white">Rango de Fechas</h2>
        <!-- Botón para expandir o contraer el componente -->
        <button on:click={toggleExpand} class="text-white hover:bg-blue-600 transition-colors">
            {#if isExpanded}
                <ChevronUp />
            {:else}
                <ChevronDown />
            {/if}
        </button>
    </div>

    {#if isExpanded}
        <div class="flex flex-col gap-4 mb-4 mt-4">
            <!-- Fecha de inicio -->
            <div class="flex items-center gap-2">
                <label class="text-white w-32">Fecha Inicial:</label>
                <input type="number" bind:value={startYear} placeholder="Año" min="1900" max="2100" class="bg-zinc-600 text-white p-2 rounded-md w-20" />
                <input type="number" bind:value={startMonth} placeholder="Mes" min="1" max="12" class="bg-zinc-600 text-white p-2 rounded-md w-20" />
                <input type="number" bind:value={startDay} placeholder="Día" min="1" max="31" class="bg-zinc-600 text-white p-2 rounded-md w-20" />
            </div>

            <!-- Fecha de fin -->
            <div class="flex items-center gap-2">
                <label class="text-white w-32">Fecha Final:</label>
                <input type="number" bind:value={endYear} placeholder="Año" min="1900" max="2100" class="bg-zinc-600 text-white p-2 rounded-md w-20" />
                <input type="number" bind:value={endMonth} placeholder="Mes" min="1" max="12" class="bg-zinc-600 text-white p-2 rounded-md w-20" />
                <input type="number" bind:value={endDay} placeholder="Día" min="1" max="31" class="bg-zinc-600 text-white p-2 rounded-md w-20" />
            </div>

            <!-- Selección de columna de referencia -->
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

        <!-- Botón para agregar un nuevo rango de fechas -->
        <button
            on:click={addDateRange}
            class="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors mb-4"
        >
            Agregar Rango de Fechas
        </button>

        <!-- Mensaje de error si es necesario -->
        {#if errorMessage}
            <p class="text-red-500 mb-4">{errorMessage}</p>
        {/if}

        <!-- Lista de rangos de fechas -->
        <div class="space-y-2">
            {#each $dateRanges as range, index}
                <div class="flex items-center bg-zinc-600/50 p-2 rounded-md">
                    <span class="text-white flex-grow">
                        {range.startDateColumn} - {range.endDateColumn} (Referencia: {range.referenceDateColumn})
                    </span>
                    <button
                        on:click={() => dateRanges.update(r => r.filter((_, i) => i !== index))}
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

<style>
    input[type="number"]::-webkit-inner-spin-button,
    input[type="number"]::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
    }
</style>

