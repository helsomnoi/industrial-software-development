-- 1. Пользователи
CREATE TABLE users (
	user_id SERIAL PRIMARY KEY,
	username VARCHAR(50) UNIQUE NOT NULL,
	full_name VARCHAR(100) NOT NULL,
	phone_number VARCHAR(20) UNIQUE NOT NULL,
	email VARCHAR(255) UNIQUE NOT NULL,
	city VARCHAR(50),
	avatar_url TEXT,
	bio TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	is_bot BOOLEAN,
	is_online BOOLEAN DEFAULT FALSE,
	last_seen TIMESTAMP,
	registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	status VARCHAR(255),
	private_settings JSONB DEFAULT '{
        "show_last_seen": true,
        "show_avatar": true,
        "allow_calls": true,
        "allow_group_invites": "everyone"
    }'::jsonb
);

-- 2. Сессии пользоваетля
CREATE TABLE users_session (
	session_id SERIAL PRIMARY KEY,
	user_id integer NOT NULL,
	device_type VARCHAR(100) NOT NULL CHECK (device_type IN ('mobile', 'desktop', 'web', 'tablet')),
	device_name VARCHAR(100),
	app_version VARCHAR(20) NOT NULL,
	ip_address INET,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	is_active BOOLEAN,
	last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. Списко контактов
CREATE TABLE contacts (
	contact_id SERIAL PRIMARY KEY,
	owner_user_id INTEGER NOT NULL,	-- Кто добавляет
	contact_user_id INTEGER NOT NULL,	-- Кого
	nickname VARCHAR(100),	-- Пользовательский ник
	is_blocked BOOLEAN DEFAULT FALSE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

	UNIQUE (owner_user_id, contact_user_id),
	FOREIGN KEY (owner_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (contact_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. Черный список
CREATE TABLE blacklists (
	blacklist_id SERIAL PRIMARY KEY,
	blocker_user_id INTEGER,
	blocked_user_id INTEGER,
	blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

	FOREIGN KEY (blocker_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (blocked_user_id) REFERENCES users(user_id) ON DELETE CASCADE	
);

-- 5. Справочная таблица со спецификой разных чатов
CREATE TABLE chat_types (
	type_id SERIAL PRIMARY KEY,
	type_name VARCHAR NOT NULL,
	max_members INTEGER,
	is_ephemeral BOOLEAN DEFAULT FALSE
);

-- Зададим сразу типы чатов
INSERT INTO chat_types (type_name, max_members, is_ephemeral) VALUES
('private', 2, FALSE),
('group', 200, FALSE),
('channel', 100000, FALSE),
('secret_chat', 2, TRUE);

-- 6. Чаты
CREATE TABLE chats (
	chat_id SERIAL PRIMARY KEY,
	type_id INTEGER NOT NULL,
	title VARCHAR(255),
	description TEXT,
	avatar_urs TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	last_message_id INTEGER, -- ключ добавляется после создания таблицы сообщений
	admin_id INTEGER,

	FOREIGN KEY (type_id) REFERENCES chat_types(type_id) ON DELETE RESTRICT,
	FOREIGN KEY (admin_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 7. Участники чатов
CREATE TABLE chat_members (
	member_id SERIAL PRIMARY KEY,
	chat_id INTEGER NOT NULL,
	user_id INTEGER NOT NULL,
	joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	is_admin BOOLEAN DEFAULT FALSE,
	notifications_on BOOLEAN DEFAULT TRUE,
	unread_count INTEGER,
	last_read_message_id INTEGER, -- FK после зодания messages
	permissions JSONB DEFAULT '{"can_send_messages": true, "can_send_media": true, "can_add_users": false}'::jsonb,

	UNIQUE (chat_id, user_id),
	FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 8. Заархивированные чаты
CREATE TABLE archived_chats (
	archive_id SERIAL PRIMARY KEY,
	chat_id INTEGER NOT NULL,
	user_id INTEGER NOT NULL,
	archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mute_until TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

	UNIQUE (chat_id, user_id),
	FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE	
);

-- 9. Справочная таблица для типов сообщений
CREATE TABLE message_types (
	type_id SERIAL PRIMARY KEY,
	type_name VARCHAR(20) UNIQUE NOT NULL,
	can_have_text BOOLEAN DEFAULT TRUE,
	can_have_caption BOOLEAN DEFAULT TRUE,
	max_size_bytes INTEGER
);

-- Заполним сразу типы сообщений
INSERT INTO message_types (type_name, can_have_text, can_have_caption, max_size_bytes) VALUES
('text', TRUE, FALSE, 4096),
('photo', TRUE, TRUE, 10485760),
('video', TRUE, TRUE, 52428800),
('audio', FALSE, TRUE, 16777216),
('voice', FALSE, FALSE, 8388608),
('document', TRUE, TRUE, 209715200),
('location', TRUE, FALSE, 1024),
('contact', TRUE, FALSE, 2048),
('system', TRUE, FALSE, 1024);

-- 10. Сообения
CREATE TABLE messages (
	message_id SERIAL PRIMARY KEY,
	type_id INTEGER,
	sender_user_id INTEGER,
	chat_id INTEGER,
	reply_to_message_id INTEGER,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	edited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	is_edited BOOLEAN DEFAULT FALSE,
	is_pinned BOOLEAN DEFAULT FALSE,
	is_deleted BOOLEAN DEFAULT FALSE,
	deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	deleted_for_everyone BOOLEAN DEFAULT FALSE,

	FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES message_types(type_id) ON DELETE CASCADE,
    FOREIGN KEY (reply_to_message_id) REFERENCES messages(message_id) ON DELETE CASCADE
);

-- добавление FK last_message_id 
ALTER TABLE chats ADD CONSTRAINT fk_chats_last_message 
    FOREIGN KEY (last_message_id) REFERENCES messages(message_id) ON DELETE SET NULL;

-- добавление FK last_read_message_id 
ALTER TABLE chat_members ADD CONSTRAINT fk_chat_members_last_read 
    FOREIGN KEY (last_read_message_id) REFERENCES messages(message_id) ON DELETE SET NULL;

-- 11. Медиа
CREATE TABLE media (
	media_id SERIAL PRIMARY KEY,
	message_id INTEGER,
	file_name VARCHAR(255),
	file_type VARCHAR(20),
	file_size INTEGER NOT NULL,
	file_url TEXT NOT NULL,
	duration_seconds INTEGER, --Тоько для аудио и видео
	width INTEGER, --фото/видео
	height INTEGER, --фото/видео

	FOREIGN KEY (message_id) REFERENCES messages(message_id)
);

-- 12. Просмотры сообщений
CREATE TABLE messages_view (
	view_id SERIAL PRIMARY KEY,
	message_id INTEGER NOT NULL,
	user_id INTEGER NOT NULL,
	viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

	UNIQUE(message_id, user_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (message_id) REFERENCES messages(message_id) ON DELETE CASCADE
);

-- 13. Сохраненные сообщения
CREATE TABLE saved_messages (
	saved_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    message_id INTEGER NOT NULL,
    saved_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    note TEXT,
    
    UNIQUE(user_id, message_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (message_id) REFERENCES messages(message_id) ON DELETE CASCADE
);

-- 14. Звонки
CREATE TABLE calls (
	call_id SERIAL PRIMARY KEY,
    initiator_user_id INTEGER NOT NULL,
    chat_id INTEGER NOT NULL,
	call_type VARCHAR(20) NOT NULL CHECK (call_type IN ('voice', 'video')),
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP WITH TIME ZONE,
    duration_seconds INTEGER,
    status VARCHAR(20) DEFAULT 'initiated' CHECK (status IN ('initiated', 'ongoing', 'completed', 'missed', 'rejected')),

	FOREIGN KEY (chat_id) REFERENCES chats(chat_id),
    FOREIGN KEY (initiator_user_id) REFERENCES users(user_id)
);

-- 15. Участники звонков
CREATE TABLE call_members (
	member_id SERIAL PRIMARY KEY,
    call_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE,
    left_at TIMESTAMP WITH TIME ZONE,
    
    UNIQUE(call_id, user_id),
    FOREIGN KEY (call_id) REFERENCES calls(call_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 16. Уведомления
CREATE TABLE notifications (
	    notification_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    type_ VARCHAR(50) NOT NULL CHECK(type_ IN ('message', 'call', 'reaction', 'invite')),
    title VARCHAR(255),
    body TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


