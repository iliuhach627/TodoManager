$(document).ready(function() {
    // Переключение комментариев
    $('.toggle-comments').click(function() {
        const taskId = $(this).data('task-id');
        const container = $('#comments-' + taskId);

        if (container.is(':visible')) {
            container.hide();
            $(this).text('Показать комментарии');
        } else {
            if (container.children().length === 0) {
                // Загружаем комментарии через AJAX
                $.get('/tasks/' + taskId + '/comments', function(comments) {
                    comments.forEach(comment => {
                        container.append(`
                            <div class="card mb-2">
                                <div class="card-body">
                                    <p class="card-text">${comment.content}</p>
                                    <small class="text-muted">
                                        ${new Date(comment.createdAt).toLocaleString()}
                                    </small>
                                </div>
                            </div>
                        `);
                    });
                });
            }
            container.show();
            $(this).text('Скрыть комментарии');
        }
    });

    // Подсветка ключевых слов в описании
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


});