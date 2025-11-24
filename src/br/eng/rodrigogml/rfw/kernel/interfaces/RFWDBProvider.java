package br.eng.rodrigogml.rfw.kernel.interfaces;

import java.util.List;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.vo.RFWMO;
import br.eng.rodrigogml.rfw.kernel.vo.RFWOrderBy;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Esta interface provê acesso aos dados da aplicação para componentes que precisam.<br>
 * A implementação desta classe deve fornecer os dados conforme solicitados para que os componentes funcionem conforme esperado.<br>
 * Os métodos desta classe são desenvolvidos na mesma assinatura utilizada no módulo RFW ORM para fácil integração.
 *
 * @author Rodrigo GML
 * @since 1.0.0 (29 de jul. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public interface RFWDBProvider {

  /**
   * Este método deve ser implementado pela classe da aplicação e retornar os IDs dos objetos conforme os parâmetros passados.
   *
   * @param voClass Class do Objeto a ser retornado.
   * @param mo Filtro dos objetos.
   * @param orderBy Definição da coluna para ordenar os objetos.
   * @return Lista dos IDs conforme a ordem solicitada.
   */
  <VO extends RFWVO> List<Long> findIDs(Class<VO> voClass, RFWMO mo, RFWOrderBy orderBy) throws RFWException;

  /**
   * Este método deve ser implementado pela classe da aplicação e retornar os IDs dos objetos conforme os parâmetros passados.
   *
   * @param voClass Class do Objeto a ser retornado.
   * @param mo Filtro dos objetos.
   * @param orderBy Definição da coluna para ordenar os objetos.
   * @param offset Quantidade de itens da lista a serem "pulados"
   * @param limit Limite de itens que devem ser retornados.
   * @return Lista dos IDs conforme a ordem solicitada.
   */
  <VO extends RFWVO> List<Long> findIDs(Class<VO> voClass, RFWMO mo, RFWOrderBy orderBy, Integer offset, Integer limit) throws RFWException;

  /**
   * Este método deve ser implementado pela classe da aplicação e retornar os objetos conforme os parâmetros passados.
   *
   * @param voClass Class do Objeto a ser retornado.
   * @param mo Filtro dos objetos.
   * @param orderBy Definição da coluna para ordenar os objetos.
   * @param attributes Array com os nomes/caminhos dos atributos que precisam estar presentes nos objetos.
   * @param offset Quantidade de itens da lista a serem "pulados"
   * @param limit Limite de itens que devem ser retornados.
   * @return Lista em ordem com dos objetos conforme solicitado.
   */
  <VO extends RFWVO> List<VO> findList(Class<VO> voClass, RFWMO mo, RFWOrderBy orderBy, String[] attributes, Integer offset, Integer limit) throws RFWException;

  /**
   * Método usado para buscar um objeto pelo seu ID no banco de dados sem depender de interfaces de "Bridge".
   *
   * @param voClass Classe da entidade sendo procurada
   * @param id ID do objeto a ser recuperado.
   * @param attributes Array com os nomes/caminhos dos atributos que precisam estar presentes nos objetos.
   * @return Lista com os objetos encontrados, lista vazia ou nula caso nada seja encontrado.
   * @throws RFWException
   */
  <VO extends RFWVO> RFWVO findByID(Class<? extends RFWVO> voClass, Long id, String[] attributes) throws RFWException;

  /**
   * Método usado para buscar um objeto no banco de dados sem depender de interfaces de "Bridge".
   *
   * @param voClass Classe da entidade sendo procurada
   * @param mo MatchObject para filtrar os dados
   * @param attributes Array com os nomes/caminhos dos atributos que precisam estar presentes nos objetos.
   * @return Lista com os objetos encontrados, lista vazia ou nula caso nada seja encontrado.
   * @throws RFWException
   */
  <VO extends RFWVO> RFWVO findUniqueMatch(Class<? extends RFWVO> voClass, RFWMO mo, String[] attributes) throws RFWException;

}
