import { useEffect, useState } from 'react';
import { getComments } from '../api';
import type { Comment } from '../types';

export function CommentsPage() {
  const [comments, setComments] = useState<Comment[]>([]);
  const [error, setError]       = useState('');

  useEffect(() => {
    getComments()
      .then(setComments)
      .catch(() => setError('Failed to load comments.'));
  }, []);

  if (error) return <p style={{ color: 'red' }}>{error}</p>;

  return (
    <div style={{ padding: 24 }}>
      <h2>Comments</h2>
      {comments.length === 0 ? <p>No comments yet.</p> : (
        <table style={{ borderCollapse: 'collapse', fontSize: 14, marginTop: 12 }}>
          <thead>
            <tr>
              <th style={th}>ID</th>
              <th style={th}>Body</th>
            </tr>
          </thead>
          <tbody>
            {comments.map(c => (
              <tr key={c.id}>
                <td style={td}>{c.id}</td>
                <td style={td}>{c.body}</td>
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