import { useState } from 'react';
import { postComment } from '../api';
 
export function SubmitPage() {
  const [text, setText]       = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
 
  const handleSubmit = async () => {
    if (!text.trim()) return;
    setLoading(true);
    setMessage('');
    try {
      await postComment(text);
      setMessage('Comment submitted successfully.');
      setText('');
    } catch {
      setMessage('Error submitting comment.');
    } finally {
      setLoading(false);
    }
  };
 
  return (
    <div style={{ padding: 24 }}>
      <h2>Submit Comment</h2>
      <textarea
        rows={5}
        style={{ display: 'block', width: 400, marginTop: 12, fontSize: 14 }}
        value={text}
        onChange={e => { setText(e.target.value); setMessage(''); }}
        placeholder="Enter your comment…"
      />
      <button onClick={handleSubmit} disabled={loading} style={{ marginTop: 8, padding: '6px 16px' }}>
        {loading ? 'Reviewing comment...' : 'Submit'}
      </button>
      {message && <p style={{ marginTop: 8 }}>{message}</p>}
    </div>
  );
}