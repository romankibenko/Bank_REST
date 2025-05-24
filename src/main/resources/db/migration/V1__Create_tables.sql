CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,                  -- Уникальный идентификатор пользователя
    username VARCHAR(255) NOT NULL UNIQUE,    -- Логин пользователя (уникальный)
    password VARCHAR(255) NOT NULL,           -- Пароль пользователя (зашифрованный)
    role VARCHAR(50) NOT NULL,                -- Роль пользователя (ADMIN или USER)
    active BOOLEAN NOT NULL,
    holder_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Дата и время обновления записи
);

CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,                  -- Уникальный идентификатор карты
    card_number VARCHAR(16) NOT NULL,         -- Номер карты (зашифрованный)
    holder_name VARCHAR(255) NOT NULL,        -- Имя владельца карты
    expiry_date DATE NOT NULL,            -- Срок действия карты
    status VARCHAR(50) NOT NULL,              -- Статус карты (ACTIVE, BLOCKED, EXPIRED)
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00, -- Баланс карты
    user_id BIGINT NOT NULL,                  -- Внешний ключ на таблицу users
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время обновления записи
    CONSTRAINT fk_cards_user_id FOREIGN KEY (user_id) REFERENCES users(id) -- Внешний ключ
);

-- Создание индекса для ускорения поиска по номеру карты
CREATE INDEX idx_cards_card_number ON cards(card_number);

-- Создание индекса для ускорения поиска по user_id
CREATE INDEX idx_cards_user_id ON cards(user_id);
