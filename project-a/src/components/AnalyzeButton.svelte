<script>
    import { rulesStore } from '../stores/rulesStores.js';
    import { excelFileStore } from '../stores/excelFileStore.js';
    import { transformJsonForAnalysis } from '../utils/jsonTransformations.js';
    import { Loader2, Download } from 'lucide-svelte';
  
    let isLoading = false;
    let error = null;
    let resultUrl = null;
  
    $: rules = $rulesStore;
    $: excelFile = $excelFileStore;
  
    async function handleAnalyze() {
      if (!excelFile) {
        error = 'No se ha cargado ningún archivo Excel.';
        return;
      }
  
      isLoading = true;
      error = null;
      resultUrl = null;
  
      try {
        const transformedRules = transformJsonForAnalysis(rules);
        
        const formData = new FormData();
        formData.append('excel', excelFile);
        formData.append('rules', JSON.stringify(transformedRules));
  
        const response = await fetch('https://valued-teal-complete.ngrok-free.app/api/analyze-databases-savia', {
          method: 'POST',
          body: formData,
          mode: 'cors',
          headers: {
            'Accept': 'application/json',
          },
        });
  
        if (!response.ok) {
          const errorData = await response.json().catch(() => null);
          throw new Error(`HTTP error! status: ${response.status}, message: ${errorData?.message || response.statusText}`);
        }
  
        const data = await response.json();
        resultUrl = data.resultUrl;
      } catch (err) {
        console.error('Error completo:', err);
        if (err.message.includes('Failed to fetch')) {
          error = 'Error de conexión. Asegúrate de que la URL del servidor es correcta y que el servidor está en funcionamiento.';
        } else if (err.message.includes('HTTP error! status: 404')) {
          error = 'La URL especificada no se encontró en el servidor. Verifica que la ruta sea correcta.';
        } else if (err.message.includes('has been blocked by CORS policy')) {
          error = 'Error de CORS. El servidor necesita configurar los encabezados CORS adecuados. Contacta al equipo de backend.';
        } else if (err.message.includes('HTTP error! status: 400')) {
          error = `Error en la solicitud: ${err.message}. Verifica que los datos enviados son correctos.`;
        } else {
          error = `Ocurrió un error durante el análisis: ${err.message}`;
        }
      } finally {
        isLoading = false;
      }
    }
</script>

<div class="space-y-4">
    <button 
      on:click={handleAnalyze} 
      disabled={isLoading || !excelFile}
      class="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
    >
      {#if isLoading}
        <Loader2 class="inline-block mr-2 h-4 w-4 animate-spin" />
        Analizando...
      {:else}
        Analizar
      {/if}
    </button>
  
    {#if error}
      <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
        <strong class="font-bold">Error:</strong>
        <span class="block sm:inline">{error}</span>
      </div>
    {/if}
  
    {#if resultUrl}
      <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative" role="alert">
        <strong class="font-bold">Análisis completado:</strong>
        <a 
          href={resultUrl} 
          download 
          class="flex items-center text-blue-600 hover:underline"
        >
          <Download class="inline-block mr-2 h-4 w-4" />
          Descargar resultados
        </a>
      </div>
    {/if}
</div>