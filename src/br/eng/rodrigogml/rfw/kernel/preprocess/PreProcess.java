package br.eng.rodrigogml.rfw.kernel.preprocess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField.RelationshipTypes;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringField;
import br.eng.rodrigogml.rfw.kernel.utils.RUReflex;
import br.eng.rodrigogml.rfw.kernel.utils.RUString;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Classe com m�todos de pr� processamento de vari�veis para facilitar o processamento dos objetos.<br>
 *
 * @author Rodrigo Leit�o
 * @since 2.0 (JUN / 2007)
 */
public final class PreProcess {

  /**
   * Esta Enum permite definir com uma constante o processimento a ser utilizado dentro das annotations e outras defini��es do validation.
   *
   * @author rodrigo
   *
   */
  public static enum PreProcessOption {
    /**
     * N�o aplica nenhum pr�-processamento no conte�do.
     */
    NONE,
    /**
     * Passa o conte�do da String para caixa alta.
     */
    STRING_UPPERCASE,
    /**
     * Limpa a String:
     * <li>remove todos os espa�os m�ltiplos dentro do texto deixando apenas um �nico;
     * <li>remove espe�os no come�o e no fim da string;
     * <li>Caso a String resulte em "" o valor � convertido para NULO.
     */
    STRING_SPACESCLEAN_TO_NULL,
    /**
     * Caso a string seja maior que o valor permitido na RFWMetaAnotation, trunca o conte�do no limite.
     */
    STRING_TRUNCATE,
    /**
     * Define para os campos data que esta deve ser atualizada a data atual do sistema.
     */
    DATE_TONOW,
  }

  /**
   * Construtor privado para classe est�tica.
   */
  private PreProcess() {
  }

