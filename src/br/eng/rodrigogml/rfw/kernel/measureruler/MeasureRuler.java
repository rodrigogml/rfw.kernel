package br.eng.rodrigogml.rfw.kernel.measureruler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.bundle.RFWBundle;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureRulerEquivalenceInterface;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.AreaUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.LengthUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.MeasureDimension;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.UnitUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.VolumeUnit;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit.WeightUnit;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe utilizada para criar as defini��es de unidades de medidas do framework. Convers�es, "tradu��es", e outras necessidades relacionadas a grandezas de medidas.<br>
 *
 * @author Rodrigo Leit�o
 * @since 10.0.0 (10 de out de 2018)
 */
public final class MeasureRuler {

  /**
   * Construtor privado para classe estt�tica.
   */
  private MeasureRuler() {
  }

  public static MeasureUnit valueOf(String name) throws RFWCriticalException {
    MeasureUnit result = null;
    try {
      result = Enum.valueOf(UnitUnit.class, name);
    } catch (IllegalArgumentException e) {
      try {
        result = Enum.valueOf(WeightUnit.class, name);
      } catch (IllegalArgumentException e1) {
        try {
          result = Enum.valueOf(VolumeUnit.class, name);
        } catch (IllegalArgumentException e2) {
          try {
            result = Enum.valueOf(AreaUnit.class, name);
          } catch (IllegalArgumentException e3) {
            try {
              result = Enum.valueOf(LengthUnit.class, name);
            } catch (IllegalArgumentException e4) {
              // Se n�o � de nenhuma unidade acima, tentamos as EQUIVALENCIAS
              // try {
              // final String[] params = name.split("\\|");
              // final EntityManager em = TransactionManager.getEntityManager();
              // final CriteriaBuilder cb = em.getCriteriaBuilder();
              // final CriteriaQuery<MeasureEquivalenceVO> cq = cb.createQuery(MeasureEquivalenceVO.class);
              // final Root<MeasureEquivalenceVO> root = cq.from(MeasureEquivalenceVO.class);
              //
              // final MeasureEquivalenceVO_ v_ = MeasureEquivalenceVO_.VO;
              // final RFWMO mo = new RFWMO();
              // mo.equal(v_.measurerulervo().id(), params[0]);
              // mo.equal(v_.measureCustomVO().id(), params[1]);
              //
              // cq.where(RFWDAO.createPredicateFromMO(cb, cq, root, mo));
              // final TypedQuery<MeasureEquivalenceVO> query = em.createQuery(cq);
              // if (query.getResultList().size() == 1) {
              // MeasureEquivalenceVO equiVO = query.getResultList().get(0);
              // RFWDAO.exploitAttribute(equiVO, MeasureEquivalenceVO_.vo().measureCustomVO().id(), em);
              // RFWDAO.exploitAttribute(equiVO, MeasureEquivalenceVO_.vo().measurerulervo().id(), em);
              // em.flush();
              // em.detach(equiVO);
              // result = new MeasureEquivalenceBean(equiVO);
              // } else if (query.getResultList().size() > 1) {
              // throw new RFWCriticalException("RFW_ERR_100713", e4);
              // }
              // } catch (Exception e5) {
              // throw new RFWCriticalException("n�o foi poss�vel desserializar o valor '" + name + "'", e4);
              // }
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * Converte um valor de uma unidade de medida para outra unidade de medida.<br>
   * O m�todo s� permite converter unidades entre a mesma Dimens�o (�rea para �rea, volume para volume, massa para massa, etc.)<br>
   * Este m�todo retorna o valor com uma precis�o de 3 casas decimais. Em geral 3 casas s�o o suficiente para exibi��o para o usu�rio, mas para c�lculos continuados esse arredondamento pode gerar falta de precis�o no c�lculo. Para trabalhar com maior precis�o de casas utilize o m�todo {@link #convertTo(BigDecimal, MeasureUnit, MeasureUnit, int)}
   *
   * @param value Valor da unidade a ser convertido.
   * @param startUnit Unidade de medida atual do valor passado.
   * @param endUnit Unidade de medida para a qual desejamos converter
   * @return Valor convertido para a nova unidade de medida com a precis�o de 3 casas decimais.
   * @throws RFWException
   */
  public static BigDecimal convertTo(BigDecimal value, MeasureUnit startUnit, MeasureUnit endUnit) throws RFWException {
    return convertTo(value, startUnit, endUnit, 3);
  }

  /**
   * Converte um valor de uma unidade de medida para outra unidade de medida.<br>
   * O m�todo s� permite converter unidades entre a mesma Dimens�o (�rea para �rea, volume para volume, massa para massa, etc.)<br>
   *
   * @param value Valor da unidade a ser convertido.
   * @param startUnit Unidade de medida atual do valor passado.
   * @param endUnit Unidade de medida para a qual desejamos converter
   * @param precision Total de casas decimais utilizadas. Evita que o arredondamento seja feito "muito curto" criando imprecis�o de c�lculo quando a convers�o � utilizada em c�lculos maiores.
   *
   * @return Valor convertido para a nova unidade de medida com a precis�o das casas decimais definidas pelo par�metro 'precision".
   * @throws RFWException
   */
  public static BigDecimal convertTo(BigDecimal value, MeasureUnit startUnit, MeasureUnit endUnit, int precision) throws RFWException {
    return convertTo(null, value, startUnit, endUnit, precision);
  }

  /**
   * Converte um valor de uma unidade de medida para outra unidade de medida.<br>
   * O m�todo s� permite converter unidades entre a mesma Dimens�o (�rea para �rea, volume para volume, massa para massa, etc.)<br>
   *
   * @param equivalence Interface de defini��o de equival�ncias entre as unidades de medidas.
   * @param value Valor da unidade a ser convertido.
   * @param startUnit Unidade de medida atual do valor passado.
   * @param endUnit Unidade de medida para a qual desejamos converter
   *
   * @return Valor convertido para a nova unidade de medida com a precis�o de 3 casas decimais.
   * @throws RFWException
   */
  public static BigDecimal convertTo(MeasureRulerEquivalenceInterface equivalence, BigDecimal value, MeasureUnit startUnit, MeasureUnit endUnit) throws RFWException {
    return convertTo(equivalence, value, startUnit, endUnit, 3);
  }

  /**
   * Converte um valor de uma unidade de medida para outra unidade de medida.<br>
   * O m�todo s� permite converter unidades entre a mesma Dimens�o (�rea para �rea, volume para volume, massa para massa, etc.)<br>
   *
   * @param equivalence Interface de defini��o de equival�ncias entre as unidades de medidas.
   * @param value Valor da unidade a ser convertido.
   * @param startUnit Unidade de medida atual do valor passado.
   * @param endUnit Unidade de medida para a qual desejamos converter
   * @param precision Total de casas decimais utilizadas. Evita que o arredondamento seja feito "muito curto" criando imprecis�o de c�lculo quando a convers�o � utilizada em c�lculos maiores.
   *
   * @return Valor convertido para a nova unidade de medida com a precis�o das casas decimais definidas pelo par�metro 'precision".
   * @throws RFWException
   */
  public static BigDecimal convertTo(MeasureRulerEquivalenceInterface equivalence, BigDecimal value, MeasureUnit startUnit, MeasureUnit endUnit, int precision) throws RFWException {
    if (startUnit == null || endUnit == null || value == null) {
      throw new RFWCriticalException("Para converter unidades de medidas, todas as informa��es s�o obrigat�rias!");
    } else {
      BigDecimal startWeight = null;
      BigDecimal endWeight = null;

      if (startUnit.getDimension() != MeasureDimension.CUSTOM && startUnit.getDimension() == endUnit.getDimension()) {
        startWeight = BigDecimal.ONE;
        endWeight = BigDecimal.ONE;
      } else {
        if (equivalence == null) throw new RFWValidationException("N�o � poss�vel converter entre Dimens�es diferentes sem uma regra de equival�ncia definida! (${0} -> ${1}')", new String[] { RFWBundle.get(startUnit.getDimension()), RFWBundle.get(endUnit.getDimension()) });

        HashMap<MeasureUnit, BigDecimal> eqHash = equivalence.getMeasureUnitHash();
        for (Entry<MeasureUnit, BigDecimal> entry : eqHash.entrySet()) {
          MeasureUnit tmu = entry.getKey();
          BigDecimal weight = entry.getValue();

          if (tmu.getDimension() == MeasureDimension.CUSTOM) {
            if (startUnit.getDimension() == MeasureDimension.CUSTOM && tmu.getSymbol().equals(startUnit.getSymbol()) && tmu.name().equals(startUnit.name())) {
              startWeight = weight.multiply(tmu.getRatio());
            } else if (endUnit.getDimension() == MeasureDimension.CUSTOM && tmu.getSymbol().equals(endUnit.getSymbol()) && tmu.name().equals(endUnit.name())) {
              endWeight = weight.multiply(tmu.getRatio());
            }
          } else {
            if (startUnit.getClass() == tmu.getClass()) {
              startWeight = weight.multiply(tmu.getRatio());
            } else if (endUnit.getClass() == tmu.getClass()) {
              endWeight = weight.multiply(tmu.getRatio());
            }
          }
          if (startWeight != null && endWeight != null) break;
        }
        if (startWeight == null) throw new RFWValidationException("N�o foi poss�vel converter as dimens�es de medida pois a r�gua de equivalencias n�o tem informa��es para a '" + RFWBundle.get(startUnit) + "/" + RFWBundle.get(startUnit.getDimension()) + "'!");
        if (endWeight == null) throw new RFWValidationException("N�o foi poss�vel converter as dimens�es de medida pois a r�gua de equivalencias n�o tem informa��es para a '" + RFWBundle.get(endUnit) + "/" + RFWBundle.get(endUnit.getDimension()) + "'!");
      }

      BigDecimal num = value.multiply(startUnit.getRatio()).multiply(endWeight);
      BigDecimal div = startWeight.multiply(endUnit.getRatio());
      return num.divide(div, precision, RFW.getRoundingMode());
    }
  }

  /**
   * Valida o conte�do da Hash retornada. <br>
   * Procura por valores redundantes e/ou incoerentes.<Br>
   * Garante que tenha no m�nimo 2 dimens�es diferentes, uma tabela de equival�ncias com um �nico valor n�o tem utilidade alguma.
   *
   * @param equivalence Interface que retorna a hash com as informa��es de equivalencia.
   * @throws RFWException
   */
  public static void validateMeasureRulerEquivalence(MeasureRulerEquivalenceInterface equivalence) throws RFWException {
    PreProcess.requiredNonNullCritical(equivalence, "A interface de equival�ncia n�o pode ser passada nula!");
    HashMap<MeasureUnit, BigDecimal> hash = equivalence.getMeasureUnitHash();
    PreProcess.requiredNonNullCritical(hash, "A hash de equival�ncias n�o pode ser nula!");

    ArrayList<Entry<MeasureUnit, BigDecimal>> entryList = new ArrayList<>(hash.entrySet());
    for (int i = 0; i < entryList.size(); i++) {
      Entry<MeasureUnit, BigDecimal> entry = entryList.get(i);

      MeasureUnit mu = entry.getKey();
      BigDecimal weight = entry.getValue();
      PreProcess.requiredNonNullCritical(mu, "A hash de equival�ncias n�o pode ter uma chave nula!");
      PreProcess.requiredNonNullCritical(weight, "A hash de equival�ncias n�o pode ter uma valor nulo! '" + mu.name() + "'!");

      if (weight.compareTo(BigDecimal.ZERO) <= 0) throw new RFWValidationException("O peso de equival�ncia deve ser positivo maior que zero! '" + mu.name() + "'.");

      for (int x = i + 1; x < entryList.size(); x++) {
        Entry<MeasureUnit, BigDecimal> entryTmp = entryList.get(x);
        if (mu.getDimension() == MeasureDimension.CUSTOM && entryTmp.getKey().getDimension() == MeasureDimension.CUSTOM) {
          if (entryTmp.getKey().getClass() == mu.getClass() && entryTmp.getKey().getSymbol().equals(mu.getSymbol()) && entryTmp.getKey().name().equals(mu.name())) {
            throw new RFWValidationException("Foram encontradas mais de uma informa��o para a unidade de medida personalizada '" + mu.name() + " (" + mu.getSymbol() + ")'");
          }
        } else {
          if (entryTmp.getKey().getDimension() == mu.getDimension()) {
            throw new RFWValidationException("Foram encontradas mais de uma informa��o para a dimens�o '" + mu.getDimension() + "'");
          }
        }
      }
    }

    if (hash.size() < 2) throw new RFWValidationException("A tabela de equival�ncia precisa ter 2 ou mais equival�ncias para ter alguma utilidade.");
  }

  /**
   * Extrai todas as unidades de medida que <b>podem</b> ser utilizadas a partir das equival�ncias definidas.<br>
   * Realiza a chamada do m�todo {@link #cleanInvalidEquivalences(MeasureRulerEquivalenceInterface)} antes de separar as unidades de medida.
   *
   * @param equivalence Equival�ncia entre unidades de medidas.
   * @return Lista com todos os objetos MeasureUnit de todas as dimens�es definidas na equival�ncia e/ou unidades personalizadas.
   * @throws RFWException
   */
  public static LinkedHashSet<MeasureUnit> extractAllMeasureUnites(MeasureRulerEquivalenceInterface equivalence) throws RFWException {
    return extractAllMeasureUnites(equivalence, null);
  }

  /**
   * Extrai todas as unidades de medida que <b>podem</b> ser utilizadas a partir das equival�ncias definidas + as unidades de medida de uma dimens�o espec�fica.<br>
   * Realiza a chamada do m�todo {@link #cleanInvalidEquivalences(MeasureRulerEquivalenceInterface)} antes de separar as unidades de medida.<br>
   * Este m�todo foi criado para que tenhamos as unidades de medida da r�gua + a unidade padr�o de um item, j� que n�o conseguimos ter uma r�gia de equival�ncias s� com uma Dimens�o (o m�todo {@link #cleanInvalidEquivalences(MeasureRulerEquivalenceInterface)} exclu�).
   *
   * @param equivalence Equival�ncia entre unidades de medidas.
   * @return Lista com todos os objetos MeasureUnit de todas as dimens�es definidas na equival�ncia e/ou unidades personalizadas.
   * @throws RFWException
   */
  public static LinkedHashSet<MeasureUnit> extractAllMeasureUnites(MeasureRulerEquivalenceInterface equivalence, MeasureDimension dimension) throws RFWException {
    HashMap<MeasureUnit, BigDecimal> hash = null;
    if (equivalence != null) hash = cleanInvalidEquivalences(equivalence);

    final LinkedHashSet<MeasureUnit> set = new LinkedHashSet<MeasureUnit>();

    if (dimension != null) {
      for (MeasureUnit unit : dimension.getUnits()) {
        set.add(unit);
      }
    }

    if (hash != null) {
      for (MeasureUnit measureUnit : hash.keySet()) {
        if (measureUnit.getDimension() == MeasureDimension.CUSTOM) {
          set.add(measureUnit);
        } else {
          for (MeasureUnit mu2 : measureUnit.getDimension().getUnits()) {
            set.add(mu2);
          }
        }
      }
    }

    return set;
  }

  /**
   * Valida os objetos dentro do equivalence e remove todos os objetos que n�o tiverem as informa��es de "ratio" e "MeasureUnit" (name e symbol) definidos.<br>
   * Se depois de limpar os itens inv�lidos sobrar apenas 0 ou 1 entrada v�lida o m�todo retorna null, pois equival�ncias s� s�o �teis quando estabelecem a rela��o entre pelo menos 2 grandezas de medidas.
   *
   * @param equivalence Interface de acesso �s Equival�ncias.
   * @return HashMap apenas com as equival�ncias v�lidas, ou nulo caso s� exista uma ou nenhuma entrada v�lida.
   * @throws RFWException
   */
  public static HashMap<MeasureUnit, BigDecimal> cleanInvalidEquivalences(MeasureRulerEquivalenceInterface equivalence) throws RFWException {
    PreProcess.requiredNonNullCritical(equivalence, "A interface de equival�ncia n�o pode ser passada nula!");
    HashMap<MeasureUnit, BigDecimal> hash = equivalence.getMeasureUnitHash();
    PreProcess.requiredNonNullCritical(hash, "A hash de equival�ncias n�o pode ser nula!");

    final HashMap<MeasureUnit, BigDecimal> newHash = new HashMap<MeasureUnit, BigDecimal>();
    for (Entry<MeasureUnit, BigDecimal> entry : hash.entrySet()) {
      if (entry.getValue() != null && entry.getKey() != null && entry.getKey().getRatio() != null && entry.getKey().getRatio().compareTo(BigDecimal.ZERO) > 0 && entry.getKey().name() != null && entry.getKey().getSymbol() != null) {
        newHash.put(entry.getKey(), entry.getValue());
      }
    }

    if (newHash.size() < 2) return null;

    return newHash;
  }

  /**
   * Verifica se a unidade de medida est� configurada nas equival�ncias passada.<br>
   * Verifica as unidades de medida padr�es do sistema comparando suas dimens�es. J� as unidades personalizadas s�o comparadas com base no seu nome e s�mbolo.<br>
   * Executa a limpeza da r�gua antes de avaliar, ou seja, s� returna que � equivalente caso a unidade esteja devidamente configurada.
   *
   * @param equivalence Interface de equival�ncias
   * @param measureUnit Unidade de medidas � ser testada contra a cole��o de equival�ncias.
   * @return true caso a unidade de medida esteja configurada nas equival�ncias, false caso contr�rio.
   * @throws RFWException
   */
  public static boolean isMeasureUnitEquivalent(MeasureRulerEquivalenceInterface equivalence, MeasureUnit measureUnit) throws RFWException {
    PreProcess.requiredNonNullCritical(equivalence, "A interface de equival�ncia n�o pode ser passada nula!");
    PreProcess.requiredNonNullCritical(equivalence.getMeasureUnitHash(), "A hash de equival�ncias n�o pode ser nula!");

    HashMap<MeasureUnit, BigDecimal> hash = cleanInvalidEquivalences(equivalence);

    if (hash != null) {
      for (Entry<MeasureUnit, BigDecimal> entry : hash.entrySet()) {
        MeasureUnit mu = entry.getKey();
        if (mu.getDimension() == MeasureDimension.CUSTOM && measureUnit.getDimension() == MeasureDimension.CUSTOM) {
          if (mu.getSymbol().equals(measureUnit.getSymbol()) && mu.name().equals(measureUnit.name())) {
            return true;
          }
        } else if (mu.getDimension() == measureUnit.getDimension()) {
          if (mu.getClass() == measureUnit.getClass()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Recupera o ratio de convers�o de uma unidade de medida atualmente configurada na r�gua de equival�ncias passada.<br>
   * Unidades de medidas personalizadas ser�o comparadas com base no seu nome e s�mbolo.<br>
   * Unidades de medida do sistema ser�o comparadas conforme sua dimens�o, e caso necess�rio ser�o convertidas. Isto quer dizer que, se na r�gua estiver configurado 1Kg, e for solicitado o ratio para "GRAM" o valor retornado ser� de 1000 e n�o de 1 como consta na Hash. (Afinal, internamente j� h� a configura��o de 1kg = 1000g).
   *
   * @param equivalence Interface de equival�ncias
   * @param measureUnit Unidade de medidas � ser encontrada na cole��o de equival�ncias.
   * @return Ratio de equival�ncia da unidade de medida solicitada, ou nulo, caso a unidade de medida solicitada n�o seja encontrada.
   * @throws RFWException
   */
  public static BigDecimal getRatio(MeasureRulerEquivalenceInterface equivalence, MeasureUnit measureUnit) throws RFWException {
    PreProcess.requiredNonNullCritical(equivalence, "A interface de equival�ncia n�o pode ser passada nula!");
    PreProcess.requiredNonNullCritical(equivalence.getMeasureUnitHash(), "A hash de equival�ncias n�o pode ser nula!");

    HashMap<MeasureUnit, BigDecimal> hash = cleanInvalidEquivalences(equivalence);

    if (hash != null) {
      for (Entry<MeasureUnit, BigDecimal> entry : hash.entrySet()) {
        MeasureUnit mu = entry.getKey();
        if (mu.getDimension() == MeasureDimension.CUSTOM && measureUnit.getDimension() == MeasureDimension.CUSTOM) {
          if (mu.getSymbol().equals(measureUnit.getSymbol()) && mu.name().equals(measureUnit.name())) {
            return entry.getValue();
          }
        } else if (mu.getDimension() == measureUnit.getDimension()) {
          if (mu.getClass() == measureUnit.getClass()) {
            return convertTo(entry.getValue(), mu, measureUnit);
          }
        }
      }
    }
    return null;
  }
}
