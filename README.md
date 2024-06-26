# rbserver
### v1.1.1
Некоторые запросы теперь требуют роль администратора.

## v1.1
Добавлена авторизация и регистрация нормального человека, через jwt токены.
Удалена психика основного разработчика

## v1.0
Полностью реализованы оставшиеся части приложения: шаги рецепта, комментарии.

### v0.4.1
Ингридиенты были протестированы (вручную): всё работает

Написаны модульные тесты для сервиса продуктов для методов: getAllPublic, add, update, delete  (тесты всех вариантов ответа).

## v0.4
Реализована работа с ингредиентами рецептов:
- Поиск по рецепту, по id;
- Добавлениие одного ингридента, списка ингредиентов;
- Обновление и удаление (полное).

Тестирование не проводилось, ибо голова заболела.

### v0.3.2
Продолжается работы над рецептами:
- реализован поиск доступных рецептов, поиск по id, поиск по строке (в виде необязательного параметра), обновление, удадение (мягкое), востановление.
- Добавлен метод для проверки соотвествия строки шаблону (для сокращённого использования Pattern)
  
### v0.3.1
Продолжается работа над рецептами:
- в сервис добавлен поиск только публичных рецептов
- в контроллере добавлено добавление (-_-) рецепта (без учёта шагов и ингридиентов, они будут добавлятся отдельными зпаросами)
- в контроллере добавлен поиск всех рецептов с разделением на полный и неполный ответ

## v0.3
Начата работа над рецептами. Снова изменена модель: в рецепты добавлено поле isPublic. Начата работа над сервисом и контроллером продуктов.
Добавлены некотрые DTO для рецептов, ингридиентов, шагов.

Решено создавать отдельные сервисы и контроллеры для шагов, рецептов, ингрилиентов. Сначала в котроллере рецептов будет создвавтся рецепт, а потом в него можно будет добавить шаги и ингридиенты в соответствующих контроллерах. Вероятно так будет проще, чем собирать всё это в один большой класс.

### v0.2.2
Исправления Get запросов, и добавлене получения по id в продуктах.

### v0.2.1
Добавлено мягкое удаление и востановление продуктов.

В рецепты заложена возможность мягкого удаления в будующем.

Необхадима перезагрузка БД для работы с новыми возможностями.

## v0.2
Добавлен полный цикл работы с продуктами(контроллер, сервис и репозиторий)

Добавлено:
- Контроллер продуктов
- DTO для запросов к  контроллеру продуктов и его ответов
- Метод GetAvalible сервиса продуктов для получения всех продуктов, доступных конкретному пользователю

Изменено:
- Метод add сервиса продуктов, добавлено возвращение значения в виде статуса, а также проверки вводимых данных, которые также были доработаны для удобства
- Метод update был доработан так же как и add

В запросах контроллера в качестве временной меры получения пользователя используется обязательный параметр userId, в идеале он должен получатся из заголовка authorization, но кое-кто всё ещё не доделал 
Spring Security.

В методе изменения продукта можно будет позволить администраторам изменять продукты (сейчас есть проверка на идентичность пользователя, сделавшего запрос, и автора продукта). В целом необходимо будет запретить некоторые запросы для простых пользователей.

## v0.1

Добавлено в сервисе и контроллере пользователей:
- получение пользователя по id
- добавление пользователя вручную (не регистрация)

Также добавлено:
- request для добавления пользователя
- enum со статусами для возвращения сервисами информации о результате добавления, обновления, удаления сущностей

Исправлено:
- securityConfig - больше не требует сгенерированный пароль при начале работы с API, ибо всем запросам отключено требование авторизации
