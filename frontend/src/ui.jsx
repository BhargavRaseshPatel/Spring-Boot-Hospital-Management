/** Shared bits used by every tab. Kept in one file rather than a components/ tree. */

export function Notice({ error, message }) {
  if (error) {
    return (
      <div className="notice error">
        <strong>{error.status}</strong> {error.message}
        {Object.entries(error.fieldErrors || {}).length > 0 && (
          <ul>
            {Object.entries(error.fieldErrors).map(([field, msg]) => (
              <li key={field}>
                <code>{field}</code> — {msg}
              </li>
            ))}
          </ul>
        )}
      </div>
    );
  }
  if (message) return <div className="notice ok">{message}</div>;
  return null;
}

export function Empty({ children }) {
  return <p className="empty">{children}</p>;
}

export function formatDateTime(value) {
  return value ? value.replace('T', ' ').slice(0, 16) : '—';
}
