package ru.hse.lorddux.structures.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMessageQueueRequestData implements RequestData {
    private String popReceipt;
}
