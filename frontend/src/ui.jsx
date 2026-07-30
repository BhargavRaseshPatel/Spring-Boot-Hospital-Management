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

/* --- Icons: inline SVG rather than an icon dependency. All 16x16, inherit currentColor. --- */

function Svg({ children, label }) {
  return (
    <svg className="icon" viewBox="0 0 16 16" width="16" height="16" fill="none"
         stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"
         role={label ? 'img' : 'presentation'} aria-label={label} aria-hidden={label ? undefined : true}>
      {children}
    </svg>
  );
}

const SHIELD = <path d="M8 1.5 13.5 3.5v4c0 3.2-2.2 6.1-5.5 7-3.3-.9-5.5-3.8-5.5-7v-4L8 1.5Z" />;

export const IconShieldCheck = ({ label }) => (
  <Svg label={label}>{SHIELD}<path d="m5.8 7.9 1.6 1.6 3-3.2" /></Svg>
);

export const IconShieldAlert = ({ label }) => (
  <Svg label={label}>{SHIELD}<path d="M8 5.4v3" /><path d="M8 10.6h.01" /></Svg>
);

export const IconShieldOff = ({ label }) => (
  <Svg label={label}>{SHIELD}<path d="m6 6 4 4M10 6l-4 4" /></Svg>
);

export const IconTrash = ({ label }) => (
  <Svg label={label}><path d="M2.5 4h11M6 4V2.5h4V4M4 4l.6 9.5h6.8L12 4" /></Svg>
);

/**
 * Insurance status for one patient. Expiry is the reason this is not a boolean —
 * a policy that lapsed last month is not the same as an active one.
 */
export function InsuranceBadge({ provider, validUntil }) {
  if (!provider) {
    return (
      <span className="badge none" title="No insurance on file">
        <IconShieldOff label="No insurance" /> None
      </span>
    );
  }

  const expired = validUntil && validUntil < new Date().toISOString().slice(0, 10);
  const Icon = expired ? IconShieldAlert : IconShieldCheck;

  return (
    <span
      className={expired ? 'badge expired' : 'badge covered'}
      title={`${provider} — ${expired ? 'expired' : 'valid until'} ${validUntil || 'unknown'}`}
    >
      <Icon label={expired ? 'Insurance expired' : 'Insured'} />
      {provider}
    </span>
  );
}
