package br.eng.rodrigogml.rfw.kernel.vo;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWRunTimeException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe pai de todos os Match Objects do sistema. Permitindo assim que m�todos consigam operar genericamente em muitos casos.<br>
 *
 * @author Rodrigo Leit�o
 * @since 3.0.0 (SET / 2009)
 * @version 7.1.0 (18/07/2015) - Rodrigo Leit�o - Substitui��o do sistema hierarquico de MOs {@link RFWMO} para este MO �nico.
 */
public final class RFWMO implements Serializable, Cloneable {

  private static final long serialVersionUID = 5581362952656098332L;

  /**
   * Classe interna usada para guardar os valores das condi��es de busca do RFWMO.
   */
  public static final class RFWMOData implements Serializable {
    private static final long serialVersionUID = -9013306659167465139L;

    final String fieldname;
    final Object value;

    public RFWMOData(String fieldname, Object value) {
      this.fieldname = fieldname;
      this.value = value;
    }

    /**
     * Gets the fieldname.
     *
     * @return the fieldname
     */
    public String getFieldname() {
      return fieldname;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public Object getValue() {
      return value;
    }

    @Override
    public String toString() {
      return this.fieldname + ": " + this.value;
    }

  }

  public static enum AppendMethod {
    AND, OR
  }

  /**
   * Define como os argumentos deste MO ser�o conectados entre si.<br>
   * Por padr�o os argumentos definidos recebem o conector condicional "AND" entre eles. Caso alterado, todos os atributos deste MO passar�o se ser conectados com o novo conector condicional.
   */
  private AppendMethod appendmethod = AppendMethod.AND;

  /**
   * Permite definir uma lista de MOs que montar�o uma condi��o "isolada". Em conjunto com as defini��es poss�veis em {@link #appendmethod} permite que condi��es com diferentes conectores sejam usadas em conjunto. Cria o efeito dos "parenteses" nas clausulas where do SQL.
   */
  private List<RFWMO> submo = null;

  private LinkedList<RFWMOData> equal = new LinkedList<>();
  private LinkedList<RFWMOData> notEqual = new LinkedList<>();
  private LinkedList<RFWMOData> greaterThanOrEqualTo = new LinkedList<>();
  private LinkedList<RFWMOData> greaterThan = new LinkedList<>();
  private LinkedList<RFWMOData> lessThanOrEqualTo = new LinkedList<>();
  private LinkedList<RFWMOData> lessThan = new LinkedList<>();
  private LinkedList<RFWMOData> isNull = new LinkedList<>();
  private LinkedList<RFWMOData> isNotNull = new LinkedList<>();
  private LinkedList<RFWMOData> like = new LinkedList<>();
  private LinkedList<RFWMOData> in = new LinkedList<>();
  private LinkedList<RFWMOData> notIn = new LinkedList<>();

  public RFWMO() {
  }

  public RFWMO(AppendMethod appendMethod) {
    this.appendmethod = appendMethod;
  }

  public RFWMO isNotNull(String fieldname) {
    this.isNotNull.add(new RFWMOData(fieldname, null));
    return this;
  }

  public RFWMO isNull(String fieldname) {
    this.isNull.add(new RFWMOData(fieldname, null));
    return this;
  }

  public RFWMO equal(String fieldname, Object value) {
    this.equal.add(new RFWMOData(fieldname, value));
    return this;
  }

