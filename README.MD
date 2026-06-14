# Инструкция к запуску программы:
1. **Установите Docker**

2. **Запустите Docker-контейнер c БД PostgreSQL**
```bash
docker run --name <имя контейнера> -e POSTGRES_PASSWORD=<пароль от БД> -p 5432:5432 -d postgres
```
Пример:
```bash
docker run --name my-postgres -e POSTGRES_PASSWORD=root -p 5432:5432 -d postgres
```

3. **В корне проекта создайте файл .env со следующим содержанием**
```.env
JWT_SECRET_KEY=<JWT-ключ длиной минимум 32 байта>
# Пример: mySuperSecretKeyForJWTThatIsAtLeast32CharactersLong123!

DB_NAME=postgres    # имя БД по умолчанию
DB_USER=postgres    # имя суперпользователя PostgreSQL
DB_PASS=<password>  # указанный пароль при запуске docker-контейнера
DB_PORT=5432        # порт для подключения на локальной машине
```

4. **Запустите приложение**
```text
Используя IDE: запустите файл WePrintSouvenirsApplication.java (путь: src/main/java/com/weprintsouvenirs/we_print_souvenirs)
Используя Gradle: ./gradlew bootRun (для Linux/MacOS) или gradlew.bat bootRun (для Windows)
```