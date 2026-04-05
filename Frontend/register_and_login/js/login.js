function switchTab(index) {
    const tabs = document.querySelectorAll('.tab');
    const forms = document.querySelectorAll('.form-content');

    tabs.forEach((tab, i) => {
        const inputs = forms[i].querySelectorAll('input');
        if (i === index) {
            tab.classList.add('active');
            forms[i].classList.add('active');
            forms[i].style.display = 'block';
            // Включаем required только для видимых полей
            inputs.forEach(input => input.required = true);
        } else {
            tab.classList.remove('active');
            forms[i].classList.remove('active');
            forms[i].style.display = 'none';
            // Отключаем required, чтобы не было ошибки "not focusable"
            inputs.forEach(input => {
                input.required = false;
            });
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    // По умолчанию открываем ВУЗ (индекс 0)
    switchTab(0);

    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const activeTab = document.querySelector('.tab.active');
        const role = activeTab.getAttribute('data-role');

        let identifier = "";
        let password = "";
        let redirectUrl = "";

        if (role === 'university') {
            identifier = document.getElementById('hsCode').value;
            password = document.getElementById('vuzPassword').value;
            redirectUrl = '../vuzi/vuz_page.html';
        } else if (role === 'student') {
            identifier = document.getElementById('diplomCode').value;
            password = document.getElementById('studentPassword').value;
            redirectUrl = 'student_page.html';
        } else if (role === 'employer') {
            identifier = document.getElementById('email').value;
            password = document.getElementById('employerPassword').value;
            redirectUrl = 'employer_page.html';
        }

        const data = {
            type: role,
            identifier: identifier,
            password: password
        };

        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            // ПРОВЕРКА: Если бэкенд возвращает JSON (с именем), используем response.json()
            // Если только строку — response.text()
            if (response.ok) {
                const result = await response.json();

                localStorage.setItem('userType', role);
                // Сохраняем имя, которое пришло из базы (hs.getName())
                localStorage.setItem('userName', result.name || identifier);
                localStorage.setItem('userIdentifier', identifier);

                window.location.href = redirectUrl;
            } else {
                const errorText = await response.text();
                alert('Ошибка: ' + errorText);
            }
        } catch (err) {
            console.error('Ошибка сети:', err);
            alert('Нет связи с сервером');
        }
    });
});

