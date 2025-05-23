# Lab 4. REST exchanger
*ФИО: Дорджеев Роман Владимирович*.


Сервис представляет из себя [тестовый обменник](https://github.com/te9c/KFD), но
теперь созданный по архитектуре `REST`.

По умолчанию обменник не содержит в себе никаких пользователей, валют и котировок.
Создается лишь стандартный пользователь с логином и паролем `admin`, `admin` соответственно.
Дальнейшее создание валют и котировок выполняется с аккаунта админа.

После каждой успешной транзакции курс всех котировок обновляется на 5%.

Большинство эндпоинтов требует аутентификации пользователя, но также есть те,
для которых подойдет анонимная аутентификация, например, `GET /api/rates`.

Важно понимать, что все операции в обменнике выполняются минимальными частями валюты,
т.е. копейки, центы, евроценты и т.д. Курс валютной пары представляет из себя 2 числа:
сам курс и фактор. Например, курс пары `usd/rub` равен `1000`, а фактор равен `100`. Это значит, что
можно купить 100 `usd` за 1000 `rub`, но нельзя при этом купить 50 `usd`, т.к. фактор равен 100.
# Showcase

https://github.com/user-attachments/assets/a9ce36a4-f023-4d00-9e53-51afb18ac6aa

# Эндпоинты API

## Аутентификация

### 1. `POST /api/auth/signup`
Эндпоинт для регистрации пользователей. Принимает:
```json
{
 "username": "string",
 "password": "string",
 "authorities": [
  "string",
  ...
 ]
}
```
`authorities` применяется, только если авторизированный
пользователь имеет роль `ADMIN`, т.е. роли может выдавать
только админ.

Возвращает созданного пользователя

### 2. `POST /api/auth/signin`
Эндпоинт для авторизации пользователй. Принимает:
```json
{
 "username": "string",
 "password": "string"
}
```
При успешной авторизации возвращает `JWT` токен

## Пользователи

### 1. `GET /api/users`
Возвращает всех пользователей. Может вызвать только пользователь с ролью `ADMIN`.

### 2. `GET /api/users/{username}`
Возвращает пользователя с именем `{username}`.
Может вызвать только пользователь с этим именем или админ.

Пример:
```json
{
    "username": "bob",
    "authorities": [
        "ROLE_USER"
    ],
    "balances": [
        {
            "currencyCode": "RUB",
            "amount": 1861080,
            "id": 1
        },
        {
            "currencyCode": "USD",
            "amount": 991000,
            "id": 2
        }
    ],
    "id": 2
}
```

### 3. `DELETE /api/users/{username}`
Удаляет пользователя с именем `{username}`.
Может вызвать только пользователь с этим именем или админ.

## Баланс пользователей

### 1. `GET /api/balance`
Возвращает все балансы. Может вызвать только админ.

### 2. `GET /api/balance/{username}`
Возвращает балансы пользователя с именем `{username}`.
Может вызвать только пользователь с этим именем или админ.

### 3. `POST /api/balance/{username}`
Добавляет баланс пользователю с именем `{username}`. Может вызвать только админ.
Принимает:
```json
{
 "currencyCode": "string",
 "amount": "int"
}
```

## Транзакции

### 1. `GET /api/transactions`
Возвращает все транзакции. Может вызвать только админ

### 2. `GET /api/transactions/user/{username}`
Возвращает все транзакции конкретного пользователя.

Пример:
```json
[
    {
        "id": 1,
        "username": "bob",
        "currencyPair": "USD/RUB",
        "rate": 10214,
        "baseCurrencyDelta": 1000,
        "quoteCurrencyDelta": -102140,
        "timestamp": "2025-01-19T00:29:15.684772"
    },
    {
        "id": 2,
        "username": "bob",
        "currencyPair": "USD/RUB",
        "rate": 10493,
        "baseCurrencyDelta": -1000,
        "quoteCurrencyDelta": 104930,
        "timestamp": "2025-01-19T00:30:19.672024"
    },
    {
        "id": 3,
        "username": "bob",
        "currencyPair": "USD/RUB",
        "rate": 10303,
        "baseCurrencyDelta": -1000,
        "quoteCurrencyDelta": 103030,
        "timestamp": "2025-01-19T00:31:49.219535"
    }
]
```

### 3. `GET /api/transactions/{id}`
Возвращает транзакцию по `{id}`

### 4. `POST /api/transactions`
Совершает транзакцию. Принимает:
```json
{
 "user": "string",
 "currencyPair": "base/quote",
 "transactionType": "selling" || "buying",
 "amount": "int"
}
```
При успешной транзакции возвращает транзакцию, при ошибке возвращает `400`.

После успешной транзакции изменяет все котировки на 5%.

## Котировки

### 1. `GET /api/rates`
Возвращает все котировки.

Пример:
```json
[
    {
        "baseCurrency": "USD",
        "quoteCurrency": "RUB",
        "rate": 8840,
        "factor": 100,
        "id": 1
    }
]
```

### 2. `GET /api/rates/{id}`

### 3. `POST /api/rates`
Добавляет новую котировку. Может вызвать только админ. Принимает:
```json
{
 "baseCurrency": "string",
 "quoteCurrency": "string",
 "rate": "int",
 "factor": "int"
}
```

### 4. `POST /api/rates/update`
Обновляет котировки на 5%.

### 5. `DELETE /api/rates/{id}`

## Баланс обменника

### 1. `GET /api/exchangerbalance`
Возвращает весь баланс обменника.

Пример:
```json
[
    {
        "currencyCode": "USD",
        "amount": 10009000,
        "id": 1
    },
    {
        "currencyCode": "RUB",
        "amount": 9138920,
        "id": 2
    }
]
```

### 2. `GET /api/exchangerbalance/{id}`

### 3. `DELETE /api/exchangerbalance/{id}`

### 4. `POST /api/exchangerbalance`
Добавляет новый баланс обменнику. Может вызвать только админ. Принимает:
```json
{
 "currencyCode": "string",
 "amount": "int"
}
```
