package org.sitecenter.notification.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.sitecenter.notification.dto.UserDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class UserDTOPageableResponse extends PageImpl<UserDTO> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserDTOPageableResponse(@JsonProperty("content") List<UserDTO> content, @JsonProperty("number") int number, @JsonProperty("size") int size,
                                           @JsonProperty("totalElements") Long totalElements, @JsonProperty("pageable") JsonNode pageable, @JsonProperty("last") boolean last,
                                           @JsonProperty("totalPages") int totalPages, @JsonProperty("sort") JsonNode sort, @JsonProperty("first") boolean first,
                                           @JsonProperty("numberOfElements") int numberOfElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    public UserDTOPageableResponse(List<UserDTO> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public UserDTOPageableResponse(List<UserDTO> content) {
        super(content);
    }

    public UserDTOPageableResponse() {
        super(new ArrayList<UserDTO>());
    }
}