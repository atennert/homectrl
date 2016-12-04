
CREATE TABLE IF NOT EXISTS hosts (
    host_id INTEGER PRIMARY KEY AUTOINCREMENT,
    host_name TEXT NOT NULL,
    device_ref TEXT NOT NULL,
    UNIQUE (host_name, device_ref) ON CONFLICT IGNORE
);

CREATE TABLE IF NOT EXISTS events (
    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
    host INTEGER NOT NULL REFERENCES hosts (host_id),
    time DATETIME DEFAULT CURRENT_TIMESTAMP,
    value TEXT NOT NULL
);

/* store only the events of the last 5 weeks */
CREATE TRIGGER IF NOT EXISTS cleanup
    AFTER INSERT ON events
    BEGIN
        DELETE FROM events WHERE time < datetime('now', '-35 days');
    END;
