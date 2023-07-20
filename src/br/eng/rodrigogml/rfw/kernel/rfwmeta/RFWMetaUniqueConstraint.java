package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Annotation usada para definir constraints de Unicidade entre atributos de um VO.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (16/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RFWMetaUniqueConstraint {

  /**
   * Define o conjunto de campos que tevem ter valor �nico.
   */
  String[] fields();

}
