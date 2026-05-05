export interface Ticket {
  id: string;
  sourceCommentId: string;
  title: string;

  //defined as enums in backend, cant see a reason to implement as enums here
  priority: string;
  category: string;

  summary: string;
}
 
export interface Comment {
  id: number;
  body: string;
}