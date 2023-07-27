package br.eng.rodrigogml.rfw.kernel.measureruler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.measureruler.impl.CustomMeasureUnitGeneric;
import br.eng.rodrigogml.rfw.kernel.measureruler.impl.MeasureRulerEquivalence;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.AreaUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.LengthUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.UnitUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.VolumeUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.WeightUnit;

/**
 * Description: Classe de Teste das funcionalidades da Régua de Unidadedes/Conversões.<br>
 *
 * @author Rodrigo GML
 * @since 10.0 (19 de set de 2020)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MeasureRulerTest {

  @Test
  public void t00_convertUnits_DiferentDimensions() throws RFWException {
    try {
      MeasureRuler.convertTo(new BigDecimal("1000"), WeightUnit.GRAM, VolumeUnit.LITER);
      Assert.fail("Era esperado uma exceção indicando a impossibilidade de se converter entre dimensões diferentes.");
    } catch (Exception e) {
    }
  }

  @Test
  public void t01_convertUnits_Volume() throws RFWException {
    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), VolumeUnit.MILLILITER, VolumeUnit.LITER);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1 e precisão de 3 casas", new BigDecimal("1.000"), v);
    } catch (Exception e) {
    }

    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), VolumeUnit.MILLILITER, VolumeUnit.LITER, 6);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1 e precisão de 6 casas", new BigDecimal("1.000000"), v);
    } catch (Exception e) {
    }
  }

  @Test
  public void t02_convertUnits_Unit() throws RFWException {
    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), UnitUnit.UNIT, UnitUnit.MIL);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1 e precisão de 3 casas", new BigDecimal("1.000"), v);
    } catch (Exception e) {
    }

    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), UnitUnit.UNIT, UnitUnit.MIL, 6);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1 e precisão de 3 casas", new BigDecimal("1.000000"), v);
    } catch (Exception e) {
    }
  }

  @Test
  public void t03_convertUnits_Weight() throws RFWException {
    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), WeightUnit.GRAM, WeightUnit.KILOGRAM);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1 e precisão de 3 casas", new BigDecimal("1.000"), v);
    } catch (Exception e) {
    }

    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), WeightUnit.GRAM, WeightUnit.KILOGRAM, 6);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1 e precisão de 3 casas", new BigDecimal("1.000000"), v);
    } catch (Exception e) {
    }
  }

  @Test
  public void t04_convertUnits_Area() throws RFWException {
    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), AreaUnit.SQUAREMETER, AreaUnit.SQUAREMETER);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1000 e precisão de 3 casas", new BigDecimal("1000.000"), v);
    } catch (Exception e) {
    }

    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), AreaUnit.SQUAREMETER, AreaUnit.SQUAREMETER, 6);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1000 e precisão de 6 casas", new BigDecimal("1000.000000"), v);
    } catch (Exception e) {
    }
  }

  @Test
  public void t05_convertUnits_Length() throws RFWException {
    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), LengthUnit.MILIMETER, LengthUnit.METER);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1 e precisão de 3 casas", new BigDecimal("1.000"), v);
    } catch (Exception e) {
    }

    try {
      BigDecimal v = MeasureRuler.convertTo(new BigDecimal("1000"), LengthUnit.MILIMETER, LengthUnit.METER, 6);
      Assert.assertEquals("Era esperado um BigDecimal com valor 1 e precisão de 3 casas", new BigDecimal("1.000000"), v);
    } catch (Exception e) {
    }
  }

  @Test
  public void t06_convertUnits_DifferentDimensions_Rule1() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.KILOGRAM, BigDecimal.ONE);
    eqVO.getMeasureUnitHash().put(UnitUnit.UNIT, new BigDecimal("1000"));
    eqVO.getMeasureUnitHash().put(VolumeUnit.LITER, new BigDecimal("250"));

    BigDecimal v = MeasureRuler.convertTo(eqVO, new BigDecimal("1"), WeightUnit.KILOGRAM, UnitUnit.UNIT, 6);
    Assert.assertEquals(new BigDecimal("1000.000000"), v);

    v = MeasureRuler.convertTo(eqVO, new BigDecimal("1000"), WeightUnit.GRAM, UnitUnit.UNIT, 6);
    Assert.assertEquals(new BigDecimal("1000.000000"), v);

    v = MeasureRuler.convertTo(eqVO, new BigDecimal("4"), UnitUnit.UNIT, VolumeUnit.LITER, 6);
    Assert.assertEquals(new BigDecimal("1.000000"), v);

    v = MeasureRuler.convertTo(eqVO, new BigDecimal("10"), VolumeUnit.LITER, UnitUnit.DOZEN, 6);
    Assert.assertEquals(new BigDecimal("3.333333"), v);
  }

  @Test
  public void t07_convertUnits_DifferentDimensions_Rule2() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.GRAM, new BigDecimal("600"));
    eqVO.getMeasureUnitHash().put(UnitUnit.UNIT, new BigDecimal("2"));

    BigDecimal v = MeasureRuler.convertTo(eqVO, new BigDecimal("4"), UnitUnit.UNIT, WeightUnit.KILOGRAM, 6);
    Assert.assertEquals(new BigDecimal("1.200000"), v);

    v = MeasureRuler.convertTo(eqVO, new BigDecimal("1"), UnitUnit.UNIT, WeightUnit.KILOGRAM, 6);
    Assert.assertEquals(new BigDecimal("0.300000"), v);
  }

  @Test
  public void t08_convertUnits_CustomUnits() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.GRAM, new BigDecimal("600"));
    eqVO.getMeasureUnitHash().put(UnitUnit.UNIT, new BigDecimal("2"));

    CustomMeasureUnitGeneric balde = new CustomMeasureUnitGeneric("Balde", "BD");
    eqVO.getMeasureUnitHash().put(balde, new BigDecimal("1"));

    BigDecimal v = MeasureRuler.convertTo(eqVO, new BigDecimal("1"), balde, WeightUnit.KILOGRAM, 6);
    Assert.assertEquals(new BigDecimal("0.600000"), v);

    v = MeasureRuler.convertTo(eqVO, new BigDecimal("2.5"), balde, WeightUnit.GRAM, 6);
    Assert.assertEquals(new BigDecimal("1500.000000"), v);
  }

  @Test
  public void t09_MeasureRuler_validateEquivalences_Valid() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.GRAM, new BigDecimal("600"));
    eqVO.getMeasureUnitHash().put(UnitUnit.UNIT, new BigDecimal("2"));

    CustomMeasureUnitGeneric balde = new CustomMeasureUnitGeneric("Balde", "BD");
    eqVO.getMeasureUnitHash().put(balde, new BigDecimal("1"));

    MeasureRuler.validateMeasureRulerEquivalence(eqVO);
  }

  @Test
  public void t10_MeasureRuler_validateEquivalences_InvalidMeasure() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.GRAM, new BigDecimal("600"));
    eqVO.getMeasureUnitHash().put(WeightUnit.KILOGRAM, new BigDecimal("1"));
    eqVO.getMeasureUnitHash().put(UnitUnit.UNIT, new BigDecimal("2"));
    CustomMeasureUnitGeneric balde = new CustomMeasureUnitGeneric("Balde", "BD");
    eqVO.getMeasureUnitHash().put(balde, new BigDecimal("1"));
    try {
      MeasureRuler.validateMeasureRulerEquivalence(eqVO);
      Assert.fail("Esta esperada uma mensagem de validação!");
    } catch (RFWValidationException e) {
    }
  }

  @Test
  public void t11_MeasureRuler_validateEquivalences_NullValues() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.GRAM, null);
    eqVO.getMeasureUnitHash().put(UnitUnit.UNIT, new BigDecimal("2"));
    CustomMeasureUnitGeneric balde = new CustomMeasureUnitGeneric("Balde", "BD");
    eqVO.getMeasureUnitHash().put(balde, new BigDecimal("1"));

    try {
      MeasureRuler.validateMeasureRulerEquivalence(eqVO);
      Assert.fail("Esta esperada uma exception Crítica!");
    } catch (RFWCriticalException e) {
    }
  }

  @Test
  public void t12_MeasureRuler_validateEquivalences_MultipleCustomUnits() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.GRAM, new BigDecimal("600"));
    eqVO.getMeasureUnitHash().put(UnitUnit.UNIT, new BigDecimal("2"));

    CustomMeasureUnitGeneric balde = new CustomMeasureUnitGeneric("Balde", "BD");
    eqVO.getMeasureUnitHash().put(balde, new BigDecimal("1"));

    CustomMeasureUnitGeneric cart = new CustomMeasureUnitGeneric("Cartela", "CTL");
    eqVO.getMeasureUnitHash().put(cart, new BigDecimal("3"));

    MeasureRuler.validateMeasureRulerEquivalence(eqVO);
  }

  @Test
  public void t14_MeasureRuler_extractMeasureUnits() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.GRAM, new BigDecimal("600"));
    eqVO.getMeasureUnitHash().put(UnitUnit.UNIT, new BigDecimal("2"));

    CustomMeasureUnitGeneric balde = new CustomMeasureUnitGeneric("Balde", "BD");
    eqVO.getMeasureUnitHash().put(balde, new BigDecimal("1"));

    CustomMeasureUnitGeneric cartela = new CustomMeasureUnitGeneric("Cartela", "CTL");
    eqVO.getMeasureUnitHash().put(cartela, new BigDecimal("3"));

    LinkedHashSet<MeasureUnit> units = MeasureRuler.extractAllMeasureUnites(eqVO);

    int count = 0;
    for (MeasureUnit mu : WeightUnit.values()) {
      if (!units.contains(mu)) {
        fail("Esta esperada a unidade de medida '" + mu.name() + "'!");
      }
      count++;
    }

    for (MeasureUnit mu : UnitUnit.values()) {
      if (!units.contains(mu)) {
        fail("Esta esperada a unidade de medida '" + mu.name() + "'!");
      }
      count++;
    }

    if (!units.contains(balde))

      fail("Esta esperada a unidade de medida '" + balde.name() + "'!");
    count++;
    if (!units.contains(cartela)) fail("Esta esperada a unidade de medida '" + cartela.name() + "'!");
    count++;
    assertEquals("A quantidade de unidades de medidas retornadas é diferente do esperado!", count, units.size());
  }

  @Test
  public void t15_MeasureRuler_cleanInvalidEquivalences() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.GRAM, null);
    eqVO.getMeasureUnitHash().put(LengthUnit.METER, BigDecimal.ONE);
    eqVO.getMeasureUnitHash().put(null, new BigDecimal("2"));

    CustomMeasureUnitGeneric balde = new CustomMeasureUnitGeneric("Balde", "BD");
    eqVO.getMeasureUnitHash().put(balde, null);

    HashMap<MeasureUnit, BigDecimal> cleannedHash = MeasureRuler.cleanInvalidEquivalences(eqVO);
    assertNull(cleannedHash);
  }

  @Test
  public void t16_MeasureRuler_cleanInvalidEquivalences() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(WeightUnit.GRAM, new BigDecimal("100"));
    eqVO.getMeasureUnitHash().put(LengthUnit.METER, BigDecimal.ONE);
    eqVO.getMeasureUnitHash().put(AreaUnit.SQUAREMETER, new BigDecimal("2"));

    CustomMeasureUnitGeneric balde = new CustomMeasureUnitGeneric("Balde", "BD");
    eqVO.getMeasureUnitHash().put(balde, null);

    CustomMeasureUnitGeneric balde2 = new CustomMeasureUnitGeneric("Balde", "BD"); // Mesmo sendo outro objeto, ele deve substituir o primeiro pq o .equals() do CUstommeasureUnit retornará true pelo conteúdo .name() e .symbol().
    eqVO.getMeasureUnitHash().put(balde2, BigDecimal.TEN);

    CustomMeasureUnitGeneric balde3 = new CustomMeasureUnitGeneric("Balde 3", "BD3");
    eqVO.getMeasureUnitHash().put(balde3, BigDecimal.TEN);

    CustomMeasureUnitGeneric balde4 = new CustomMeasureUnitGeneric("Balde 4", "BD4");
    eqVO.getMeasureUnitHash().put(balde4, null);

    assertEquals(true, MeasureRuler.isMeasureUnitEquivalent(eqVO, WeightUnit.GRAM));
    assertEquals(true, MeasureRuler.isMeasureUnitEquivalent(eqVO, WeightUnit.KILOGRAM));
    assertEquals(true, MeasureRuler.isMeasureUnitEquivalent(eqVO, AreaUnit.SQUAREMETER));
    assertEquals(true, MeasureRuler.isMeasureUnitEquivalent(eqVO, LengthUnit.METER));
    assertEquals(false, MeasureRuler.isMeasureUnitEquivalent(eqVO, VolumeUnit.LITER));
    assertEquals(true, MeasureRuler.isMeasureUnitEquivalent(eqVO, balde));
    assertEquals(true, MeasureRuler.isMeasureUnitEquivalent(eqVO, balde2));
    assertEquals(true, MeasureRuler.isMeasureUnitEquivalent(eqVO, balde3));
    assertEquals(false, MeasureRuler.isMeasureUnitEquivalent(eqVO, balde4));

    CustomMeasureUnitGeneric fake = new CustomMeasureUnitGeneric("Cartela", "CTL");
    eqVO.getMeasureUnitHash().put(fake, null);
    assertEquals(false, MeasureRuler.isMeasureUnitEquivalent(eqVO, fake));
  }

  @Test
  public void t17_MeasureRuler_addSmartEquivalence() throws RFWException {
    MeasureRulerEquivalence eqVO = new MeasureRulerEquivalence();
    eqVO.getMeasureUnitHash().put(VolumeUnit.LITER, BigDecimal.ONE);
    eqVO.getMeasureUnitHash().put(WeightUnit.KILOGRAM, new BigDecimal("3"));

    eqVO.addComparativeEquivalence(new BigDecimal("5"), LengthUnit.METER, new BigDecimal("250"), VolumeUnit.MILLILITER);

    assertEquals(new BigDecimal("20000.0000000"), MeasureRuler.convertTo(eqVO, new BigDecimal("3"), WeightUnit.TONNE, LengthUnit.METER, 7));
    assertEquals(new BigDecimal("50.0000"), MeasureRuler.convertTo(eqVO, new BigDecimal("0.15"), WeightUnit.TONNE, VolumeUnit.LITER, 4));
  }
}
