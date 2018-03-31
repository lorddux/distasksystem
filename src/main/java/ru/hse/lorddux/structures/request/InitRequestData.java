package ru.hse.lorddux.structures.request;

import lombok.Data;

@Data
public class InitRequestData {
    private Integer cpu;
    private Integer ram;
    private Integer hdd;
    private Integer ssd;
}
