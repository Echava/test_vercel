<script>
    import { read, utils } from "xlsx";
    import { headersStore } from "../stores/headersStore.js";
  
    let file;
  
    // Función para manejar el cambio de archivo (cuando el usuario selecciona un archivo)
    const handleFileChange = async (event) => {
      file = event.target.files[0]; // Obtener el archivo seleccionado
      if (file) {
        const reader = new FileReader(); // Crear un lector de archivos
        reader.onload = (e) => {
          const data = new Uint8Array(e.target.result); // Leer el contenido del archivo como array de bytes
          const workbook = read(data, { type: "array" }); // Leer el archivo como un libro de trabajo (workbook)
          const firstSheetName = workbook.SheetNames[0]; // Obtener el nombre de la primera hoja del archivo
          const worksheet = workbook.Sheets[firstSheetName]; // Obtener la primera hoja de trabajo
          const headers = utils.sheet_to_json(worksheet, { header: 1 })[0]; // Extraer los nombres de las cabeceras (primera fila)
          console.log(headers); // Mostrar los nombres de las cabeceras en la consola
          headersStore.set(headers); // Guardar los nombres de las cabeceras en el store para compartirlos
        };
        reader.readAsArrayBuffer(file); // Leer el archivo como un ArrayBuffer
      }
    };
  </script>
  
  <div>
    <input
      id="file-input"
      type="file"
      accept=".xlsx, .xls"
      on:change="{handleFileChange}"
      class="hidden"
    />
    <label for="file-input" class="bg-blue-500 text-white p-2 rounded hover:bg-blue-700 transition-colors cursor-pointer">
      Cargar Archivo
    </label>
  </div>
  
  
  
  