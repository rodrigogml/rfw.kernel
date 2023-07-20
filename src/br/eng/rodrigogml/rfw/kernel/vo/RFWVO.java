package br.eng.rodrigogml.rfw.kernel.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.utils.RURecursiveClone;
import br.eng.rodrigogml.rfw.kernel.utils.RUReflex;

/**
 * Description: Classe pai de todos os Value Objects do sistema. Permitindo assim que m�todos consigam operar genericamente em muitos casos.<br>
 *
 * @author Rodrigo Leit�o
 * @since 3.0.0 (SET / 2009)
 */
public abstract class RFWVO implements RFWRecursiveClonable, Serializable, Cloneable {

  private static final long serialVersionUID = 1207774479606774431L;

  /**
   * Este atributo indica se o objeto foi totalmente do banco de dados (com o m�todo {@link RFWDAO#findForFullUpdate}). Permitindo que o objeto seja atualizado com o m�todo {@link RFWDAO#update(RFWVO)}.<br>
   * Caso este atributo esteja como false o m�todo lan�arpa uma exception avisando que o objeto usado n�o est� completo para edi��o. Evitando assim que um update de um objeto errado fa�a altera��es irrevers�veis aos dados do sistema.<br>
   * <b>ATEN��O: N�o altere o valor deste atributo manualmente, ele � gerenciado pelo {@link RFWDAO}.</b>
   *
   * @deprecated N�O DEVE SER UTILIZADO EM <B><U>NENHUM</U></B> PONTO DO SISTEMA PELO USU�RIO! EM HIP�TESE ALGUMA FAZER O OVERRIDE! ESTE ATRIBUTO DEVE SER UTILIZADO APENAS NO {@link RFWDAO}
   */
  @Deprecated
  private boolean _fullLoaded = false; // Come�a com _ para ficar no topo quando inspecionamos o VO no debug e n�o misturar com os outros atributos

  /**
   * Este atributo permite que um VO seja inserido no banco de dados for�ando a defini��o do ID do objeto.<br>
   * Esta op��o s� tem a finalidade de permitir a migra��o de dados de outros sitemas, permitindo que os objetos utilizem os IDs de identifica��o j� utilizados. Tamb�m s� � recomendado o uso em uma base de dados "limpa" para evitar conflitos de IDs com objetos que j� existam no banco. <Br>
   * <Br>
   * Note que alguns dialetos de bando de dados, como o DerbyDB, nunca inserem o atributo ID no statement de insert uma vez que ele seja gerado automaticamente. Para esses casos esse atributo n�o far� nenhuma diferen�a exceto nas valida��es.<br>
   * <b>Aten��o:</b> Quando um objeto tem ID, mas tem essa flag definida como true, indica que o objeto n�o est� persistido no banco de dados. Caso o objeto seja inserido, esta flag deve ser definida como false pelo {@link RFWDAO}.
   *
   * @deprecated N�O DEVE SER UTILIZADO PELO SISTEMA. Este recurso � �til em casos muito espec�ficos, como migra��o de dados, nunca para as rotinas do sistema.
   */
  @Deprecated
  private boolean _insertWithID = false; // Come�a com _ para ficar no topo quando inspecionamos o VO no debug e n�o misturar com os outros atributos

  public RFWVO() {
  }

  public RFWVO(Long id) {
    this.id = id;
  }

  /**
   * ID do Objeto. Usado sempre como PK no banco de dados.
   */
  private Long id = null;

  /**
   * # iD do Objeto. Usado sempre como PK no banco de dados.
   *
   * @return the iD do Objeto
   */
  public Long getId() {
    return id;
  }

  /**
   * # iD do Objeto. Usado sempre como PK no banco de dados.
   *
   * @param id the new iD do Objeto
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Implementa��o de um clone gen�rico para todos os VOs do Framework.<br>
   * Duplica o objeto e todos os objetos n�o mut�veis que tenham m�todos get e set.<br>
   * Para uma clonagem mais espec�fica extender este m�todo em cada VO
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Este m�todo n�o deve ser sobrescrito, e serve apenas para ser evocado quando no in�cio de um output dos VOs.<br>
   * Caso j� tenha iniciado, usar o m�todo <code>{@link #printMySelf(StringBuilder, List)}</code>
   *
   * @return String com o conte�do do objeto.
   */
  public final String printMySelf() {
    StringBuilder buff = new StringBuilder();
    printMySelf(buff, new ArrayList<>());
    return buff.toString();
  }

