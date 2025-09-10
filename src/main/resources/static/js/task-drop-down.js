document.addEventListener('DOMContentLoaded', function () {
    // Раскрытие карточки по клику на заголовок
    document.querySelectorAll('.task-card .card-header').forEach(header => {
        header.addEventListener('click', function (e) {
            if (e.target.tagName === 'BUTTON' || e.target.closest('button') ||
                e.target.tagName === 'A' || e.target.closest('a')) {
                return;
            }

            const cardBody = this.closest('.card').querySelector('.description-section');
            const bsCollapse = new bootstrap.Collapse(cardBody);
            bsCollapse.toggle();
        });
    });

    // Обработчик кнопки комментариев
    document.querySelectorAll('.comments-btn').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();

            const commentsContainer = this.closest('.card').querySelector('.comments-container');
            const bsCollapse = new bootstrap.Collapse(commentsContainer);
            bsCollapse.toggle();

            // Показываем описание если скрыто
            const descriptionSection = this.closest('.card').querySelector('.description-section');
            if (!descriptionSection.classList.contains('show')) {
                new bootstrap.Collapse(descriptionSection).show();
            }
        });
    });
});