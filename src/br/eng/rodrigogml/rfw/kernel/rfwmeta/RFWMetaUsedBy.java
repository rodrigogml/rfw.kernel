package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Meta Annotation para indicar quando uma classe � utilizada por outra de forma dependente.<br>
 * Ao indicar que uma classe � utilizada por outra, a outra classe dever� conter uma {@link RFWMetaDependency} para indiciar a forma da dependencia com esta classe.<br>
 * Esta indica��o s� deve serfeita nas seguitnes condi��es:
 * <ul>
 * <li>Caso este objeto n�o possa ser exclu�do quando algum objeto que depende deste impe�a a exclus�o, como ForeignKeys no banco sem</Li>
 * </ul>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (16/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(RFWMetaUsedByArray.class)
public @interface RFWMetaUsedBy {

  /**
   * Define a classe da entidade que utiliza esta classe.
   */
  Class<? extends RFWVO> voClass();

}
