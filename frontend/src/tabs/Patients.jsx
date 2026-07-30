import { useEffect, useState } from 'react';
import { api, BLOOD_GROUPS } from '../api.js';
import { Notice, Empty, InsuranceBadge, IconShieldCheck, IconTrash } from '../ui.jsx';

const BLANK = { name: '', birthDate: '', email: '', gender: 'MALE', bloodGroup: 'O_POSITIVE' };

export default function Patients() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [form, setForm] = useState(BLANK);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);
  const [insuranceFor, setInsuranceFor] = useState(null);

  const load = () => {
    setError(null); // a failed load must not leave its error behind once one succeeds
    return api.listPatients(page).then(setData).catch(setError);
  };
  useEffect(() => { load(); }, [page]);

  const reset = () => { setError(null); setMessage(null); };

  async function create(event) {
    event.preventDefault();
    reset();
    try {
      const created = await api.createPatient(form);
      setMessage(`Created patient #${created.id} — ${created.name}`);
      setForm(BLANK);
      load();
    } catch (err) {
      setError(err);
    }
  }

  async function remove(id) {
    reset();
    try {
      await api.deletePatient(id);
      setMessage(`Deleted patient #${id} (appointments and insurance cascaded)`);
      load();
    } catch (err) {
      setError(err);
    }
  }

  const patients = data?.content || [];
  const meta = data?.page;

  return (
    <section>
      <h2>Add a patient</h2>
      <p className="hint">
        Submit an invalid form to see the API's RFC 7807 validation response rendered per field.
      </p>

      <form onSubmit={create} className="grid">
        <label>
          Name
          <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        </label>
        <label>
          Birth date
          <input type="date" value={form.birthDate}
                 onChange={(e) => setForm({ ...form, birthDate: e.target.value })} />
        </label>
        <label>
          Email
          <input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        </label>
        <label>
          Gender
          <select value={form.gender} onChange={(e) => setForm({ ...form, gender: e.target.value })}>
            <option>MALE</option>
            <option>FEMALE</option>
          </select>
        </label>
        <label>
          Blood group
          <select value={form.bloodGroup}
                  onChange={(e) => setForm({ ...form, bloodGroup: e.target.value })}>
            {BLOOD_GROUPS.map((bg) => <option key={bg}>{bg}</option>)}
          </select>
        </label>
        <button type="submit" className="primary">Create</button>
      </form>

      <Notice error={error} message={message} />

      <h2>Patients</h2>
      {patients.length > 0 && (
        <p className="hint">
          <IconShieldCheck /> {patients.filter((p) => p.insuranceProvider).length} of{' '}
          {patients.length} on this page have insurance on file.
        </p>
      )}
      {patients.length === 0 ? (
        <Empty>No patients on this page.</Empty>
      ) : (
        <div className="table-scroll">
        <table>
          <thead>
            <tr>
              <th>ID</th><th>Name</th><th>Birth date</th><th>Email</th>
              <th>Blood group</th><th>Insurance</th><th></th>
            </tr>
          </thead>
          <tbody>
            {patients.map((p) => (
              <tr key={p.id} className={p.insuranceProvider ? 'is-covered' : undefined}>
                <td>{p.id}</td>
                <td>{p.name}</td>
                <td>{p.birthDate}</td>
                <td className="muted">{p.email}</td>
                <td><span className="pill">{p.bloodGroup}</span></td>
                <td>
                  <InsuranceBadge provider={p.insuranceProvider} validUntil={p.insuranceValidUntil} />
                </td>
                <td className="actions">
                  <button title="Manage insurance"
                          onClick={() => setInsuranceFor(insuranceFor === p.id ? null : p.id)}>
                    <IconShieldCheck /> Insurance
                  </button>
                  <button className="danger" title="Delete patient" onClick={() => remove(p.id)}>
                    <IconTrash /> Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        </div>
      )}

      {meta && (
        <div className="pager">
          <button disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
          <span>
            Page {meta.number + 1} of {meta.totalPages} &middot; {meta.totalElements} patients
          </span>
          <button disabled={page + 1 >= meta.totalPages} onClick={() => setPage(page + 1)}>
            Next
          </button>
        </div>
      )}

      {insuranceFor && (
        <InsurancePanel
          patientId={insuranceFor}
          onDone={(msg) => { setMessage(msg); setError(null); }}
          onError={(err) => { setError(err); setMessage(null); }}
        />
      )}
    </section>
  );
}

function InsurancePanel({ patientId, onDone, onError }) {
  const [form, setForm] = useState({ policyNumber: '', provider: '', validUntil: '' });

  async function assign(event) {
    event.preventDefault();
    try {
      const saved = await api.assignInsurance(patientId, form);
      onDone(`Insurance ${saved.policyNumber} assigned to patient #${patientId}`);
    } catch (err) {
      onError(err);
    }
  }

  async function remove() {
    try {
      await api.removeInsurance(patientId);
      onDone(`Insurance removed from patient #${patientId} (row deleted by orphanRemoval)`);
    } catch (err) {
      onError(err);
    }
  }

  return (
    <div className="panel">
      <h3>Insurance for patient #{patientId}</h3>
      <p className="hint">One-to-one. Replacing it deletes the previous row rather than orphaning it.</p>
      <form onSubmit={assign} className="grid">
        <label>
          Policy number
          <input value={form.policyNumber}
                 onChange={(e) => setForm({ ...form, policyNumber: e.target.value })} />
        </label>
        <label>
          Provider
          <input value={form.provider}
                 onChange={(e) => setForm({ ...form, provider: e.target.value })} />
        </label>
        <label>
          Valid until
          <input type="date" value={form.validUntil}
                 onChange={(e) => setForm({ ...form, validUntil: e.target.value })} />
        </label>
        <button type="submit" className="primary">Assign</button>
        <button type="button" className="danger" onClick={remove}>Remove</button>
      </form>
    </div>
  );
}
