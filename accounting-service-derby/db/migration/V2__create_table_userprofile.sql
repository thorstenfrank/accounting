CREATE TABLE UserProfile (
  name VARCHAR(50) NOT NULL,
  description VARCHAR(1000),
  primaryAddress INTEGER REFERENCES Address(id),
  taxId VARCHAR(100),
  bankAccount INTEGER REFERENCES BankAccount(id),
  PRIMARY KEY(name)
);