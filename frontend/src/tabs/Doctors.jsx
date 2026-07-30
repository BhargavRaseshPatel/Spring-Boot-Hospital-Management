import { useEffect, useState } from 'react';
import { api } from '../api.js';
import { Notice, Empty } from '../ui.jsx';

export default function Doctors() {
  const [doctors, setDoctors] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => { api.listDoctors().then(setDoctors).catch(setError); }, []);

  return (
    <section>
      <h2>Doctors</h2>
      <Notice error={error} />
      {doctors.length === 0 && !error ? (
        <Empty>No doctors found.</Empty>
      ) : (
        <table>
          <thead>
            <tr><th>ID</th><th>Name</th><th>Specialization</th><th>Email</th></tr>
          </thead>
          <tbody>
            {doctors.map((d) => (
              <tr key={d.id}>
                <td>{d.id}</td>
                <td>{d.name}</td>
                <td><span className="pill">{d.specialization}</span></td>
                <td className="muted">{d.email}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}
