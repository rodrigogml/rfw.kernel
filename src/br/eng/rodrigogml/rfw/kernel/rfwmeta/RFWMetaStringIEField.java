package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess.PreProcessOption;

/**
 * Description: Annotation usada para definir um atributo do tipo String que funcionar� como um IE.<BR>
 * O campo IE ser� uma String apenas com os d�gitos, sem nenhum tipo de formata��o.
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (04/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaStringIEField {

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
   * Define a UF da IE. Se deixar como uma String vazia indica que pode ser uma IE de qualquer estado.
   */
  String uf() default "";

  /**
   * Define o atributo do mesmo VO que cont�m a UF desta IE. O atributo pode ser indicado de forma encadeada com separa��o de pontos.<br>
   * O Atributo apontado, se for uma String, deve conter a UF com duas letras.
   */
  String uffield() default "";

  /**
   * Define o padr�o de pr�-processamento a ser aplicado na String antes da valida��o.<br>
   * Note que o preprocessamento � aplicado na ordem que for definido!
   */
  PreProcessOption[] preProcess() default { PreProcessOption.NONE };
}
