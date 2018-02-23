
/*
  used so that events are only processed once
  this is a duplicate of what is in the person-impl ddl,
  all services are using the same table (for simplicity's sake)
 */
CREATE TABLE IF NOT EXISTS read_side_offsets(
  read_side_id VARCHAR(255),
  tag VARCHAR(255),
  sequence_offset int8,
  time_uuid_offset char(36),
  PRIMARY KEY (read_side_id, tag)
);

/*
 stores email data on all emails sent
 */
CREATE TABLE IF NOT EXISTS email_history (
  person_id TEXT NOT NULL,
  email_address TEXT NOT NULL,
  email_template TEXT NOT NULL,
  sent_at TIMESTAMP
);
