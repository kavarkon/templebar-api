alter table file
    add column created_at timestamptz
        not null
        default current_timestamp;

create index idx_file_created_at
    on file (created_at);
