CREATE TABLE Client (
  name VARCHAR(50) NOT NULL,
  clientNumber VARCHAR(50),
  primaryAddress INTEGER REFERENCES Address(id),
  UNIQUE(clientNumber),
  PRIMARY KEY(name)
);