-- =============================================================================
-- RBAC: RENT ve QR rolleri + CRUD yetkileri (tek sefer çalıştırın)
-- =============================================================================
-- Önkoşul: `authorities`, `roles` (service_code kolonu), `role_authorities` tabloları mevcut.
-- Tekrar çalıştırma: ON DUPLICATE KEY / INSERT IGNORE ile idempotent.
-- Not: app.rbac.rent-qr-seed-enabled=true iken AuthService açılışında aynı veri Java ile de eklenir (sql.init kapalı olsa bile).
-- Eski isimler (RENT_CUSTOMER, RENT_STAFF vb.) kullanıyorsanız önce manuel temizlik yapın.
-- =============================================================================

-- ----------------------------------------------------------------------------- authorities (RENT)
INSERT INTO authorities (code, description) VALUES
  ('RENT_ADMIN_READ',    'Kiralama admin — okuma'),
  ('RENT_ADMIN_UPDATE',  'Kiralama admin — güncelleme'),
  ('RENT_ADMIN_WRITE',   'Kiralama admin — oluşturma'),
  ('RENT_ADMIN_DELETE',  'Kiralama admin — silme'),
  ('RENT_MANAGER_READ',    'Kiralama manager — okuma'),
  ('RENT_MANAGER_UPDATE',  'Kiralama manager — güncelleme'),
  ('RENT_MANAGER_WRITE',   'Kiralama manager — oluşturma'),
  ('RENT_MANAGER_DELETE',  'Kiralama manager — silme'),
  ('RENT_USER_READ',    'Kiralama kullanıcı — okuma'),
  ('RENT_USER_UPDATE',  'Kiralama kullanıcı — güncelleme'),
  ('RENT_USER_WRITE',   'Kiralama kullanıcı — oluşturma'),
  ('RENT_USER_DELETE',  'Kiralama kullanıcı — silme')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- ----------------------------------------------------------------------------- authorities (QR)
INSERT INTO authorities (code, description) VALUES
  ('QR_ADMIN_READ',    'QR admin — okuma'),
  ('QR_ADMIN_UPDATE',  'QR admin — güncelleme'),
  ('QR_ADMIN_WRITE',   'QR admin — oluşturma'),
  ('QR_ADMIN_DELETE',  'QR admin — silme'),
  ('QR_MANAGER_READ',    'QR manager — okuma'),
  ('QR_MANAGER_UPDATE',  'QR manager — güncelleme'),
  ('QR_MANAGER_WRITE',   'QR manager — oluşturma'),
  ('QR_MANAGER_DELETE',  'QR manager — silme'),
  ('QR_USER_READ',    'QR kullanıcı — okuma'),
  ('QR_USER_UPDATE',  'QR kullanıcı — güncelleme'),
  ('QR_USER_WRITE',   'QR kullanıcı — oluşturma'),
  ('QR_USER_DELETE',  'QR kullanıcı — silme')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- ----------------------------------------------------------------------------- roles
INSERT INTO roles (code, name, service_code) VALUES
  ('RENT_ADMIN',   'Kiralama — admin',    'RENT'),
  ('RENT_MANAGER', 'Kiralama — manager',  'RENT'),
  ('RENT_USER',    'Kiralama — kullanıcı', 'RENT'),
  ('QR_ADMIN',     'QR — admin',          'QR'),
  ('QR_MANAGER',   'QR — manager',        'QR'),
  ('QR_USER',      'QR — kullanıcı',      'QR')
ON DUPLICATE KEY UPDATE name = VALUES(name), service_code = VALUES(service_code);

-- ----------------------------------------------------------------------------- role_authorities (RENT_ADMIN)
INSERT IGNORE INTO role_authorities (role_id, authority_id)
SELECT r.id, a.id FROM roles r
JOIN authorities a ON a.code IN (
  'RENT_ADMIN_READ', 'RENT_ADMIN_UPDATE', 'RENT_ADMIN_WRITE', 'RENT_ADMIN_DELETE'
) WHERE r.code = 'RENT_ADMIN';

-- RENT_MANAGER
INSERT IGNORE INTO role_authorities (role_id, authority_id)
SELECT r.id, a.id FROM roles r
JOIN authorities a ON a.code IN (
  'RENT_MANAGER_READ', 'RENT_MANAGER_UPDATE', 'RENT_MANAGER_WRITE', 'RENT_MANAGER_DELETE'
) WHERE r.code = 'RENT_MANAGER';

-- RENT_USER
INSERT IGNORE INTO role_authorities (role_id, authority_id)
SELECT r.id, a.id FROM roles r
JOIN authorities a ON a.code IN (
  'RENT_USER_READ', 'RENT_USER_UPDATE', 'RENT_USER_WRITE', 'RENT_USER_DELETE'
) WHERE r.code = 'RENT_USER';

-- QR_ADMIN
INSERT IGNORE INTO role_authorities (role_id, authority_id)
SELECT r.id, a.id FROM roles r
JOIN authorities a ON a.code IN (
  'QR_ADMIN_READ', 'QR_ADMIN_UPDATE', 'QR_ADMIN_WRITE', 'QR_ADMIN_DELETE'
) WHERE r.code = 'QR_ADMIN';

-- QR_MANAGER
INSERT IGNORE INTO role_authorities (role_id, authority_id)
SELECT r.id, a.id FROM roles r
JOIN authorities a ON a.code IN (
  'QR_MANAGER_READ', 'QR_MANAGER_UPDATE', 'QR_MANAGER_WRITE', 'QR_MANAGER_DELETE'
) WHERE r.code = 'QR_MANAGER';

-- QR_USER
INSERT IGNORE INTO role_authorities (role_id, authority_id)
SELECT r.id, a.id FROM roles r
JOIN authorities a ON a.code IN (
  'QR_USER_READ', 'QR_USER_UPDATE', 'QR_USER_WRITE', 'QR_USER_DELETE'
) WHERE r.code = 'QR_USER';
