package uk.gov.gsi.justice.po.alfresco.proxy.bdd.util;

public enum StubPath {
    UPLOAD_NEW_PATH("/uploadnew"),
    RESERVE_PATH("/reserve/1234"),
    UPLOAD_AND_RELEASE_PATH("/uploadandrelease/123"),
    RELEASE_PATH("/release/1234"),
    DELETE_PATH("/delete/1234"),
    DELETE_ALL_PATH("/deleteall/1234"),
    DELETE_HARD_PATH("/deletehard/1234"),
    DELETE_MULTIPLE_PATH("/multidelete"),
    MOVE_PATH("/movedocument/123/456"),
    UNDELETE_PATH("/undelete/123"),
    UPDATE_METADATA_PATH("/updatemetadata/456"),
    LOCK_PATH("/lock/1234"),
    NOTIFICATION_STATUS_PATH("/notificationStatus");

    private final String path;

     StubPath(final String path) {
        this.path = path;
    }
}
