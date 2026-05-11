import React, { useState, useEffect, useCallback } from 'react';

function ParticipantList({ eventId, onUpdate }) {
  const [participants, setParticipants] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchEventParticipants = useCallback(async () => {
    if (!eventId) return;

    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/event-participants/event/${eventId}`);
      const data = await response.json();
      setParticipants(data);
    } catch (error) {
      console.error('Hata:', error);
    } finally {
      setLoading(false);
    }
  }, [eventId]);

  useEffect(() => {
    if (eventId) {
      fetchEventParticipants();
    }
  }, [eventId, fetchEventParticipants]);

  const handleRemoveParticipant = async (participantId) => {
    if (!window.confirm('Katılımcıyı çıkarmak istediğinize emin misiniz?')) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/event-participants/unregister?eventId=${eventId}&participantId=${participantId}`,
        { method: 'DELETE' }
      );

      if (response.ok) {
        setParticipants(participants.filter((p) => p.participantId !== participantId));
        alert('Katılımcı çıkarıldı!');
        if (onUpdate) onUpdate();
      } else {
        alert('Katılımcı çıkarılırken hata oluştu!');
      }
    } catch (error) {
      console.error('Hata:', error);
      alert('Katılımcı çıkarılırken hata oluştu!');
    }
  };

  if (loading) {
    return (
      <div>
        <h3>Katılımcılar</h3>
        <p>Yükleniyor...</p>
      </div>
    );
  }

  return (
    <div
      style={{
        padding: '15px',
        border: '1px solid #ddd',
        borderRadius: '5px',
        marginTop: '20px',
        backgroundColor: '#f8f9fa',
      }}
    >
      <h3>👥 Katılımcılar ({participants.length})</h3>

      {participants.length === 0 ? (
        <p style={{ color: '#666' }}>Henüz katılımcı yok.</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {participants.map((p) => (
            <li
              key={p.id}
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '10px',
                borderBottom: '1px solid #ddd',
                marginBottom: '5px',
              }}
            >
              <span>
                <strong>{p.participantName}</strong>
                <br />
                <span style={{ fontSize: '12px', color: '#666' }}>
                  📧 {p.participantEmail}
                </span>
                {p.status && (
                  <div style={{ marginTop: '4px', fontSize: '12px', color: '#444' }}>
                    Durum: {p.status}
                  </div>
                )}
              </span>
              <button
                onClick={() => handleRemoveParticipant(p.participantId)}
                style={{
                  padding: '5px 10px',
                  backgroundColor: '#ff9800',
                  color: 'white',
                  border: 'none',
                  borderRadius: '5px',
                  cursor: 'pointer',
                  fontSize: '12px',
                }}
              >
                ✕ Çıkar
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default ParticipantList;
