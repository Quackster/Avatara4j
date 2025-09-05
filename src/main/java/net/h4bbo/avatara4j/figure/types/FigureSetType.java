package net.h4bbo.avatara4j.figure.types;

public class FigureSetType {
    private String set;
    private Integer paletteId;
    private Boolean isMandatory;
    private Boolean isMaleMandatoryNonHC;
    private Boolean isMaleMandatoryHC;
    private Boolean isFemaleMandatoryNonHC;
    private Boolean isFemaleMandatoryHC;

    public FigureSetType(String set, Integer paletteId, Boolean isMandatory, Boolean isMaleMandatoryNonHC,
                         Boolean isMaleMandatoryHC, Boolean isFemaleMandatoryNonHC, Boolean isFemaleMandatoryHC) {
        this.set = set;
        this.paletteId = paletteId;
        this.isMandatory = isMandatory;
        this.isMaleMandatoryNonHC = isMaleMandatoryNonHC;
        this.isMaleMandatoryHC = isMaleMandatoryHC;
        this.isFemaleMandatoryNonHC = isFemaleMandatoryNonHC;
        this.isFemaleMandatoryHC = isFemaleMandatoryHC; // Fixed: was isFemaleMandatoryNonHC in original
    }

    @Override
    public String toString() {
        return "FigureSetType{" +
                "set='" + set + '\'' +
                ", paletteId=" + paletteId +
                ", isMandatory=" + isMandatory +
                ", isMaleMandatoryNonHC=" + isMaleMandatoryNonHC +
                ", isMaleMandatoryHC=" + isMaleMandatoryHC +
                ", isFemaleMandatoryNonHC=" + isFemaleMandatoryNonHC +
                ", isFemaleMandatoryHC=" + isFemaleMandatoryHC +
                '}';
    }

    // Getters
    public String getSet() { return set; }
    public Integer getPaletteId() { return paletteId; }
    public Boolean getIsMandatory() { return isMandatory; }
    public Boolean getIsMaleMandatoryNonHC() { return isMaleMandatoryNonHC; }
    public Boolean getIsMaleMandatoryHC() { return isMaleMandatoryHC; }
    public Boolean getIsFemaleMandatoryNonHC() { return isFemaleMandatoryNonHC; }
    public Boolean getIsFemaleMandatoryHC() { return isFemaleMandatoryHC; }

    // Setters
    public void setSet(String set) { this.set = set; }
    public void setPaletteId(Integer paletteId) { this.paletteId = paletteId; }
    public void setIsMandatory(Boolean mandatory) { this.isMandatory = mandatory; }
    public void setIsMaleMandatoryNonHC(Boolean maleMandatoryNonHC) { this.isMaleMandatoryNonHC = maleMandatoryNonHC; }
    public void setIsMaleMandatoryHC(Boolean maleMandatoryHC) { this.isMaleMandatoryHC = maleMandatoryHC; }
    public void setIsFemaleMandatoryNonHC(Boolean femaleMandatoryNonHC) { this.isFemaleMandatoryNonHC = femaleMandatoryNonHC; }
    public void setIsFemaleMandatoryHC(Boolean femaleMandatoryHC) { this.isFemaleMandatoryHC = femaleMandatoryHC; }
}
