package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Annotation usada para definir um atributo do tipo Float.<BR>
 *
 * @author Rodrigo Leitão
 * @since (21 de ago. de 2024)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaFloatField {

  /**
   * Define o nome da coluna na tabela do banco de dados. Caso deixado em branco, será utilizado o nome da propriedade.
   */
  String column() default "";

  /**
   * Define o nome do atributo/campo. Este nome é usado para facilitar mensagens de erros, validações, em UIs, etc.<br>
   * Não utilize ":" no final ou outras formatações específicas do local de uso. Aqui deve ser definido apenas o nome, como "Caixa", "Nome do Usuário", etc.
   */
  String caption();

  /**
   * Define se o atributo é obrigatório ou não na entidade.
   */
  boolean required();

  /**
   * Define se o atributo é único.
   */
  boolean unique() default false;

  /**
   * Valor máximo aceito pelo atributo.
   */
  double maxValue() default Float.MAX_VALUE;

  /**
   * Valor mínimo aceito pelo atributo.
   */
  double minValue() default Float.MIN_VALUE;

}
