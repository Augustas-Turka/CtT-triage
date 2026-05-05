interface NavProps {
  active: string;
  onNavigate: (v: string) => void;
}

export function Nav({ active, onNavigate }: NavProps) {
  return (
    <nav>
      <button onClick={() => onNavigate('submit')}>Submit</button>
      <button onClick={() => onNavigate('tickets')}>Tickets</button>
      <button onClick={() => onNavigate('comments')}>Comments</button>
    </nav>
  );
}