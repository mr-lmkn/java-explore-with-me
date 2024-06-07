package ru.practicum.ewmServer.likes.dto;

public interface EventTopChartDto {
    Integer getNpp();

    Long getEventId();

    String getTitle();

    String getDescription();

    String getCategoryName();

    Long getCategoryId();

    Long getUserId();

    String getUserName();

    Integer getRating();

    Integer getCountLikes();

    Integer getCountDislike();

    Integer getCountEvents();
}
