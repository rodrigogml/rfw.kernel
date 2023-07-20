package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Annotation usada para definit um atributo do tipo Integer.<BR>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (03/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaIntegerField {

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
   * Define o valor m�ximo que pode ser definido no atributo.
   */
  int maxvalue() default Integer.MAX_VALUE;

  /**
   * Define o valor m�nimo aceito no atributo.
   */
  int minvalue() default Integer.MIN_VALUE;

}
