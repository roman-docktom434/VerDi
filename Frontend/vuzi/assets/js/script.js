const dropZone = document.getElementById('drop-zone');
const fileInput = document.getElementById('file-input');
const fileList = document.getElementById('file-list');

// Обработка выбора файлов
fileInput.addEventListener('change', (e) => handleFiles(e.target.files));

function handleFiles(files) {
    if (files.length === 0) return;

    dropZone.classList.add('drop-zone--has-files');

    Array.from(files).forEach(file => {
        // Проверка: только CSV и Excel
        const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                        file.type === 'application/vnd.ms-excel';
        const isCSV = file.type === 'text/csv' || file.name.endsWith('.csv');

        if (isExcel || isCSV) {
            addFileToList(file);
        } else {
            alert(`Файл ${file.name} не является реестром (нужен .csv или .xlsx).`);
        }
    });
    fileInput.value = ''; // Сброс для возможности повторной загрузки
}

function addFileToList(file) {
    const fileItem = document.createElement('div');
    fileItem.className = 'file-item';
    fileItem.innerHTML = `
        <div class="file-item__info">
            <span>📊 ${file.name}</span>
        </div>
        <div class="file-item__controls">
            <label class="status-container">
                <span class="status-label">Аннулирован/Верифицирован</span>
                <input type="checkbox" class="status-checkbox">
            </label>
            <button class="btn-remove">✕</button>
        </div>
    `;

    fileItem.querySelector('.btn-remove').onclick = () => {
        fileItem.remove();
        if (fileList.children.length === 0) {
            dropZone.classList.remove('drop-zone--has-files');
        }
    };

    fileList.appendChild(fileItem);
}

// Drag & Drop события
dropZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropZone.classList.add('drop-zone--dragover');
});

dropZone.addEventListener('dragleave', () => {
    dropZone.classList.remove('drop-zone--dragover');
});

dropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropZone.classList.remove('drop-zone--dragover');
    handleFiles(e.dataTransfer.files);
});