  /**
   * Este m�todo pode (e deve em muitos casos) ser sobrescrito pelas classes filhas para otimizar o "output" do objeto para efeitos de Debug.<br>
   * VOs que devem sobrescrever este m�todo s�o basicamente os m�todos que tem atributos como List ou Map, cujo print padr�o n�o consegue descobrir as classes gen�ricas implementadas e imprimir o objeto padr�o. Para simplificar o output dos objetos cheque os m�todos usados no print padr�o e n�o use seus pr�prios m�todos para n�o haver diferen�a de padr�es de sa�da.
   *
   * @param buff
   * @param printedobjects
   */
  public void printMySelf(StringBuilder buff, List<Object> printedobjects) {
    RUReflex.printObject(this, getClass(), buff, printedobjects);
  }

  /**
   * M�todo respons�vel por clonar o RFWVO inteiro evitando que suas refer�ncias sejam alteradas.<br>
   *
   * @return Objeto clonado recursivamente.
   * @throws RFWException
   */
  @Override
  public RFWVO cloneRecursive() throws RFWException {
    return cloneRecursive(new HashMap<RFWRecursiveClonable, RFWRecursiveClonable>());
  }

  @Override
  public RFWVO cloneRecursive(HashMap<RFWRecursiveClonable, RFWRecursiveClonable> clonedobjects) throws RFWException {
    return (RFWVO) RURecursiveClone.cloneRecursive(this, clonedobjects);
  }

  /**
   * Este atributo indica se o objeto foi totalmente carregado do banco de dados (com o m�todo {@link RFWDAO#findForUpdate(Long, String[])}). Permitindo que o objeto seja atualizado com o m�todo {@link RFWDAO#persist(RFWVO)}.<br>
   * Caso este atributo esteja como false o m�todo lan�arpa uma exception avisando que o objeto usado n�o est� completo para edi��o. Evitando assim que um update de um objeto errado fa�a altera��es irrevers�veis aos dados do sistema.<br>
   * <b>ATEN��O: N�o altere o valor deste atributo manualmente, ele � gerenciado pelo {@link RFWDAO}.</b>
   *
   * @deprecated N�O DEVE SER UTILIZADO EM <B><U>NENHUM</U></B> PONTO DO SISTEMA PELO USU�RIO! EM HIP�TESE ALGUMA FAZER O OVERRIDE! ESTE ATRIBUTO DEVE SER UTILIZADO APENAS NO {@link RFWDAO}
   * @return true se o objeto foi completamente carregado no banco de dados, false caso contr�rio.
   */
  @Deprecated
  public boolean isFullLoaded() {
    return _fullLoaded;
  }

  /**
   * Este atributo indica se o objeto foi totalmente do banco de dados (com o m�todo {@link RFWDAO#findForUpdate(Long, String[])}). Permitindo que o objeto seja atualizado com o m�todo {@link RFWDAO#persist(RFWVO)}.<br>
   * Caso este atributo esteja como false o m�todo lan�arpa uma exception avisando que o objeto usado n�o est� completo para edi��o. Evitando assim que um update de um objeto errado fa�a altera��es irrevers�veis aos dados do sistema.<br>
   * <b>ATEN��O: N�o altere o valor deste atributo manualmente, ele � gerenciado pelo {@link RFWDAO}.</b>
   *
   * @deprecated N�O DEVE SER UTILIZADO EM <B><U>NENHUM</U></B> PONTO DO SISTEMA PELO USU�RIO! EM HIP�TESE ALGUMA FAZER O OVERRIDE! ESTE ATRIBUTO DEVE SER UTILIZADO APENAS NO {@link RFWDAO}
   * @param fullLoaded indicador se o objeto foi inteiramente carregado no banco de dados.
   */
  @Deprecated
  public void setFullLoaded(boolean fullLoaded) {
    this._fullLoaded = fullLoaded;
  }