  /**
   * Este m�todo simplifica a escrita de uma condi��o para buscar/selecionar um registro que retorne TRUE para uma sobreposi��o de valores (datas, ranges num�ricos, etc.). <br>
   * <br>
   * Simplismente escreve a seguinte condi��o: (fs == null || fs < ne) && (fe == null || fe > ns), onde:
   * <li>fs = Field Start (Coluna da tabela com o valor inicial do per�odo na base)
   * <li>fe = Field End (Coluna da tabela com o valor final do per�odo na base)
   * <li>ns = New Start (Valor inicial do per�odo que estamos querendo testar a sobreposi��o)
   * <li>ne = New End (Valod final do per�odo que estamos querendo testar a sobreposi��o) <br>
   * <br>
   * <b>Observa��o:</b> por escrever duas condi��es que obrigatoriamente precisam ser consideradas com o operador {@link AppendMethod#AND}, caso este MO esteja definido para outro {@link #appendmethod} que n�o {@link AppendMethod#AND} ser� automaticamente criado um novo {@link RFWMO} e colocado como um SubMO.<br>
   * <b>Obseva��o 2:</b> Os intervalos "s�o exclusivos", isto �, s�o utilizados os comparativos > e < e n�o >= e <=. Em outras palavras, mesmo que um periodo termine em uma data e o outro comece na mesma data, n�o � considerado sobreposi��o. <br>
   * <b>Observa��o 3:</b> Caso os valores das colunas estejam nulos � considerado que trata-se de valor "infinito". Isto �, se a coluna de in�cio do per�odo for nula � considerado que ela come�ou no "infinito incial", logo qualquer valor passado periodo informado ter� iniciado depois. O mesmo vale para a coluna de fim do per�odo, vindo valor nulo � considerado que o per�odo do banco n�o terminou
   * (final infinito), em outras palavras qualquer per�odo informado ter� terminado antes do per�odo da base.
   *
   * @param startFieldName Coluna que cont�m o valor do in�cio do per�odo na base de dados.
   * @param endFieldName Coluna que cont�m o valor do fim do per�odo na base de dados.
   * @param startValue Valor inicial do per�odo que ser� testado contra o per�odo da base. N�o pode ser nulo. Caso n�o tenha um valor inicial a busca pode ser realizada "for�ando" o menor valor poss�vel para o tipo de dado, algo como {@link Integer#MIN_VALUE} ou {@link LocalDate#MAX}.
   * @param endValue Valor final do per�odo que ser� testado contra o per�odo da base. N�o pode ser nulo. Caso n�o tenha um valor final a busca pode ser realizada "for�ando" o maior valor poss�vel para o tipo de dado, algo como {@link Integer#MAX_VALUE} ou {@link LocalDate#MIN}
   * @return Retorna esta inst�ncia do RFWMO.
   */
  public RFWMO overlap(String startFieldName, String endFieldName, Object startValue, Object endValue) {
    RFWMO mo = this;
    if (mo.getAppendmethod() != AppendMethod.AND) { // Observa��o 1
      mo = new RFWMO(AppendMethod.AND);
      this.getSubmo().add(mo);
    }
    {// (fs == null || fs < ne)
      RFWMO mo2 = new RFWMO(AppendMethod.OR);
      mo2.isNull(startFieldName); // fs == null
      mo2.lessThan(startFieldName, endValue); // fs < ne
      mo.getSubmo().add(mo2);
    }
    { // (fe == null || fe > ns)
      RFWMO mo2 = new RFWMO(AppendMethod.OR);
      mo2.isNull(endFieldName);
      mo2.greaterThan(endFieldName, startValue);
      mo.getSubmo().add(mo2);
    }
    return this;
  }

  public RFWMO like(String fieldname, String pattern) {
    this.like.add(new RFWMOData(fieldname, pattern));
    return this;
  }

  public RFWMO in(String fieldname, Collection<?> valuelist) {
    if (valuelist == null || valuelist.size() == 0) throw new RFWRunTimeException("N�o � permitido incluir uma lista sem valores no atributo IN do RFWMO!");
    this.in.add(new RFWMOData(fieldname, valuelist));
    return this;
  }

  public RFWMO in(String fieldname, Object[] valuelist) {
    List<Object> l = new LinkedList<>();
    for (Object t : valuelist) {
      l.add(t);
    }
    this.in.add(new RFWMOData(fieldname, l));
    return this;
  }

  public RFWMO notIn(String fieldname, Collection<?> valuelist) {
    if (valuelist == null || valuelist.size() == 0) throw new RFWRunTimeException("N�o � permitido incluir uma lista sem valores no atributo NOT IN do RFWMO!");
    this.notIn.add(new RFWMOData(fieldname, valuelist));
    return this;
  }

  public RFWMO notIn(String fieldname, Object[] valuelist) {
    List<Object> l = new LinkedList<>();
    for (Object t : valuelist) {
      l.add(t);
    }
    this.notIn.add(new RFWMOData(fieldname, l));
    return this;
  }

  public RFWMO notEqual(String fieldname, Object value) {
    this.notEqual.add(new RFWMOData(fieldname, value));
    return this;
  }

  public RFWMO greaterThanOrEqualTo(String fieldname, Object value) {
    this.greaterThanOrEqualTo.add(new RFWMOData(fieldname, value));
    return this;
  }

  public RFWMO greaterThan(String fieldname, Object value) {
    this.greaterThan.add(new RFWMOData(fieldname, value));
    return this;
  }

