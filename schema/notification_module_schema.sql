-- Notification Module Schema for PostgreSQL

-- Drop tables if they exist
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS notification_templates CASCADE;

-- Create notification_templates table
CREATE TABLE notification_templates (
    id UUID PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    content TEXT NOT NULL,
    subject VARCHAR(255),
    channel VARCHAR(50) NOT NULL,
    locale VARCHAR(10) NOT NULL,
    html_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

-- Create notifications table with indexes
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    template_id VARCHAR(255),
    group_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    scheduled_for TIMESTAMP,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP,
    metadata TEXT,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    max_attempts INTEGER DEFAULT 3,
    next_retry_at TIMESTAMP,
    attachment_url VARCHAR(1024),
    sender VARCHAR(255),
    html_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    failure_reason TEXT
);

-- Create indexes
CREATE INDEX idx_notifications_recipient ON notifications(recipient);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_scheduled_for ON notifications(scheduled_for);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_updated_at ON notifications(updated_at);
CREATE INDEX idx_notifications_next_retry_at ON notifications(next_retry_at);
CREATE INDEX idx_notifications_group_id ON notifications(group_id);
CREATE INDEX idx_notifications_template_id ON notifications(template_id);

-- Comment on tables
COMMENT ON TABLE notification_templates IS 'Stores notification templates for various channels';
COMMENT ON TABLE notifications IS 'Stores all notification records with their statuses and metadata';

-- Insert some default templates
INSERT INTO notification_templates (
    id, code, name, description, content, subject, channel, 
    locale, html_enabled, created_at, updated_at, active
) VALUES (
    gen_random_uuid(), 
    'welcome_email', 
    'Welcome Email', 
    'Email sent to new users upon registration', 
    '<html><body><h1>Welcome to our platform, ${name}!</h1><p>Thank you for joining us. We are excited to have you on board.</p></body></html>', 
    'Welcome to Our Platform!', 
    'EMAIL', 
    'en', 
    TRUE, 
    NOW(), 
    NOW(), 
    TRUE
);

INSERT INTO notification_templates (
    id, code, name, description, content, subject, channel, 
    locale, html_enabled, created_at, updated_at, active
) VALUES (
    gen_random_uuid(), 
    'password_reset', 
    'Password Reset', 
    'Email for password reset', 
    '<html><body><h1>Password Reset</h1><p>Use the following code to reset your password: ${resetCode}</p><p>This code is valid for 30 minutes.</p></body></html>', 
    'Reset Your Password', 
    'EMAIL', 
    'en', 
    TRUE, 
    NOW(), 
    NOW(), 
    TRUE
);

INSERT INTO notification_templates (
    id, code, name, description, content, subject, channel, 
    locale, html_enabled, created_at, updated_at, active
) VALUES (
    gen_random_uuid(), 
    'email_verification', 
    'Email Verification', 
    'Email for verification of email address', 
    '<html><body><h1>Verify your email address</h1><p>Please click the link below to verify your email address:</p><p><a href="${verificationUrl}">Verify Email</a></p></body></html>', 
    'Verify Your Email Address', 
    'EMAIL', 
    'en', 
    TRUE, 
    NOW(), 
    NOW(), 
    TRUE
);

INSERT INTO notification_templates (
    id, code, name, description, content, subject, channel, 
    locale, html_enabled, created_at, updated_at, active
) VALUES (
    gen_random_uuid(), 
    'web_notification', 
    'Web Notification', 
    'General web notification', 
    '{"title": "New Message", "body": "You have a new message from ${sender}", "icon": "message.png"}', 
    'New Message', 
    'WEB', 
    'en', 
    TRUE, 
    NOW(), 
    NOW(), 
    TRUE
);

INSERT INTO notification_templates (
    id, code, name, description, content, subject, channel, 
    locale, html_enabled, created_at, updated_at, active
) VALUES (
    gen_random_uuid(), 
    'account_locked', 
    'Account Locked', 
    'Email sent when account is locked', 
    '<html><body><h1>Your account has been locked</h1><p>Please contact support to unlock your account.</p></body></html>', 
    'Account Locked', 
    'EMAIL', 
    'en', 
    TRUE, 
    NOW(), 
    NOW(), 
    TRUE
);

INSERT INTO notification_templates (
    id, code, name, description, content, subject, channel, 
    locale, html_enabled, created_at, updated_at, active
) VALUES (
    gen_random_uuid(), 
    'payment_confirmation', 
    'Payment Confirmation', 
    'Email sent after a successful payment', 
    '<html><body><h1>Payment Confirmation</h1><p>Thank you for your payment of ${amount}. Your payment has been successfully processed.</p></body></html>', 
    'Payment Confirmation', 
    'EMAIL', 
    'en', 
    TRUE, 
    NOW(), 
    NOW(), 
    TRUE
);