  /**
   * Implementa��o padr�o do HashCode. Pode ser substituida em cada VO
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /**
   * N�O MUDE A IMPLEMENTA��O DO EQUALS!!!!<br>
   * <br>
   *
   * O m�todo de equals deve utilizar o equals padr�o da classe Object por v�rias quest�es do framework.<br>
   * Uma das necessidades � que o {@link RFWDAO} utiliza um cache para saber se j� persistiu determinado objeto (quando est� investigando cadeias circulares de refer�ncia ds objetos) e n�o ficar em loop persistindo o mesmo objeto.<br>
   * <br>
   * Uma das necessidades de implementar um equals diferente (que comparasse IDs ao inv�s do objeto na mem�ria), era necess�ria para o funcionamento do TreeGrid do Vaadin, que utiliza o equals para comparar os Lazy Objects na hora de expandir e reorganizar.<br>
   * <br>
   * Para resolver esse e outros problemas que dependam de um equals baseado no ID e n�o na inst�ncia do objeto, verifique a solu��o dada pelo {@link GVO}.
   *
   */
  @Override
  public final boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**
   * Este atributo permite que um VO seja inserido no banco de dados for�ando a defini��o do ID do objeto.<br>
   * Esta op��o s� tem a finalidade de permitir a migra��o de dados de outros sitemas, permitindo que os objetos utilizem os IDs de identifica��o j� utilizados. Tamb�m s� � recomendado o uso em uma base de dados "limpa" para evitar conflitos de IDs com objetos que j� existam no banco. <Br>
   * <Br>
   * Note que alguns dialetos de bando de dados, como o DerbyDB, nunca inserem o atributo ID no statement de insert uma vez que ele seja gerado automaticamente. Para esses casos esse atributo n�o far� nenhuma diferen�a exceto nas valida��es.<br>
   * <b>Aten��o:</b> Quando um objeto tem ID, mas tem essa flag definida como true, indica que o objeto n�o est� persistido no banco de dados. Caso o objeto seja inserido, esta flag deve ser definida como false pelo {@link RFWDAO}.
   *
   * @deprecated N�O DEVE SER UTILIZADO PELO SISTEMA. Este recurso � �til em casos muito espec�ficos, como migra��o de dados, nunca para as rotinas do sistema.
   * @return se o objeto � uma inser��o com ID.
   */
  @Deprecated
  public boolean isInsertWithID() {
    return _insertWithID;
  }

  /**
   * Este atributo permite que um VO seja inserido no banco de dados for�ando a defini��o do ID do objeto.<br>
   * Esta op��o s� tem a finalidade de permitir a migra��o de dados de outros sitemas, permitindo que os objetos utilizem os IDs de identifica��o j� utilizados. Tamb�m s� � recomendado o uso em uma base de dados "limpa" para evitar conflitos de IDs com objetos que j� existam no banco. <Br>
   * <Br>
   * Note que alguns dialetos de bando de dados, como o DerbyDB, nunca inserem o atributo ID no statement de insert uma vez que ele seja gerado automaticamente. Para esses casos esse atributo n�o far� nenhuma diferen�a exceto nas valida��es.<br>
   * <b>Aten��o:</b> Quando um objeto tem ID, mas tem essa flag definida como true, indica que o objeto n�o est� persistido no banco de dados. Caso o objeto seja inserido, esta flag deve ser definida como false pelo {@link RFWDAO}.
   *
   * @deprecated N�O DEVE SER UTILIZADO PELO SISTEMA. Este recurso � �til em casos muito espec�ficos, como migra��o de dados, nunca para as rotinas do sistema.
   * @param insertWithID define se este objeto deve ser inserido com um ID predefinido.
   */
  @Deprecated
  public void setInsertWithID(boolean insertWithID) {
    this._insertWithID = insertWithID;
  }

}
