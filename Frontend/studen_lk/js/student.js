function setTime(btn) {
    const container = btn.closest('#time-select');
    container.querySelectorAll('button')
        .forEach(b => b.classList.remove('active'));
    
    btn.classList.add('active');
}