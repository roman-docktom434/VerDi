document.addEventListener('DOMContentLoaded', () => {
    // ЭЛЕМЕНТЫ ФОРМЫ 
    const studentForm = document.getElementById('studentForm');
    const generateBtn = document.getElementById('generateBtn');
    const passwordInput = document.getElementById('generatedPassword');
    const studentNameInput = document.getElementById('studentName');
    const nameError = document.getElementById('nameError');
    const passError = document.getElementById('passError');

    const updateUniversityName = () => {
        // Достаем название, которое сохранили при логине
        const uniName = localStorage.getItem('userName'); // или 'uniIdentifier', смотря что сохранял
        const displayElement = document.querySelector('.user-info__name');

        if (uniName && displayElement) {
            displayElement.textContent = uniName;
        } else if (displayElement) {
            displayElement.textContent = "ВУЗ не определен";
        }
    };

// Вызываем функцию сразу при загрузке
    updateUniversityName();

    // СБРОС ФОРМЫ 
    const resetFormToInitial = () => {
        studentForm.reset(); 
        
        generateBtn.disabled = true;
        
        passwordInput.value = '';
        passwordInput.placeholder = "Сначала введите ФИО";
        
        studentNameInput.classList.remove('input-error');
        passwordInput.classList.remove('input-error');
        
        nameError.style.display = 'none';
        passError.style.display = 'none';
    };

    // ФУНКЦИЯ УВЕДОМЛЕНИЯ
    const showSuccessModal = (name, diploma, password) => {
        const modalOverlay = document.createElement('div');
        modalOverlay.style = `
            position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,0.75); display: flex; align-items: center;
            justify-content: center; z-index: 1000; font-family: 'Inter', sans-serif;
        `;

        const modal = document.createElement('div');
        modal.style = `
            background: #222; color: #fff; padding: 30px; border-radius: 15px;
            width: 90%; max-width: 450px; box-shadow: 0 15px 40px rgba(0,0,0,0.6);
        `;

        const dataToCopy = `ФИО: ${name}\nНомер диплома: ${diploma}\nПароль для входа: ${password}`;

        modal.innerHTML = `
            <h3 style="margin-bottom: 20px; font-size: 20px; letter-spacing: 0.5px;">Уведомление</h3>
            <p style="margin-bottom: 20px; color: #4ade80; display: flex; align-items: center; gap: 10px; font-weight: 500;">
                <span style="background: #4ade80; color: #222; border-radius: 4px; padding: 2px 6px; font-size: 12px; font-weight: 800;">✓</span> 
                Студент успешно зарегистрирован!
            </p>
            <div style="margin-bottom: 25px; line-height: 1.8; font-size: 15px; background: #2d2d2d; padding: 18px; border-radius: 12px; border: 1px solid #3d3d3d;">
                <p><strong>ФИО:</strong> ${name}</p>
                <p><strong>Номер диплома:</strong> ${diploma}</p>
                <p><strong>Пароль для входа:</strong> <span style="color: #8eadc0;">${password}</span></p>
            </div>
            <p style="margin-bottom: 25px; opacity: 0.7; font-size: 14px;">Скопируйте данные и передайте их студенту.</p>
            <div style="display: flex; gap: 12px; justify-content: flex-end;">
                <button id="copyAllBtn" style="background: #648296; color: white; border: none; padding: 12px 20px; border-radius: 10px; cursor: pointer; font-weight: 600; transition: 0.2s;">Копировать</button>
                <button id="closeModalBtn" style="background: #e2e8f0; color: #222; border: none; padding: 12px 25px; border-radius: 10px; cursor: pointer; font-weight: 600;">Закрыть</button>
            </div>
        `;

        document.body.appendChild(modalOverlay);
        modalOverlay.appendChild(modal);

        document.getElementById('copyAllBtn').onclick = () => {
            navigator.clipboard.writeText(dataToCopy).then(() => {
                const btn = document.getElementById('copyAllBtn');
                btn.textContent = 'Скопировано!';
                btn.style.background = '#48bb78';
                setTimeout(() => { 
                    btn.textContent = 'Копировать'; 
                    btn.style.background = '#648296';
                }, 1500);
            });
        };

        document.getElementById('closeModalBtn').onclick = () => modalOverlay.remove();
    };

    studentNameInput.addEventListener('input', () => {
        const hasText = studentNameInput.value.trim().length > 0;
        
        generateBtn.disabled = !hasText;

        if (hasText) {
            studentNameInput.classList.remove('input-error');
            nameError.style.display = 'none';
        }
    });

    //ГЕНЕРАЦИЯ ПАРОЛЯ
    generateBtn.addEventListener('click', () => {
        const charset = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        let password = "";
        for (let i = 0; i < 10; i++) {
            password += charset.charAt(Math.floor(Math.random() * charset.length));
        }
        passwordInput.value = password;

        passwordInput.classList.remove('input-error');
        passError.style.display = 'none';
    });

    //Отправка формы
    studentForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const fullName = studentNameInput.value.trim();
        const generatedPass = passwordInput.value;

        if (!generatedPass) {
            passwordInput.classList.add('input-error');
            passError.style.display = 'block';
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/students/activate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    fullName: fullName,
                    password: generatedPass
                })
            });

            if (response.ok) {
                const result = await response.json();
                // Показываем модалку с данными, которые подтвердил сервер
                showSuccessModal(result.name, result.diploma, result.password);
                resetFormToInitial();
            } else {
                const errorMsg = await response.text();
                studentNameInput.classList.add('input-error');
                nameError.textContent = errorMsg; // Выводим текст ошибки от сервера
                nameError.style.display = 'block';
            }
        } catch (err) {
            console.error("Ошибка сети:", err);
            alert("Нет связи с сервером");
        }
    });

    passwordInput.addEventListener('click', function() {
        if (this.value) {
            navigator.clipboard.writeText(this.value);
            const originalTip = this.placeholder;
            this.placeholder = "Пароль скопирован!";
            setTimeout(() => { this.placeholder = originalTip; }, 1000);
        }
    });
});