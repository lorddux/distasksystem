package ru.hse.lorddux.structures.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitRequestData implements RequestData {
    private Integer cpu;
    private Integer ram;
    private Integer hdd;
    private Integer ssd;
}
