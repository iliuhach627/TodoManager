document.addEventListener('DOMContentLoaded', function() {
    const searchCard = document.getElementById('searchCard');
    const searchButton = document.querySelector('.collapsible-search-btn');

    // Инициализация Bootstrap Collapse
    const bsCollapse = new bootstrap.Collapse(searchCard, {
        toggle: false
    });

    // Функция обновления текста кнопки
    function updateButtonText(isOpen) {
        const textElement = document.querySelector('.search-btn-text');
        const arrow = document.querySelector('.search-arrow');

        if (isOpen) {
            textElement.textContent = 'Скрыть поиск и фильтры';
            arrow.classList.remove('fa-chevron-down');
            arrow.classList.add('fa-chevron-up');
        } else {
            textElement.textContent = 'Показать поиск и фильтры';
            arrow.classList.remove('fa-chevron-up');
            arrow.classList.add('fa-chevron-down');
        }
    }

    // Проверяем, есть ли параметры поиска в URL
    const urlParams = new URLSearchParams(window.location.search);
    const hasSearchParams = urlParams.has('keyword') || urlParams.has('tagIds');
    const hasFilterInPath = window.location.pathname.includes('/filter/tag/');

    // Проверяем сохраненное состояние
    const savedState = localStorage.getItem('searchOpen');
    const shouldOpen = hasSearchParams || hasFilterInPath || savedState === 'true';

    // Устанавливаем начальное состояние
    if (shouldOpen) {
        bsCollapse.show();
        updateButtonText(true);
    } else {
        bsCollapse.hide();
        updateButtonText(false);
    }

    // Обработчики событий для карточки, а не для кнопки
    searchCard.addEventListener('show.bs.collapse', function() {
        localStorage.setItem('searchOpen', 'true');
        updateButtonText(true);
    });

    searchCard.addEventListener('hide.bs.collapse', function() {
        localStorage.setItem('searchOpen', 'false');
        updateButtonText(false);
    });

    // Обработчик клика по кнопке
    searchButton.addEventListener('click', function() {
        setTimeout(() => {
            const isOpen = searchCard.classList.contains('show');
            updateButtonText(isOpen);
        }, 350);
    });

    // Дополнительно: закрытие карточки при клике на кнопку сброса
    document.querySelector('a[href="/tasks"]').addEventListener('click', function() {
        if (searchCard.classList.contains('show')) {
            bsCollapse.hide();
        }
    });
});