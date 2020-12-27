# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table category (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  parent_id                     bigint,
  version                       bigint not null,
  creation_date                 timestamp not null,
  update_date                   timestamp not null,
  constraint pk_category primary key (id)
);

create table food (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  version                       bigint not null,
  creation_date                 timestamp not null,
  update_date                   timestamp not null,
  constraint pk_food primary key (id)
);

create table ingredient (
  id                            bigint auto_increment not null,
  quantity                      integer not null,
  food_id                       bigint,
  measure_id                    bigint,
  recipe_id                     bigint,
  version                       bigint not null,
  creation_date                 timestamp not null,
  update_date                   timestamp not null,
  constraint pk_ingredient primary key (id)
);

create table measure (
  id                            bigint auto_increment not null,
  description                   varchar(255),
  version                       bigint not null,
  creation_date                 timestamp not null,
  update_date                   timestamp not null,
  constraint pk_measure primary key (id)
);

create table recipe (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  description                   TEXT,
  version                       bigint not null,
  creation_date                 timestamp not null,
  update_date                   timestamp not null,
  constraint pk_recipe primary key (id)
);

create table recipe_category (
  recipe_id                     bigint not null,
  category_id                   bigint not null,
  constraint pk_recipe_category primary key (recipe_id,category_id)
);

create table recipe_photo (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  url                           varchar(255),
  width                         integer not null,
  height                        integer not null,
  recipe                        bigint,
  version                       bigint not null,
  creation_date                 timestamp not null,
  update_date                   timestamp not null,
  constraint uq_recipe_photo_recipe unique (recipe),
  constraint pk_recipe_photo primary key (id)
);

create index ix_category_parent_id on category (parent_id);
alter table category add constraint fk_category_parent_id foreign key (parent_id) references category (id) on delete restrict on update restrict;

create index ix_ingredient_food_id on ingredient (food_id);
alter table ingredient add constraint fk_ingredient_food_id foreign key (food_id) references food (id) on delete restrict on update restrict;

create index ix_ingredient_measure_id on ingredient (measure_id);
alter table ingredient add constraint fk_ingredient_measure_id foreign key (measure_id) references measure (id) on delete restrict on update restrict;

create index ix_ingredient_recipe_id on ingredient (recipe_id);
alter table ingredient add constraint fk_ingredient_recipe_id foreign key (recipe_id) references recipe (id) on delete restrict on update restrict;

create index ix_recipe_category_recipe on recipe_category (recipe_id);
alter table recipe_category add constraint fk_recipe_category_recipe foreign key (recipe_id) references recipe (id) on delete restrict on update restrict;

create index ix_recipe_category_category on recipe_category (category_id);
alter table recipe_category add constraint fk_recipe_category_category foreign key (category_id) references category (id) on delete restrict on update restrict;

alter table recipe_photo add constraint fk_recipe_photo_recipe foreign key (recipe) references recipe (id) on delete restrict on update restrict;


# --- !Downs

alter table category drop constraint if exists fk_category_parent_id;
drop index if exists ix_category_parent_id;

alter table ingredient drop constraint if exists fk_ingredient_food_id;
drop index if exists ix_ingredient_food_id;

alter table ingredient drop constraint if exists fk_ingredient_measure_id;
drop index if exists ix_ingredient_measure_id;

alter table ingredient drop constraint if exists fk_ingredient_recipe_id;
drop index if exists ix_ingredient_recipe_id;

alter table recipe_category drop constraint if exists fk_recipe_category_recipe;
drop index if exists ix_recipe_category_recipe;

alter table recipe_category drop constraint if exists fk_recipe_category_category;
drop index if exists ix_recipe_category_category;

alter table recipe_photo drop constraint if exists fk_recipe_photo_recipe;

drop table if exists category;

drop table if exists food;

drop table if exists ingredient;

drop table if exists measure;

drop table if exists recipe;

drop table if exists recipe_category;

drop table if exists recipe_photo;

