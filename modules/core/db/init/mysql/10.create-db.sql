-- begin TESTCAMERA_CAMERA
create table TESTCAMERA_CAMERA (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    CAMERA_ID integer,
    CAMERA_NUMBER varchar(255),
    CAMERA_ADDRESS varchar(255),
    ACCOUNT varchar(255),
    PASSWORD varchar(255),
    VIDEO_ADDRESS varchar(255),
    THUMBNAIL_ADDRESS varchar(255),
    --
    primary key (ID)
)^
-- end TESTCAMERA_CAMERA
