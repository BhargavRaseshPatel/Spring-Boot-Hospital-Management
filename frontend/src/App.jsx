import { useState } from 'react';
import Patients from './tabs/Patients.jsx';
import Appointments from './tabs/Appointments.jsx';
import Doctors from './tabs/Doctors.jsx';
import Analytics from './tabs/Analytics.jsx';

// Four screens do not need a router — a tab name in state is the whole navigation model.
const TABS = {
  Patients: Patients,
  Appointments: Appointments,
  Doctors: Doctors,
  Analytics: Analytics,
};

export default function App() {
  const [tab, setTab] = useState('Patients');
  const Active = TABS[tab];

  return (
    <>
      <header>
        <h1>Hospital Management</h1>
        <p className="sub">
          Spring Boot 4 &middot; Spring Data JPA &middot; PostgreSQL &mdash;{' '}
          <a href="/swagger-ui.html" target="_blank" rel="noreferrer">
            API docs
          </a>
        </p>
      </header>

      <nav>
        {Object.keys(TABS).map((name) => (
          <button
            key={name}
            className={name === tab ? 'tab active' : 'tab'}
            onClick={() => setTab(name)}
          >
            {name}
          </button>
        ))}
      </nav>

      <main>
        <Active />
      </main>
    </>
  );
}
