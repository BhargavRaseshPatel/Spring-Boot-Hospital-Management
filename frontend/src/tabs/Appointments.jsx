import { useEffect, useState } from 'react';
import { api } from '../api.js';
import { Notice, Empty, formatDateTime } from '../ui.jsx';

export default function Appointments() {
  const [doctors, setDoctors] = useState([]);
  const [patientId, setPatientId] = useState(1);
  const [appointments, setAppointments] = useState([]);
  const [form, setForm] = useState({ appointmentTime: '', reason: '', doctorId: '', patientId: 1 });
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => { api.listDoctors().then(setDoctors).catch(setError); }, []);

  const load = (id) => {
    setError(null); // drop a stale "patient not found" when switching to another id
    return api.appointmentsForPatient(id).then(setAppointments).catch((err) => {
      setAppointments([]);
      setError(err);
    });
  };

  useEffect(() => { load(patientId); }, [patientId]);

  async function book(event) {
    event.preventDefault();
    setError(null);
    setMessage(null);
    try {
      const created = await api.createAppointment({
        ...form,
        doctorId: Number(form.doctorId),
        patientId: Number(form.patientId),
      });
      setMessage(`Booked appointment #${created.id} with ${created.doctorName}`);
      setPatientId(Number(form.patientId));
      load(Number(form.patientId));
    } catch (err) {
      setError(err);
    }
  }

  async function reassign(appointmentId, doctorId) {
    setError(null);
    setMessage(null);
    try {
      const updated = await api.reassignAppointment(appointmentId, Number(doctorId));
      setMessage(`Appointment #${updated.id} reassigned to ${updated.doctorName}`);
      load(patientId);
    } catch (err) {
      setError(err);
    }
  }

  return (
    <section>
      <h2>Book an appointment</h2>
      <p className="hint">The API rejects times in the past — try one to see the field error.</p>

      <form onSubmit={book} className="grid">
        <label>
          When
          <input type="datetime-local" value={form.appointmentTime}
                 onChange={(e) => setForm({ ...form, appointmentTime: e.target.value })} />
        </label>
        <label>
          Reason
          <input value={form.reason}
                 onChange={(e) => setForm({ ...form, reason: e.target.value })} />
        </label>
        <label>
          Doctor
          <select value={form.doctorId}
                  onChange={(e) => setForm({ ...form, doctorId: e.target.value })}>
            <option value="">Select…</option>
            {doctors.map((d) => (
              <option key={d.id} value={d.id}>{d.name} — {d.specialization}</option>
            ))}
          </select>
        </label>
        <label>
          Patient ID
          <input type="number" min="1" value={form.patientId}
                 onChange={(e) => setForm({ ...form, patientId: e.target.value })} />
        </label>
        <button type="submit" className="primary">Book</button>
      </form>

      <Notice error={error} message={message} />

      <h2>
        Appointments for patient{' '}
        <input className="inline-number" type="number" min="1" value={patientId}
               onChange={(e) => setPatientId(Number(e.target.value) || 1)} />
      </h2>

      {appointments.length === 0 ? (
        <Empty>No appointments for this patient.</Empty>
      ) : (
        <table>
          <thead>
            <tr><th>ID</th><th>When</th><th>Reason</th><th>Doctor</th><th>Reassign to</th></tr>
          </thead>
          <tbody>
            {appointments.map((a) => (
              <tr key={a.id}>
                <td>{a.id}</td>
                <td>{formatDateTime(a.appointmentTime)}</td>
                <td>{a.reason}</td>
                <td>{a.doctorName}</td>
                <td>
                  <select value="" onChange={(e) => e.target.value && reassign(a.id, e.target.value)}>
                    <option value="">Choose doctor…</option>
                    {doctors.filter((d) => d.id !== a.doctorId).map((d) => (
                      <option key={d.id} value={d.id}>{d.name}</option>
                    ))}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}
