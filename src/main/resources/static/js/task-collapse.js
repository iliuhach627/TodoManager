document.addEventListener('DOMContentLoaded', function() {
    // Инициализация состояния свернутости из localStorage
    initializeCollapseState();

    // Обработчики для кнопок сворачивания
    document.querySelectorAll('.toggle-section-btn').forEach(button => {
        button.addEventListener('click', function() {
            const targetSelector = this.getAttribute('data-bs-target');
            const container = document.querySelector(targetSelector);
            const icon = this.querySelector('.collapse-icon');

            if (container) {
                // Переключаем видимость
                if (container.style.display === 'none') {
                    container.style.display = 'block';
                    icon.classList.remove('fa-chevron-down');
                    icon.classList.add('fa-chevron-up');
                    // Сохраняем состояние
                    localStorage.setItem(targetSelector, 'expanded');
                } else {
                    container.style.display = 'none';
                    icon.classList.remove('fa-chevron-up');
                    icon.classList.add('fa-chevron-down');
                    // Сохраняем состояние
                    localStorage.setItem(targetSelector, 'collapsed');
                }
            }
        });
    });

    function initializeCollapseState() {
        // Для каждой секции проверяем сохраненное состояние
        document.querySelectorAll('.toggle-section-btn').forEach(button => {
            const targetSelector = button.getAttribute('data-bs-target');
            const container = document.querySelector(targetSelector);
            const icon = button.querySelector('.collapse-icon');
            const savedState = localStorage.getItem(targetSelector);

            if (container) {
                // Если состояние сохранено как свернутое или задач больше 5
                if (savedState === 'collapsed' ||
                    (savedState === null && container.children.length > 5)) {
                    container.style.display = 'none';
                    icon.classList.remove('fa-chevron-up');
                    icon.classList.add('fa-chevron-down');
                } else {
                    container.style.display = 'block';
                    icon.classList.remove('fa-chevron-down');
                    icon.classList.add('fa-chevron-up');
                }
            }
        });
    }
});