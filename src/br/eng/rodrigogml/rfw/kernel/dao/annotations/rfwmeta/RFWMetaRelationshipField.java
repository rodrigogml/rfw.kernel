package br.eng.rodrigogml.rfw.kernel.dao.annotations.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Annotation usada para definir um atributo de associa��o com outro VO que extenda RFWVO.<BR>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (04/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaRelationshipField {

  public static enum RelationshipTypes {
    /**
     * Associa��o indica que os dois objetos no relacionamento podem se "ter um link" entre eles, mas que ambos existem independente do relacionamento. Ex: Pessoas e Revistas, associadas por um link de assinantes/assinaturas.<br>
     * <b>Notas do RFWValidator:</b>
     * <ul>
     * <li>Este relacionamento obrigar� que o filho j� tenha um ID definido. Afinal o relacionamento � entre dois objetos distintos sem depend�ncia de exist�ncia entre eles.</li>
     * </ul>
     */
    ASSOCIATION,
    /**
     * Estabelece exatamente o mesmo relacionamento que {@link #ASSOCIATION}. No entanto, quando � ASSOCIATION o RFWDAO ao persistir verifica as associa��es que sumiram ou novas criadas para persistir.<br>
     * Nesta WEAK_ASSOCIATION, indica que n�o vamos atualiar as associa��es entre os objetos a partir deste objeto. (Normalmente s� atrav�s da contra-parte).<br>
     * Este mapemanto � utilizad por exemplo quando tempos um Objeto principal, e outros secunr�dios que se ligam a este. Mas olhando da perspectiva do objeto principal as associa��es s�o indiferentes e n�o � interessante altera-las ou corrigi-las a partir do objeto principal.<br>
     * Ent�o fica a pergunta, e pra que fazer esta associa��o? Pq n�o deixar s� no objeto secund�rio? A associa��o a partir do objeto principal pode ser �til para filtrar dados, pois assim estabelece uma rela��o entre este objeto e o outro. Ou mesmo montagem de relat�rio.<br>
     * <br>
     * Note ent�o que este relacionamento n�o ser� retornado pelo "findForUpdate()" e ser� ignorado pelos m�todos de persist().
     */
    WEAK_ASSOCIATION,
    /**
     * Composi��o indica que os dois objetos se relacionam de maneira que um objeto "completa" o outro, isto �, um dos objetos s� existe para suprir dados ou informa��es de outro objetos. Neste tipo de relacionamento, o objeto do atributo n�o existe caso o pai deixe de existir. Consequ�ntemente um objeto filho n�o pode ser "reutilizado" por outro objeto pai.<Br>
     * <b>Notas do RFWValidator:</b>
     * <ul>
     * <li>Este relacionamento aceitar� que o objeto filho s� tenha ID definido caso o objeto pai j� tenha um ID definido. Caso o pai seja um objeto novo (para inser��o) o objeto filho dever� ser um objeto novo tamb�m.</li>
     * </ul>
     */
    COMPOSITION,
    /**
     * Similiar � {@value #COMPOSITION}, este atributo indica uma composi��o de um objeto para outro. A �nica diferen�a que a composi��o � com o pr�prio objeto.<br>
     * Esse tipo de composi��o indica que um objeto tem outros do mesmo tipo dentro dele mesmo. Como em uma organiza��o hierarquica (forma de �rvore).<br>
     * Esse tipo de composi��o precisa de tratamento especial pois pode ter "infitas" composi��es de objeto.
     */
    COMPOSITION_TREE,
    /**
     * Associa��o do Pai indica que o objeto associado no relacionamento � o "pai" deste relacionamento. Isto �, que este objeto � parte da composi��o do objeto pai. Obrigat�riamente o objeto relacionado aqui deve ter uma refer�ncia � este objeto do tipo {@link RelationshipTypes#COMPOSITION}.<br>
     * <b>Notas do RFWValidator:</b>
     * <ul>
     * <li>Este relacionamento ser� validado exatamente como uma ASSOCIATION.</li>
     * </ul>
     * <b>Notas do RFWDAO:</b>
     * <ul>
     * <li>Neste tipo de relacionamento o objeto ser� carregado automaticamente durante o m�todo findForFullUpdate, para que seja poss�vel detectar a ausencia de algum objeto e fazer a exclus�o no banco de dados.</li>
     * </ul>
     */
    PARENT_ASSOCIATION,
    /**
     * Similar ao PARENT_ASSOCIATION, este relacionamento permite que um objeto referencie outro que ser� adicionado/editado junto com o objeto principal, mas n�o necessariamente � o objeto pai deste objeto.<br>
     * Objetos com esse tipo de relacionamento podem ter que ser inseridos em duas etapas. Por exemplo, se o objeto associado ainda n�o tiver um ID, vamos inserir este objeto no banco, e posteriormente fazer um update para completar o ID da associa��o.<br>
     * ISSO QUER DIZER QUE, a n�o ser que o objeto associado esteja hierarquicamente acima deste, a coluna no banco de dados n�o pode estar definida como Not Null ou a primeira atapalha falhar�.<br>
     * Al�m disso, a FK deve ser sempre ON DELETE CASCADE ou ON DELETE SET NULL, para que exclus�o do objeto associado n�o seja barrada pelo banco de dados.
     */
    INNER_ASSOCIATION,
    /**
     * Uma associa��o "Muitos para Muitos" ou "N:N" � sempre do tipo associa��o, isto �, os objetos existem independentemente e � criado apenas uma associa��o entre eles.<br>
     * <b>Notas do RFWValidator:</b>
     * <ul>
     * <li>Este relacionamento obrigar� que os filhos j� tenham ID definido. Afinal o relacionamento � entre dois objetos distintos sem depend�ncia de exist�ncia entre eles.</li>
     * </ul>
     * <b>Notas do RFWDAO:</b>
     * <ul>
     * <li>Neste tipo de relacionamento os objetos ser�o carregados automaticamente durante o m�todo findForFullUpdate, para que seja poss�vel detectar a ausencia de algum objeto e fazer a exclus�o no banco de dados.</li>
     * </ul>
     */
    MANY_TO_MANY,
  }

  /**
   * Nome da tabela de "join" para os relacionamentos de Many_To_Many.
   */
  String joinTable() default "";

  /**
   * Coluna do ID do objeto relacionado. Deve ser preenchido caso a FK fique na tabela deste objeto.<br>
   * Se {@link RelationshipTypes#MANY_TO_MANY}: deve ser informado a coluna da tabela de join em que est� o ID deste objeto.<br>
   * Se {@link RelationshipTypes#PARENT_ASSOCIATION}: obrigat�rio com o nome da coluna com a FK da tabela do objeto pai.<br>
   * Se {@link RelationshipTypes#ASSOCIATION} : preenchido somente se a coluna estiver na tabela deste objeto. Caso contr�rio, veja {@link #columnMapped()}.<br>
   * Se {@link RelationshipTypes#COMPOSITION} : nunca utilizado, j� que � sempre o objeto filho quem carrega o ID deste objeto.<br>
   * Se {@link RelationshipTypes#COMPOSITION_TREE} : nunca utilizado, j� que � sempre o objeto filho quem carrega o ID deste objeto.<br>
   * Se {@link RelationshipTypes#INNER_ASSOCIATION} : preenchido somente se a coluna estiver na tabela deste objeto. Caso contr�rio, veja {@link #columnMapped()}.<br>
   * Se {@link RelationshipTypes#WEAK_ASSOCIATION} : preenchido somente se a coluna estiver na tabela deste objeto. Caso contr�rio, veja {@link #columnMapped()}.<br>
   *
   */
  String column() default "";

  /**
   * Coluna com a FK quando na tabela da contra-parte do relacionamento.<br>
   * Se {@link RelationshipTypes#MANY_TO_MANY}: Nome da coluna na tabela de join, com o ID do "outro" objeto no relacionamento.<Br>
   * Se {@link RelationshipTypes#PARENT_ASSOCIATION} : Nunca utilizado j� que o coluna com o ID do pai deve estar na nossa tabela e definido em {@link #column()}<br>
   * Se {@link RelationshipTypes#ASSOCIATION} : Nome da coluna, quando a FK est� na tabela do outro objeto.<br>
   * Se {@link RelationshipTypes#COMPOSITION} : Indica o nome da coluna da tabela filha que cont�m o nosso ID para associa��o.<br>
   * Se {@link RelationshipTypes#COMPOSITION_TREE} : Indica o nome da coluna da tabela filha que cont�m o nosso ID para associa��o.<br>
   * Se {@link RelationshipTypes#INNER_ASSOCIATION} : Nome da coluna, quando a FK est� na tabela do outro objeto.<br>
   * Se {@link RelationshipTypes#WEAK_ASSOCIATION} : Nome da coluna, quando a FK est� na tabela do outro objeto.<br>
   *
   * @return valor definido.
   */
  String columnMapped() default "";

  /**
   * Define o nome do atributo/campo. Este nome � usado para facilitar mensagens de erros, valida��es, em UIs, etc.<br>
   * N�o utilize ":" no final ou outras formata��es espec�ficas do local de uso. Aqui deve ser definido apenas o nome, como "Caixa", "Nome do Usu�rio", etc.
   *
   * @return valor definido.
   */
  String caption();

  /**
   * Define se o atributo � obrigat�rio ou n�o na entidade.
   *
   * @return valor definido.
   */
  boolean required();

  /**
   * Define se o atributo � �nico.
   *
   * @return valor definido.
   */
  boolean unique() default false;

  /**
   * Define se o objeto associado j� deve existir no banco de dados. Quando true, indica que o objeto associado neste atributo j� existe no banco de dados antes da eixst�ncia deste objeto. Em outras palavras, caso true, o VO associado deve ter um ID definido.
   *
   * @return valor definido.
   */
  RelationshipTypes relationship();

  /**
   * Define a classe alvo do relacionamento. Ao indicar a classe neste atributo qualquer objeto associado ao atributo dever� ser igual ou herdeirada classe indicada.<br>
   * <ul>
   * <li>RFWValidator: Utiliza este argumento para validar os objetos quando em uma {@link List} ou {@link HashMap}.</li>
   * </ul>
   *
   * @return valor definido.
   */
  Class<? extends RFWVO> targetRelationship() default RFWVO.class;

  /**
   * Para relacionamentos dentro de uma {@link List} ou {@link HashMap} este atributo pode ser usado para validar a quantidade m�nima que a cole��o deve conter.<br>
   * Lembrando que o atributo {@link #required()} simplesmente valida se n�o � nulo, n�o se a cole��o est� vazia.
   *
   * @return valor definido.
   */
  int minSize() default -1;

  /**
   * Define o nome do atributo do objeto destino que � utilizado para <b>colocar o objeto dentro da Hash</b>. Este atributo s� � utilizado quando a cole��o do mapeamento � uma Hash.<br>
   * ATEN��O, N�O SUPORTA PROPRIEDADE ANINHADAS!!! O sistema falha se tentar utilizar propriedades aninhadas para a chave da Map, isso pq os sub-objetos ainda n�o ter�o sido montados quando precisamos colocar o objeto na Hash.
   *
   * @return valor definido.
   */
  String keyMap() default "";

  /**
   * Em caso de mapeamento em uma lista, permite informar o nome da coluna que cont�m o �ndice de ordem do objeto. Neste atributo ser� persistido o index da ordem que estava na lista, e na recupera��o do objeto ser� montado na mesma ordem.<br>
   * <b>Aten��o, esta coluna n�o deve ser um atributo no objeto filho!</b>
   *
   * @return valor definido.
   */
  String sortColumn() default "";
}
