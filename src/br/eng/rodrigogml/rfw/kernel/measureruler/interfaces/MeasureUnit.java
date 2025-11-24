package br.eng.rodrigogml.rfw.kernel.measureruler.interfaces;

import java.io.Serializable;
import java.math.BigDecimal;

import br.eng.rodrigogml.rfw.kernel.RFW;

/**
 * Interface utilizada para definir uma unidade de medida.
 */
public interface MeasureUnit extends Serializable {

  /**
   * Enum utilizada para definir as grandezas de medidas do sistema.
   */
  public static enum MeasureDimension {
    WEIGHT(WeightUnit.values()), UNIT(UnitUnit.values()), VOLUME(VolumeUnit.values()), LENGTH(LengthUnit.values()), AREA(AreaUnit.values()), CUSTOM(null);

    private MeasureUnit[] units = null;

    MeasureDimension(MeasureUnit[] units) {
      this.units = units;
    }

    public MeasureUnit[] getUnits() {
      return units;
    }
  }

  public static enum WeightUnit implements MeasureUnit {
    KILOGRAM(new BigDecimal("1000"), "Kg"), GRAM(BigDecimal.ONE, "g"), MILIGRAM(new BigDecimal("0.001"), "mg"), TONNE(new BigDecimal("1000000"), "t");

    WeightUnit(BigDecimal ratio, String symbol) {
      this.ratio = ratio;
      this.symbol = symbol;
    }

    private BigDecimal ratio;
    private String symbol;

    @Override
    public BigDecimal getRatio() {
      return this.ratio;
    }

    @Override
    public String getSymbol() {
      return this.symbol;
    }

    @Override
    public String getName() {
      return super.name();
    }

    @Override
    public MeasureDimension getDimension() {
      return MeasureDimension.WEIGHT;
    }
  }

  public static enum UnitUnit implements MeasureUnit {
    UNIT(BigDecimal.ONE, "Und"), COUPLE(new BigDecimal("2"), "Par"), DOZEN(new BigDecimal("12"), "Dz"), CENT(RFW.BIGHUNDRED, "CNT"), MIL(new BigDecimal("1000"), "MIL");

    private BigDecimal ratio;
    private String symbol;

    UnitUnit(BigDecimal ratio, String symbol) {
      this.ratio = ratio;
      this.symbol = symbol;
    }

    @Override
    public BigDecimal getRatio() {
      return this.ratio;
    }

    @Override
    public String getName() {
      return super.name();
    }

    @Override
    public String getSymbol() {
      return this.symbol;
    }

    @Override
    public MeasureDimension getDimension() {
      return MeasureDimension.UNIT;
    }
  }

  public static enum VolumeUnit implements MeasureUnit {
    LITER(BigDecimal.ONE, "L"), MILLILITER(new BigDecimal("0.001"), "mL"), CUBICMETER(new BigDecimal("1000"), "m3");

    private BigDecimal ratio;
    private String symbol;

    VolumeUnit(BigDecimal ratio, String symbol) {
      this.ratio = ratio;
      this.symbol = symbol;
    }

    @Override
    public BigDecimal getRatio() {
      return this.ratio;
    }

    @Override
    public String getName() {
      return super.name();
    }

    @Override
    public String getSymbol() {
      return this.symbol;
    }

    @Override
    public MeasureDimension getDimension() {
      return MeasureDimension.VOLUME;
    }
  }

  public static enum LengthUnit implements MeasureUnit {
    METER(BigDecimal.ONE, "m"), CENTIMETER(new BigDecimal("0.01"), "cm"), MILIMETER(new BigDecimal("0.001"), "mm");

    private BigDecimal ratio;
    private String symbol;

    LengthUnit(BigDecimal ratio, String symbol) {
      this.ratio = ratio;
      this.symbol = symbol;
    }

    @Override
    public BigDecimal getRatio() {
      return this.ratio;
    }

    @Override
    public String getName() {
      return super.name();
    }

    @Override
    public String getSymbol() {
      return this.symbol;
    }

    @Override
    public MeasureDimension getDimension() {
      return MeasureDimension.LENGTH;
    }
  }

  public static enum AreaUnit implements MeasureUnit {
    SQUAREMETER(BigDecimal.ONE, "m2");

    private BigDecimal ratio;
    private String symbol;

    AreaUnit(BigDecimal ratio, String symbol) {
      this.ratio = ratio;
      this.symbol = symbol;
    }

    @Override
    public BigDecimal getRatio() {
      return this.ratio;
    }

    @Override
    public String getName() {
      return super.name();
    }

    @Override
    public String getSymbol() {
      return this.symbol;
    }

    @Override
    public MeasureDimension getDimension() {
      return MeasureDimension.AREA;
    }
  }

  /**
   * Razão de tamanho da unidade dentro da Dimensão.
   *
   * @return Ex: 1000 para Kg, 1 para g, 1 para m, 1000 para Km, etc.
   */
  BigDecimal getRatio();

  /**
   * Retorna a Dimensão da unidade atual.
   *
   * @return {@link MeasureDimension} do {@link MeasureUnit} atual.
   */
  MeasureDimension getDimension();

  /**
   * Simbolo da Unidade de Medida.
   *
   * @return Ex: Kg para Kilograma, Km para Kilometro, t para Tonelada,
   */
  String getSymbol();

  /**
   * Nome da Unidade de Medida.<br>
   * Redundância em relação ao método {@link #name()}, para que seja possível utilziar o padrão de métodos "get" com as classes do Framework.
   *
   * @return Retorna o .name() do Enum mascarado pela interface MeasureUnit.
   */
  String getName();

  /**
   * Nome da Unidade de Medida.
   *
   * @return Retorna o .name() do Enum mascarado pela interface MeasureUnit.
   */
  String name();

  /**
   * Número Ordinal do item.
   *
   * @return Retorna o .orginal() do Enum mascarado pela interface MeasureUnit.
   */
  int ordinal();
}