
/*
  CyberShield AI - Funcionalidad FrontEnd Unidad II
  Primera iteración dinámica con 3 formularios solicitados:
  1. Formulario de inicio de sesión con validación de usuario y contraseña.
  2. Formulario de registro de incidente con varios campos obligatorios.
  3. Formulario de validación final con un solo mensaje de resultado.

  Además se agregan filtros, búsqueda, drawer de alertas, modal, toasts y generación simulada de reportes.
*/

const DEMO_EMAIL = 'demo@cybershield.ai';
const DEMO_PASSWORD = 'demo1234';

function showToast(message){
    const toast = document.getElementById('toast');
    if(!toast) return;

    toast.textContent = message;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 2200);
}

function initLogin(){
    const form = document.getElementById('loginForm');
    if(!form) return;

    const emailInput = document.getElementById('emailInput');
    const passwordInput = document.getElementById('passwordInput');
    const errorMsg = document.getElementById('loginError');

    form.addEventListener('submit', function(event){
        event.preventDefault();

        const email = emailInput.value.trim().toLowerCase();
        const password = passwordInput.value;

        if(email === DEMO_EMAIL && password === DEMO_PASSWORD){
            errorMsg.classList.remove('show');
            localStorage.setItem('cybershieldUser', 'Carlos Martínez');
            localStorage.setItem('cybershieldRole', 'Analista de seguridad');
            window.location.href = 'dashboard.html';
        } else {
            errorMsg.classList.add('show');
            passwordInput.value = '';
            passwordInput.focus();
        }
    });
}

function initDashboard(){
    const metricCards = document.querySelectorAll('.metric-card .value');
    if(metricCards.length === 0) return;

    const subtitle = document.querySelector('.topbar .subtitle');
    if(subtitle){
        subtitle.textContent = 'Resumen de seguridad - sesión activa de demostración';
    }
}

function initAlertas(){
    const chips = document.querySelectorAll('.chip');
    const rows = document.querySelectorAll('#alertTable tr');
    const searchInput = document.getElementById('searchInput');

    if(rows.length === 0 || !searchInput) return;

    function applyFilters(){
        const activeChip = document.querySelector('.chip.active').dataset.filter;
        const query = searchInput.value.toLowerCase();

        rows.forEach(row => {
            const matchesSeverity = activeChip === 'todas' || row.dataset.sev === activeChip;
            const matchesSearch = row.textContent.toLowerCase().includes(query);
            row.style.display = (matchesSeverity && matchesSearch) ? '' : 'none';
        });
    }

    chips.forEach(chip => {
        chip.addEventListener('click', () => {
            chips.forEach(item => item.classList.remove('active'));
            chip.classList.add('active');
            applyFilters();
        });
    });

    searchInput.addEventListener('input', applyFilters);

    const overlay = document.getElementById('drawerOverlay');
    const drawerBadge = document.getElementById('drawerBadge');
    const drawerDesc = document.getElementById('drawerDesc');
    const drawerRec = document.getElementById('drawerRec');
    let selectedRow = null;

    rows.forEach(row => {
        row.addEventListener('click', () => {
            selectedRow = row;
            const severity = row.dataset.sev;
            drawerBadge.className = 'badge ' + (severity === 'alta' ? 'high' : severity === 'media' ? 'med' : 'low');
            drawerBadge.textContent = severity.charAt(0).toUpperCase() + severity.slice(1);
            drawerDesc.textContent = row.dataset.desc;
            drawerRec.textContent = row.dataset.rec;
            overlay.classList.add('open');
        });
    });

    const drawerClose = document.getElementById('drawerClose');
    if(drawerClose){
        drawerClose.addEventListener('click', () => overlay.classList.remove('open'));
    }

    overlay.addEventListener('click', (event) => {
        if(event.target === overlay){
            overlay.classList.remove('open');
        }
    });

    document.getElementById('markAttended').addEventListener('click', () => {
        if(selectedRow){
            const statusCell = selectedRow.querySelector('td:last-child');
            statusCell.innerHTML = '<span class="badge resolved">Atendida</span>';
        }
        overlay.classList.remove('open');
        showToast('Alerta marcada como atendida');
    });

    document.getElementById('escalate').addEventListener('click', () => {
        window.location.href = 'incidentes.html?nuevo=1';
    });
}

