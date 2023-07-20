package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess.PreProcessOption;

/**
 * Description: Annotation usada para definir um atributo do tipo String que funcionar� como um Email.<BR>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (13/08/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaStringEmailField {

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
   * Define o padr�o de pr�-processamento a ser aplicado na String antes da valida��o.<br>
   * Note que o preprocessamento � aplicado na ordem que for definido!
   */
  PreProcessOption[] preProcess() default { PreProcessOption.NONE };

  /**
   * Define se o padr�o do endere�o de e-mail segue a especifica��o do RFC822.<br>
   * Caso false o valor dever� ser sempre "s�" o endere�o de e-mail. Ex: nome@dominio.com.br<Br>
   * Caso true o valor poder� ser "s�" o endere�o e-mail, como em caso de falso, mas tamb�m aceita o formato:<br>
   * <li>"Nome e Sobrenome" <nome@dominio.com.br>
   */
  boolean useRFC822() default false;

}
