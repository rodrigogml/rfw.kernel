package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Permite o agrupamento de {@link RFWMetaUsedBy}.<br>
 * Esta classe é utilizada apenas para permitir que sejam usadas uma coleção da classe {@link RFWMetaUsedBy} quando necessário, montrando um array diretamente.
 *
 * @author Rodrigo Leitão
 * @since 7.1.0 (16/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RFWMetaUsedByArray {

  RFWMetaUsedBy[] value();

}
