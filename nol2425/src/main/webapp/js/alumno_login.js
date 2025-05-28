window.addEventListener('DOMContentLoaded', () => {
	const urlParams = new URLSearchParams(window.location.search);
	if (urlParams.get('error')=='true') {
		alert('Inicio de sesión incorrecto. Verifica tus credenciales.')
	}
});