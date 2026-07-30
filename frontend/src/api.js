const BASE = '/api';

/** Carries the RFC 7807 body so forms can show per-field messages. */
export class ApiError extends Error {
  constructor(problem, status) {
    super(problem?.detail || `Request failed with status ${status}`);
    this.status = status;
    this.fieldErrors = problem?.errors || {};
  }
}

async function request(path, options = {}) {
  const res = await fetch(BASE + path, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });

  if (res.status === 204) return null;

  const body = await res.json().catch(() => null);
  if (!res.ok) throw new ApiError(body, res.status);
  return body;
}

export const api = {
  listPatients: (page = 0, size = 5) =>
    request(`/patients?page=${page}&size=${size}&sort=id,asc`),
  getPatient: (id) => request(`/patients/${id}`),
  createPatient: (patient) =>
    request('/patients', { method: 'POST', body: JSON.stringify(patient) }),
  deletePatient: (id) => request(`/patients/${id}`, { method: 'DELETE' }),
  patientsWithAppointments: () => request('/patients/with-appointments'),
  bloodGroupDistribution: () => request('/patients/blood-group-distribution'),

  listDoctors: () => request('/doctors'),

  appointmentsForPatient: (patientId) => request(`/appointments?patientId=${patientId}`),
  createAppointment: (appointment) =>
    request('/appointments', { method: 'POST', body: JSON.stringify(appointment) }),
  reassignAppointment: (id, doctorId) =>
    request(`/appointments/${id}/doctor/${doctorId}`, { method: 'PATCH' }),

  assignInsurance: (patientId, insurance) =>
    request(`/patients/${patientId}/insurance`, {
      method: 'PUT',
      body: JSON.stringify(insurance),
    }),
  removeInsurance: (patientId) =>
    request(`/patients/${patientId}/insurance`, { method: 'DELETE' }),
};

export const BLOOD_GROUPS = [
  'A_POSITIVE', 'A_NEGATIVE',
  'B_POSITIVE', 'B_NEGATIVE',
  'AB_POSITIVE', 'AB_NEGATIVE',
  'O_POSITIVE', 'O_NEGATIVE',
];
