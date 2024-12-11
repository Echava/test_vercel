<script>
  import { setHeaders, setExcelFile } from '../stores/unifiedStore.js';
  import * as XLSX from 'xlsx';

  let fileInput;

  function handleFileChange(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = (e) => {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, { type: 'array' });
      const firstSheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[firstSheetName];
      const headers = XLSX.utils.sheet_to_json(worksheet, { header: 1 })[0];

      // Verificar duplicados
      const uniqueHeaders = new Set(headers);
      if (uniqueHeaders.size !== headers.length) {
        alert('Se detectaron cabeceras duplicadas. Por favor, corrija los duplicados en el archivo Excel antes de importar.');
        return;
      }

      // Si no hay duplicados, actualizar el store unificado
      setHeaders(headers);
      setExcelFile(file);
      console.log('Cabeceras cargadas:', headers);
    };

    reader.readAsArrayBuffer(file);
  }
</script>

<input
  type="file"
  accept=".xlsx, .xls"
  on:change={handleFileChange}
  bind:this={fileInput}
  class="block w-full text-sm text-gray-500
    file:mr-4 file:py-2 file:px-4
    file:rounded-full file:border-0
    file:text-sm file:font-semibold
    file:bg-blue-50 file:text-blue-700
    hover:file:bg-blue-100"
/>


  
  