function initIncidentes(){
    const modalOverlay = document.getElementById('modalOverlay');
    const openModal = document.getElementById('openModal');
    const cancelModal = document.getElementById('cancelModal');
    const incidentForm = document.getElementById('incidentForm');
    const incidentTable = document.getElementById('incidentTable');
    const incidentError = document.getElementById('incidentError');

    if(!modalOverlay || !openModal || !incidentForm) return;

    openModal.addEventListener('click', () => modalOverlay.classList.add('open'));
    cancelModal.addEventListener('click', () => {
        modalOverlay.classList.remove('open');
        incidentError.classList.remove('show');
    });
    modalOverlay.addEventListener('click', (event) => {
        if(event.target === modalOverlay){
            modalOverlay.classList.remove('open');
            incidentError.classList.remove('show');
        }
    });

    incidentForm.addEventListener('submit', (event) => {
        event.preventDefault();

        const type = document.getElementById('incidentType').value.trim();
        const date = document.getElementById('incidentDate').value;
        const severity = document.getElementById('incidentSeverity').value.trim();
        const description = document.getElementById('incidentDesc').value.trim();
        const owner = document.getElementById('incidentOwner').value.trim();

        if(!type || !date || !severity || description.length < 10 || !owner){
            incidentError.classList.add('show');
            return;
        }

        incidentError.classList.remove('show');

        const firstIdCell = incidentTable.querySelector('tr:first-child td:first-child');
        const currentId = firstIdCell ? parseInt(firstIdCell.textContent.replace('#',''), 10) : 102;
        const nextId = '#' + String(currentId + 1).padStart(3, '0');
        const readableDate = date.split('-').reverse().join('/');

        const row = document.createElement('tr');
        row.innerHTML = `<td>${nextId}</td><td>${type} (${severity})</td><td>${readableDate}</td><td>${owner}</td><td><span class="badge open">Abierto</span></td>`;
        incidentTable.insertBefore(row, incidentTable.firstChild);

        incidentForm.reset();
        document.getElementById('incidentDate').value = '2026-06-21';
        modalOverlay.classList.remove('open');
        showToast('Incidente registrado correctamente');
    });

    if(new URLSearchParams(window.location.search).get('nuevo') === '1'){
        modalOverlay.classList.add('open');
    }
}

function initReportes(){
    const generateBtn = document.getElementById('generateBtn');
    const reportTable = document.getElementById('reportTable');

    if(!generateBtn || !reportTable) return;

    generateBtn.addEventListener('click', () => {
        showToast('Generando reporte...');

        setTimeout(() => {
            const today = new Date().toLocaleDateString('es-MX');
            const row = document.createElement('tr');
            row.innerHTML = `<td>Reporte_Ejecutivo_UnidadII.pdf</td><td>${today}</td><td><a href="#" class="download-link">Descargar &darr;</a></td>`;
            reportTable.insertBefore(row, reportTable.firstChild);
            showToast('Reporte generado correctamente');
        }, 1000);
    });
}

function initValidacion(){
    const form = document.getElementById('validationForm');
    const moduleSelect = document.getElementById('validationModule');
    const message = document.getElementById('validationMessage');
    if(!form || !moduleSelect || !message) return;

    form.addEventListener('submit', (event) => {
        event.preventDefault();
        const moduleName = moduleSelect.value;

        if(!moduleName){
            message.className = 'single-message error';
            message.textContent = 'Validación no realizada: primero selecciona un módulo del FrontEnd.';
            showToast('Selecciona un módulo para validar');
            return;
        }

        message.className = 'single-message success';
        message.textContent = `Validación realizada correctamente: el módulo ${moduleName} respondió de forma dinámica en el FrontEnd.`;
        showToast('Validación realizada correctamente');
    });
}

document.addEventListener('DOMContentLoaded', () => {
    initLogin();
    initDashboard();
    initAlertas();
    initIncidentes();
    initReportes();
    initValidacion();
});
