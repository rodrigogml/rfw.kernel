package br.eng.rodrigogml.rfw.kernel.dao.annotations.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Annotation usada para informar que a informa��o deve ficar criptografada no banco de dados.<br>
 *
 * @author Rodrigo Leit�o
 * @since 10.0.0 (12 de jul de 2018)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaEncrypt {

  /**
   * Define uma chave String para encriptar o conte�do no banco de dados. No VO o valor estar� sempre descodificado, por�m no banco evita que o conte�do fique a mostra. N�o � uma criptografia forte e � poss�vel de ser quebrada, mas requer algum esfor�o.<br>
   * ATEN��O: CUIDADO AO ALTERAR ESSE VALOR QUANDO J� EXISTEM DADOS NO BANCO DE DADOS, ELES PRECISAR�O SER REDEFINIDOS OU SER�O COMPLETAMENTE DESCONFIGURADOS.
   */
  String key() default "";
}
