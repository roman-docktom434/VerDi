function switchTab(index) {
        let tabs = document.querySelectorAll('.tab');
        let forms = document.querySelectorAll('.form');

        tabs.forEach(tab => tab.classList.remove('active'));
        forms.forEach(form => form.classList.remove('active'));

        tabs[index].classList.add('active');
        forms[index].classList.add('active');
    }