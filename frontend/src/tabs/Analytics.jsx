import { useEffect, useState } from 'react';
import { api } from '../api.js';
import { Notice, Empty, formatDateTime } from '../ui.jsx';

export default function Analytics() {
  const [distribution, setDistribution] = useState([]);
  const [graph, setGraph] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.bloodGroupDistribution().then(setDistribution).catch(setError);
    api.patientsWithAppointments().then(setGraph).catch(setError);
  }, []);

  // CSS bars beat pulling in a charting library for eight numbers.
  const max = Math.max(1, ...distribution.map((d) => d.count));

  return (
    <section>
      <h2>Blood group distribution</h2>
      <p className="hint">
        JPQL <code>GROUP BY</code> with a constructor projection — the query returns typed DTOs,
        not <code>Object[]</code>.
      </p>
      <Notice error={error} />

      {distribution.length === 0 ? (
        <Empty>No data.</Empty>
      ) : (
        <div className="bars">
          {[...distribution]
            .sort((a, b) => a.bloodGroupType.localeCompare(b.bloodGroupType))
            .map((d) => (
              <div className="bar-row" key={d.bloodGroupType}>
                <span className="bar-label">{d.bloodGroupType}</span>
                <span className="bar" style={{ width: `${(d.count / max) * 100}%` }} />
                <span className="bar-value">{d.count}</span>
              </div>
            ))}
        </div>
      )}

      <h2>Patients with appointments</h2>
      <p className="hint">
        Served by one <code>LEFT JOIN FETCH</code> covering patients, appointments and doctors —
        1 SQL statement instead of the 10 that lazy loading produced.
      </p>

      {graph.length === 0 ? (
        <Empty>No data.</Empty>
      ) : (
        <div className="cards">
          {graph.map((p) => (
            <article className="card" key={p.id}>
              <h3>#{p.id} {p.name}</h3>
              <p className="muted">{p.email}</p>
              {p.appointments.length === 0 ? (
                <p className="empty">No appointments</p>
              ) : (
                <ul>
                  {p.appointments.map((a) => (
                    <li key={a.id}>
                      {formatDateTime(a.appointmentTime)} — {a.reason}
                      <br />
                      <span className="muted">{a.doctorName}</span>
                    </li>
                  ))}
                </ul>
              )}
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
