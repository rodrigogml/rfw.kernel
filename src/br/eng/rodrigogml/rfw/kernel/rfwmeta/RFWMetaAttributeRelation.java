package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: RFWDeprec Annotation usada para definir uma constraint de rela��o entre dois atributos da entidade.<BR>
 * <ul>
 * <li>As compara��es de equal e notequal consideram iguais quando ambos s�o nulos, e diferente caso 1 seja nulo e outro n�o.</li>
 * <li>As compara��es de grandeza (<, <=, > e >=) retornam true caso um dos atributos seja nulo. Pois n�o � poss�vel compara-lo.</li>
 * </ul>
 * <br>
 * <b>A exce��o ser� lan�ada caso a rela��o n�o seja v�lida, isto �, seja falsa!</b>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (17/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(RFWMetaAttributeRelationArray.class)
public @interface RFWMetaAttributeRelation {

  public static enum COMPAREOPERATION {
    EQUAL, NOTEQUAL, LESSTHAN, LESSOREQUALTHAN, MORETHAN, MOREOREQUALTHAN,
  }

  /**
   * Define o atributo da classe a ser comparado.
   */
  String attribute();

  /**
   * Segundo atributo com qual o primeiro ser� comparado.
   */
  String attribute2();

  /**
   * Opera��o de compara��o a ser realizada entre os atributos definidos.
   */
  COMPAREOPERATION operation();

  /**
   * C�digo da exce��o a ser lan�ada caso a opera��o de compara��o n�o se satisfa�a. Caso n�o seja informado um c�digo espec�fico a valida��o padr�o ser� lan�ada.
   */
  String exceptioncode();

}
