document.addEventListener('DOMContentLoaded', () => {
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
                processNewFiles(Array.from(this.files));
                this.value = ''; 
            }
        });
    }

    function processNewFiles(files) {
        files.forEach(file => {
            const isAuthentic = Math.random() > 0.15; 
            const score = isAuthentic ? Math.floor(Math.random() * (100 - 85) + 85) : Math.floor(Math.random() * 45);

            const fileObj = {
                id: Math.random().toString(36).substr(2, 9),
                file: file,
                verified: false,
                annulled: false,
                authentic: isAuthentic,
                score: score
            };
            
            selectedFiles.push(fileObj);
            addFileElement(fileObj); 
        });
        
        if (selectedFiles.length > 0) {
            promptContent.style.display = 'none';
            submitBtn.disabled = false;
        }
    }

    function addFileElement(obj) {
        const item = document.createElement('div');
        item.className = 'file-item';
        item.setAttribute('data-id', obj.id);
        
        if (!obj.authentic) {
            item.style.borderLeft = "4px solid var(--danger-color)";
            item.style.backgroundColor = "#fffafa";
        }

        item.innerHTML = `
            <div class="file-item__name">
                <span style="font-size: 20px;">📄</span> 
                <div>
                    <div style="font-weight:600">${obj.file.name}</div>
                    <small style="color: ${obj.authentic ? 'var(--success-color)' : 'var(--danger-color)'}">
                        ${obj.authentic ? `Проверка пройдена (${obj.score}%)` : `Подозрение на несоответствие (${obj.score}%)`}
                    </small>
                </div>
            </div>
            <div class="file-controls">
                <div class="annul-wrapper">
                    <span>Аннулировать</span>
                    <button type="button" class="btn-annul btn-action-annul"></button>
                    ${obj.authentic ? `
                        <span>Верифицировать</span>
                        <button type="button" class="btn-annul btn-action-verify"></button>
                    ` : '<b style="color:var(--danger-color); font-size:10px;">ОТКАЗАНО В ВЕРИФИКАЦИИ</b>'}
                </div>
                <button type="button" class="btn-remove" title="Удалить">&times;</button>
            </div>
        `;

        const annulBtn = item.querySelector('.btn-action-annul');
        const verifyBtn = item.querySelector('.btn-action-verify');

        annulBtn.onclick = () => {
            obj.annulled = !obj.annulled;
            if (obj.annulled) {
                obj.verified = false;
                if (verifyBtn) verifyBtn.classList.remove('active');
            }
            annulBtn.classList.toggle('active', obj.annulled);
        };

        if (verifyBtn) {
            verifyBtn.onclick = () => {
                obj.verified = !obj.verified;
                if (obj.verified) {
                    obj.annulled = false;
                    annulBtn.classList.remove('active');
                }
                verifyBtn.classList.toggle('active', obj.verified);
            };
        }

        item.querySelector('.btn-remove').onclick = () => {
            selectedFiles = selectedFiles.filter(f => f.id !== obj.id);
            item.remove();
            if (selectedFiles.length === 0) {
                promptContent.style.display = 'block';
                submitBtn.disabled = true;
            }
        };

        fileListDisplay.appendChild(item);
    }

    if (submitBtn) {
        submitBtn.addEventListener('click', (e) => {
            e.preventDefault(); 
            alert('Синхронизация завершена успешно!');
            selectedFiles = [];
            fileListDisplay.innerHTML = '';
            promptContent.style.display = 'block';
            submitBtn.disabled = true;
        });
    }
});