  /**
   * Processa o VO completamente e recursivamente procurando pelas RFWMetaAnnotations com defini��o de atributo 'preProcess()' cujo tipo seja {@link PreProcessOption}.<br>
   * Todo o processamento � feito diretamente no objeto recebido. Por isso n�o � retorno deste m�todo.
   *
   * @param vo VO a ser preprocessado para a valida��o.
   * @throws RFWException Este m�todo n�o deve retornar exceptions pois n�o faz nenhum tipo de valida��o, apenas prepara a informa��o se ela existir. No entando devido as opera��es de reflex�o, as exceptions podem ser lan�adas.
   */
  public static <VO extends RFWVO> void processVO(VO vo) throws RFWException {
    if (vo != null) {
      // Iteramos todos os atributos do VO em busca das RFWMetaAnnotations
      final Field[] fields = vo.getClass().getDeclaredFields();
      for (Field field : fields) {
        Object value = null;
        try {
          value = RUReflex.getPropertyValue(vo, field.getName());
        } catch (Exception e) {
          // Esta exception pode ocorrer quando a propriedade n�o tem o m�todo de GET! por isso s� ignoramos
        }

        final PreProcessOption[] ppList = RUReflex.getRFWMetaAnnotationPreProcess(vo.getClass(), field.getName());
        if (ppList != null) {
          final Class<?> clazz = RUReflex.getPropertyTypeByObjectAndType(vo, field.getName());
          for (int i = 0; i < ppList.length; i++) {
            PreProcessOption preProcess = ppList[i];
            if (preProcess != PreProcessOption.NONE) {
              switch (preProcess) {
                case NONE:
                  // nada a fazer. Nem chega a entrar aqui pq o IF j� filtra, s� deixamos aqui para completar o switch. � importante que o NONE esteja no IF pq assim j� evitamos que o SET seja feito quando n�o h� altera��o, deixando o c�digo mais r�pido.
                  break;
                case STRING_SPACESCLEAN_TO_NULL:
                  if (!String.class.isAssignableFrom(clazz)) throw new RFWCriticalException("O preProcess '${0}' n�o � v�lido para o tipo do atributo '${1}' da classe '${2}'", new String[] { preProcess.toString(), field.getName(), vo.getClass().getCanonicalName() });
                  value = processStringToNull((String) value);
                  break;
                case STRING_TRUNCATE:
                  if (!String.class.isAssignableFrom(clazz)) throw new RFWCriticalException("O preProcess '${0}' n�o � v�lido para o tipo do atributo '${1}' da classe '${2}'", new String[] { preProcess.toString(), field.getName(), vo.getClass().getCanonicalName() });
                  if (value != null) {
                    Annotation ann = RUReflex.getRFWMetaAnnotation(field);
                    int maxLength = 0;
                    if (ann instanceof RFWMetaStringField) {
                      maxLength = ((RFWMetaStringField) ann).maxLength();
                    } else {
                      throw new RFWCriticalException("O preProcess '${0}' n�o � tem suporte para a annotation '${3}' do atributo '${1}' da classe '${2}'", new String[] { preProcess.toString(), field.getName(), vo.getClass().getCanonicalName(), ann.getClass().getCanonicalName() });
                    }
                    value = RUString.truncate((String) value, maxLength);
                  }
                  break;
                case STRING_UPPERCASE:
                  if (!String.class.isAssignableFrom(clazz)) throw new RFWCriticalException("O preProcess '${0}' n�o � v�lido para o tipo do atributo '${1}' da classe '${2}'", new String[] { preProcess.toString(), field.getName(), vo.getClass().getCanonicalName() });
                  if (value != null) {
                    value = ((String) value).toUpperCase();
                  }
                  break;
                case DATE_TONOW:
                  if (LocalDate.class.isAssignableFrom(clazz)) {
                    value = RFW.getDate();
                  } else if (LocalTime.class.isAssignableFrom(clazz)) {
                    value = RFW.getTime();
                  } else if (LocalDateTime.class.isAssignableFrom(clazz)) {
                    value = RFW.getDateTime();
                  } else if (Date.class.isAssignableFrom(clazz)) {
                    // value = new Date();
                    throw new RFWCriticalException("Por defini��o o framework n�o permite mais utilizar o objeto 'java.util.Date'. Verifique seu c�digo e substituia por LocalDate, LocalTime ou LocalDateTime!");
                  } else {
                    throw new RFWCriticalException("O preProcess '${0}' n�o � v�lido para o tipo do atributo '${1}' da classe '${2}'", new String[] { preProcess.toString(), field.getName(), vo.getClass().getCanonicalName() });
                  }
                  break;
              }
              try {
                RUReflex.setPropertyValue(vo, field.getName(), value, false);
              } catch (Exception e) {
                // Esta exception pode ocorrer quando n�o temos o m�todo de SET, s� ignoramos poide pode ser alguma propriedade s� de leitura
              }
            }
          }
        }

        if (value != null) {
          // Verifica se temos a annotation de relationship, e se for um relacionamento de composi��o (em que o objeto � manipulado junto) temos de processar suas informa��es tamb�m
          final RFWMetaRelationshipField relAnn = field.getAnnotation(RFWMetaRelationshipField.class);
          if (relAnn != null && (relAnn.relationship() == RelationshipTypes.COMPOSITION || relAnn.relationship() == RelationshipTypes.MANY_TO_MANY)) {
            if (RFWVO.class.isAssignableFrom(value.getClass())) {
              PreProcess.processVO((RFWVO) value);
            } else if (Collection.class.isAssignableFrom(value.getClass())) {
              for (Object obj : (Collection<?>) value) {
                if (obj instanceof RFWVO) PreProcess.processVO((RFWVO) obj);
              }
            } else if (Map.class.isAssignableFrom(value.getClass())) {
              for (Object obj : ((Map<?, ?>) value).values()) {
                if (obj instanceof RFWVO) PreProcess.processVO((RFWVO) obj);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Processa a string passada, removendo espa�os (do come�o, do final e espa�os duplos entre as palavras) e convertendo para null quando a string for vazia.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static String processStringToNull(String value) {
    if (value != null) {
      value = RUString.replaceFakeSpacesByUniqueSpace(value);
      value = RUString.replaceTabsByUniqueSpace(value);
      value = RUString.replaceDoubleSpaces(value);
      value = value.trim();
      if (value.length() == 0) {
        value = null;
      }
    }
    return value;
  }

  /**
   * Processa a string passada, removendo espa�os e convertendo para uma string em branco (tamanho zero) quando a string tiver apenas espa�os em branco ou for nula.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static String processStringToZeroLenght(String value) {
    if (value == null) {
      value = "";
    } else {
      value = RUString.replaceDoubleSpaces(value).trim();
    }
    return value;
  }

  /**
   * Processa qualquer n�mero inteiro para nulo, caso seja menor ou igual a zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Integer processIntegerToNullIfZeroOrNegative(Integer value) {
    if (value != null && value <= 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero inteiro para nulo, caso seja menor que zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Integer processIntegerToNullIfNegative(Integer value) {
    if (value != null && value < 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero float para nulo, caso seja menor ou igual a zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Float processFloatToNullIfZeroOrNegative(Float value) {
    if (value != null && value <= 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero BigDecimal para nulo, caso seja menor ou igual a zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static BigDecimal processBigDecimalToNullIfZeroOrNegative(BigDecimal value) {
    if (value != null && value.compareTo(BigDecimal.ZERO) <= 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero double para nulo, caso seja menor ou igual a zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Double processDoubleToNullIfZeroOrNegative(Double value) {
    if (value != null && value <= 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero Long para nulo, caso seja menor ou igual a zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Long processLongToNullIfZeroOrNegative(Long value) {
    if (value != null && value <= 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero Long para nulo, caso seja menor que zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Long processLongToNullIfNegative(Long value) {
    if (value != null && value < 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero float para zero, caso seja nulo ou negativo.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Float processFloatToZeroIfNullOrNegative(Float value) {
    if (value == null || value < 0) {
      value = 0f;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero double para zero, caso seja nulo ou negativo.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Double processDoubleToZeroIfNullOrNegative(Double value) {
    if (value == null || value < 0) {
      value = 0d;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero float para nulo, caso seja igual a zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Float processFloatToNullIfZero(Float value) {
    if (value != null && value == 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero float para nulo, caso seja menor que zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Float processFloatToNullIfNegative(Float value) {
    if (value != null && value < 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero BigDecimal para nulo, caso seja menor que zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static BigDecimal processBigDecimalToNullIfNegative(BigDecimal value) {
    if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero double para nulo, caso seja igual a zero.
   *
   * @param value valor a ser processado
   * @return valor processado
   */
  public static Double processDoubleToNullIfZero(Double value) {
    if (value != null && value == 0) {
      value = null;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero float para conter apenas duas casas decimais. Arredonda para o lado mais justo.
   *
   * @param value Valor para formatar como monet�rio.
   * @return Valor arredondado para apenas 2 casas decimais.
   */
  public static Float processCurrency(Float value) {
    if (value != null) {
      value = (float) Math.round(value * 100) / 100;
    }
    return value;
  }

  /**
   * Processa qualquer n�mero BigDecimal para conter apenas duas casas decimais. Arredonda usando a defini��o padr�o do sistema (classe {@link RFW}).
   *
   * @param value Valor a ser arredondado.
   * @return Valor arredondado.
   */
  public static BigDecimal processCurrency(BigDecimal value) {
    if (value != null) {
      value = value.setScale(2, RFW.getRoundingMode());
    }
    return value;
  }

  /**
   * Processa um array de 'byte' para nulo, caso tenha tamanho zero.
   *
   * @param value Array para ser processado.
   * @return pr�prio array, ou null caso o array tenha tamanho zero.
   */
  public static byte[] processByteArrayToNullIfZeroLength(byte[] value) {
    if (value != null) {
      if (value.length == 0) {
        value = null;
      }
    }
    return value;
  }

  /**
   * Procesa uma data e retorna null caso a data seja posterior a atual.
   *
   * @param date data a ser processada.
   * @return Retorna a data se ela for passada, ou null se for uma data futura.
   */
  public static Date processDateToNullIfFutureDate(Date date) {
    if (date != null) {
      if (date.compareTo(new Date()) > 0) {
        date = null;
      }
    }
    return date;
  }

  /**
   * Valida se o valor passado � positivo.
   *
   * @throws RFWException
   */
  public static void requiredPositive(Long value) throws RFWException {
    requiredPositive(value, "Esperado um valor positivo.");
  }

  /**
   * Valida se o valor passado � positivo.
   *
   * @throws RFWException
   */
  public static void requiredPositive(Long value, String message) throws RFWException {
    if (value == null || value <= 0) throw new RFWValidationException(message);
  }

  /**
   * Valida se o objeto passado n�o � nulo. Caso seja lan�a uma {@link RFWValidationException} com mensagem padr�o.
   *
   * @param value Objeto a ser testado.
   */
  public static void requiredNonNull(Object value) throws RFWException {
    requiredNonNull(value, "RFW_000001");
  }

  /**
   * Valida se o objeto passado n�o � nulo. Caso seja lan�a {@link RFWValidationException} com mensagem personalizada.
   *
   * @param value Objeto a ser testado.
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   */
  public static void requiredNonNull(Object value, String msg) throws RFWException {
    if (value == null) throw new RFWValidationException(msg);
  }

  /**
   * Valida se o objeto passado n�o � nulo. Caso seja lan�a {@link RFWValidationException} com mensagem personalizada.
   *
   * @param value Objeto a ser testado.
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   * @param params Argumentos para serem usados como substitutos de campos ${i} na mensagem de erro.
   */
  public static void requiredNonNull(Object value, String msg, String[] params) throws RFWException {
    if (value == null) throw new RFWValidationException(msg, params);
  }

  /**
   * Valida se o objeto passado n�o � nulo. Caso seja lan�a exce��o com mensagem padr�o.<br>
   * Lan�a uma exception Cr�tica.
   *
   * @param value Objeto a ser testado.
   */
  public static void requiredNonNullCritical(Object value) throws RFWException {
    requiredNonNullCritical(value, "RFW_000001");
  }

  /**
   * Valida se o objeto passado n�o � nulo. Caso seja lan�a exce��o com mensagem personalizada.<br>
   * Lan�a uma exception Cr�tica.
   *
   * @param value Objeto a ser testado.
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   */
  public static void requiredNonNullCritical(Object value, String msg) throws RFWException {
    if (value == null) throw new RFWCriticalException(msg);
  }

  /**
   * Valida se o objeto passado n�o � nulo. Caso seja lan�a exce��o com mensagem personalizada.<br>
   * Lan�a uma exception Cr�tica.
   *
   * @param value Objeto a ser testado.
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   * @param params Argumentos para serem usados como substitutos de campos ${i} na mensagem de erro.
   */
  public static void requiredNonNullCritical(Object value, String msg, String[] params) throws RFWException {
    if (value == null) throw new RFWCriticalException(msg, params);
  }

  /**
   * Valida se o objeto passado � nulo. Caso seja lan�a uma {@link RFWValidationException} com mensagem padr�o.
   *
   * @param value Objeto a ser testado.
   */
  public static void requiredNull(Object value) throws RFWException {
    requiredNull(value, "RFW_000002");
  }

  /**
   * Valida se o objeto passado � nulo. Caso seja lan�a {@link RFWValidationException} com mensagem personalizada.
   *
   * @param value Objeto a ser testado.
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   */
  public static void requiredNull(Object value, String msg) throws RFWException {
    if (value != null) throw new RFWValidationException(msg);
  }

  /**
   * Valida se o objeto passado � nulo. Caso seja lan�a exce��o com mensagem padr�o.<br>
   * Lan�a uma exception Cr�tica.
   *
   * @param value Objeto a ser testado.
   */
  public static void requiredNullCritical(Object value) throws RFWException {
    requiredNullCritical(value, "O valor deve ser nulo!");
  }

  /**
   * Valida se o objeto passado n�o � nulo. Caso seja lan�a exce��o com mensagem personalizada.<br>
   * Lan�a uma exception Cr�tica.
   *
   * @param value Objeto a ser testado.
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   */
  public static void requiredNullCritical(Object value, String msg) throws RFWException {
    if (value != null) throw new RFWCriticalException(msg);
  }

  /**
   * Garante que o array recebido n�o � nulo e nem vazio, tem o tamanho maior ou igual a 1.<br>
   * Note que este m�todo n�o verificar� se o conte�do dentro do array � nulo, s� se o array � nulo e de tamanho maior que zero.
   *
   *
   * @param array Array para valida��o
   * @param msg Mensagem da Exception caso n�o passe na valida��o.
   * @throws RFWCriticalException
   */
  public static <T> void requiredNonEmpty(T[] array, String msg) throws RFWException {
    if (array == null || array.length == 0) throw new RFWValidationException(msg);
  }

  /**
   * Garante que o array recebido n�o � nulo e nem vazio, tem o tamanho maior ou igual a 1.<br>
   * Note que este m�todo n�o verificar� se o conte�do dentro do array � nulo, s� se o array � nulo e de tamanho maior que zero.
   *
   *
   * @param array Array para valida��o
   * @throws RFWCriticalException
   */
  public static <T> void requiredNonEmpty(T[] array) throws RFWException {
    if (array == null || array.length == 0) throw new RFWValidationException("RFW_000042");
  }

  /**
   * Garante que o array recebido n�o � nulo e nem vazio, tem o tamanho maior ou igual a 1.<br>
   * Note que este m�todo n�o verificar� se o conte�do dentro do array � nulo, s� se o array � nulo e de tamanho maior que zero.
   *
   *
   * @param array Array para valida��o
   * @throws RFWCriticalException
   */
  public static <T> void requiredNonEmptyCritical(T[] array) throws RFWException {
    if (array == null || array.length == 0) throw new RFWCriticalException("RFW_000042");
  }

  /**
   * Garante que o array recebido n�o � nulo e nem vazio, tem o tamanho maior ou igual a 1.<br>
   * Note que este m�todo n�o verificar� se o conte�do dentro do array � nulo, s� se o array � nulo e de tamanho maior que zero.
   *
   *
   * @param array Array para valida��o
   * @param msg Mensagem da Exception caso n�o passe na valida��o.
   * @throws RFWCriticalException
   */
  public static <T> void requiredNonEmptyCritical(T[] array, String msg) throws RFWException {
    if (array == null || array.length == 0) throw new RFWCriticalException(msg);
  }

  /**
   * Valida se um valor est� entre dois valores INCLUSIVE, ou seja a condi��o � "minValue <= value <= maxValue".<br>
   * Lan�a uma exception Cr�tica.
   *
   * @param value Valor a ser verificado. Se nulo lan�a a exception como se a condi��o tivesse dado falso.
   * @param minValue Menor valor aceito. N�o pode ser nulo.
   * @param maxValue Maior valor aceito. N�o pode ser nulo.
   * @param msg Mensagem personalizada de erro.
   * @throws RFWException
   */
  public static void requiredBetweenCritical(Number value, Number minValue, Number maxValue, String msg) throws RFWException {
    requiredNonNullCritical(minValue);
    requiredNonNullCritical(maxValue);
    if (value == null || value.doubleValue() < minValue.doubleValue() || value.doubleValue() > maxValue.doubleValue()) throw new RFWCriticalException(msg);
  }

  /**
   *
   * @param values Valores para an�lise.
   * @return retorna o primeiro objeto n�o nulo do array
   */
  @SafeVarargs
  public static <T> T coalesce(T... values) {
    for (T i : values)
      if (i != null) return i;
    return null;
  }

  /**
   * Calculates the average of the given BigDecimal values, ignoring nulls. Returns null if all values are null.
   *
   * @param values the BigDecimal values to calculate the average
   * @return the average of the non-null values or null if all are null
   */
  public static BigDecimal avg(BigDecimal... values) {
    if (values == null || values.length == 0) {
      return null;
    }

    BigDecimal sum = BigDecimal.ZERO;
    int count = 0;

    for (BigDecimal value : values) {
      if (value != null) {
        sum = sum.add(value);
        count++;
      }
    }

    return count == 0 ? null : sum.divide(BigDecimal.valueOf(count), BigDecimal.ROUND_HALF_UP);
  }

  /**
   * Calculates the average of the given Double values, ignoring nulls. Returns null if all values are null.
   *
   * @param values the Double values to calculate the average
   * @return the average of the non-null values or null if all are null
   */
  public static Double avg(Double... values) {
    if (values == null || values.length == 0) {
      return null;
    }

    double sum = 0.0;
    int count = 0;

    for (Double value : values) {
      if (value != null) {
        sum += value;
        count++;
      }
    }

    return count == 0 ? null : sum / count;
  }

  /**
   * Calculates the average of the given Integer values, ignoring nulls. Returns null if all values are null.
   *
   * @param values the Integer values to calculate the average
   * @return the average of the non-null values or null if all are null
   */
  public static Integer avg(Integer... values) {
    if (values == null || values.length == 0) {
      return null;
    }

    int sum = 0;
    int count = 0;

    for (Integer value : values) {
      if (value != null) {
        sum += value;
        count++;
      }
    }

    return count == 0 ? null : sum / count;
  }

  /**
   * Calculates the average of the given Long values, ignoring nulls. Returns null if all values are null.
   *
   * @param values the Long values to calculate the average
   * @return the average of the non-null values or null if all are null
   */
  public static Long avg(Long... values) {
    if (values == null || values.length == 0) {
      return null;
    }

    long sum = 0L;
    int count = 0;

    for (Long value : values) {
      if (value != null) {
        sum += value;
        count++;
      }
    }

    return count == 0 ? null : sum / count;
  }

  /**
   * Calculates the average of the given Float values, ignoring nulls. Returns null if all values are null.
   *
   * @param values the Float values to calculate the average
   * @return the average of the non-null values or null if all are null
   */
  public static Float avg(Float... values) {
    if (values == null || values.length == 0) {
      return null;
    }

    float sum = 0f;
    int count = 0;

    for (Float value : values) {
      if (value != null) {
        sum += value;
        count++;
      }
    }

    return count == 0 ? null : sum / count;
  }

  /**
   * Calculates the average of the given Short values, ignoring nulls. Returns null if all values are null.
   *
   * @param values the Short values to calculate the average
   * @return the average of the non-null values or null if all are null
   */
  public static Short avg(Short... values) {
    if (values == null || values.length == 0) {
      return null;
    }

    int sum = 0;
    int count = 0;

    for (Short value : values) {
      if (value != null) {
        sum += value;
        count++;
      }
    }

    return count == 0 ? null : (short) (sum / count);
  }

  /**
   * Retorna o menor valor entre uma s�rie de BigDecimals passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o menor valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static BigDecimal min(BigDecimal... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .min(BigDecimal::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o menor valor entre uma s�rie de Doubles passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o menor valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Double min(Double... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .min(Double::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o menor valor entre uma s�rie de Integers passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o menor valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Integer min(Integer... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .min(Integer::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o menor valor entre uma s�rie de Longs passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o menor valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Long min(Long... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .min(Long::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o menor valor entre uma s�rie de Floats passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o menor valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Float min(Float... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .min(Float::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o menor valor entre uma s�rie de Shorts passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o menor valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Short min(Short... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .min(Short::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o maior valor entre uma s�rie de BigDecimals passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o maior valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static BigDecimal max(BigDecimal... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .max(BigDecimal::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o maior valor entre uma s�rie de Doubles passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o maior valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Double max(Double... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .max(Double::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o maior valor entre uma s�rie de Integers passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o maior valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Integer max(Integer... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .max(Integer::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o maior valor entre uma s�rie de Longs passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o maior valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Long max(Long... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .max(Long::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o maior valor entre uma s�rie de Floats passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o maior valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Float max(Float... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .max(Float::compareTo)
        .orElse(null);
  }

  /**
   * Retorna o maior valor entre uma s�rie de Shorts passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem comparados.
   * @return o maior valor n�o nulo ou nulo se todos os valores forem nulos.
   */
  public static Short max(Short... values) {
    return Arrays.stream(values)
        .filter(value -> value != null)
        .max(Short::compareTo)
        .orElse(null);
  }

  /**
   * Retorna a soma de uma s�rie de BigDecimals passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem somados.
   * @return a soma dos valores n�o nulos ou nulo se todos os valores forem nulos.
   */
  public static BigDecimal sum(BigDecimal... values) {
    return Arrays.stream(values)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Retorna a soma de uma s�rie de Doubles passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem somados.
   * @return a soma dos valores n�o nulos ou nulo se todos os valores forem nulos.
   */
  public static Double sum(Double... values) {
    return Arrays.stream(values)
        .filter(Objects::nonNull)
        .reduce(0.0, Double::sum);
  }

  /**
   * Retorna a soma de uma s�rie de Integers passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem somados.
   * @return a soma dos valores n�o nulos ou nulo se todos os valores forem nulos.
   */
  public static Integer sum(Integer... values) {
    return Arrays.stream(values)
        .filter(Objects::nonNull)
        .reduce(0, Integer::sum);
  }

  /**
   * Retorna a soma de uma s�rie de Longs passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem somados.
   * @return a soma dos valores n�o nulos ou nulo se todos os valores forem nulos.
   */
  public static Long sum(Long... values) {
    return Arrays.stream(values)
        .filter(Objects::nonNull)
        .reduce(0L, Long::sum);
  }

  /**
   * Retorna a soma de uma s�rie de Floats passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem somados.
   * @return a soma dos valores n�o nulos ou nulo se todos os valores forem nulos.
   */
  public static Float sum(Float... values) {
    return Arrays.stream(values)
        .filter(Objects::nonNull)
        .reduce(0.0f, Float::sum);
  }

  /**
   * Retorna a soma de uma s�rie de Shorts passados como argumento, ignorando valores nulos. Se todos os valores forem nulos, retorna nulo.
   *
   * @param values os valores a serem somados.
   * @return a soma dos valores n�o nulos ou nulo se todos os valores forem nulos.
   */
  public static Short sum(Short... values) {
    return Arrays.stream(values)
        .filter(Objects::nonNull)
        .reduce((short) 0, (a, b) -> (short) (a + b));
  }

  /**
   * Valida se a cole��o n�o � nula nem vazia. Caso a valida��o falhe uma {@link RFWCriticalException} � lan�ada.
   *
   * @param <T>
   * @param list Cole��o a ser validada.
   * @throws RFWValidationException
   */
  public static <T> void requiredNonNullNorEmpty(Collection<T> list) throws RFWValidationException {
    if (list == null) {
      throw new RFWValidationException("A lista n�o pode ser nula ou vazia!");
    } else if (list.size() == 0) {
      throw new RFWValidationException("A lista n�o pode ser vazia!");
    }
  }

  /**
   * Valida se a cole��o n�o � nula nem vazia. Caso a valida��o falhe uma {@link RFWCriticalException} � lan�ada.
   *
   * @param <T>
   * @param list Cole��o a ser validada.
   * @param message Mensagem para a Exception.
   * @throws RFWValidationException
   */
  public static <T> void requiredNonNullNorEmpty(Collection<T> list, String message) throws RFWValidationException {
    if (list == null || list.size() == 0) {
      throw new RFWValidationException(message);
    }
  }

  /**
   * Valida se a cole��o n�o � nula nem vazia. Caso a valida��o falhe uma {@link RFWCriticalException} � lan�ada.
   *
   * @param <T>
   * @param list Cole��o a ser validada.
   * @throws RFWCriticalException
   */
  public static <T> void requiredNonNullNorEmptyCritical(Collection<T> list) throws RFWCriticalException {
    if (list == null) {
      throw new RFWCriticalException("A lista n�o pode ser nula ou vazia!");
    } else if (list.size() == 0) {
      throw new RFWCriticalException("A lista n�o pode ser vazia!");
    }
  }

  /**
   * Valida se a cole��o n�o � nula nem vazia. Caso a valida��o falhe uma {@link RFWCriticalException} � lan�ada.
   *
   * @param <T>
   * @param list Cole��o a ser validada.
   * @param message Mensagem para a Exception.
   * @throws RFWCriticalException
   */
  public static <T> void requiredNonNullNorEmptyCritical(Collection<T> list, String message) throws RFWCriticalException {
    if (list == null || list.size() == 0) {
      throw new RFWCriticalException(message);
    }
  }

  /**
   * Requer que os objetos A e B sejam iguais. Testa o equals com null safe (ambos nulos s�o considerados iguais).
   *
   * @param a Objeto A para compara��o
   * @param b Objeto B para compara��o
   * @throws RFWValidationException Lan�ado caso os objetos n�o satisfa��o o equals.
   */
  public static void requiredEquals(Object a, Object b) throws RFWValidationException {
    if (!Objects.equals(a, b)) {
      throw new RFWValidationException("RFW_000013");
    }
  }

  /**
   * Requer que os objetos A e B sejam iguais. Testa o equals com null safe (ambos nulos s�o considerados iguais).
   *
   * @param a Objeto A para compara��o
   * @param b Objeto B para compara��o
   * @param message Mensagem/C�digo de Erro personalizado para a exce��o.
   * @throws RFWValidationException Lan�ado caso os objetos n�o satisfa��o o equals.
   */
  public static void requiredEquals(Object a, Object b, String message) throws RFWException {
    if (!Objects.equals(a, b)) {
      throw new RFWValidationException(message);
    }
  }

  /**
   * Requer que os objetos A e B sejam iguais. Testa o equals com null safe (ambos nulos s�o considerados iguais).
   *
   * @param a Objeto A para compara��o
   * @param b Objeto B para compara��o
   * @param message Mensagem/C�digo de Erro personalizado para a exce��o.
   * @throws RFWValidationException Lan�ado caso os objetos n�o satisfa��o o equals.
   */
  public static void requiredEqualsCritical(Object a, Object b, String message) throws RFWException {
    if (!Objects.equals(a, b)) {
      throw new RFWCriticalException(message);
    }
  }

  /**
   * Tenta converter uma String para {@link Integer} utilizando o construtor Integer(String), mas salvo de {@link NullPointerException}.
   *
   * @param value Valor em String para tentar converter para o Integer
   * @return nulo se receber o valor nulo, objeto {@link Integer} com o valor da {@link String}.
   */
  public static Integer toInteger(String value) {
    if (value == null) return null;
    return new Integer(value);
  }

  /**
   * Tenta converter uma String para {@link BigDecimal} utilizando o construtor BigDecimal(String), mas salvo de {@link NullPointerException}.
   *
   * @param value Valor em String para tentar converter para o {@link BigDecimal}
   * @return nulo se receber o valor nulo, objeto Integer com o valor da {@link String}.
   */
  public static BigDecimal toBigDecimal(String value) {
    if (value == null) return null;
    return new BigDecimal(value);
  }

  /**
   * Tenta converter uma String para {@link BigDecimal} utilizando o construtor BigDecimal(String), mas salvo de {@link NullPointerException}.<br>
   * Em seguida, for�a o tamanho do Scale utilizando o {@link RFW#getRoundingMode()} para arredondamento.
   *
   * @param value Valor em String para tentar converter para o {@link BigDecimal}
   * @param newScale Quantidade de casas decimais que o BigDecimal deve conter.
   * @return nulo se receber o valor nulo, objeto Integer com o valor da {@link String}.
   */
  public static BigDecimal toBigDecimal(String value, int newScale) {
    if (value == null) return null;
    return new BigDecimal(value).setScale(newScale, RFW.getRoundingMode());
  }

  /**
   * Tenta converter uma String para {@link Long} utilizando o construtor Long(String), mas salvo de {@link NullPointerException}.
   *
   * @param value Valor em String para tentar converter para o Long
   * @return nulo se receber o valor nulo, objeto {@link Long} com o valor da {@link String}.
   */
  public static Long toLong(String value) {
    if (value == null) return null;
    return new Long(value);
  }

  /**
   * Tenta converter uma String para {@link Double} utilizando o construtor Double(String), mas salvo de {@link NullPointerException}.
   *
   * @param value Valor em String para tentar converter para o Double
   * @return nulo se receber o valor nulo, objeto {@link Double} com o valor da {@link String}.
   */
  public static Double toDouble(String value) {
    if (value == null) return null;
    return new Double(value);
  }

  /**
   * Valida se o conte�do n�o � nulo e se satisfaz a express�o regular passada. Caso contr�io emite uma {@link RFWValidationException} com a mensagem passada.
   *
   * @param value Valor em String para tentar validar com a Express�o Regular
   * @param regExp Express�o regulara para valida��o
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   * @throws RFWException
   */
  public static void requiredNonNullMatch(String value, String regExp) throws RFWException {
    requiredNonNullMatch(value, regExp, "RFW_000057");
  }

  /**
   * Valida se o conte�do n�o � nulo e se satisfaz a express�o regular passada. Caso contr�io emite uma {@link RFWValidationException} com a mensagem passada.
   *
   * @param value Valor em String para tentar validar com a Express�o Regular
   * @param regExp Express�o regulara para valida��o
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   * @throws RFWException
   */
  public static void requiredNonNullMatch(String value, String regExp, String msg) throws RFWException {
    requiredNonNull(value, msg);
    try {
      value.matches(regExp);
    } catch (Exception e) {
      throw new RFWValidationException(msg, new String[] { value, regExp }, e);
    }
  }

  /**
   * Valida se o conte�do satisfaz a express�o regular passada. Caso contr�io emite uma {@link RFWValidationException} com a mensagem passada. <Br>
   * Se o conte�do for nulo, apenas ignora.
   *
   * @param value Valor em String para tentar validar com a Express�o Regular
   * @param regExp Express�o regulara para valida��o
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   * @throws RFWException
   */
  public static void requiredMatch(String value, String regExp) throws RFWException {
    if (value == null) return;
    requiredMatch(value, regExp, "RFW_000057");
  }

  /**
   * Valida se o conte�do satisfaz a express�o regular passada. Caso contr�io emite uma {@link RFWValidationException} com a mensagem passada. <Br>
   * Se o conte�do for nulo, apenas ignora.
   *
   * @param value Valor em String para tentar validar com a Express�o Regular
   * @param regExp Express�o regulara para valida��o
   * @param msg Mensagem/C�digo da Exception a ser colocado na Exception
   * @throws RFWException
   */
  public static void requiredMatch(String value, String regExp, String msg) throws RFWException {
    if (value == null) return;
    requiredNonNull(value, msg);
    try {
      value.matches(regExp);
    } catch (Exception e) {
      throw new RFWValidationException(msg, new String[] { value, regExp }, e);
    }
  }
}
