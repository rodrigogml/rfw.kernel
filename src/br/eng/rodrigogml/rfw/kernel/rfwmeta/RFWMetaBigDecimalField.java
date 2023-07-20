package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Annotation usada para definir um atributo do tipo BigDecimal.<BR>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (17/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaBigDecimalField {

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
   * Define o valor m�ximo aceito pelo atributo.<br>
   * Escrever o valor em String, no formato aceido pelo BigDecimal. N�o usamos ponto flutuante (como Double ou Float) pela falta de precis�o.
   */
  String maxValue() default "";

  /**
   * Define o valor m�nimo aceito pelo atributo.<br>
   * Escrever o valor em String, no formato aceido pelo BigDecimal. N�o usamos ponto flutuante (como Double ou Float) pela falta de precis�o.
   */
  String minValue() default "";

  /**
   * Define o tamanho da escala/precis�o que este BigDecimal deve ter.
   */
  int scale() default -1;

  /**
   * Define o tamanho m�ximo da escala/precis�o que este BigDecimal pode ter.
   */
  int scaleMax() default -1;

  /**
   * Define se o valor deve sempre ser absoluto, isto �, se deve apenar ser positivo.
   */
  boolean absolute() default false;
}
