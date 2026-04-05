function switchTab(index) {
    const tabs = document.querySelectorAll('.tab');
    const forms = document.querySelectorAll('.form');
    const typeInput = document.getElementById('type');
    const types = ['university', 'student', 'employer'];

    if (typeInput) typeInput.value = types[index];
    tabs.forEach(tab => tab.classList.remove('active'));
    tabs[index].classList.add('active');

    forms.forEach((form, i) => {
        const isActive = (i === index);
        form.classList.toggle('active', isActive);
        form.querySelectorAll('input').forEach(input => {
            if (input.type !== 'hidden') input.required = isActive;
        });
    });
}

function setupAutocomplete(inputId, suggestionsId) {
    const input = document.getElementById(inputId);
    const suggestionsContainer = document.getElementById(suggestionsId);

    if (!input || !suggestionsContainer) return;

    // Применяем "аккуратные" стили программно
    Object.assign(suggestionsContainer.style, {
        position: 'absolute',
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        borderRadius: '4px',
        maxHeight: '150px', // Ограничение высоты
        overflowY: 'auto',   // Скролл
        width: input.offsetWidth + 'px', // Ширина ровно как у инпута
        zIndex: '1000',
        display: 'none',
        boxShadow: '0 4px 6px rgba(0,0,0,0.1)'
    });

    input.addEventListener('input', async () => {
        const query = input.value.trim();
        if (query.length < 3) {
            suggestionsContainer.style.display = 'none';
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/dict/universities?query=${encodeURIComponent(query)}`);
            const uniList = await response.json();

            if (uniList.length > 0) {
                suggestionsContainer.innerHTML = '';
                suggestionsContainer.style.display = 'block';
                suggestionsContainer.style.width = input.offsetWidth + 'px'; // Обновляем ширину на случай ресайза

                uniList.forEach(fullName => {
                    const item = document.createElement('div');
                    // Стили элемента списка
                    Object.assign(item.style, {
                        padding: '8px 12px',
                        cursor: 'pointer',
                        fontSize: '14px',
                        whiteSpace: 'nowrap',    // В одну строчку
                        overflow: 'hidden',       // Скрываем лишнее
                        textOverflow: 'ellipsis', // Троеточие
                        borderBottom: '1px solid #f0f0f0'
                    });

                    item.textContent = fullName;
                    item.title = fullName; // Подсказка при наведении мышкой

                    item.onmouseover = () => item.style.backgroundColor = '#f5f5f5';
                    item.onmouseout = () => item.style.backgroundColor = '#fff';

                    item.onclick = () => {
                        input.value = fullName;
                        const hiddenHsCode = document.getElementById('hsCodeHidden');
                        if (hiddenHsCode && input.id === 'hsSearch') hiddenHsCode.value = fullName;
                        suggestionsContainer.style.display = 'none';
                    };
                    suggestionsContainer.appendChild(item);
                });
            } else {
                suggestionsContainer.style.display = 'none';
            }
        } catch (err) {
            console.error("Ошибка словаря:", err);
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    switchTab(0);
    setupAutocomplete('uniNameSearch', 'uniSuggestions');
    setupAutocomplete('hsSearch', 'suggestions');

    document.addEventListener('click', (e) => {
        if (!e.target.closest('.autocomplete-wrapper')) {
            document.querySelectorAll('[id$="uggestions"]').forEach(el => el.style.display = 'none');
        }
    });

    const regForm = document.getElementById('registrationForm');
    if (regForm) {
        regForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const typeValue = document.getElementById('type').value;
            const activeForm = document.querySelector('.form.active');
            const data = { type: typeValue };

            activeForm.querySelectorAll('input').forEach(input => {
                if (input.name && input.name !== 'confirm_password') {
                    data[input.name] = input.value;
                }
            });

            const password = activeForm.querySelector('input[name="password"]');
            const confirm = activeForm.querySelector('input[name="confirm_password"]');
            if (password && confirm && password.value !== confirm.value) {
                alert("Пароли не совпадают!");
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });
                const res = await response.text();
                if (response.ok) {
                    alert('Успех!');
                    window.location.href = 'login.html';
                } else {
                    alert('Ошибка: ' + res);
                }
            } catch (err) {
                alert('Бэкенд не отвечает');
            }
        });
    }
});