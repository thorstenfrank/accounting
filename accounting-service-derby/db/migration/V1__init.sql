CREATE TABLE BankAccount (
  id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  accountNumber varchar(100),
  bankName varchar(100),
  bankCode varchar(100),
  iban varchar(100),
  bic varchar(100),
  PRIMARY KEY(id)
);
