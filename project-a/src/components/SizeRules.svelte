<script>
    import { headersStore } from '../stores/headersStore.js';
    import { rulesStore } from '../stores/rulesStores.js';
    import { writable } from 'svelte/store';
    import { ChevronDown, ChevronUp, X } from 'lucide-svelte';

    let selectedHeader = ''; // Cabecera seleccionada para asociar
    let newSize = ''; // Tamaño asociado a la cabecera
    const sizeAssociations = writable([]); // Store local para manejar las asociaciones entre cabeceras y tamaños
    let isExpanded = false; // Estado de expansión del componente

    // Obtener las cabeceras del store global
    $: headers = $headersStore;

    // Función para alternar el estado de expansión del componente
    function toggleExpand() {
        isExpanded = !isExpanded;
    }

    // Función para añadir una asociación entre una cabecera y un tamaño
    function addSizeAssociation() {
        if (selectedHeader && newSize) {
            sizeAssociations.update(assocs => {
                // Verificar si la cabecera ya está asociada antes de añadirla
                if (!assocs.some(a => a.header === selectedHeader)) {
                    return [...assocs, { header: selectedHeader, size: parseInt(newSize, 10) }];
                }
                return assocs;
            });
            // Resetear las selecciones después de añadir una asociación
            selectedHeader = '';
            newSize = '';
            updateRulesStore();
        }
    }

    // Función para eliminar una asociación existente
    function removeSizeAssociation(header) {
        sizeAssociations.update(assocs => assocs.filter(a => a.header !== header));
        updateRulesStore();
    }

    // Función para actualizar el rulesStore
    function updateRulesStore() {
        rulesStore.update(rules => ({
            ...rules,
            categories: {
                ...rules.categories,
                sizeRules: $sizeAssociations
            }
        }));
    }
</script>

<div class="bg-zinc-800 p-4 rounded-lg">
    <div class="flex items-center justify-between">
        <h2 class="text-lg font-bold text-white">Reglas de Tamaño</h2>
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
        <div class="flex flex-col sm:flex-row gap-4 mb-4 mt-4">
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

            <!-- Input para el tamaño asociado -->
            <input
                type="number"
                bind:value={newSize}
                class="bg-zinc-600 text-white p-2 rounded-md flex-grow"
                placeholder="Tamaño"
                aria-label="Ingresar tamaño"
            />

            <!-- Botón para agregar una nueva asociación -->
            <button
                on:click={addSizeAssociation}
                class="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors"
                disabled={!selectedHeader || !newSize}
            >
                Agregar
            </button>
        </div>

        <!-- Mostrar la lista de asociaciones añadidas -->
        <div class="space-y-2">
            {#each $sizeAssociations as association (association.header)}
                <div class="flex items-center justify-between bg-zinc-600/50 p-2 rounded-md">
                    <span class="text-white">{association.header}: {association.size}</span>
                    <!-- Botón para eliminar una asociación específica -->
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
