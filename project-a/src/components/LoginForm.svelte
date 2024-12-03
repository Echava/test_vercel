<script>
  import { jwtDecode } from 'jwt-decode'; // Importar jwtDecode de forma nombrada

  let email = '';
  let password = '';
  let message = '';

  function handleClick(event) {
    event.preventDefault(); // Evitar la recarga de la página
    handleSubmit(); // Llamar la función de envío personalizada
  }

  async function handleSubmit() {
    console.log('handleSubmit called, event default prevented.');

    try {
      const response = await fetch('http://localhost:3000/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      console.log('Fetch response received:', response);

      const data = await response.json();
      console.log('Response data:', data);

      if (response.ok && data.token) {
        console.log('Login successful, received token:', data.token);

        // Usar jwtDecode para decodificar el token
        const decoded = jwtDecode(data.token);
        const userRole = decoded?.rol;

        // Guardar el token y el rol en localStorage
        localStorage.setItem('token', data.token);
        localStorage.setItem('role', userRole);

        // Redirigir a la página principal
        window.location.href = '/';
      } else {
        console.log('Login failed:', data.error);
        message = data.error || 'Login failed. Please try again.';
      }
    } catch (error) {
      console.error('Error:', error);
      message = 'An error occurred. Please try again.';
    }
  }
</script>

<div class="min-h-screen flex items-center justify-center bg-gradient-to-t from-zinc-500 via-fuchsia-600/7">
  <div class="bg-white p-8 rounded-lg shadow-md w-96">
    <div class="flex justify-center">
      <img src="../images/logo.jpg" alt="Logo" class="h-20 mb-6 w-auto" />
    </div>
    
    <form class="space-y-4">
      <div>
        <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
        <input
          type="email"
          id="email"
          bind:value={email}
          required
          class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
        />
      </div>
      <div>
        <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
        <input
          type="password"
          id="password"
          bind:value={password}
          required
          class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
        />
      </div>
      <button
        on:click={handleClick}
        class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-500 hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-400"
      >
        Enter
      </button>
    </form>
    {#if message}
      <p class="mt-4 text-center text-sm text-red-600">
        {message}
      </p>
    {/if}
  </div>
</div>




  