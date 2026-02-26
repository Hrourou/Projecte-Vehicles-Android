package cat.copernic.backendProjecte3.dto;

import java.math.BigDecimal;

public class VehicleResponseDTO {

    private String matricula;
    private String marca;
    private String model;
    private String variant;
    private String fotoUrl;
    private String potencia;
    private String color;
    private Integer limitQuilometratge;
    private BigDecimal preuHora;
    private BigDecimal fiancaEstandard;
    private Integer minDiesLloguer;
    private Integer maxDiesLloguer;

    public VehicleResponseDTO(
            String matricula,
            String marca,
            String model,
            String variant,
            String fotoUrl,
            String potencia,
            String color,
            Integer limitQuilometratge,
            BigDecimal preuHora,
            BigDecimal fiancaEstandard,
            Integer minDiesLloguer,
            Integer maxDiesLloguer) {

        this.matricula = matricula;
        this.marca = marca;
        this.model = model;
        this.variant = variant;
        this.fotoUrl = fotoUrl;
        this.potencia = potencia;
        this.color = color;
        this.limitQuilometratge = limitQuilometratge;
        this.preuHora = preuHora;
        this.fiancaEstandard = fiancaEstandard;
        this.minDiesLloguer = minDiesLloguer;
        this.maxDiesLloguer = maxDiesLloguer;
    }

    public String getMatricula() { return matricula; }
    public String getMarca() { return marca; }
    public String getModel() { return model; }
    public String getVariant() { return variant; }
    public String getFotoUrl() { return fotoUrl; }
    public String getPotencia() { return potencia; }
    public String getColor() { return color; }
    public Integer getLimitQuilometratge() { return limitQuilometratge; }
    public BigDecimal getPreuHora() { return preuHora; }
    public BigDecimal getFiancaEstandard() { return fiancaEstandard; }
    public Integer getMinDiesLloguer() { return minDiesLloguer; }
    public Integer getMaxDiesLloguer() { return maxDiesLloguer; }
}