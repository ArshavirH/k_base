create table vector_store
(
    id        uuid default gen_random_uuid() not null primary key,
    content   text,
    metadata  json,
    embedding vector(1536)
);

alter table vector_store
    owner to postgres;

create index spring_ai_vector_index
    on vector_store using hnsw (embedding public.vector_cosine_ops);
