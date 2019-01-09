CREATE TABLE Questionnaire (
	QuestionnaireId text,
	QuestionnaireLabel varchar,
	QuestionnaireName varchar,
	QuestionnaireDescription varchar,
	QuestionnaireType varchar
);

CREATE TABLE QuestionnaireType (
	TypeName varchar
);

CREATE TABLE QuestionPool (
	QuestionId text,
	QuestionType varchar
);

CREATE TABLE QuestionOption (
	QuestionId varchar,
	QuestionLangId varchar,
	OptionText varchar
);

CREATE TABLE QuestionLangVersion (
	QuestionId text,
	QuestionLangId text,
	QuestionText varchar
);

CREATE TABLE Language (
	LanguageId text,
	LanguageName varchar,
	LanguageDesc varchar,
	LanguageOtherNames varchar,
	LanguageTypeId text
);

CREATE TABLE LanguageType (
	LangTypeId text,
	LangTypeName varchar
);

CREATE TABLE QuestionPropertyDef (
	PropertyId text,
	PropertyName varchar
);

CREATE TABLE QuestionProperty (
	QuestionId text,
	QuestionPropertyId text,
	QuestionPropertyValue integer
);

CREATE TABLE QuestionnaireContent (
	QuestionnaireId text,
	QuestionId text,
	QuestionOrder varchar
);

CREATE TABLE QuestionnairePropertyDef (
	QuesnirPropertyId text,
	QuesnirPropertyName varchar,
	QuesnirPropertyDesc varchar
);

CREATE TABLE QuestionnaireProperty (
	QuesnirId text,
	QuesnirProperty text,
	QuesnirPropertyValue integer
);

CREATE TABLE Answer (
	QuestionnaireId text,
	QuestionId text,
	AnswerId text,
	AnswerLabel varchar,
	AnswerText varchar
);

CREATE TABLE File (
	FileId text,
	FileName varchar,
	FileAnswerId text,
	FileType varchar,
	FilePath varchar,
	FileCreator text,
	FileStartTime datetime,
	FileEndTime datetime
);

CREATE TABLE Person (
	PersonId text,
	PersonName varchar,
	PersonOtherNames varchar,
	PersonDOB datetime,
	PersonMainRole text,
	PersonPhoto blob,
	PersonPhotoDesc varchar,
	PersonIntroQuestnirDesc varchar
);

CREATE TABLE Role (
	RoleId text,
	RoleName varchar,
	RoleIntroRequired integer,
	RolePhotoRequired integer,
	RoleOnClient integer
);

CREATE TABLE Session (
	SessionId text,
	SessionLabel varchar,
	SessionName varchar,
	SessionStartTime datetime,
	SessionLocation varchar,
	SessionDesc varchar
);

CREATE TABLE SessionPerson (
	SessionId text,
	SessionPersonId text,
	SessionPersonRoleId text
);

CREATE TABLE SessionAnswer (
	SessionId text,
	QuestionnaireId text,
	QuestionId text,
	AnswerId text
);

CREATE TABLE FieldTrip (
	FieldTripId text,
	FieldTripName varchar,
	FieldTripStartDate datetime,
	FieldTripEndDate datetime
);

CREATE TABLE FieldTripSession (
	FieldTripId text,
	SessionId text
);

