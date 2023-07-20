package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Meta Annotation para indica depend�ncia com outras classes.<br>
 * Ao anotar esta classe como dependente de outra, indicar� que as outras entidades n�o poder�o ser exclu�das enquanto esta � estiver usando. Note que al�m desta indica��o, � necess�rio que na outra entidade seja anotada a {@link RFWMetaUsedBy} pra garantir a valida��o da depend�ncia quando ele for manipulado.
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (16/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(RFWMetaDependencyArray.class)
public @interface RFWMetaDependency {

  /**
   * Define a classe da entidade da qual esta classe depende.
   */
  Class<? extends RFWVO> voClass();

  /**
   * Nome do atributo da classe que mant�m o relacionamento/uso desta esta classe.
   */
  String attribute();

}
