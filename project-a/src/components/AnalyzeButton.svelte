<script>
  import { rulesStore } from "../stores/rulesStores.js";
  import { excelFileStore } from "../stores/excelFileStore.js";
  import { transformJsonForAnalysis } from "../utils/jsonTransformations.js";
  import { Loader2, Download } from "lucide-svelte";
  import { onDestroy } from "svelte";
  import axios from 'axios';

  const API_BASE_URL = 'https://valued-teal-complete.ngrok-free.app/api';

  let isLoading = false;
  let error = null;
  let resultFilePath = null;
  let reportId = null;
  let analysisStatus = null;
  let pollingInterval = null;

  $: rules = $rulesStore;
  $: excelFile = $excelFileStore;

  async function handleAnalyze() {
    if (!excelFile) {
      error = "No se ha cargado ningún archivo Excel.";
      return;
    }

    isLoading = true;
    error = null;
    resultFilePath = null;
    analysisStatus = "Iniciando análisis...";

    try {
      const transformedRules = transformJsonForAnalysis(rules);
      const rulesBlob = new Blob([JSON.stringify(transformedRules)], {
        type: "application/json",
      });
      const rulesFile = new File([rulesBlob], "rules.json", {
        type: "application/json",
      });

      const formData = new FormData();
      formData.append("db-file", excelFile);
      formData.append("br-file", rulesFile);

      console.log("Enviando solicitud de análisis...");
      const response = await axios.post(`${API_BASE_URL}/analyze-databases-savia`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Accept': '*/*'
        },
      });

      console.log("Respuesta recibida:", response.data);
      reportId = response.data.reportId;
      startPolling();
    } catch (err) {
      console.error("Error al iniciar el análisis:", err);
      handleError(err);
    }
  }

  function startPolling() {
    const pollInterval = 10000; // 10 segundos para desarrollo
    console.log(`Iniciando polling cada ${pollInterval / 1000} segundos`);
    pollingInterval = setInterval(checkAnalysisStatus, pollInterval);
  }

  async function checkAnalysisStatus() {
    console.log(`Verificando estado del análisis para reportId: ${reportId}`);
    try {
      const response = await axios.get(`${API_BASE_URL}/report-status/${reportId}`);
      console.log("Estado del análisis:", response.data);
      analysisStatus = response.data.status;

      if (response.data.status === "COMPLETADO") {
        console.log("Análisis completado. Ruta del archivo de resultado:", response.data.resultFilePath);
        clearInterval(pollingInterval);
        resultFilePath = response.data.resultFilePath;
        isLoading = false;
      } else if (response.data.status === "PENDIENTE") {
        console.log("El análisis aún está en proceso...");
      } else {
        console.error("Estado de análisis desconocido:", response.data.status);
        clearInterval(pollingInterval);
        throw new Error("El análisis ha fallado o tiene un estado desconocido");
      }
    } catch (err) {
      console.error("Error al verificar el estado del análisis:", err);
      handleError(err);
      clearInterval(pollingInterval);
    }
  }

  function handleError(err) {
    console.error("Error completo:", err);
    if (err.response) {
      error = `Error del servidor: ${err.response.status} - ${err.response.data}`;
    } else if (err.request) {
      error = "No se pudo conectar con el servidor. Verifica tu conexión a internet.";
    } else {
      error = `Error: ${err.message}`;
    }
    isLoading = false;
  }

  onDestroy(() => {
    if (pollingInterval) {
      clearInterval(pollingInterval);
    }
  });
</script>

<div class="space-y-4">
  <button
    on:click={handleAnalyze}
    disabled={isLoading || !excelFile}
    class="w-full bg-green-500 text-white p-2 rounded-md hover:bg-green-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
    aria-live="polite"
  >
    {#if isLoading}
      <Loader2
        class="inline-block mr-2 h-4 w-4 animate-spin"
        aria-hidden="true"
      />
      {analysisStatus === "PENDIENTE"
        ? "Analizando..."
        : "Iniciando análisis..."}
    {:else}
      Analizar
    {/if}
  </button>

  {#if error}
    <div
      class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
      role="alert"
    >
      <strong class="font-bold">Error:</strong>
      <span class="block sm:inline">{error}</span>
    </div>
  {/if}

  {#if resultFilePath}
    <div
      class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative"
      role="alert"
    >
      <strong class="font-bold">Análisis completado:</strong>
      <p>El archivo de resultados se ha generado en el servidor.</p>
      <p>Ruta del archivo: {resultFilePath}</p>
      <p>Por favor, contacta al administrador del sistema para obtener el archivo.</p>
    </div>
  {/if}
</div>
