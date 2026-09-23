alter table event
    add column file_id bigint;

alter table event
    add constraint fk_event_file
        foreign key (file_id)
        references file (id);

alter table event
    add constraint uk_event_file
        unique (file_id);

alter table event
    drop column image;
