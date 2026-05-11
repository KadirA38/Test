

import './App.css';

import EventList from './EventList';
import EventForm from './EventForm';
import ParticipantList from './ParticipantList';
import AddParticipantForm from './AddParticipantForm';
import AdminApprovalPanel from './AdminApprovalPanel';
import React, { useState } from 'react';

function App() {
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [showAdminPanel, setShowAdminPanel] = useState(false);
  const [alert, setAlert] = useState('');
  const [refreshEvents, setRefreshEvents] = useState(0);

  const handleSelectEvent = (event) => {
    setSelectedEvent(event);
  };

  const handleBack = () => setSelectedEvent(null);

  const refreshEventList = () => setRefreshEvents((prev) => prev + 1);

  const handleEventCreated = () => {
    setAlert('Etkinlik oluşturuldu. Admin onayını bekliyor.');
    refreshEventList();
    setTimeout(() => setAlert(''), 3000);
  };

  const handleEventDeleted = (eventId) => {
    if (selectedEvent?.id === eventId) {
      setSelectedEvent(null);
    }
    setAlert('Etkinlik silindi!');
    refreshEventList();
    setTimeout(() => setAlert(''), 2000);
  };

  const handleParticipantRegistered = () => {
    setAlert('Katılımcı eklendi!');
    setTimeout(() => setAlert(''), 2000);
  };

  const deleteSelectedEvent = async () => {
    if (!selectedEvent) return;
    if (!window.confirm('Etkinliği silmek istediğinize emin misiniz?')) return;

    try {
      const response = await fetch(`http://localhost:8080/api/events/${selectedEvent.id}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        handleEventDeleted(selectedEvent.id);
      } else {
        setAlert('Etkinlik silinemedi.');
        setTimeout(() => setAlert(''), 3000);
      }
    } catch (error) {
      console.error('Hata:', error);
      setAlert('Sunucuya bağlanırken hata oluştu.');
      setTimeout(() => setAlert(''), 3000);
    }
  };

  return (
    <div className="App">
      <h1>🎉 Online Etkinlik Yönetim Sistemi</h1>

      {alert && <div className="alert">{alert}</div>}

      <div
        style={{
          marginBottom: '20px',
          display: 'flex',
          gap: '10px',
          justifyContent: 'center',
        }}
      >
        <button
          onClick={() => setShowAdminPanel(!showAdminPanel)}
          style={{
            padding: '10px 20px',
            backgroundColor: showAdminPanel ? '#ff6b6b' : '#0066cc',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer',
            fontSize: '16px',
          }}
        >
          {showAdminPanel ? '❌ Admin Panelini Kapat' : '⚙️ Admin Paneli Aç'}
        </button>
      </div>

      {!selectedEvent ? (
        <div className="card">
          {showAdminPanel && <AdminApprovalPanel onApprovalChanged={refreshEventList} />}

          <EventForm onCreated={handleEventCreated} />
          <EventList onSelect={handleSelectEvent} onDelete={handleEventDeleted} refreshTrigger={refreshEvents} />
        </div>
      ) : (
        <div className="card">
          <button
            onClick={handleBack}
            style={{ marginBottom: 16, padding: '8px 15px', cursor: 'pointer' }}
          >
            ← Geri Dön
          </button>

          <h2>
            {selectedEvent.title}
            <span style={{ marginLeft: '10px' }}>🎉</span>
          </h2>

          <div
            style={{
              backgroundColor: '#f0f8ff',
              padding: '15px',
              borderRadius: '5px',
              marginBottom: '15px',
            }}
          >
            <p>
              <strong>📍 Konum:</strong> {selectedEvent.location}
            </p>
            <p>
              <strong>📅 Başlangıç:</strong>{' '}
              {new Date(selectedEvent.startDate).toLocaleString('tr-TR')}
            </p>
            {selectedEvent.endDate && (
              <p>
                <strong>⏱️ Bitiş:</strong>{' '}
                {new Date(selectedEvent.endDate).toLocaleString('tr-TR')}
              </p>
            )}
            <p>
              <strong>👥 Kapasite:</strong> {selectedEvent.capacity}
            </p>
            {selectedEvent.city && (
              <p>
                <strong>🏙️ Şehir:</strong> {selectedEvent.city}
              </p>
            )}
            {selectedEvent.description && (
              <p>
                <strong>📝 Açıklama:</strong> {selectedEvent.description}
              </p>
            )}
            {selectedEvent.isFree ? (
              <p style={{ color: 'green', fontWeight: 'bold' }}>💰 Ücretsiz</p>
            ) : (
              <p>
                <strong>💰 Fiyat:</strong> {selectedEvent.price} TL
              </p>
            )}
            <p>
              <strong>📊 Onay Durumu:</strong> {selectedEvent.approvalStatus || 'ONAYLANDI'}
            </p>
          </div>

          <AddParticipantForm eventId={selectedEvent.id} onRegistered={handleParticipantRegistered} />
          <ParticipantList eventId={selectedEvent.id} onUpdate={handleParticipantRegistered} />

          <button
            className="delete-btn"
            onClick={deleteSelectedEvent}
            style={{
              marginTop: 16,
              background: '#ef4444',
              padding: '10px 15px',
              cursor: 'pointer',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
            }}
          >
            🗑️ Etkinliği Sil
          </button>
        </div>
      )}
    </div>
  );
}

export default App;
