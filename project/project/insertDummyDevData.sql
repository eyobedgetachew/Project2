-- Dummy data insert for development (PostgreSQL)

DO $$
DECLARE
  userId1 INTEGER := 23;
  userId2 INTEGER := 24;
  place1 INTEGER;
  place2 INTEGER;
  
BEGIN
  -- Clean up tables
  DELETE FROM reservation;
  DELETE FROM menu;
  DELETE FROM place;
  

  -- Insert dummy places
  INSERT INTO place (name, short_description, long_description, price)
  VALUES 
    ('Product #1', 'Product one short description.', 'This is a very long description of product #1.', 5.50),
    ('Product #2', 'Product two short description.', 'This is a very long description of product #2.', 10.56);

  -- Retrieve place IDs
  SELECT id INTO place1 FROM place WHERE name = 'Product #1';
  SELECT id INTO place2 FROM place WHERE name = 'Product #2';

  -- Assign menus to places
  INSERT INTO menu (place_id, user_id)
  VALUES (place1, userId1), (place2, userId1);


  
  INSERT INTO reservation (user_id, place_id, address_id)
  VALUES 
    (userId1, place1, address1),
    (userId1, place2, address1);
END;
 $$;