  public RFWMO lessThanOrEqualTo(String fieldname, Object value) {
    this.lessThanOrEqualTo.add(new RFWMOData(fieldname, value));
    return this;
  }

  public RFWMO lessThan(String fieldname, Object value) {
    this.lessThan.add(new RFWMOData(fieldname, value));
    return this;
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
   * M�todo respons�vel por clonar o RFWMO inteiro evitando que suas refer�ncias sejam alteradas.<br>
   * Usado, por exemplo, nas fachadas para evitar que o objeto seja alterado no CRUD e fique diferente caso Facade e Invoker estejam na mesma JVM.
   *
   * @return Retorna o RFWMO clonado recursivamente
   * @throws RFWException
   */
  public RFWMO cloneRecursive() throws RFWException {
    try {
      return cloneRecursive(new HashMap<RFWMO, RFWMO>());
    } catch (CloneNotSupportedException e) {
      throw new RFWCriticalException(e);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected RFWMO cloneRecursive(HashMap<RFWMO, RFWMO> clonedobjects) throws CloneNotSupportedException {
    // Veririca se j� estamos na hash, se estiver retornamos esse objeto
    RFWMO clonedvo = clonedobjects.get(this);
    if (clonedvo != null) {
      return clonedvo;
    }

    // Se ainda n�o estamos na hash de objetos clonados, nos clonamos e incluimos na hash
    clonedvo = (RFWMO) super.clone();
    clonedobjects.put(this, clonedvo);

    // Clona primeiro os LinkedList dos valores
    clonedvo.equal = (LinkedList<RFWMOData>) this.equal.clone();
    clonedvo.notEqual = (LinkedList<RFWMOData>) this.notEqual.clone();
    clonedvo.greaterThanOrEqualTo = (LinkedList<RFWMOData>) this.greaterThanOrEqualTo.clone();
    clonedvo.greaterThan = (LinkedList<RFWMOData>) this.greaterThan.clone();
    clonedvo.lessThanOrEqualTo = (LinkedList<RFWMOData>) this.lessThanOrEqualTo.clone();
    clonedvo.lessThan = (LinkedList<RFWMOData>) this.lessThan.clone();
    clonedvo.isNull = (LinkedList<RFWMOData>) this.isNull.clone();
    clonedvo.isNotNull = (LinkedList<RFWMOData>) this.isNotNull.clone();
    clonedvo.like = (LinkedList<RFWMOData>) this.like.clone();
    clonedvo.in = (LinkedList<RFWMOData>) this.in.clone();
    clonedvo.notIn = (LinkedList<RFWMOData>) this.notIn.clone();

    // Recupera a lista de m�todos desse objeto
    Method[] methods = this.getClass().getMethods();
    // Itera essa lista atr�s de m�todos do tipo "get" ou "is"
    for (int i = 0; i < methods.length; i++) {
      String methodname = methods[i].getName();
      if (methodname.startsWith("get") || methodname.startsWith("is")) {
        Method methodget = methods[i];
        Class<?> returntype = methodget.getReturnType();
        Method methodset = null;
        try {
          methodset = this.getClass().getMethod("set" + methodname.substring(3, methodname.length()), returntype);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
        // Verifica a exist�ncia do M�todo SET
        if (methodset != null) {
          // Verifica se o retorno do m�todo get n�o � nulo
          try {
            Object gettedvalue = methodget.invoke(this);
            if (gettedvalue != null) {
              // Verifica se o tipo de objeto � um dos que desejamos fazer o "deep clone"
              if (gettedvalue instanceof RFWMO) {
                RFWMO clonedvalue = ((RFWMO) gettedvalue).cloneRecursive(clonedobjects);
                methodset.invoke(clonedvo, clonedvalue);
              } else if (gettedvalue instanceof LinkedList) {
                LinkedList currentvalue = (LinkedList) gettedvalue;
                LinkedList clonedvalue = (LinkedList) currentvalue.clone();
                clonedvalue.clear();
                for (Object object : currentvalue) {
                  if (object instanceof RFWMO) {
                    clonedvalue.add(((RFWMO) object).cloneRecursive(clonedobjects));
                  } else {
                    clonedvalue.add(object);
                  }
                }
                methodset.invoke(clonedvo, clonedvalue);
              } else if (gettedvalue instanceof HashMap) {
                HashMap currentvalue = (HashMap) gettedvalue;
                HashMap clonedvalue = (HashMap) currentvalue.clone();
                clonedvalue.clear();
                for (Object key : currentvalue.keySet()) {
                  Object object = currentvalue.get(key);
                  if (object instanceof RFWMO) {
                    clonedvalue.put(key, ((RFWMO) object).cloneRecursive(clonedobjects));
                  } else {
                    clonedvalue.put(key, object);
                  }
                }
                methodset.invoke(clonedvo, clonedvalue);
              }
            }
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
    }

    return clonedvo;
  }

  /**
   * Recupera o define como os argumentos deste MO ser�o conectados entre si.<br>
   * Por padr�o os argumentos definidos recebem o conector condicional "AND" entre eles. Caso alterado, todos os atributos deste MO passar�o se ser conectados com o novo conector condicional.
   *
   * @return the define como os argumentos deste MO ser�o conectados entre si
   */
  public AppendMethod getAppendmethod() {
    return appendmethod;
  }

  /**
   * Define o define como os argumentos deste MO ser�o conectados entre si.<br>
   * Por padr�o os argumentos definidos recebem o conector condicional "AND" entre eles. Caso alterado, todos os atributos deste MO passar�o se ser conectados com o novo conector condicional.
   *
   * @param appendmethod the new define como os argumentos deste MO ser�o conectados entre si
   */
  public void setAppendmethod(AppendMethod appendmethod) {
    this.appendmethod = appendmethod;
  }

  /**
   * Recupera o permite definir uma lista de MOs que montar�o uma condi��o "isolada". Em conjunto com as defini��es poss�veis em {@link #appendmethod} permite que condi��es com diferentes conectores sejam usadas em conjunto. Cria o efeito dos "parenteses" nas clausulas where do SQL.
   *
   * @return the permite definir uma lista de MOs que montar�o uma condi��o "isolada"
   */
  public List<RFWMO> getSubmo() {
    if (submo == null) this.submo = new LinkedList<>();
    return submo;
  }

  /**
   * Define o permite definir uma lista de MOs que montar�o uma condi��o "isolada". Em conjunto com as defini��es poss�veis em {@link #appendmethod} permite que condi��es com diferentes conectores sejam usadas em conjunto. Cria o efeito dos "parenteses" nas clausulas where do SQL.
   *
   * @param submo the new permite definir uma lista de MOs que montar�o uma condi��o "isolada"
   */
  public void setSubmo(List<RFWMO> submo) {
    this.submo = submo;
  }

  /**
   * Gets the equal.
   *
   * @return the equal
   */
  public LinkedList<RFWMOData> getEqual() {
    return equal;
  }

  /**
   * Gets the not equal.
   *
   * @return the not equal
   */
  public LinkedList<RFWMOData> getNotEqual() {
    return notEqual;
  }

  /**
   * Gets the greater than or equal to.
   *
   * @return the greater than or equal to
   */
  public LinkedList<RFWMOData> getGreaterThanOrEqualTo() {
    return greaterThanOrEqualTo;
  }

  /**
   * Gets the greater than.
   *
   * @return the greater than
   */
  public LinkedList<RFWMOData> getGreaterThan() {
    return greaterThan;
  }

  /**
   * Gets the less than or equal to.
   *
   * @return the less than or equal to
   */
  public LinkedList<RFWMOData> getLessThanOrEqualTo() {
    return lessThanOrEqualTo;
  }

  /**
   * Gets the less than.
   *
   * @return the less than
   */
  public LinkedList<RFWMOData> getLessThan() {
    return lessThan;
  }

  /**
   * Gets the checa se null.
   *
   * @return the checa se null
   */
  public LinkedList<RFWMOData> getIsNull() {
    return isNull;
  }

  /**
   * Gets the checa se not null.
   *
   * @return the checa se not null
   */
  public LinkedList<RFWMOData> getIsNotNull() {
    return isNotNull;
  }

  /**
   * Gets the like.
   *
   * @return the like
   */
  public LinkedList<RFWMOData> getLike() {
    return like;
  }

  /**
   * Gets the in.
   *
   * @return the in
   */
  public LinkedList<RFWMOData> getIn() {
    return in;
  }

  /**
   * Gets the not in.
   *
   * @return the not in
   */
  public LinkedList<RFWMOData> getNotIn() {
    return notIn;
  }

  /**
   * Sets the equal.
   *
   * @param equal the new equal
   */
  protected final void setEqual(LinkedList<RFWMOData> equal) {
    this.equal = equal;
  }

  /**
   * Sets the not equal.
   *
   * @param notEqual the new not equal
   */
  protected final void setNotEqual(LinkedList<RFWMOData> notEqual) {
    this.notEqual = notEqual;
  }

  /**
   * Sets the greater than or equal to.
   *
   * @param greaterThanOrEqualTo the new greater than or equal to
   */
  protected final void setGreaterThanOrEqualTo(LinkedList<RFWMOData> greaterThanOrEqualTo) {
    this.greaterThanOrEqualTo = greaterThanOrEqualTo;
  }

  /**
   * Sets the greater than.
   *
   * @param greaterThan the new greater than
   */
  protected final void setGreaterThan(LinkedList<RFWMOData> greaterThan) {
    this.greaterThan = greaterThan;
  }

  /**
   * Sets the less than or equal to.
   *
   * @param lessThanOrEqualTo the new less than or equal to
   */
  protected final void setLessThanOrEqualTo(LinkedList<RFWMOData> lessThanOrEqualTo) {
    this.lessThanOrEqualTo = lessThanOrEqualTo;
  }

  /**
   * Sets the less than.
   *
   * @param lessThan the new less than
   */
  protected final void setLessThan(LinkedList<RFWMOData> lessThan) {
    this.lessThan = lessThan;
  }

  /**
   * Sets the checa se null.
   *
   * @param isNull the new checa se null
   */
  protected final void setIsNull(LinkedList<RFWMOData> isNull) {
    this.isNull = isNull;
  }

  /**
   * Sets the checa se not null.
   *
   * @param isNotNull the new checa se not null
   */
  protected final void setIsNotNull(LinkedList<RFWMOData> isNotNull) {
    this.isNotNull = isNotNull;
  }

  /**
   * Sets the like.
   *
   * @param like the new like
   */
  protected final void setLike(LinkedList<RFWMOData> like) {
    this.like = like;
  }

  /**
   * Sets the in.
   *
   * @param in the new in
   */
  protected final void setIn(LinkedList<RFWMOData> in) {
    this.in = in;
  }

  /**
   * Sets the not in.
   *
   * @param notIn the new not in
   */
  protected final void setNotIn(LinkedList<RFWMOData> notIn) {
    this.notIn = notIn;
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findEqual(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : equal) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findNotEqual(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : notEqual) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findGreaterThanOrEqualTo(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : greaterThanOrEqualTo) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findGreaterThan(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : greaterThan) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findLessThan(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : lessThan) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findLessThanOrEqualTo(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : lessThanOrEqualTo) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findIsNull(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : isNull) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findIsNotNull(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : isNotNull) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findLike(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : like) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findIn(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : in) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Procura os filtros de um determinado campo.
   *
   * @param fieldname Nome do Campo para procurar os filtros
   * @return Array com todos os valores definidos para o filtro.
   */
  public Object[] findNotIn(String fieldname) {
    final LinkedList<Object> found = new LinkedList<>();
    for (RFWMOData data : in) {
      if (fieldname.equals(data.getFieldname())) {
        found.add(data.value);
      }
    }
    return found.toArray(new Object[0]);
  }

  /**
   * Este m�todo imprime as condi��es deste MO como sendo o SQL. Usado apenas para DEBUG e registro do objeto
   *
   * @return String para visualiza��o com as condi��es definidas no RFWMO.
   */
  public String printConditions() {
    StringBuilder sb = new StringBuilder();
    for (RFWMOData d : equal) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append('=').append(d.value);
    }
    for (RFWMOData d : notEqual) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append("!=").append(d.value);
    }
    for (RFWMOData d : greaterThan) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append('>').append(d.value);
    }
    for (RFWMOData d : greaterThanOrEqualTo) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append(">=").append(d.value);
    }
    for (RFWMOData d : lessThan) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append('<').append(d.value);
    }
    for (RFWMOData d : lessThanOrEqualTo) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append("<=").append(d.value);
    }
    for (RFWMOData d : isNull) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append(" is null");
    }
    for (RFWMOData d : isNotNull) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append(" is not null");
    }
    for (RFWMOData d : like) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append(" like '").append(d.value).append("'");
    }
    for (RFWMOData d : in) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append(" in (").append(d.value).append(")");
    }
    for (RFWMOData d : notIn) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append(d.fieldname).append(" not in (").append(d.value).append(")");
    }
    for (RFWMO bMO : getSubmo()) {
      if (sb.length() > 0) sb.append(' ').append(this.getAppendmethod()).append(' ');
      sb.append("(").append(bMO.printConditions()).append(")");
    }
    return sb.toString();
  }

  /**
   * Sobreescrito para ajudar no DEBUG durante o desenvolvimento. N�o utilizar como parte do c�digo j� que sua implementa��o pode ser alterada. Para obter o "impress�o" das condi��es utilize o m�todo printConditions()
   */
  @Override
  public String toString() {
    return super.toString() + "/" + printConditions();
  }

  /**
   * Recupera a lista com todos os atributos usados no MO, incluindo dos SubMOs, caso existam.
   *
   * @return lista dos atributos.
   */
  public List<String> getAttributes() {
    final LinkedList<String> list = new LinkedList<>();

    for (RFWMOData d : equal)
      list.add(d.fieldname);
    for (RFWMOData d : notEqual)
      list.add(d.fieldname);
    for (RFWMOData d : greaterThan)
      list.add(d.fieldname);
    for (RFWMOData d : greaterThanOrEqualTo)
      list.add(d.fieldname);
    for (RFWMOData d : lessThan)
      list.add(d.fieldname);
    for (RFWMOData d : lessThanOrEqualTo)
      list.add(d.fieldname);
    for (RFWMOData d : isNull)
      list.add(d.fieldname);
    for (RFWMOData d : isNotNull)
      list.add(d.fieldname);
    for (RFWMOData d : like)
      list.add(d.fieldname);
    for (RFWMOData d : in)
      list.add(d.fieldname);
    for (RFWMOData d : notIn)
      list.add(d.fieldname);
    for (RFWMO bMO : getSubmo())
      list.addAll(bMO.getAttributes());

    return list;
  }

  /**
   * Total de condi��es que este MO cont�m.
   *
   * @return Retorna o total de "condi��es" que este MO cont�m. Incluindo as condi��es dos SubMOs.
   */
  public int size() {
    int size = 0;
    if (this.submo != null) for (RFWMO mo : this.submo) {
      size += mo.size();
    }
    return size + this.equal.size() + this.notEqual.size() + this.greaterThanOrEqualTo.size() + this.greaterThan.size() + this.lessThanOrEqualTo.size() + this.lessThan.size() + this.isNull.size() + this.isNotNull.size() + this.like.size() + this.in.size() + this.notIn.size();
  }

  /**
   * Permite criar um filtro que verifica se a data de hoje est� entre dois campos de datas do VO.<br>
   * Far� uma consulta similar �: <b>periodStartField <= Hoje && (periodEndField >= hoje || periodEndField is null)</b>
   *
   * @param periodStartField Campo com a data de in�cio do per�odo.
   * @param periodEndField Campin com a data de fim do per�odo.
   * @throws RFWException
   */
  public void periodHasNow(String periodStartField, String periodEndField) throws RFWException {
    final LocalDateTime now = RFW.getDateTime();
    periodHasFullPeriod(periodStartField, periodEndField, now, now);
  }

  /**
   * Filtra os registros cujo per�odo contenham outro per�odo completamente. Em outras palavras, o per�odo passado deve estar completamente dentro de um per�odo definido no registro (VO).<br>
   * Caso a data fim do per�odo do registro seja nula, � considerada como uma "data infinita" (um valor muito grande), fazendo com que nulo seja maior que qualquer data.<br>
   * Far� uma consulta similar �: <b>periodStartField <= periodStart && (periodEndField >= periodEnd || periodEndField is null)</b>
   *
   * @param periodStartField Campo com a data de in�cio do per�odo.
   * @param periodEndField Campo com a data de fim do per�odo.
   * @param periodStart Data de in�cio do per�odo. Inclusivo.
   * @param periodEnd Data de fim do per�odo. Inclusivo.
   * @throws RFWException
   */
  public void periodHasFullPeriod(String periodStartField, String periodEndField, LocalDate periodStart, LocalDate periodEnd) throws RFWException {
    periodHasFullPeriod(periodStartField, periodEndField, periodStart.atStartOfDay(), periodEnd.atStartOfDay());
  }

  /**
   * Filtra os registros cujo per�odo contenham outro per�odo completamente. Em outras palavras, o per�odo passado deve estar completamente dentro de um per�odo definido no registro (VO).<br>
   * Caso a data fim do per�odo do registro seja nula, � considerada como uma "data infinita" (um valor muito grande), fazendo com que nulo seja maior que qualquer data.<br>
   * Far� uma consulta similar �: <b>periodStartField <= date && (periodEndField >= date || periodEndField is null)</b>
   *
   * @param periodStartField Campo com a data de in�cio do per�odo.
   * @param periodEndField Campin com a data de fim do per�odo.
   * @param date Data que deve estar entre as datas.
   * @throws RFWException
   */
  public void periodHasDate(String periodStartField, String periodEndField, LocalDate date) throws RFWException {
    periodHasFullPeriod(periodStartField, periodEndField, date.atStartOfDay(), date.atStartOfDay());
  }

  /**
   * Filtra os registros cujo per�odo contenham outro per�odo completamente. Em outras palavras, o per�odo passado deve estar completamente dentro de um per�odo definido no registro (VO).<br>
   * Caso a data fim do per�odo do registro seja nula, � considerada como uma "data infinita" (um valor muito grande), fazendo com que nulo seja maior que qualquer data.<br>
   * Far� uma consulta similar �: <b>periodStartField <= periodStart && (periodEndField >= periodEnd || periodEndField is null)</b> Far� uma consulta similar �: <b>periodStartField <= dateTime && (periodEndField >= dateTime || periodEndField is null)</b>
   *
   * @param periodStartField Campo com a data de in�cio do per�odo.
   * @param periodEndField Campin com a data de fim do per�odo.
   * @param dateTime DateTime que deve estar entre as datas.
   * @throws RFWException
   */
  public void periodHasDate(String periodStartField, String periodEndField, LocalDateTime dateTime) throws RFWException {
    periodHasFullPeriod(periodStartField, periodEndField, dateTime, dateTime);
  }

  /**
   * Filtra os registros cujo per�odo contenham outro per�odo completamente. Em outras palavras, o per�odo passado deve estar completamente dentro de um per�odo definido no registro (VO).<br>
   * Caso a data fim do per�odo do registro seja nula, � considerada como uma "data infinita" (um valor muito grande), fazendo com que nulo seja maior que qualquer data.<br>
   * Far� uma consulta similar �: <b>periodStartField <= periodStart && (periodEndField >= periodEnd || periodEndField is null)</b> <br>
   * <b>Observa��o:</b> por escrever duas condi��es que obrigatoriamente precisam ser consideradas com o operador {@link AppendMethod#AND}, caso este MO esteja definido para outro {@link #appendmethod} que n�o {@link AppendMethod#AND} ser� automaticamente criado um novo {@link RFWMO} e colocado como um SubMO.<br>
   *
   * @param periodStartField Campo com a data de in�cio do per�odo.
   * @param periodEndField Campo com a data de fim do per�odo.
   * @param periodStart Data de in�cio do per�odo. Inclusivo.
   * @param periodEnd Data de fim do per�odo. Inclusivo.
   * @throws RFWException
   */
  public void periodHasFullPeriod(String periodStartField, String periodEndField, LocalDateTime periodStart, LocalDateTime periodEnd) throws RFWException {
    PreProcess.requiredNonNull(periodStartField);
    PreProcess.requiredNonNull(periodEndField);
    PreProcess.requiredNonNull(periodEnd);
    PreProcess.requiredNonNull(periodStart);
    if (periodEnd.compareTo(periodStart) < 0) throw new RFWValidationException("N�o h� um per�odo v�lido entre " + periodStart + " e " + periodEnd + "!");

    RFWMO mo = this;
    if (mo.getAppendmethod() != AppendMethod.AND) { // Observa��o 1
      mo = new RFWMO(AppendMethod.AND);
      this.getSubmo().add(mo);
    }

    mo.lessThanOrEqualTo(periodStartField, periodStart); // Filtra os registros que tenham iniciado antes do per�odo
    RFWMO mo2 = new RFWMO(AppendMethod.OR);
    mo2.isNull(periodEndField); // Filtra os objetos cuja da de fim n�o esteja definido ("v�lido para sempre")
    mo2.greaterThanOrEqualTo(periodEndField, periodEnd); // Ou que o per�odo de t�rmino da vig�ncia s� tenha ocorrido depois que o nosso per�odo tenha come�ado
    mo.getSubmo().add(mo2);
  }

  /**
   * Filtra os registros cujo per�odo interseccione outro per�odo. Em outras palavras, a intersec��o de um per�odo com outro deve ser maior que zero.<br>
   * Caso a data fim do per�odo do registro seja nula, � considerada como uma "data infinita" (um valor muito grande), fazendo com que nulo seja maior que qualquer data.<br>
   * Far� uma consulta similar �: <b>periodStartField <= periodEnd && (periodEndField >= periodStart || periodEndField is null)</b>
   *
   * @param periodStartField Campo com a data de in�cio do per�odo.
   * @param periodEndField Campo com a data de fim do per�odo.
   * @param periodStart Data de in�cio do per�odo. Inclusivo.
   * @param periodEnd Data de fim do per�odo. Inclusivo.
   * @throws RFWException
   */
  public void periodIntersectsAnotherPeriod(String periodStartField, String periodEndField, LocalDate periodStart, LocalDate periodEnd) throws RFWException {
    periodIntersectsAnotherPeriod(periodStartField, periodEndField, periodStart.atStartOfDay(), periodEnd.atStartOfDay());
  }

  /**
   * Filtra os registros cujo per�odo interseccione outro per�odo. Em outras palavras, a intersec��o de um per�odo com outro deve ser maior que zero.<br>
   * Caso a data fim do per�odo do registro seja nula, � considerada como uma "data infinita" (um valor muito grande), fazendo com que nulo seja maior que qualquer data.<br>
   * Far� uma consulta similar �: <b>periodStartField <= periodEnd && (periodEndField >= periodStart || periodEndField is null)</b> <br>
   * <b>Observa��o:</b> por escrever duas condi��es que obrigatoriamente precisam ser consideradas com o operador {@link AppendMethod#AND}, caso este MO esteja definido para outro {@link #appendmethod} que n�o {@link AppendMethod#AND} ser� automaticamente criado um novo {@link RFWMO} e colocado como um SubMO.<br>
   *
   * @param periodStartField Campo com a data de in�cio do per�odo.
   * @param periodEndField Campo com a data de fim do per�odo.
   * @param periodStart Data de in�cio do per�odo. Inclusivo.
   * @param periodEnd Data de fim do per�odo. Inclusivo.
   * @throws RFWException
   */
  public void periodIntersectsAnotherPeriod(String periodStartField, String periodEndField, LocalDateTime periodStart, LocalDateTime periodEnd) throws RFWException {
    PreProcess.requiredNonNull(periodStartField);
    PreProcess.requiredNonNull(periodEndField);
    PreProcess.requiredNonNull(periodEnd);
    PreProcess.requiredNonNull(periodStart);
    if (periodEnd.compareTo(periodStart) < 0) throw new RFWValidationException("N�o h� um per�odo v�lido entre " + periodStart + " e " + periodEnd + "!");

    RFWMO mo = this;
    if (mo.getAppendmethod() != AppendMethod.AND) { // Observa��o 1
      mo = new RFWMO(AppendMethod.AND);
      this.getSubmo().add(mo);
    }

    mo.lessThanOrEqualTo(periodStartField, periodEnd);
    RFWMO mo2 = new RFWMO(AppendMethod.OR);
    mo2.isNull(periodEndField); // Filtra os objetos cuja da de fim n�o esteja definido ("v�lido para sempre")
    mo2.greaterThanOrEqualTo(periodEndField, periodStart); // Ou que o per�odo de t�rmino da vig�ncia s� tenha ocorrido depois que o nosso per�odo tenha come�ado
    mo.getSubmo().add(mo2);
  }

  /**
   * Filetra os registros que tenham uma data dentro de um per�odo espec�fico.
   *
   * @param dateField Campo do VO que cont�m a data.
   * @param periodStart In�cio do Per�odo. InclusivoS.
   * @param periodEnd Fim do Per�odo. Inclusivo.
   * @throws RFWException
   */
  public void dateInPeriod(String dateField, LocalDate periodStart, LocalDate periodEnd) throws RFWException {
    dateInPeriod(dateField, periodStart.atStartOfDay(), periodEnd.atStartOfDay());
  }

  /**
   * Filetra os registros que tenham uma data dentro de um per�odo espec�fico.
   *
   * @param dateField Campo do VO que cont�m a data.
   * @param periodStart In�cio do Per�odo. InclusivoS.
   * @param periodEnd Fim do Per�odo. Inclusivo.
   * @throws RFWException
   */
  public void dateInPeriod(String dateField, LocalDateTime periodStart, LocalDateTime periodEnd) throws RFWException {
    PreProcess.requiredNonNull(periodEnd);
    PreProcess.requiredNonNull(periodStart);
    if (periodEnd.compareTo(periodStart) < 0) throw new RFWValidationException("N�o h� um per�odo v�lido entre " + periodStart + " e " + periodEnd + "!");

    this.greaterThanOrEqualTo(dateField, periodStart);
    this.lessThanOrEqualTo(dateField, periodEnd);
  }

}