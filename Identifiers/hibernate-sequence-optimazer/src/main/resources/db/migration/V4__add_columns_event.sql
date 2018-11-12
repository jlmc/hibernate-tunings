ALTER TABLE event ADD name text NOT NULL;
ALTER TABLE event ADD version INTEGER NOT NULL;
ALTER TABLE event ADD price_base_values INTEGER[] NOT NULL;
ALTER TABLE event ADD ports_names TEXT[] NOT NULL;