# TodoManager 🚀

[![Java](https://img.shields.io/badge/Java-17-%23ED8B00?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-%236DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-%23336791?logo=postgresql)](https://www.postgresql.org/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.3-%2359666C?logo=hibernate)](https://hibernate.org/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-%23005F0F?logo=thymeleaf)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-%237952B3?logo=bootstrap)](https://getbootstrap.com/)

**TodoManager** — это простое и удобное веб-приложение для управления вашими задачами, построенное на современном стеке технологий Java. Создавайте, редактируйте, отслеживайте и упорядочивайте свои ежедневные задачи с помощью интуитивно понятного интерфейса.

![TodoManager Preview](https://i.imgur.com/riGiWux.png)

## ✨ Возможности

*   **📝 Полный CRUD для задач**: Создание, просмотр, редактирование и удаление задач.
*   **✅ Отметка о выполнении**: Легко отмечайте задачи как выполненные.
*   **🏷️ Категории задач**: Организуйте задачи по категориям для лучшей структуризации.
*   **📅 Даты создания и выполнения**: Устанавливайте дедлайны и отслеживайте, когда задача была создана.
*   **🎨 Чистый и адаптивный UI**: Красивый интерфейс на основе Bootstrap, который отлично выглядит на любом устройстве.
*   **💾 Надежное хранение данных**: Данные сохраняются в реляционной базе данных PostgreSQL.

## 🛠️ Технологический стек

*   **Бэкенд:** Java 17, Spring Boot 3.1.5, Spring Data JPA (Hibernate)
*   **Фронтенд:** Thymeleaf, Bootstrap 5.3, HTML5
*   **База данных:** PostgreSQL
*   **Сборка:** Maven

## 📦 Установка и запуск

Следуйте этим шагам, чтобы запустить проект локально.

### Предварительные требования

1.  **Java 17** или выше.
2.  **Maven** 3.6 или выше.
3.  **PostgreSQL** (убедитесь, что сервер БД запущен).

### Шаги для запуска

1.  **Клонируйте репозиторий:**
    ```bash
    git clone https://github.com/iliuhach627/TodoManager.git
    cd TodoManager
    ```

2.  **Настройте базу данных:**
    *   Создайте новую базу данных в PostgreSQL (например, `todomanager`).
    *   Откройте файл `src/main/resources/application.properties`.
    *   Обновите настройки подключения к БД в соответствии с вашим окружением:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/todomanager
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```

3.  **Соберите и запустите приложение:**
    ```bash
    mvn clean spring-boot:run
    ```

4.  **Откройте приложение в браузере:**
    Перейдите по адресу [http://localhost:8080/tasks](http://localhost:8080/tasks).

## 🗄️ Структура базы данных

Приложение автоматически создаст необходимые таблицы при первом запуске благодаря Hibernate DDL-Auto.

Основные сущности:
*   **`task`**: Содержит информацию о задачах (id, заголовок, описание, статус выполнения, тэги, даты).

## 🚀 Использование

1.  **Главная страница**: На главной странице отображается список всех задач.
2.  **Добавление задачи**: Нажмите кнопку "Создать задачу", заполните заголовок, описание, выберите тэг и нажмите "Создать".
3.  **Редактирование задачи**: Нажмите на задачу в списке, чтобы перейти к форме редактирования.
4.  **Связь с другими задачами**: В карточке задачи выберите задачу, которую хотели бы связать с текущей. 
5.  **Удаление задачи**: Нажмите кнопку "Удалить" в карточке задачи.
6.  **Отметка как выполненной**: Установите флажок "Завершить задачу" в карточке задачи.

## 📁 Структура проекта

```
TodoManager/
├── src/
│ └── main/
│ ├── java/
│ │ └── com/
│ │ └── example/
│ │ └── todomanager/
│ │ ├── controller/ # Контроллеры (TaskController)
│ │ ├── model/ # Модели (Task, Category)
│ │ ├── repository/ # Репозитории (JPA)
│ │ ├── service/ # Сервисный слой
│ │ └── TodoManagerApplication.java
│ └── resources/
│ ├── static/ # CSS, JS
│ ├── templates/ # Thymeleaf HTML шаблоны
│ └── application.properties
├── pom.xml # Maven зависимости
└── README.md
```
