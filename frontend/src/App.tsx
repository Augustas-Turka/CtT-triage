import { useState } from 'react';
import { Nav }          from './navbar';
import { SubmitPage }   from './pages/addComment';
import { TicketsPage }  from './pages/tickets';
import { CommentsPage } from './pages/comments';
 
export default function App() {
  const [view, setView] = useState('submit');
 
  return (
    <>
      <Nav onNavigate={setView} />
      {view === 'submit'   && <SubmitPage />}
      {view === 'tickets'  && <TicketsPage />}
      {view === 'comments' && <CommentsPage />}
    </>
  );
}