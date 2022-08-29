Микросервис авторизации проекта mission

Для регистрации нового пользователя в МС используется End-point (POST) : /reg, который принимает логин и пароль нового
пользователя.
Юзер сохраняется в PostgresSQL таблице 'auth_users', если такого пользователя в БД нет

Для аутентификации используется End-point (POST) : /login, который принимает логин, пароль и информацию о пользователе(
например отпечаток устройства).
Если логин и пароль соответствуют сохраненным в БД, то генерируются access и
refresh токены
которые возвращаются как результат. Так же сохраняется информация о сессии по refresh токену в таблице 'auth_tokens'

Для проверки активности access токена используется End-point (POST) : /check который принимает access токен.
Полученный токен дешифрируется с проверкой сигнатуры, если тип токена не ACCESS или значение expiresAt меньше текущего
времени, то возвращается ошибка

Для востановления протухшего access токена используется End-point (POST) : /refresh который принимает refresh токен и
информацию о пользователе
Полученный токен дешифрируется с проверкой сигнатуры, если тип токена не REFRESH или значение expiresAt меньше текущего
времени, то возвращается ошибка. Из БД получаем информацию о сессии по refresh токену, сверяем время действия токена из
таблицы, проверяем статус сесии и если статус ACTIVE, то генерируется новая пара access, refresh токенов. В случае если
информация о сессии идентична полученной из тела запроса, то информация о сессии переписывается иначе у старой записи
изменяется статус на INVALID и сохраняется новое значение сессии

Для блокировки сессии т.е. refresh токена используем End-point (POST) : /block который принимает refresh jwt токен.
Полученный токен дешифрируется с проверкой сигнатуры, в БД 'auth_tokens' находим соответствующую запись сессии и
изменяет статус на PAUSED

Хеширование пароля:
Для хеширования пароля используем алгоритм Argon2 как наиболее современный и безопасный.
Основанием использования этого алгоритма является
статья https://medium.com/analytics-vidhya/password-hashing-pbkdf2-scrypt-bcrypt-and-argon2-e25aaf41598e
А также информация из
документации https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-argon2

QR авторизация.
Система должна поддерживать авторизацию через куар код. Для этого создан отдельный End-point (POST) : /share, который
принимает refresh-token. 
