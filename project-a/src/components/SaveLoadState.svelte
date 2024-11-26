<script>
    import { rulesStore } from '../stores/rulesStores.js';
    import { ArrowDownToLine, ArrowUpFromLine } from 'lucide-svelte';

    let fileInput;
    let errorMessage = '';
    let successMessage = '';

    function transformJsonForBackend(state) {
        const transformedState = JSON.parse(JSON.stringify(state));
        
        // Transform variableTypeRules
        if (transformedState.categories?.variableTypeRules) {
            transformedState.categories.variableTypeRules = transformedState.categories.variableTypeRules.map(rule => ({
                name: rule.header,
                type: rule.type
            }));
        }

        // Transform sizeRules
        if (transformedState.categories?.sizeRules) {
            transformedState.categories.sizeRules = transformedState.categories.sizeRules.map(rule => ({
                name: rule.header,
                size: rule.size
            }));
        }

        // Transform minimumAndMaximumRules
        if (transformedState.categories?.minimumAndMaximumRules) {
            transformedState.categories.minimumAndMaximumRules = transformedState.categories.minimumAndMaximumRules.map(rule => ({
                name: rule.header,
                minValue: rule.minValue,
                maxValue: rule.maxValue
            }));
        }

        // Transform comparisonsWithOtherColumnRules
        if (transformedState.categories?.comparisonsWithOtherColumnRules) {
            transformedState.categories.comparisonsWithOtherColumnRules = transformedState.categories.comparisonsWithOtherColumnRules.map(rule => ({
                comparetorOne: rule.header1,
                comparetorTwo: rule.header2,
                operator: rule.operator === '<' ? 'less_than' : rule.operator === '>' ? 'greater_than' : 'equal_to'
            }));
        }

        return transformedState;
    }

    function transformJsonFromBackend(state) {
        const transformedState = JSON.parse(JSON.stringify(state));
        
        // Transform variableTypeRules
        if (transformedState.categories?.variableTypeRules) {
            transformedState.categories.variableTypeRules = transformedState.categories.variableTypeRules.map(rule => ({
                header: rule.name,
                type: rule.type
            }));
        }

        // Transform sizeRules
        if (transformedState.categories?.sizeRules) {
            transformedState.categories.sizeRules = transformedState.categories.sizeRules.map(rule => ({
                header: rule.name,
                size: rule.size
            }));
        }

        // Transform minimumAndMaximumRules
        if (transformedState.categories?.minimumAndMaximumRules) {
            transformedState.categories.minimumAndMaximumRules = transformedState.categories.minimumAndMaximumRules.map(rule => ({
                header: rule.name,
                minValue: rule.minValue,
                maxValue: rule.maxValue
            }));
        }

        // Transform comparisonsWithOtherColumnRules
        if (transformedState.categories?.comparisonsWithOtherColumnRules) {
            transformedState.categories.comparisonsWithOtherColumnRules = transformedState.categories.comparisonsWithOtherColumnRules.map(rule => ({
                header1: rule.comparetorOne,
                header2: rule.comparetorTwo,
                operator: rule.operator === 'less_than' ? '<' : rule.operator === 'greater_than' ? '>' : '='
            }));
        }

        return transformedState;
    }

    function saveState() {
        const state = transformJsonForBackend($rulesStore);
        const blob = new Blob([JSON.stringify(state, null, 2)], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'rules_state.json';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
        successMessage = 'Estado guardado exitosamente.';
        setTimeout(() => successMessage = '', 3000);
    }

    function loadState(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                try {
                    const loadedState = JSON.parse(e.target.result);
                    const transformedState = transformJsonFromBackend(loadedState);
                    rulesStore.set(transformedState);
                    successMessage = 'Estado cargado exitosamente.';
                    setTimeout(() => successMessage = '', 3000);
                } catch (error) {
                    errorMessage = 'Error al cargar el archivo. Asegúrese de que es un JSON válido.';
                    setTimeout(() => errorMessage = '', 5000);
                }
            };
            reader.readAsText(file);
        }
    }
</script>

<div class="bg-zinc-800 p-4 rounded-lg mb-4">
    <h2 class="text-lg font-bold text-white mb-4">Guardar/Cargar Estado</h2>
    <div class="flex gap-4">
        <button
            on:click={saveState}
            class="flex items-center justify-center gap-2 bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors"
        >
            <ArrowDownToLine size={20} />
            Guardar Estado
        </button>
        <label
            for="load-state"
            class="flex items-center justify-center gap-2 bg-green-500 text-white p-2 rounded-md hover:bg-green-600 transition-colors cursor-pointer"
        >
            <ArrowUpFromLine size={20} />
            Cargar Estado
        </label>
        <input
            id="load-state"
            type="file"
            accept=".json"
            on:change={loadState}
            class="hidden"
            bind:this={fileInput}
        />
    </div>
    {#if errorMessage}
        <p class="text-red-500 mt-2">{errorMessage}</p>
    {/if}
    {#if successMessage}
        <p class="text-green-500 mt-2">{successMessage}</p>
    {/if}
</div>