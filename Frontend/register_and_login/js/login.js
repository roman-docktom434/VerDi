function switchTab(index) {
    const tabs = document.querySelectorAll('.tab');
    const forms = document.querySelectorAll('.form');
    const typeInput = document.getElementById('type');

    tabs.forEach(tab => tab.classList.remove('active'));
    forms.forEach((form, i) => {
        const isActive = (i === index);
        form.classList.toggle('active', isActive);

        // ВАЖНО: Отключаем required для всех полей, которые сейчас скрыты
        form.querySelectorAll('input').forEach(input => {
            if (input.type !== 'hidden') {
                input.required = isActive;
            }
        });
    });

    tabs[index].classList.add('active');

    const types = ['university', 'student', 'employer'];
    if (typeInput) typeInput.value = types[index];
}

// ... switchTab остается без изменений ...

document.addEventListener('DOMContentLoaded', () => {
    switchTab(0);
    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const typeValue = document.getElementById('type').value;
        const activeForm = document.querySelector('.form.active');
        const data = { type: typeValue };

        // Собираем данные
        activeForm.querySelectorAll('input').forEach(input => {
            if (input.name) {
                data[input.name] = input.value;
            }
        });

        // --- КОРРЕКЦИЯ КЛЮЧЕЙ ПОД ТВОЙ БЭКЕНД ---

        if (typeValue === 'university') {
            // Твой бэк ждет "identifier", а в HTML у нас "hsCode"
            data["identifier"] = data["hsCode"];
        }
        else if (typeValue === 'employer') {
            // Твой бэк ждет "identifier" (это почта), а в HTML "email"
            data["identifier"] = data["email"];
        }

        console.log('Данные улетают на бэк:', data);

        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            const resultText = await response.text();

            if (response.ok) {
                alert('Успешно: ' + resultText);
                window.location.href = 'dashboard.html';
            } else {
                // Если придет 401 (Неверные данные), ты увидишь это тут
                alert('Ошибка: ' + resultText);
            }
        } catch (err) {
            alert('Ошибка сети или 403 (CORS/Security)');
        }
    });
});