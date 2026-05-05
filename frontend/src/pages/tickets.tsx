import { useEffect, useState } from 'react';
import { getTickets } from '../api';
import type { Ticket } from '../types';
 
export function TicketsPage() {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [error, setError]     = useState('');
 
  useEffect(() => {
    getTickets()
      .then(setTickets)
      .catch(() => setError('Failed to load tickets.'));
  }, []);
 
  if (error) return <p style={{ color: 'red' }}>{error}</p>;
 
  return (
    <div style={{ padding: 24 }}>
      <h2>Tickets</h2>
      {tickets.length === 0 ? <p>No tickets yet.</p> : (
        <table style={{ borderCollapse: 'collapse', fontSize: 14, marginTop: 12 }}>
          <thead>
            <tr>
              {['ID', 'Source Comment ID', 'Title', 'Priority', 'Category', 'Summary'].map(h => (
                <th key={h} style={th}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {tickets.map(t => (
              <tr key={t.id}>
                <td style={td}>{t.id}</td>
                <td style={td}>{t.sourceCommentId}</td>
                <td style={td}>{t.title}</td>
                <td style={td}>{t.priority}</td>
                <td style={td}>{t.category}</td>
                <td style={td}>{t.summary}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
 
const th: React.CSSProperties = { border: '1px solid #ccc', padding: '6px 12px', background: '#f5f5f5', textAlign: 'left' };
const td: React.CSSProperties = { border: '1px solid #ccc', padding: '6px 12px' };