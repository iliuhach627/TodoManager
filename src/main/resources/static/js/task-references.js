document.addEventListener('DOMContentLoaded', function() {
    // Автодополнение для поля ввода ID задачи
    const referenceInput = document.querySelector('input[name="referencedTaskId"]');
    if (referenceInput) {
        referenceInput.addEventListener('input', function() {
            const searchTerm = this.value;
            if (searchTerm.length > 1) {
                fetch('/tasks/' + taskId + '/references/search?q=' + encodeURIComponent(searchTerm))
                    .then(response => response.json())
                    .then(data => {
                        // Обновляем datalist
                        const datalist = document.getElementById('tasksList');
                        datalist.innerHTML = '';
                        data.forEach(task => {
                            const option = document.createElement('option');
                            option.value = task.id;
                            option.textContent = task.text;
                            datalist.appendChild(option);
                        });
                    });
            }
        });
    }
});
