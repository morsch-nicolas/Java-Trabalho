package com.projeto.api.model;

public class EstatisticaDTO {
    public long count;
    public double sum;
    public double avg;
    public double min;
    public double max;

    public EstatisticaDTO(long count, double sum, double avg, double min, double max) {
        this.count = count;
        this.sum = sum;
        this.avg = avg;
        this.min = min;
        this.max = max;
    }

    public static EstatisticaDTO vazia() {
        return new EstatisticaDTO(0, 0.0, 0.0, 0.0, 0.0);
    }
}
