package com.sitecenter.notification.service.events;

import lombok.ToString;

@ToString
public class AppEventInfo {
    String message;
    public enum EVENT_TYPE { CREATED,UPDATED,DELETED,VIEWED }
    public enum ENTITY_TYPE { NOTIFICATION, NOTIFICATION_FOR_USER, NOTIFICATION_FOR_GROUP}

    private EVENT_TYPE eventType;
    private ENTITY_TYPE entityType;
    private Long entity_id;
    private String entity_uuid;

    public AppEventInfo(String message, EVENT_TYPE eventType, ENTITY_TYPE entityType, Long entity_id) {
        this.message = message;
        this.eventType = eventType;
        this.entityType = entityType;
        this.entity_id = entity_id;
    }

    public AppEventInfo(String message, EVENT_TYPE eventType, ENTITY_TYPE entityType, String entity_uuid) {
        this.message = message;
        this.eventType = eventType;
        this.entityType = entityType;
        this.entity_uuid = entity_uuid;
    }

    public String getMessage() {
        return message;
    }

    public Long getEntity_id() {
        return entity_id;
    }

    public String getEntity_uuid() {
        return entity_uuid;
    }

    public EVENT_TYPE getEventType() {
        return eventType;
    }

    public ENTITY_TYPE getEntityType() {
        return entityType;
    }
}