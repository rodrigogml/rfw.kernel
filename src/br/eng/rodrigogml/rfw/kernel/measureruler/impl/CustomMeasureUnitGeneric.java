package br.eng.rodrigogml.rfw.kernel.measureruler.impl;

import java.math.BigDecimal;
import java.util.Objects;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.CustomMeasureUnit;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Objeto imut�vel utilizado para definir uma unidade de medida personalizada.<br>
 * Esta classe tem o m�todo equals implementando para que ela seja considerada a mesma classe sempre que os atributos {@link #name} e {@link #symbol} retornarem equals() == true.
 *
 * @author Rodrigo Leit�o
 * @since 10.0 (25 de nov. de 2021)
 */
public class CustomMeasureUnitGeneric implements CustomMeasureUnit {

  private static final long serialVersionUID = 6075304169912995798L;

  /**
   * Nome da unidade de medida.
   */
  private final String name;

  /**
   * S�mbolo da unidade de medida. O s�mbolo � uma abrevia��o utilizada para identificar a unidade de medida ao lado do n�mero ou em tabelas. Assim como o "g" representa gramas, ou "mL" representa milil�tros.
   */
  private final String symbol;

  /**
   * Constroi uma nova Unidade de Medida.<br>
   * Note que este objeto � imut�vel, nome e s�mbolo n�o podem ser alterados.
   *
   * @param name nome da unidade de medida.
   * @param symbol s�mbolo da unidade de medida. O s�mbolo � uma abrevia��o utilizada para identificar a unidade de medida ao lado do n�mero ou em tabelas. Assim como o "g" representa gramas, ou "mL" representa milil�tros.
   * @throws RFWException Lan�ado caso o nome ou o s�mbolo da unidade de medida sejam inv�lidos.
   */
  public CustomMeasureUnitGeneric(String name, String symbol) throws RFWException {
    super();
    name = PreProcess.processStringToNull(name);
    symbol = PreProcess.processStringToNull(symbol);
    PreProcess.requiredNonNullCritical(name, "O nome da unidade de medida n�o pode ser nulo!");
    PreProcess.requiredNonNullCritical(symbol, "O s�mbolo da unidade de medida n�o pode ser nulo!");
    this.name = name;
    this.symbol = symbol;
  }

  /**
   * N�o tem um Ratio pois as unidades de medidas personalizadas n�o tem equival�ncia entre elas. Retornamos sempre 1 para simplificar as formulas de transi��o entre elas e as dimens�es suportadas pelo sistema.
   *
   * @return BigDecimal com valor 1
   */
  @Override
  public BigDecimal getRatio() {
    return BigDecimal.ONE;
  }

  /**
   * Retorna sempre a constante {@link MeasureDimension#CUSTOM}
   *
   * @return Retorna sempre a constante {@link MeasureDimension#CUSTOM}
   */
  @Override
  public MeasureDimension getDimension() {
    return MeasureDimension.CUSTOM;
  }

  /**
   * # s�mbolo da unidade de medida. O s�mbolo � uma abrevia��o utilizada para identificar a unidade de medida ao lado do n�mero ou em tabelas. Assim como o "g" representa gramas, ou "mL" representa milil�tros.
   *
   * @return # s�mbolo da unidade de medida
   */
  @Override
  public String getSymbol() {
    return this.symbol;
  }

  /**
   * Name.
   *
   * @return the string
   */
  @Override
  public String name() {
    return this.name;
  }

  /**
   * # nome da unidade de medida.
   *
   * @return # nome da unidade de medida
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Sempre retorna -1 por n�o ter uma numera��o como o ENUM.
   */
  @Override
  public int ordinal() {
    return -1;
  }

  /**
   * Hash code.
   *
   * @return the int
   */
  @Override
  public int hashCode() {
    return Objects.hash(name, symbol);
  }

  /**
   * Equals.
   *
   * @param obj the obj
   * @return true, if successful
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CustomMeasureUnitGeneric other = (CustomMeasureUnitGeneric) obj;
    return Objects.equals(name, other.name) && Objects.equals(symbol, other.symbol);
  }
}
