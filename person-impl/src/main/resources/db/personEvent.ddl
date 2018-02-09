
/*
  used so that events are only processed once
 */
CREATE TABLE IF NOT EXISTS read_side_offsets(
  read_side_id VARCHAR(255),
  tag VARCHAR(255),
  sequence_offset int8,
  time_uuid_offset char(36),
  PRIMARY KEY (read_side_id, tag)
);

/*
 stores profile data on all persons
 */
CREATE TABLE IF NOT EXISTS person_profile (
  id TEXT PRIMARY KEY NOT NULL,
  name TEXT NOT NULL,
  email TEXT NOT NULL,
  textNumber TEXT
);
