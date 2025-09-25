document.addEventListener('DOMContentLoaded', function() {
    initializeDateFilters();
    setupCustomDateValidation();
});

function initializeDateFilters() {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const applyButton = document.getElementById('findTasksByDate');

    if (!startDateInput || !endDateInput || !applyButton) {
        return;
    }

    // Устанавливаем максимальную дату как сегодня
    const today = new Date().toISOString().split('T')[0];
    startDateInput.max = today;
    endDateInput.max = today;

    // Загружаем сохраненные значения из localStorage
    const savedStartDate = localStorage.getItem('customDateStart');
    const savedEndDate = localStorage.getItem('customDateEnd');

    if (savedStartDate) startDateInput.value = savedStartDate;
    if (savedEndDate) endDateInput.value = savedEndDate;

    // Проверяем состояние кнопки при загрузке
    updateApplyButtonState();

    // Обработчики изменений дат
    startDateInput.addEventListener('change', function() {
        if (this.value && endDateInput.value && this.value > endDateInput.value) {
            endDateInput.value = this.value;
        }
        endDateInput.min = this.value;
        saveDatePreferences();
        updateApplyButtonState();
    });

    endDateInput.addEventListener('change', function() {
        if (this.value && startDateInput.value && this.value < startDateInput.value) {
            this.value = startDateInput.value;
        }
        saveDatePreferences();
        updateApplyButtonState();
    });

    // Обработчик очистки полей
    const clearButton = document.querySelector('.btn-outline-danger');
    if (clearButton) {
        clearButton.addEventListener('click', function() {
            setTimeout(() => {
                startDateInput.value = '';
                endDateInput.value = '';
                localStorage.removeItem('customDateStart');
                localStorage.removeItem('customDateEnd');
                updateApplyButtonState();
            }, 100);
        });
    }

    function updateApplyButtonState() {
        const hasStartDate = startDateInput.value.trim() !== '';
        const hasEndDate = endDateInput.value.trim() !== '';
        const isFormValid = hasStartDate && hasEndDate;

        if (isFormValid) {
            applyButton.disabled = false;
            applyButton.classList.remove('btn-secondary');
            applyButton.classList.add('btn-primary');
            applyButton.title = 'Применить фильтр по дате';
        } else {
            applyButton.disabled = true;
            applyButton.classList.remove('btn-primary');
            applyButton.classList.add('btn-secondary');
            applyButton.title = 'Заполните обе даты для применения фильтра';
        }

        // Визуальная индикация обязательных полей
        if (hasStartDate !== hasEndDate) {
            // Только одна дата заполнена - показываем предупреждение
            if (hasStartDate && !hasEndDate) {
                endDateInput.classList.add('is-invalid');
                startDateInput.classList.remove('is-invalid');
            } else if (!hasStartDate && hasEndDate) {
                startDateInput.classList.add('is-invalid');
                endDateInput.classList.remove('is-invalid');
            }
        } else {
            startDateInput.classList.remove('is-invalid');
            endDateInput.classList.remove('is-invalid');
        }
    }

    function saveDatePreferences() {
        if (startDateInput.value) {
            localStorage.setItem('customDateStart', startDateInput.value);
        }
        if (endDateInput.value) {
            localStorage.setItem('customDateEnd', endDateInput.value);
        }
    }
}

function setupCustomDateValidation() {
    const dateForm = document.querySelector('.date-range-form');

    if (dateForm) {
        dateForm.addEventListener('submit', function(e) {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            if (!startDate || !endDate) {
                e.preventDefault();
                showDateValidationError();
                return false;
            }

            // Проверяем, что начальная дата не больше конечной
            if (new Date(startDate) > new Date(endDate)) {
                e.preventDefault();
                showDateRangeError();
                return false;
            }

            return true;
        });
    }
}

function showDateValidationError() {
    // Можно добавить красивую нотификацию вместо alert
    const existingAlert = document.querySelector('.date-validation-alert');
    if (existingAlert) {
        existingAlert.remove();
    }

    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-warning alert-dismissible fade show date-validation-alert mt-2';
    alertDiv.innerHTML = `
        <i class="fas fa-exclamation-triangle me-2"></i>
        <strong>Заполните обе даты</strong> - начальную и конечную дату для фильтрации.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    const dateForm = document.querySelector('.date-range-form');
    dateForm.parentNode.insertBefore(alertDiv, dateForm.nextSibling);

    // Автоматически скрываем через 5 секунд
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

function showDateRangeError() {
    const existingAlert = document.querySelector('.date-range-alert');
    if (existingAlert) {
        existingAlert.remove();
    }

    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show date-range-alert mt-2';
    alertDiv.innerHTML = `
        <i class="fas fa-calendar-times me-2"></i>
        <strong>Ошибка в диапазоне дат</strong> - начальная дата не может быть больше конечной.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    const dateForm = document.querySelector('.date-range-form');
    dateForm.parentNode.insertBefore(alertDiv, dateForm.nextSibling);

    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

// Функция для установки быстрых дат (сегодня, неделя, месяц)
function setQuickDateRange(range) {
    const today = new Date();
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');

    let startDate = new Date();

    switch(range) {
        case 'today':
            startDate = today;
            break;
        case 'week':
            startDate.setDate(today.getDate() - 7);
            break;
        case 'month':
            startDate.setMonth(today.getMonth() - 1);
            break;
        default:
            return;
    }

    // Форматируем даты в YYYY-MM-DD
    const formatDate = (date) => date.toISOString().split('T')[0];

    startDateInput.value = formatDate(startDate);
    endDateInput.value = formatDate(today);

    // Обновляем состояние кнопки
    updateApplyButtonState();

    // Сохраняем в localStorage
    localStorage.setItem('customDateStart', startDateInput.value);
    localStorage.setItem('customDateEnd', endDateInput.value);
}

// Функция для очистки дат
function clearDateFilters() {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');

    if (startDateInput) startDateInput.value = '';
    if (endDateInput) endDateInput.value = '';

    localStorage.removeItem('customDateStart');
    localStorage.removeItem('customDateEnd');

    updateApplyButtonState();
}

// Экспортируем функции для глобального использования
window.dateFilter = {
    setQuickRange: setQuickDateRange,
    clearFilters: clearDateFilters
};