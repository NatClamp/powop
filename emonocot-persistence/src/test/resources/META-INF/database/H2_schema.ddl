drop table if exists Annotation;
create table Annotation (id bigint generated by default as identity, identifier varchar(255) default RANDOM_UUID(), annotatedObjType varchar(255), annotatedObjId bigint, code varchar(255), dateTime timestamp, jobId bigint, recordType varchar(255), text clob, type varchar(255), value varchar(255), authority_id bigint, primary key (id), unique (identifier),);
drop table if exists Comment;
create table Comment (id bigint not null, identifier varchar(255), commentPage_id bigint, commentPage_type varchar(255), aboutData_id bigint, aboutData_type varchar(255), comment longtext, created datetime, status varchar(15), subject varchar(255), user_id bigint, inResponseTo_id bigint, authority_id bigint, primary key(id), unique(identifier));
drop table if exists Comment_alternativeIdentifiers;
create table Comment_alternativeIdentifiers (Comment_id bigint not null, alternativeIdentifiers_KEY varchar(255), alternativeIdentifiers varchar(255));
drop table if exists Concept;
create table Concept (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, creator varchar(255), license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), prefLabel varchar(255), definition clob, altLabel varchar(255), authority_id bigint, prefSymbol_id bigint, source_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists Description;
create table Description (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, creator varchar(255), license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), source varchar(255), description clob, type varchar(255), language varchar(32), contributor varchar(255), audience varchar(255), authority_id bigint, taxon_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists Description_Reference;
create table Description_Reference (Description_id bigint not null, references_id bigint not null, primary key (Description_id, references_id));
drop table if exists Distribution;
create table Distribution (id bigint not null, identifier varchar(255), accessRights varchar(255),  created timestamp, creator varchar(255), license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), source varchar(255), location varchar(255), locality varchar(255), occurrenceRemarks varchar(255), occurrenceStatus varchar(255), establishmentMeans varchar(255), authority_id bigint, taxon_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists Distribution_Reference;
create table Distribution_Reference (Distribution_id bigint not null, references_id bigint not null, primary key (Distribution_id, references_id));
drop table if exists Group_permissions;
create table Group_permissions (Group_id bigint not null, permissions integer);
drop table if exists hibernate_sequences;
create table hibernate_sequences (sequence_name varchar(255), sequence_next_hi_value bigint);
drop table if exists IdentificationKey;
create table IdentificationKey (id bigint not null, accessRights varchar(255), created timestamp, creator varchar(255), identifier varchar(255) not null, license varchar(255), matrix clob, modified timestamp, rights clob, rightsHolder varchar(255), description clob, title varchar(255), authority_id bigint, audience varchar(255), contributor varchar(255), format varchar(255), publisher varchar(255), "references" varchar(255), source varchar(255), uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists Identifier;
create table Identifier (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, creator varchar(255), license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), source varchar(255), format varchar(255), subject varchar(255), title varchar(255), authority_id bigint, taxon_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists Image;
create table Image (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, creator varchar(255), license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), source varchar(255), "references" varchar(255), title varchar(255), description clob, format varchar(255), subject varchar(255), contributor varchar(255), publisher varchar(255), audience varchar(255), latitude double, longitude double, location BLOB, locality varchar(255), authority_id bigint, image_id bigint, taxon_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists Resource;
create table Resource (id bigint not null, identifier varchar(255), duration varchar(255), exitDescription clob, baseUrl varchar(255), exitCode varchar(255), jobId bigint, lastHarvestedJobId bigint, jobInstance varchar(255), resourceType varchar(255), title varchar(255), lastHarvested timestamp, lastAttempt timestamp, nextAvailableDate timestamp, processSkip integer, recordsRead integer, readSkip integer, resource varchar(255), scheduled boolean, schedulingPeriod varchar(255), startTime timestamp, status varchar(255), uri varchar(255), resource_id bigint, writeSkip integer, written integer, organisation_id bigint, primary key (id), unique (identifier));
drop table if exists Resource_parameters;
create table Resource_parameters (Resource_id bigint not null,  parameters_KEY varchar(255) not null, parameters varchar(255), primary key (Resource_id, parameters_KEY));
drop table if exists MeasurementOrFact;
create table MeasurementOrFact (id bigint not null, identifier varchar(255), accessRights varchar(255), bibliographicCitation varchar(255), created timestamp, creator varchar(255), license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), measurementAccuracy varchar(255), measurementDeterminedBy varchar(255),  measurementDeterminedDate timestamp, measurementMethod varchar(255), measurementRemarks varchar(255), measurementType varchar(255), measurementUnit varchar(255), measurementValue varchar(255), source varchar(255), authority_id bigint, taxon_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists Place;
create table Place (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, modified timestamp, title varchar(255), fipsCode varchar(5), shape blob, point blob, license varchar(255), rights clob, rightsHolder varchar(255), source varchar(255), creator varchar(255), authority_id bigint, mapFeatureId bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists PhylogeneticTree;
create table PhylogeneticTree (id bigint not null, accessRights varchar(255), created timestamp, creator varchar(255), identifier varchar(255) not null, license varchar(255), phylogeny clob, numberOfExternalNodes bigint, hasBranchLengths boolean, modified timestamp, rights clob, rightsHolder varchar(255), description clob, source varchar(255), format varchar(255), contributor varchar(255), publisher varchar(255), audience varchar(255), "references" varchar(255), title varchar(255), authority_id bigint, bibliographicReference_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists PhylogeneticTree_Taxon;
create table PhylogeneticTree_Taxon (PhylogeneticTree_id bigint not null, leaves_id bigint not null);
drop table if exists Principal;
create table Principal (DTYPE varchar(31) not null, id bigint not null, identifier varchar(255), apiKey varchar(255), created timestamp, modified timestamp, accountNonExpired boolean, accountNonLocked boolean, credentialsNonExpired boolean, enabled boolean, password varchar(255), nonce varchar(255), name varchar(255), firstName varchar(255), familyName varchar(255), organization varchar(255), accountName varchar(255), img varchar(255), topicInterest varchar(255), homepage varchar(255), notifyByEmail boolean default false, primary key (id), unique (identifier));
drop table if exists Reference;
create table Reference (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, creator varchar(255), license varchar(255), language varchar(32), modified timestamp, rights clob, rightsHolder varchar(255), source varchar(255), bibliographicCitation clob, date varchar(255), subject varchar(255), description clob, taxonRemarks varchar(255), title varchar(255), uri varchar(255), resource_id bigint, type integer, authority_id bigint, primary key (id), unique (identifier));
drop table if exists Organisation;
create table Organisation (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, creator varchar(255), insertCommentsIntoScratchpad boolean, license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), bibliographicCitation varchar(255), creatorEmail varchar(255), description clob, logoUrl varchar(255), publisherEmail varchar(255), publisherName varchar(255), subject varchar(255), title varchar(255), uri varchar(255), resource_id bigint, commentsEmailedTo varchar(255), authority_id bigint, footerLogoPosition integer, primary key (id), unique (identifier));
drop table if exists Taxon;
create table Taxon (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, creator varchar(255), license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), source varchar(255), bibliographicCitation varchar(255), namePublishedInString varchar(255), namePublishedInYear integer, scientificNameAuthorship varchar(128), class varchar(128), family varchar(128), genus varchar(128), subgenus varchar(128), infraSpecificEpithet varchar(128), kingdom varchar(128), scientificName varchar(128), scientificNameID varchar(255), nomenclaturalCode varchar(255), ordr varchar(128), phylum varchar(128), taxonRank varchar(255), taxonRemarks varchar(255), specificEpithet varchar(128), nomenclaturalStatus varchar(255), taxonomicStatus varchar(255), subfamily varchar(255), subtribe varchar(255), tribe varchar(255), verbatimTaxonRank varchar(255), authority_id bigint, acceptedNameUsage_id bigint, parentNameUsage_id bigint, originalNameUsage_id bigint, nameAccordingTo_id bigint, namePublishedIn_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists Taxon_Concept;
create table Taxon_Concept (Taxon_id bigint not null, concepts_id bigint not null);
drop table if exists Taxon_Image;
create table Taxon_Image (Taxon_id bigint not null, images_id bigint not null);
drop table if exists Taxon_IdentificationKey;
create table Taxon_IdentificationKey (Taxon_id bigint not null, keys_id bigint not null);
drop table if exists Taxon_PhylogeneticTree;
create table Taxon_PhylogeneticTree (Taxon_id bigint not null, trees_id bigint not null);
drop table if exists Taxon_Reference;
create table Taxon_Reference (Taxon_id bigint not null, references_id bigint not null, primary key (Taxon_id, references_id));
drop table if exists Taxon_TypeAndSpecimen;
create table Taxon_TypeAndSpecimen (Taxon_id bigint not null, typesAndSpecimens_id bigint not null);
drop table if exists TypeAndSpecimen;
create table TypeAndSpecimen (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, creator varchar(255), license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), bibliographicCitation varchar(255), catalogNumber varchar(255), collectionCode varchar(255), institutionCode varchar(255), locality clob, recordedBy varchar(255), scientificName varchar(255), sex varchar(255), source varchar(255), taxonRank varchar(255), typeDesignatedBy varchar(255), typeDesignationType varchar(255), typeStatus varchar(255), verbatimEventDate varchar(255), verbatimLabel varchar(255), verbatimLatitude varchar(255), verbatimLongitude varchar(255), decimalLatitude double, decimalLongitude double, location BLOB, authority_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
drop table if exists User_Group;
create table User_Group (User_id bigint not null, groups_id bigint not null, primary key (User_id, groups_id));
drop table if exists User_permissions;
create table User_permissions (User_id bigint not null, permissions integer);
drop table if exists VernacularName;
create table VernacularName (id bigint not null, identifier varchar(255), accessRights varchar(255), created timestamp, creator varchar(255), license varchar(255), modified timestamp, rights clob, rightsHolder varchar(255), countryCode varchar(255), language varchar(255), lifeStage varchar(255), locality varchar(255), location varchar(255), organismPart varchar(255), plural boolean, preferredName boolean, sex varchar(255), source varchar(255), taxonRemarks varchar(255), temporal varchar(255), vernacularName varchar(255), authority_id bigint, taxon_id bigint, uri varchar(255), resource_id bigint, primary key (id), unique (identifier));
alter table Annotation add constraint FK1A21C74FCF3DA2C4 foreign key (authority_id) references Organisation;
alter table Comment add constraint FKC35AE4F16C64D29C foreign key (authority_id) references Organisation;
alter table Comment_alternativeIdentifiers add constraint FK7B63B2A45090CB20 foreign key (Comment_id) references Comment;
alter table Concept add constraint FK437B94B544A087 foreign key (prefSymbol_id) references Image;
alter table Concept add constraint FK437B95B1EDCD08D foreign key (source_id) references Reference;
alter table Image add constraint FK437B96B6B53D29C foreign key (authority_id) references Organisation;
alter table Distribution add constraint FKAB93A2A41EDCD08D foreign key (taxon_id) references Taxon;
alter table Distribution add constraint FKAB93A2A46B53D29C foreign key (authority_id) references Organisation;
alter table Distribution_Reference add constraint FKF3F2F783968322D2 foreign key (references_id) references Reference;
alter table Distribution_Reference add constraint FKF3F2F7832D1A3055 foreign key (Distribution_id) references Distribution;
alter table Group_permissions add constraint FK7A63C2A45090CB20 foreign key (Group_id) references Principal;
alter table IdentificationKey add constraint FKC35AE4F16B53D29C foreign key (authority_id) references Organisation;
alter table Identifier add constraint FK165A88C96B53D29C foreign key (authority_id) references Organisation;
alter table Identifier add constraint FK165A88C9351368A7 foreign key (taxon_id) references Taxon;
alter table Image add constraint FK437B93B544A087 foreign key (image_id) references Image;
alter table Image add constraint FK437B93B1EDCD08D foreign key (taxon_id) references Taxon;
alter table Image add constraint FK437B93B6B53D29C foreign key (authority_id) references Organisation;
alter table Resource add constraint FK1239DCF3DA2C4 foreign key (organisation_id) references Organisation;
alter table Resource_parameters add constraint FKB4682A309E0AAB54 foreign key (Resource_id) references Resource;
alter table MeasurementOrFact add constraint FK424D5F2B6B53D29C foreign key (taxon_id) references Taxon;
alter table MeasurementOrFact add constraint FK414D5F2B6B53D29C foreign key (authority_id) references Organisation;
alter table PhylogeneticTree add constraint FKC35AE4F16B53D29D foreign key (authority_id) references Organisation;
alter table PhylogeneticTree add constraint FKC35AE4F16B53D39D foreign key (bibliographicReference_id) references Reference;
alter table Reference add constraint FK404D5F2B6B53D29C foreign key (authority_id) references Organisation;
alter table Organisation add constraint FK93F5543B6B53D29C foreign key (authority_id) references Organisation;
alter table Taxon add constraint FK4CD9EAA54493690 foreign key (acceptedNameUsage_id) references Taxon;
alter table Taxon add constraint FK4CD9EAA6B53D29C foreign key (authority_id) references Organisation;
alter table Taxon add constraint FK5CD9EAACA0AED foreign key (nameAccordingTo_id) references Reference;
alter table Taxon add constraint FK4CE9EAA54493690 foreign key (originalNameUsage_id) references Taxon;
alter table Taxon add constraint FK4CD9EAACA0AED foreign key (namePublishedIn_id) references Reference;
alter table Taxon add constraint FK4CD9EAAA9E98AAD foreign key (parentNameUsage_id) references Taxon;
alter table Taxon_IdentificationKey add constraint FK56D693661EDCD08E foreign key (Taxon_id) references Taxon;
alter table Taxon_IdentificationKey add constraint FK56D69466437564A foreign key (keys_id) references IdentificationKey;
alter table Taxon_Concept add constraint FK56D793661EDCD08D foreign key (Taxon_id) references Taxon;
alter table Taxon_Concept add constraint FK56D79366437564A foreign key (concepts_id) references Concept;
alter table Taxon_Image add constraint FK56D693661EDCD08D foreign key (Taxon_id) references Taxon;
alter table Taxon_Image add constraint FK56D69366437564A foreign key (images_id) references Image;
alter table Taxon_PhylogeneticTree add constraint FK164D2BD6968323D1 foreign key (trees_id) references PhylogeneticTree;
alter table Taxon_PhylogeneticTree add constraint FK164D2BD61EDCD18D foreign key (Taxon_id) references Taxon;
alter table Taxon_Reference add constraint FK164D2BD6968322D1 foreign key (references_id) references Reference;
alter table Taxon_Reference add constraint FK164D2BD61EDCD08D foreign key (Taxon_id) references Taxon;
alter table Taxon_TypeAndSpecimen add constraint FK164D2BD6968322E1 foreign key (typesAndSpecimens_id) references TypeAndSpecimen;
alter table Taxon_TypeAndSpecimen add constraint FK164D2BD61EDCD08E foreign key (Taxon_id) references Taxon;
alter table Description add constraint FK6A10726C1EDCD08D foreign key (taxon_id) references Taxon;
alter table Description add constraint FK6A10726C6B53D29C foreign key (authority_id) references Organisation;
alter table Description_Reference add constraint FKF3F2F783968322D1 foreign key (references_id) references Reference;
alter table Description_Reference add constraint FKF3F2F7832D1A3054 foreign key (Description_id) references Description;
alter table PhylogeneticTree_Taxon add constraint FK164D2BD6968323D2 foreign key (PhylogeneticTree_id) references PhylogeneticTree;
alter table PhylogeneticTree_Taxon add constraint FK164D2BD61EDCD17D foreign key (leaves_id) references Taxon;
alter table User_Group add constraint FKE7B7ED0BDA0BABAB foreign key (groups_id) references Principal;
alter table User_Group add constraint FKE7B7ED0B9E0AAB54 foreign key (User_id) references Principal;
alter table User_permissions add constraint FKB4582A309E0AAB54 foreign key (User_id) references Principal;
alter table VernacularName add constraint FK524D5F2B6B53D29C foreign key (taxon_id) references Taxon;
alter table VernacularName add constraint FK514D5F2B6B53D29C foreign key (authority_id) references Organisation;
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Comment',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Concept',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Description',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Distribution',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('IdentificationKey',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Identifier',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Image',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Resource',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('MeasurementOrFact',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Place',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Principal',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Reference',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Organisation',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Taxon',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('TypeAndSpecimen',1);
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('VernacularName',1);