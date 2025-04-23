# Лабораторная работа по DevOps
*ФИО: Дорджеев Роман Владимирович*

`REST` обменник из предыдущей лабораторной работы был распилен на 2 микросервиса:
`microservice-user`, который отвечает за аутентификацию, регистрацию и получение информации
о пользователях, и `microservice-exchanger`, в котором реализована вся логика обменика.
Микросервисы регистрируются в `service-registry` (`eureka`), гейтвей берет сервисы оттуда.
Distrubuted tracing реализован с помощью `Zipkin`. Аутентификация происходит через
`JWT` токены.

Все `REST` методы остались теми же, что и в [прошлой лабе](https://github.com/te9c/KFD-homework/tree/main/Lab4)

# Showcase


https://github.com/user-attachments/assets/03d521fd-3a35-4e11-af0b-ddf3f37c3ff7

