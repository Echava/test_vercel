<script>
    import { headersStore } from '../stores/headersStore.js';
    import { rulesStore } from '../stores/rulesStores.js';
    import { writable } from 'svelte/store';
    import { ChevronDown, ChevronUp, X } from 'lucide-svelte';

    let selectedHeader = ''; // Cabecera seleccionada para asociar
    let minValue = ''; // Valor mínimo asociado a la cabecera
    let maxValue = ''; // Valor máximo asociado a la cabecera
    const minMaxAssociations = writable([]); // Store local para manejar las asociaciones entre cabeceras, mínimo y máximo
    let isExpanded = false; // Estado de expansión del componente

    // Obtener las cabeceras del store global
    $: headers = $headersStore;

    // Función para alternar el estado de expansión del componente
    function toggleExpand() {
        isExpanded = !isExpanded;
    }

    // Función para añadir una asociación entre una cabecera, un valor mínimo y un valor máximo
    function addMinMaxAssociation() {
        if (selectedHeader && minValue && maxValue) {
            minMaxAssociations.update(assocs => {
                // Verificar si la cabecera ya está asociada antes de añadirla
                if (!assocs.some(a => a.header === selectedHeader)) {
                    return [...assocs, { header: selectedHeader, minValue: parseFloat(minValue), maxValue: parseFloat(maxValue) }];
                }
                return assocs;
            });
            // Resetear las selecciones después de añadir una asociación
            selectedHeader = '';
            minValue = '';
            maxValue = '';
            updateRulesStore();
        }
    }

    // Función para eliminar una asociación existente
    function removeMinMaxAssociation(header) {
        minMaxAssociations.update(assocs => assocs.filter(a => a.header !== header));
        updateRulesStore();
    }

    // Función para actualizar el rulesStore
    function updateRulesStore() {
        rulesStore.update(rules => ({
            ...rules,
            categories: {
                ...rules.categories,
                minimumAndMaximumRules: $minMaxAssociations
            }
        }));
    }
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
        <h2 class="text-lg font-bold text-white">Reglas de Mínimo y Máximo</h2>
        <!-- Botón para expandir o contraer el componente -->
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
                <!-- Selector para la cabecera a asociar -->
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

                <!-- Input para el valor mínimo asociado -->
                <input
                    type="number"
                    bind:value={minValue}
                    class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
                    placeholder="Valor Mínimo"
                    aria-label="Ingresar valor mínimo"
                />

                <!-- Input para el valor máximo asociado -->
                <input
                    type="number"
                    bind:value={maxValue}
                    class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
                    placeholder="Valor Máximo"
                    aria-label="Ingresar valor máximo"
                />
            </div>

            <!-- Botón para agregar una nueva asociación -->
            <button
                on:click={addMinMaxAssociation}
                class="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors w-full"
                disabled={!selectedHeader || !minValue || !maxValue}
            >
                Agregar
            </button>
        </div>

        <!-- Mostrar la lista de asociaciones añadidas -->
        <div class="space-y-2">
            {#each $minMaxAssociations as association (association.header)}
                <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
                    <span class="text-white">{association.header}: Mínimo {association.minValue}, Máximo {association.maxValue}</span>
                    <!-- Botón para eliminar una asociación específica -->
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


