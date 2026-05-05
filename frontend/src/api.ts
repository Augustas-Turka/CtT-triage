import type { Comment, Ticket } from './types';
 
const BASE = import.meta.env.VITE_API_URL as string;
 
export const getTickets  = () =>
  fetch(`${BASE}/api/tickets`).then(r => r.json()) as Promise<Ticket[]>;
 
export const getComments = () =>
  fetch(`${BASE}/api/comments`).then(r => r.json()) as Promise<Comment[]>;
 
export const postComment = (text: string) =>
  fetch(`${BASE}/api/comments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text }),
  }).then(r => r.json());