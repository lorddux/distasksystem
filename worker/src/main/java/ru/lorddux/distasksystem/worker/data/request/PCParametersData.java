package ru.lorddux.distasksystem.worker.data.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCParametersData implements RequestData {
    private Integer cpu;
    private Integer ram;
    private Integer hdd;
    private Integer ssd;
    private String apiAddress;
}
