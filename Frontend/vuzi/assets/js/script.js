document.addEventListener('DOMContentLoaded', () => {
    //ЭЛЕМЕНТЫ СТРАНИЦЫ
    const fileInput = document.getElementById('file-input');
    const fileListDisplay = document.getElementById('file-list');
    const submitBtn = document.getElementById('submit-to-registry');
    const promptContent = document.getElementById('drop-zone-prompt');
    const dropZone = document.getElementById('drop-zone');
    const addFileBtn = document.getElementById('add-file-btn');

    let selectedFiles = [];

    if (submitBtn) submitBtn.disabled = true;

    if (addFileBtn && fileInput) {
        addFileBtn.addEventListener('click', () => fileInput.click());
    }

    if (fileInput) {
        fileInput.addEventListener('change', function() {
            if (this.files.length > 0) {
                const newFiles = Array.from(this.files).map(file => ({
                    file: file,
                    verified: false 
                }));
                addFiles(newFiles);
                this.value = ''; 
            }
        });
    }

    if (dropZone) {
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            dropZone.addEventListener(eventName, (e) => {
                e.preventDefault();
                e.stopPropagation();
            }, false);
        });

        dropZone.addEventListener('drop', (e) => {
            const dt = e.dataTransfer;
            const droppedFiles = Array.from(dt.files).filter(file => 
                file.name.endsWith('.csv') || file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
            );

            if (droppedFiles.length > 0) {
                const newFiles = droppedFiles.map(file => ({
                    file: file,
                    verified: false
                }));
                addFiles(newFiles);
            } else {
                alert('Пожалуйста, загружайте только файлы таблиц (.csv, .xlsx)');
            }
        });
    }

    function addFiles(newObjects) {
        selectedFiles = [...selectedFiles, ...newObjects];
        renderFileList();
    }

    function renderFileList() {
        if (!fileListDisplay) return;
        fileListDisplay.innerHTML = '';
        
        if (selectedFiles.length > 0) {
            if (promptContent) promptContent.style.display = 'none';
            if (submitBtn) submitBtn.disabled = false;

            selectedFiles.forEach((obj, index) => {
                const item = document.createElement('div');
                item.className = 'file-item';
                item.innerHTML = `
                <div class="file-item__name">
                    <span style="font-size: 20px;">📄</span> 
                    <span>${obj.file.name}</span>
                </div>
                <div class="file-controls">
                    <div class="annul-wrapper">
                        <span>Аннулирован</span>
                        <button type="button" class="btn-annul ${obj.verified ? 'active' : ''}"></button>
                        <span>Верифицирован</span>
                    </div>
                    <button type="button" class="btn-remove" title="Удалить">&times;</button>
                </div>
            `;

                const toggle = item.querySelector('.btn-annul');
                toggle.addEventListener('click', () => {
                    obj.verified = !obj.verified;
                    toggle.classList.toggle('active');
                });

                const removeBtn = item.querySelector('.btn-remove');
                removeBtn.addEventListener('click', () => {
                    item.classList.add('removing');
                    setTimeout(() => {
                        selectedFiles.splice(index, 1);
                        renderFileList();
                    }, 300);
                });

                fileListDisplay.appendChild(item);
            });
        } else {
            if (promptContent) promptContent.style.display = 'block';
            if (submitBtn) submitBtn.disabled = true;
        }
    }

    if (submitBtn) {
        submitBtn.addEventListener('click', (e) => {
            e.preventDefault(); // Предотвращаем перезагрузку формы
            console.log('Отправка данных:', selectedFiles);
            alert('Дипломы успешно отправлены в реестр!');
            selectedFiles = [];
            renderFileList();
        });
    }
});