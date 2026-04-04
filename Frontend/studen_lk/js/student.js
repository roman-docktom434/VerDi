function setTime(btn) {
        document.querySelectorAll('.time-buttons button')
            .forEach(b => b.classList.remove('active'));

        btn.classList.add('active');
    }