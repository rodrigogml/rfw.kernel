package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Esta annotation permite uma valida��o simples, orientada ao pr�prio 'Object', para campos anotados por interfaces muito gen�ricas que dificultam a valida��o por conta do polimorfismo.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (18/02/2016)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaGenericField {

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

}
