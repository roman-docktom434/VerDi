document.addEventListener('DOMContentLoaded', () => {
    // --- ЭЛЕМЕНТЫ СТРАНИЦЫ ВУЗА ---
    const fileInput = document.getElementById('file-input');
    const fileListDisplay = document.getElementById('file-list');
    const submitBtn = document.getElementById('submit-to-registry');
    const promptContent = document.getElementById('drop-zone-prompt');
    
    let selectedFiles = [];

    if (fileInput) {
        fileInput.addEventListener('change', function() {
            const newFiles = Array.from(this.files);
            selectedFiles = [...selectedFiles, ...newFiles];
            
            renderFileList();
            this.value = '';
        });
    }

    // Функция для отрисовки (рендера) списка файлов
    function renderFileList() {
        if (!fileListDisplay) return;

        fileListDisplay.innerHTML = '';
        
        if (selectedFiles.length > 0) {
            // Скрываем иконку папки и текст-заглушку
            if (promptContent) promptContent.style.display = 'none';
            if (submitBtn) submitBtn.disabled = false;

            selectedFiles.forEach((file, index) => {
                const item = document.createElement('div');
                item.className = 'file-item';
                
                // Формируем внутреннюю структуру плашки
                item.innerHTML = `
                    <span class="file-item__name">${file.name}</span>
                    <div class="file-controls">
                        <button type="button" class="btn-annul" title="Аннулировать/Верифицировать"></button>
                        <button type="button" class="btn-remove" title="Удалить из списка">&times;</button>
                    </div>
                `;

                // 1. Логика чекбокса (Аннуляция)
                const annulBtn = item.querySelector('.btn-annul');
                annulBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    annulBtn.classList.toggle('active');
                });

                // 2. Логика удаления файла с анимацией
                const removeBtn = item.querySelector('.btn-remove');
                removeBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    
                    // Добавляем класс для CSS-анимации исчезновения
                    item.classList.add('removing');
                    
                    // Ждем 300мс (пока отыграет анимация), затем удаляем из массива и перерисовываем
                    setTimeout(() => {
                        selectedFiles.splice(index, 1);
                        renderFileList();
                    }, 300);
                });

                fileListDisplay.appendChild(item);
            });
        } else {
            // Если файлов нет, возвращаем заглушку и блокируем кнопку отправки
            if (promptContent) promptContent.style.display = 'block';
            if (submitBtn) submitBtn.disabled = true;
        }
    }

    // Обработка финальной отправки
    if (submitBtn) {
        submitBtn.addEventListener('click', () => {
            alert('Данные успешно отправлены в реестр дипломов!');
            selectedFiles = []; // Очищаем список после "отправки"
            renderFileList();
        });
    }

    // --- ЭЛЕМЕНТЫ СТРАНИЦЫ ДОБАВЛЕНИЯ СТУДЕНТА ---
    const generateBtn = document.getElementById('generateBtn');
    if (generateBtn) {
        generateBtn.addEventListener('click', () => {
            const passInput = document.getElementById('generatedPassword');
            const charset = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";
            let password = "";
            
            // Генерация случайного пароля из 10 символов
            for (let i = 0; i < 10; i++) {
                const randomIndex = Math.floor(Math.random() * charset.length);
                password += charset.charAt(randomIndex);
            }
            
            if (passInput) {
                passInput.value = password;
                // Небольшой визуальный эффект для инпута
                passInput.style.backgroundColor = '#f0f7ff';
                setTimeout(() => passInput.style.backgroundColor = '', 500);
            }
        });
    }
});