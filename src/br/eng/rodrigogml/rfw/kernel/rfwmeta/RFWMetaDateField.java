package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess.PreProcessOption;

/**
 * Description: Annotation usada para definir um atributo do tipo Date.<BR>
 * ATEN��O: Essa tag automaticamente define a resulu��o por padr�o � apenas de DIA. Para incluir horas, minutos, segundos, etc., configure a resolu��o.
 *
 * @author Davy Monteoliva
 * @since 7.1.0 (20/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaDateField {

  public static enum DateResolution {
    MILLISECONDS, SECOND, MINUTE, HOUR, DAY, MONTH, YEAR;
  }

  /**
   * Define o nome da coluna na tabela do banco de dados. Caso deixado em branco, ser� utilizado o nome da propriedade.
   */
  String column() default "";

  /**
   * Define o nome do atributo/campo. Este nome � usado para facilitar mensagens de erros, valida��es, em UIs, etc.<br>
   * N�o utilize ":" no final ou outras formata��es espec�ficas do local de uso. Aqui deve ser definido apenas o nome, como "Caixa", "Nome do Usu�rio", etc.
   */
  String caption();

  /**
   * Define se o atributo � obrigat�rio ou n�o na entidade.
   */
  boolean required();

  /**
   * Define se o atributo � �nico.
   */
  boolean unique() default false;

  /**
   * Define a data m�xima. <br>
   * Formato: yyyyMMddHHmmssSSSS<br>
   * <li>yyyy = ano com quatro d�gitos
   * <li>MM = m�s com dois d�gitos
   * <li>dd = dia com dois d�gitos
   * <li>HH = hora com dois d�gitos
   * <li>mm = minuto com dois d�gitos
   * <li>ss = segundo com dois d�gitos
   * <li>SSSS = milisegundo com quatro d�gitos
   */
  String maxValue() default "";

  /**
   * Define a data m�nima. <br>
   * Formato: yyyyMMddHHmmssSSSS<br>
   * <li>yyyy = ano com quatro d�gitos
   * <li>MM = m�s com dois d�gitos
   * <li>dd = dia com dois d�gitos
   * <li>HH = hora com dois d�gitos
   * <li>mm = minuto com dois d�gitos
   * <li>ss = segundo com dois d�gitos
   * <li>SSSS = milisegundo com quatro d�gitos
   */
  String minValue() default "";

  /**
   * Define a resolu��o do campo Date.
   */
  DateResolution resolution() default DateResolution.DAY;

  /**
   * Define um Pr� Processamento a ser feito no atributo antes das persist�ncias do objeto.
   */
  PreProcessOption preProcess() default PreProcessOption.NONE;

}
