package ru.hse.lorddux.structures.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMessageQueueRequestData implements RequestData {
    private Integer numberOfMessages;
    private Integer visibilityTimeout;
}
