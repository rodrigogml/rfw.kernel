package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Meta Annotation usada para estabelecer um conjundo de validações que só são válidas baseadas com uma pré-condição de valores de outras variáveis.<BR>
 * Esta classe é utilizada apenas para permitir que sejam usadas uma coleção da classe {@link RFWMetaAttributeRelation} quando necessário, montrando um array diretamente.
 *
 *
 * @author Rodrigo Leit?o
 * @since 7.1.0 (17/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RFWMetaAttributeRelationArray {

  RFWMetaAttributeRelation[] value();

}