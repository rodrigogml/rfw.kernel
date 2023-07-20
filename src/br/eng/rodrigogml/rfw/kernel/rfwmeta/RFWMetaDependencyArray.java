package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Permite o agrupamento de {@link RFWMetaUsedBy}.<br>
 * Esta classe � utilizada apenas para permitir que sejam usadas uma cole��o da classe {@link RFWMetaDependencyArray} quando necess�rio, montrando um array diretamente.
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (16/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RFWMetaDependencyArray {

  RFWMetaDependency[] value();

}
