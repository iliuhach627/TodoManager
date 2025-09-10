// Обработчик кнопки комментариев
$('.toggle-comments').click(function() {
    const taskId = $(this).data('task-id');
    const container = $(this).next('.comments-container');
    container.collapse('toggle');
});

// Подсветка ключевых слов
$('.card-text').each(function() {
    const text = $(this).text();
    const keywords = $(this).closest('.task-card').find('.keyword-badge').map(function() {
        return $(this).text();
    }).get();

    if (keywords.length > 0) {
        let highlightedText = text;
        keywords.forEach(keyword => {
            const regex = new RegExp(keyword, 'gi');
            highlightedText = highlightedText.replace(regex,
                `<span class="highlighted-keyword">${keyword}</span>`);
        });
        $(this).html(highlightedText);
    }
});