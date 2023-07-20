package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Annotation usada para definir um atributo do tipo String que funcionar� como um n�mero de telefone no padr�o RFWDeprec: "DDI|DDD|Phone".<BR>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (04/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaStringPhoneField {

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
   * Indica se o atributo aceita apenas form�tos conhecidos ou aceita qualquer string como telefone.<br>
   * Na pr�tica faz com que o RFWValidator n�o falhe caso o m�todo {@link RFWPhoneDataFormatter#getPhoneType(String)} retorne UNKNOW, e avisa aos desenvolvedores que o n�mero do telefone est� fora do formato do RFWDeprec, o que pode causar falhar em campos, relat�rios, etc. ao ser utilizado.
   */
  boolean acceptUnknowFormats() default false;

}
