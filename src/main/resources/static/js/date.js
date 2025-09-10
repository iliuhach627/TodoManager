document.addEventListener('DOMContentLoaded', function() {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const findButton = document.getElementById('findTasksByDate');

    const initialValues = {
        startDate: startDateInput.value,
        endDate: endDateInput.value
    };

    function validateDates() {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);
        const hasChanges = startDateInput.value !== initialValues.startDate &&
            endDateInput.value !== initialValues.endDate;

        // Проверяем, что дата "От" не позже даты "До"
        const isValid = !startDateInput.value || !endDateInput.value || startDate <= endDate;

        findButton.disabled = !hasChanges || !isValid;

        // Визуальная индикация
        if (!isValid) {
            findButton.title = "Дата 'От' не может быть позже даты 'До'";
        } else {
            findButton.title = "";
        }
    }

    // Обработчики событий
    startDateInput.addEventListener('change', validateDates);
    endDateInput.addEventListener('change', validateDates);
    startDateInput.addEventListener('input', validateDates);
    endDateInput.addEventListener('input', validateDates);

    validateDates();
});