package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Esta annotation permite uma valida��o simples de objetos de cole��o.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (18/03/2016)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaCollectionField {

  /**
   * Define o nome do atributo/campo. Este nome � usado para facilitar mensagens de erros, valida��es, em UIs, etc.<br>
   * N�o utilize ":" no final ou outras formata��es espec�ficas do local de uso. Aqui deve ser definido apenas o nome, como "Caixa", "Nome do Usu�rio", etc.
   */
  String caption();

  /**
   * Define se o atributo � obrigat�rio ou n�o na entidade. Ser obrigat�rio basta ser diferente de null.
   */
  boolean required();

  /**
   * Define a quantidade m�nima de elementos dentro da colection. Este atributo s� � validado se o objeto for diferente de nulo. Neste caso combine com o {@link #required()}
   */
  int minSize() default -1;

  /**
   * Define a tabela que armazena a lista de itens da cole��o.
   */
  String table();

  /**
   * Define o nome da coluna que armazena o valor desta field.
   *
   * @return
   */
  String column();

  /**
   * Define o nome da coluna da tabela onde ser� salvo o �ndice de ordena��o dos itens da cole��o.
   */
  String sortColumn() default "";

  /**
   * Define o nome da coluna onde ser� salvo a chave da hash, caso a colection esteja em uma Hash.
   */
  String keyColumn() default "";

  /**
   * Define a classe do {@link RFWDAOConverterInterface} que ser� utilizado.<Br>
   * Se n�o for uma classe do tipo {@link RFWDAOConverterInterface} � considerado como valor null do atributo
   */
  Class<?> keyConverterClass() default Object.class;

  /**
   * Define o tamanho m�ximo da chave do Map.<br>
   * <b>Este atrubuto � analizado apenas quando o tipo do atributo � um MAP e as chaves s�o do tipo String.</b>
   */
  int maxLenghtKey() default -1;

  /**
   * Define o tamanho m�ximo de caracteres dos valores da cole��o.<br>
   * Valido para os tipos:
   * <li>String
   */
  int maxLenght() default -1;

  /**
   * Coluna onde ser� colocado o ID do objeto pai para criar o relacionamento.
   */
  String fkColumn();

  /**
   * Define se dentro da lista os itens podem se repetir. O valor padr�o � 'true' (n�o permite valores repetidos).<br>
   * Para Hashs o valor acaba sendo true, pois as "Set" s� permitem 1 item com a mesma chave.<br>
   * ATEN��O: ESTE ATRIBUTO N�O EST� SENDO VALIDADO PELO VALIDATOR AINDA!!!!
   */
  boolean uniqueValues() default true;

  /**
   * Define a classe alvo da Lista. Ao indicar a classe neste atributo os objetos ser�o convertidos para este objeto ao serem recuperados/persistidos na base de dados.<br>
   */
  Class<?> targetRelationship();
}
