INSERT INTO categories (name)
VALUES ('Fiction'), ('Science'), ('Technology');

INSERT INTO users (email, password, role)
VALUES
  ('librarian@library.com', 'password123', 'LIBRARIAN'),
  ('user1@library.com', 'password123', 'USER');

INSERT INTO books (title, author, genre, language, isbn, category_id)
VALUES
  ('Clean ', 'Robert C. Martin', 'Programming', 'English', '9780132350835', 3),
  ('1986', 'George ', 'Dystopian', 'English', '9780451524987', 1);

INSERT INTO reservations (user_id, book_id, reservation_date, due_date)
VALUES
  (2, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY));
