# 🍔 Food Delivery REST API

Spring Boot REST API для сервиса доставки еды (подобный Яндекс.Еда/Uber Eats).

## 🚀 Технологии
- Java 17
- Spring Boot 3.1.5
- Spring Data JPA
- H2 Database (in-memory)
- Maven
- JUnit 5 + MockMvc

## 📋 Реализованные функции

### Пользователи ✅
- `POST /users` - регистрация пользователя
- `GET /users/{id}` - профиль пользователя
- `PUT /users/{id}` - обновление профиля
- `GET /users?role=...` - фильтр по роли
- `DELETE /users/{id}` - деактивация пользователя
- `PATCH /users/{id}/activate` - активация пользователя

### Модели данных
- **User** с полями: id, username, email, password, phone, role, active, createdAt, updatedAt
- **Role**: CUSTOMER, RESTAURANT_MANAGER, COURIER, ADMIN

## 🛠️ Установка и запуск

### Требования
- Java 17 или выше
- Maven 3.6+

### Запуск приложения
```bash
# Клонируйте репозиторий
git clone https://github.com/YOUR_USERNAME/food-delivery-api.git
cd food-delivery-api

# Соберите проект
mvn clean package

# Запустите приложение
mvn spring-boot:run
