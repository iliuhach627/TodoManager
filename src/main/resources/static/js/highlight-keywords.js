// Подсветка ключевых слов при наведении
document.addEventListener('DOMContentLoaded', function() {
    // Подсветка соответствующих ключевых слов при наведении
    const keywordBadges = document.querySelectorAll('.keyword-badge');

    keywordBadges.forEach(badge => {
        const keyword = badge.getAttribute('data-keyword');

        badge.addEventListener('mouseenter', function() {
            // Подсветка этого ключевого слова в тексте
            highlightKeywordInText(keyword);
        });

        badge.addEventListener('mouseleave', function() {
            // Убрать подсветку
            removeHighlightFromText();
        });

        badge.addEventListener('click', function() {
            // Поиск по этому ключевому слову
            searchByKeyword(keyword);
        });
    });
});

function highlightKeywordInText(keyword) {
    // Подсветка ключевого слова в описаниях задач
    const descriptions = document.querySelectorAll('.card-text');
    descriptions.forEach(desc => {
        const text = desc.textContent;
        const regex = new RegExp(`(${keyword})`, 'gi');
        const highlighted = text.replace(regex, '<mark class="keyword-highlight">$1</mark>');
        desc.innerHTML = highlighted;
    });
}

function removeHighlightFromText() {
    // Убрать подсветку
    const marks = document.querySelectorAll('.keyword-highlight');
    marks.forEach(mark => {
        mark.outerHTML = mark.textContent;
    });
}

function searchByKeyword(keyword) {
    // Перенаправить на поиск по ключевому слову
    window.location.href = `/tasks/search?keyword=${encodeURIComponent(keyword)}`;
}