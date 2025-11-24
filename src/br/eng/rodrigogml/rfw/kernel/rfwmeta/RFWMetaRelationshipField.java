package br.eng.rodrigogml.rfw.kernel.rfwmeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Annotation usada para definir um atributo de associação com outro VO que extenda RFWVO.<BR>
 *
 * @author Rodrigo Leitão
 * @since 7.1.0 (04/07/2015)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RFWMetaRelationshipField {

  public static enum RelationshipTypes {

    /**
     * Representa uma associação simples entre dois objetos, onde ambos existem de forma independente e apenas possuem um vínculo lógico entre si. Nenhum dos objetos depende do outro para existir.
     *
     * <p>
     * Exemplo: Pessoas e Revistas, relacionadas por uma estrutura de assinantes/assinaturas.
     * </p>
     *
     * <b>Notas do RFWValidator:</b>
     * <ul>
     * <li>Esse tipo de relacionamento exige que o objeto filho já possua um ID definido, pois são entidades independentes e o vínculo ocorre apenas entre objetos já existentes.</li>
     * </ul>
     */
    ASSOCIATION,

    /**
     * Representa o mesmo tipo de relacionamento de {@link #ASSOCIATION}, com a diferença de que, neste caso, o RFWDAO não atualiza automaticamente as associações removidas ou adicionadas ao persistir o objeto atual.
     *
     * <p>
     * No relacionamento {@code ASSOCIATION}, o RFWDAO atualiza a relação conforme alterações são feitas no objeto principal. Já em {@code WEAK_ASSOCIATION}, essas associações não serão atualizadas a partir deste objeto, devendo ser manipuladas apenas pela entidade "contraparte".
     * </p>
     *
     * <p>
     * Esse mapeamento é útil quando existe um objeto principal e outros secundários relacionados a ele, mas, da perspectiva do objeto principal, essas associações não são relevantes para manutenção, consistência ou persistência.
     * </p>
     *
     * <p>
     * Por que manter essa associação aqui então? Porque ela ainda é útil para filtros, consultas, relatórios ou navegação de dados, mesmo que não seja usada para manutenção das relações no banco.
     * </p>
     *
     * <p>
     * Esse relacionamento não será retornado pelo método {@code findForUpdate()} e será ignorado pelos métodos de persistência.
     * </p>
     */
    WEAK_ASSOCIATION,

    /**
     * Representa uma relação de composição, onde o objeto filho existe exclusivamente em função do objeto pai. O objeto filho complementa o pai, e não pode existir isolado ou ser reaproveitado por outro objeto.
     *
     * <p>
     * Caso o objeto pai deixe de existir, todos os filhos deixam de existir também.
     * </p>
     *
     * <b>Notas do RFWValidator:</b>
     * <ul>
     * <li>Um objeto filho só pode ter ID definido se o objeto pai também tiver ID definido.</li>
     * <li>Se o pai for novo (a ser inserido), o filho também deve ser novo.</li>
     * </ul>
     */
    COMPOSITION,

    /**
     * Igual à {@link #COMPOSITION}, porém aplicada a objetos que possuem relação hierárquica consigo mesmos.
     *
     * <p>
     * Esse tipo de composição indica que um objeto contém outros do mesmo tipo, formando uma estrutura de árvore. Exemplo: hierarquia organizacional.
     * </p>
     *
     * <p>
     * Esse tipo de relacionamento exige tratamento especial, pois pode haver composições encadeadas potencialmente infinitas.
     * </p>
     */
    COMPOSITION_TREE,

    /**
     * Indica uma associação em que o objeto relacionado é o "pai" deste objeto. Isso significa que este objeto compõe o objeto pai, sendo parte obrigatória de sua composição.
     *
     * <p>
     * O objeto relacionado aqui deve obrigatoriamente possuir uma referência deste tipo marcada como {@link RelationshipTypes#COMPOSITION} no objeto pai.
     * </p>
     *
     * <b>Notas do RFWValidator:</b>
     * <ul>
     * <li>Esse relacionamento é validado da mesma forma que uma {@link #ASSOCIATION}.</li>
     * </ul>
     *
     * <b>Notas do RFWDAO:</b>
     * <ul>
     * <li>Esse relacionamento é carregado automaticamente durante o método {@code findForFullUpdate}, permitindo detectar ausência de objetos que devem ser removidos no banco de dados.</li>
     * </ul>
     */
    PARENT_ASSOCIATION,

    /**
     * Similar a {@link #PARENT_ASSOCIATION}, mas aplicado a relacionamentos internos onde o objeto associado será inserido ou atualizado juntamente com o objeto principal, sem ser necessariamente o "pai" hierárquico.
     *
     * <p>
     * Objetos deste tipo podem precisar ser inseridos em duas etapas: primeiro o objeto associado é salvo para gerar seu ID, e depois o objeto principal é atualizado para registrar a referência.
     * </p>
     *
     * <p>
     * Isso implica que, exceto quando o objeto associado está hierarquicamente acima, a coluna de FK no banco de dados não pode ser {@code NOT NULL}, pois a primeira etapa da inserção falharia.
     * </p>
     *
     * <p>
     * A FK deve ser sempre configurada como {@code ON DELETE CASCADE} ou {@code ON DELETE SET NULL}, permitindo a exclusão do objeto associado sem violação de integridade referencial.
     * </p>
     */
    INNER_ASSOCIATION,

    /**
     * Representa uma associação "muitos para muitos" (N:N), em que os objetos são independentes, e a ligação entre eles é representada por uma tabela associativa.
     *
     * <b>Notas do RFWValidator:</b>
     * <ul>
     * <li>Requer que os objetos filhos já tenham ID definido, uma vez que ambos os lados da relação são independentes.</li>
     * </ul>
     *
     * <b>Notas do RFWDAO:</b>
     * <ul>
     * <li>Esse relacionamento é carregado automaticamente no {@code findForFullUpdate}, permitindo detectar elementos removidos para exclusão adequada no banco.</li>
     * </ul>
     */
    MANY_TO_MANY,
  }

  /**
   * Nome da tabela de "join" para os relacionamentos de Many_To_Many.
   */
  String joinTable()

  default "";

  /**
   * Coluna do ID do objeto relacionado. Deve ser preenchido caso a FK fique na tabela deste objeto.<br>
   * Se {@link RelationshipTypes#MANY_TO_MANY}: deve ser informado a coluna da tabela de join em que está o ID deste objeto.<br>
   * Se {@link RelationshipTypes#PARENT_ASSOCIATION}: obrigatório com o nome da coluna com a FK da tabela do objeto pai.<br>
   * Se {@link RelationshipTypes#ASSOCIATION} : preenchido somente se a coluna estiver na tabela deste objeto. Caso contrário, veja {@link #columnMapped()}.<br>
   * Se {@link RelationshipTypes#COMPOSITION} : quase nunca utilizado, já que o objeto filho quem costuma carregar o ID deste objeto. No entanto, em relacionamentos 1:1 raros pode ser utilizado quando o objeto pai que tem o ID do objeto filho.<br>
   * Se {@link RelationshipTypes#COMPOSITION_TREE} : nunca utilizado, já que é sempre o objeto filho quem carrega o ID deste objeto.<br>
   * Se {@link RelationshipTypes#INNER_ASSOCIATION} : preenchido somente se a coluna estiver na tabela deste objeto. Caso contrário, veja {@link #columnMapped()}.<br>
   * Se {@link RelationshipTypes#WEAK_ASSOCIATION} : preenchido somente se a coluna estiver na tabela deste objeto. Caso contrário, veja {@link #columnMapped()}.<br>
   *
   */
  String column()

  default "";

  /**
   * Coluna com a FK quando na tabela da contra-parte do relacionamento.<br>
   * Se {@link RelationshipTypes#MANY_TO_MANY}: Nome da coluna na tabela de join, com o ID do "outro" objeto no relacionamento.<Br>
   * Se {@link RelationshipTypes#PARENT_ASSOCIATION} : Nunca utilizado já que o coluna com o ID do pai deve estar na nossa tabela e definido em {@link #column()}<br>
   * Se {@link RelationshipTypes#ASSOCIATION} : Nome da coluna, quando a FK está na tabela do outro objeto.<br>
   * Se {@link RelationshipTypes#COMPOSITION} : Indica o nome da coluna da tabela filha que contém o nosso ID para associação.<br>
   * Se {@link RelationshipTypes#COMPOSITION_TREE} : Indica o nome da coluna da tabela filha que contém o nosso ID para associação.<br>
   * Se {@link RelationshipTypes#INNER_ASSOCIATION} : Nome da coluna, quando a FK está na tabela do outro objeto.<br>
   * Se {@link RelationshipTypes#WEAK_ASSOCIATION} : Nome da coluna, quando a FK está na tabela do outro objeto.<br>
   *
   * @return valor definido.
   */
  String columnMapped()

  default "";

  /**
   * Define o nome do atributo/campo. Este nome é usado para facilitar mensagens de erros, validações, em UIs, etc.<br>
   * Não utilize ":" no final ou outras formatações específicas do local de uso. Aqui deve ser definido apenas o nome, como "Caixa", "Nome do Usuário", etc.
   *
   * @return valor definido.
   */
  String caption();

  /**
   * Define se o atributo é obrigatório ou não na entidade.
   *
   * @return valor definido.
   */
  boolean required();

  /**
   * Define se o atributo é único.
   *
   * @return valor definido.
   */
  boolean unique()

  default false;

  /**
   * Define se o objeto associado já deve existir no banco de dados. Quando true, indica que o objeto associado neste atributo já existe no banco de dados antes da eixstência deste objeto. Em outras palavras, caso true, o VO associado deve ter um ID definido.
   *
   * @return valor definido.
   */
  RelationshipTypes relationship();

  /**
   * Define a classe alvo do relacionamento. Ao indicar a classe neste atributo qualquer objeto associado ao atributo deverá ser igual ou herdeirada classe indicada.<br>
   * <ul>
   * <li>RFWValidator: Utiliza este argumento para validar os objetos quando em uma {@link List} ou {@link HashMap}.</li>
   * </ul>
   *
   * @return valor definido.
   */
  Class<? extends RFWVO> targetRelationship()

  default RFWVO.class;

  /**
   * Para relacionamentos dentro de uma {@link List} ou {@link HashMap} este atributo pode ser usado para validar a quantidade mínima que a coleção deve conter.<br>
   * Lembrando que o atributo {@link #required()} simplesmente valida se não é nulo, não se a coleção está vazia.
   *
   * @return valor definido.
   */
  int minSize()

  default -1;

  /**
   * Para relacionamentos dentro de uma {@link List} ou {@link HashMap} este atributo pode ser usado para validar a quantidade máxima que a coleção deve conter.<br>
   * Lembrando que o atributo {@link #required()} simplesmente valida se não é nulo, não se a coleção está vazia.
   *
   * @return valor definido.
   */
  int maxSize()

  default Integer.MAX_VALUE;

  /**
   * Define o nome do atributo do objeto destino que é utilizado para <b>colocar o objeto dentro da Hash</b>. Este atributo só é utilizado quando a coleção do mapeamento é uma Hash.<br>
   * ATENÇÃO, NÃO SUPORTA PROPRIEDADE ANINHADAS!!! O sistema falha se tentar utilizar propriedades aninhadas para a chave da Map, isso pq os sub-objetos ainda não terão sido montados quando precisamos colocar o objeto na Hash.
   *
   * @return valor definido.
   */
  String keyMap()

  default "";

  /**
   * Em caso de mapeamento em uma lista, permite informar o nome da coluna que contém o índice de ordem do objeto. Neste atributo será persistido o index da ordem que estava na lista, e na recuperação do objeto será montado na mesma ordem.<br>
   * <b>Atenção, esta coluna não deve ser um atributo no objeto filho!</b>
   *
   * @return valor definido.
   */
  String sortColumn() default "";
}
