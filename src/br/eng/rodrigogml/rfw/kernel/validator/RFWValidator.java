package br.eng.rodrigogml.rfw.kernel.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.dataformatters.RFWCEPDataFormatter;
import br.eng.rodrigogml.rfw.kernel.dataformatters.RFWPhoneDataFormatter;
import br.eng.rodrigogml.rfw.kernel.dataformatters.RFWPhoneDataFormatter.PhoneType;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationGroupException;
import br.eng.rodrigogml.rfw.kernel.interfaces.RFWDBProvider;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaAttributeRelation;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaAttributeRelation.CompareOperation;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaBigDecimalCurrencyField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaBigDecimalField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaBigDecimalPercentageField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaBooleanField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaByteArrayField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaCollectionField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDependency;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDoubleField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaEnumField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaFloatField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaGenericField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaIntegerField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaLongField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField.RelationshipTypes;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringCEPField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringCNPJField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringCPFField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringCPFOrCNPJField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringEmailField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringIEField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringPhoneField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaUniqueConstraint;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaUsedBy;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaUsedByArray;
import br.eng.rodrigogml.rfw.kernel.utils.RUDV;
import br.eng.rodrigogml.rfw.kernel.utils.RUMail;
import br.eng.rodrigogml.rfw.kernel.utils.RUReflex;
import br.eng.rodrigogml.rfw.kernel.vo.RFWMO;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Classe principal do RFWValidator.<BR>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (03/07/2015)
 */
public class RFWValidator {

  /**
   * Enum com os tipos de valida��es da classe. Usada para passar para os "subm�todos" qual o tipo de valida��o sendo executada para os casos que precisam de diferencia��o.
   */
  private static enum VALIDATION {
    INSERT, UPDATE, DELETE
  }

  /**
   * DataProvider fornecido para realizar as consultas no banco de dados.
   */
  private RFWDBProvider dataProvider = null;

  public RFWValidator() {
  }

  public RFWValidator(RFWDBProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  /**
   * Valida o objeto para exclus�o.
   *
   * @param voClass Classe da Entidade.
   * @param id ID da entidade a ser exclu�da.
   * @throws RFWException Lan�ado em caso de valida��o ou problemas durante a execu��o das valida��es.
   */
  public void validateDelete(Class<? extends RFWVO> voClass, Long id) throws RFWException {
    // Verifica se o id n�o � nulo
    if (id == null) {
      throw new RFWCriticalException("N�o � poss�vel excluir um objeto sem especificar o ID!");
    }

    // Validamos a Depend�ncia antes de excluir
    if (voClass.isAnnotationPresent(RFWMetaUsedByArray.class) || voClass.isAnnotationPresent(RFWMetaUsedBy.class)) {
      validateUsedBy(voClass, id, voClass.getSimpleName().toLowerCase());
    }
  }

  /**
   * Valida o objeto para persistir.
   *
   * @param voClass Classe da Entidade.
   * @param vo Entidade a ser validado.
   * @throws RFWException Lan�ado em caso de valida��o ou problemas durante a execu��o das valida��es.
   */
  public void validatePersist(Class<? extends RFWVO> voClass, RFWVO vo) throws RFWException {
    if (vo != null && !vo.getClass().isAssignableFrom(voClass)) throw new RFWCriticalException("Objecto diferente da classe passada no BISValidator! O objeto passado para valida��o � do tipo '" + vo.getClass().getCanonicalName() + "' enquanto que a classe passada � do tipo '" + voClass.getCanonicalName() + "'.");
    // validatePersist(voClass, vo, vo.getClass().getSimpleName().toLowerCase(), (vo.getId() == null ? VALIDATION.INSERT : VALIDATION.UPDATE), null, vo, null, new ArrayList<RFWVO>(), null);
    // Em 12/8/21 foi removido o basePath com o nome inicial do VO. Isso pq os MetaObjects deixaram de ter o .val() e passaram a ter o mesmo padr�o utilizado pelo BUReflex. Passar o nome do VO como base do caminho fazia com que os campos passados em forceRequiredFields e a associa��o dos objetos na tela deixassem de funcionar.
    validatePersist(voClass, vo, null, (vo.getId() == null || vo.isInsertWithID() ? VALIDATION.INSERT : VALIDATION.UPDATE), null, vo, null, new ArrayList<RFWVO>(), null);
  }

  /**
   * Valida o objeto para persistir.
   *
   * @param voClass Classe da Entidade.
   * @param vo Entidade a ser validado.
   * @param forceRequiredFields Lista com os campos que devem ser verificamos como "required = true", independente do que estiver definido no @BISMetaAnnotation do campo. Passar nulo caso n�o queira for�ar nenhuma valida��o de obrigatoriedade.
   * @throws RFWException Lan�ado em caso de valida��o ou problemas durante a execu��o das valida��es.
   */
  public void validatePersist(Class<? extends RFWVO> voClass, RFWVO vo, String[] forceRequiredFields) throws RFWException {
    if (vo != null && !vo.getClass().isAssignableFrom(voClass)) throw new RFWCriticalException("Objecto diferente da classe passada no BISValidator! O objeto passado para valida��o � do tipo '" + vo.getClass().getCanonicalName() + "' enquanto que a classe passada � do tipo '" + voClass.getCanonicalName() + "'.");
    // validatePersist(voClass, vo, vo.getClass().getSimpleName().toLowerCase(), (vo.getId() == null ? VALIDATION.INSERT : VALIDATION.UPDATE), null, vo, null, new ArrayList<RFWVO>(), forceRequiredFields);
    // Em 12/8/21 foi removido o basePath com o nome inicial do VO. Isso pq os MetaObjects deixaram de ter o .val() e passaram a ter o mesmo padr�o utilizado pelo BUReflex. Passar o nome do VO como base do caminho fazia com que os campos passados em forceRequiredFields e a associa��o dos objetos na tela deixassem de funcionar.
    validatePersist(voClass, vo, null, (vo.getId() == null || vo.isInsertWithID() ? VALIDATION.INSERT : VALIDATION.UPDATE), null, vo, null, new ArrayList<RFWVO>(), forceRequiredFields);
  }

  /**
   * Recebe um VO e procura pelas meta-annotation do BIS com a descri��o dos campos e realiza as valida��es do objeto recebido.
   *
   * @param voClass Classe da Entidade.
   * @param moclazz Classe do MatchObjeto (MO) da entidade.
   * @param vo Entidade a ser validado.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param validation Define o tipo da valida��o
   * @param parentvo Objeto pai, usado quando estamos executando uma valida��o recursiva (Objetos mapeados dentro do objeto principal)
   * @param rootvo Objeto Raiz, o que foi passado quando o BISValidator foi chamado. Mesmo quando estivermos validando recursivamente, esse objeto ser� sempre o mesmo.
   * @param rootpath Caminho desde o objeto raiz at� o atributo que estamos validando atualmente. Nulo quando estamos validando o pr�prio objeto raiz.
   * @param newVOs Lista com os VOs que ainda n�o est�o no banco mas fazem parte do objeto. Objetos nesta lista n�o ser�o validados se tem ID em uma eventual ASSOCIA��O. Este objeto � gerenciado internamente com a recurs�o do m�todo. Para a chamada inicial passar uma lista vazia.
   * @param forceRequiredFields Lista com os campos que devem ser verificamos como "required = true", independente do que estiver definido no @BISMetaAnnotation do campo. Passar nulo caso n�o queira for�ar nenhuma valida��o de obrigatoriedade.
   * @throws RFWException Lan�ado em caso de valida��o ou problemas durante a execu��o das valida��es.
   */
  private void validatePersist(Class<? extends RFWVO> voClass, RFWVO vo, String basepath, VALIDATION validation, RFWVO parentvo, RFWVO rootvo, String rootpath, List<RFWVO> newVOs, String[] forceRequiredFields) throws RFWException {
    ArrayList<RFWValidationException> vallist = new ArrayList<>();

    // Verifica se o objeto n�o � nulo!
    if (vo == null) {
      throw new RFWCriticalException("O objeto sendo validado n�o pode ser nulo!");
    }

    // Validamos os IDs do objeto:
    if (validation == VALIDATION.INSERT) {
      // Em caso de Insert, o objeto pai nem os objetos filhos mapeados com COMPOSITION devem ter IDs. N�o estamos validando se � composition aqui ou n�o, j� que s� mapeamentos do tipo composition s�o validados recursivamente (voltam para este ponto) mapeamentos do tipo ASSOCIATION s�o validados apenas pelo ID e n�o de forma recursiva.
      if (vo.getId() != null && !vo.isInsertWithID()) {
        throw new RFWCriticalException("N�o � permitido inserir um objeto j� com o ID definido!");
      }
      newVOs.add(vo);
    } else if (validation == VALIDATION.UPDATE) {
      // Em caso de update, o objeto pai deve ter ID, os objetos filho podem ou n�o ter filhos no caso de COMPOSITION, pois podem j� existir no banco ou ser um filho novo... por isso s� validamos se for o objeto raiz, neste caso parentvo == null
      if (parentvo == null && vo.getId() == null) {
        throw new RFWCriticalException("N�o � permitido atualiza um objeto j� sem o ID definido!");
      }
      // Se for uma atualiza��o E se o objeto sendo testado n�o � o objeto raiz (parentvo != null), ele � um objeto de composi��o, se n�o tiver ID provavelmente ser� criado. Caso contr�rio ser� validado como associa��o mais abaixo e ser� lan�ada exce��o por ele n�o ter ID
      if (parentvo != null && vo.getId() == null) newVOs.add(vo);
    }

    // Iteramos os Fields da Classe para procurar as meta-annotations do bis.
    for (Field field : voClass.getDeclaredFields()) {
      boolean forceRequired = false; // Flag indicando se deve for�ar a valida��o de obrigatoriedade
      if (forceRequiredFields != null) {
        for (String reqField : forceRequiredFields) {
          String path = createPath(basepath, field.getName(), null);
          if (reqField.equals(path)) {
            forceRequired = true;
            break;
          } else {
            // Se n�o encontrou o caminho completo, tentamos se tem um caminho gen�rico (sem os ids de itera��o e hashs)
            path = RUReflex.getCleanPath(path);
            if (reqField.equals(path)) {
              forceRequired = true;
              break;
            }
          }
        }
      }
      try {
        if (field.isAnnotationPresent(RFWMetaStringField.class)) {
          validateStringField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaIntegerField.class)) {
          validateIntegerField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaLongField.class)) {
          validateLongField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaBigDecimalField.class)) {
          validateBigDecimalField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaBigDecimalCurrencyField.class)) {
          validateBigDecimalCurrencyField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaBigDecimalPercentageField.class)) {
          validateBigDecimalPercentageField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaEnumField.class)) {
          validateEnumField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaCollectionField.class)) {
          validateCollectionField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaDoubleField.class)) {
          validateDoubleField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaFloatField.class)) {
          validateFloatField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaBooleanField.class)) {
          validateBooleanField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaStringCNPJField.class)) {
          validateStringCNPJField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaStringCPFField.class)) {
          validateStringCPFField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaStringCPFOrCNPJField.class)) {
          validateStringCPFOrCNPJField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaStringEmailField.class)) {
          validateStringEmailField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaStringIEField.class)) {
          validateStringIEField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaStringCEPField.class)) {
          validateStringCEPField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaStringPhoneField.class)) {
          validateStringPhoneField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaDateField.class)) {
          validateDateField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaRelationshipField.class)) {
          validateRelationshipField(voClass, vo, field, basepath, validation, rootvo, rootpath, newVOs, forceRequired, forceRequiredFields);
        } else if (field.isAnnotationPresent(RFWMetaByteArrayField.class)) {
          validateByteArrayField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else if (field.isAnnotationPresent(RFWMetaGenericField.class)) {
          validateGenericField(voClass, vo, field, basepath, rootvo, rootpath, forceRequired);
        } else {
          // Se o n�o encontramos nenhuma das BISMeta annotations conhecidas, verificamos se encontramos alguma BISMeta annotation que n�o estamos validando e lan�amos como cr�tico! Garantimos assim que n�o esquecemos de fazer alguma valida��o para alguma nova BISMeta annotation.
          final Annotation[] anns = field.getDeclaredAnnotations();
          for (Annotation annotation : anns) {
            // Fazemos a compara��o de package usando o pacote atual de uma annotation conhecida ao inv�s de uma String fixa. Evitamos assim que esta verifica��o v� por �gua abaixo em algum refactor futuro.
            final String basepackage = RFWMetaStringField.class.getPackage().getName();
            if (annotation.annotationType().getCanonicalName().startsWith(basepackage + ".BISMeta")) {
              throw new RFWCriticalException("Encontrada BISMeta Annotation n�o suportada pelo BISValidator! BISMeta: ${0} - VO: ${1}.", new String[] { annotation.annotationType().getCanonicalName(), voClass.getCanonicalName() });
            }
          }
        }
      } catch (RFWValidationException e) {
        if (e instanceof RFWValidationGroupException) {
          vallist.addAll(((RFWValidationGroupException) e).getValidationlist());
        } else {
          vallist.add(e);
        }
      }
    }

    // Depois de validado os BISMetas, e somente se ainda n�o tiver falhado em nenhum campo validamos as Anota��es de Classe
    if (vallist.size() == 0) {
      try {
        // *** RFWMetaUniqueConstraint
        if (voClass.isAnnotationPresent(RFWMetaUniqueConstraint.class)) {
          validateUniqueConstraint(voClass, vo, basepath);
        }

        // *** RFWMetaAttributeRelation
        if (voClass.isAnnotationPresent(RFWMetaAttributeRelation.class)) {
          validateAttributeRelation(voClass, vo, basepath);
        }
      } catch (RFWValidationException e) {
        if (e instanceof RFWValidationGroupException) {
          vallist.addAll(((RFWValidationGroupException) e).getValidationlist());
        } else {
          vallist.add(e);
        }
      }
    }

    // Verifica se temos valida��es na lista, se tiver lan�a todas juntas
    if (vallist.size() > 0) throw new RFWValidationGroupException(vallist);
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaStringField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param forceRequired
   */
  private void validateStringField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaStringField ann = field.getAnnotation(RFWMetaStringField.class);
    String value;
    try {
      value = (String) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaStringField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    // Valida maxlength
    if (ann.maxLength() <= 0) {
      throw new RFWCriticalException("RFWMetaStringField definido com maxlength = 0 na classe '${0}'.", new String[] { voClass.getCanonicalName() });
    } else if (value != null) {
      if (value.length() > ann.maxLength()) throw new RFWValidationException("'${fieldname}' com tamanho excessivo! O tamanho m�ximo deve ser de ${0} caracteres.", new String[] { "" + ann.maxLength() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    if (value != null) {
      // Valida minlength
      if (ann.minlength() >= 0) {
        if (value.length() < ann.minlength()) throw new RFWValidationException("'${fieldname}' muito curto! O tamanho m�nimo deve ser de ${0} caracteres.", new String[] { "" + ann.minlength(), "" + value }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida pattern
      if (!"".equals(ann.pattern())) {
        if (!value.matches(ann.pattern())) throw new RFWValidationException("O valor de '${fieldname}' n�o est� em um padr�o aceito!", new String[] { "" + ann.pattern(), "" + value }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaGenericField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param forceRequired
   */
  private void validateGenericField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaGenericField ann = field.getAnnotation(RFWMetaGenericField.class);
    Object value;
    try {
      value = RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaGenericField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaByteArrayField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param forceRequired
   */
  private void validateByteArrayField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaByteArrayField ann = field.getAnnotation(RFWMetaByteArrayField.class);
    byte[] value;
    try {
      value = (byte[]) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaStringField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    // Valida maxlength
    if (ann.maxlength() <= 0) {
      throw new RFWCriticalException("RFWMetaStringField definido com maxlength = 0 na classe '${0}'.", new String[] { voClass.getCanonicalName() });
    } else if (value != null) {
      if (value.length > ann.maxlength()) throw new RFWValidationException("'${fieldname}' com tamanho excessivo! O tamanho m�ximo deve ser de ${0} caracteres.", new String[] { "" + ann.maxlength() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    if (value != null) {
      // Valida minlength
      if (ann.minlength() >= 0) {
        if (value.length < ann.minlength()) throw new RFWValidationException("'${fieldname}' muito curto! O tamanho m�nimo deve ser de ${0} caracteres.", new String[] { "" + ann.minlength(), "" + value }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em uma classe com a RFWMetaUniqueConstraint.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param basepath Caminho base at� chegar nesta valida��o
   */
  private void validateUniqueConstraint(Class<? extends RFWVO> voClass, RFWVO vo, String basepath) throws RFWException {
    // Busca no banco se temos
    if (this.dataProvider != null) {
      // Recuperamos a anota��o se suas defini��es
      final RFWMetaUniqueConstraint ann = voClass.getAnnotation(RFWMetaUniqueConstraint.class);

      // Verificamos os valores dos campos definidos na constraint
      Object[] value = new Object[ann.fields().length];
      String[] fieldcaptions = new String[ann.fields().length];
      for (int i = 0; i < ann.fields().length; i++) {
        String fieldname = ann.fields()[i];
        try {
          value[i] = RUReflex.getPropertyValue(vo, fieldname);
          fieldcaptions[i] = RUReflex.getRFWMetaAnnotationCaption(vo.getClass(), ann.fields()[i]);
        } catch (Exception e) {
          throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaUniqueConstraint.class.getName(), fieldname }, e);
        }
      }

      // Montamos o MO para verificar se encontramos um objeto no banco com os mesmos valores
      RFWMO mo = new RFWMO();
      try {
        for (int i = 0; i < value.length; i++) {
          Object object = value[i];
          String fieldname = ann.fields()[i];

          // Se o valor da propriedade for null vamos utilizar o m�todo de isnull do MO
          if (object == null) {
            mo.isNull(fieldname);
          } else {
            if (object instanceof RFWVO) {
              // Se o ID estiver nulo, abortamos a valida��o, pois consideramos que esse ID nulo seja de um objeto que ainda ser� persistido. Logo n�o ter� problema de constraint. (talvez tenha problema se o pr�prio objeto estiver criando objetos com contraints duplicadas.
              if (((RFWVO) value[i]).getId() == null) return;
              // Se for um RFWVO, buscamos pela igualdade do seu ID e n�o do pr�prio objeto
              mo.equal(fieldname + ".id", ((RFWVO) value[i]).getId());
            } else {
              mo.equal(fieldname, value[i]);
            }
          }
        }
        // Caso o VO tenha um ID, adicionamos a condi��o de n�o aceitar esse objeto na busca para evitar de contrastar a constraint com esse pr�prio objeto no caso de um update.
        if (vo.getId() != null) mo.notEqual("id", vo.getId());
      } catch (Exception e) {
        throw new RFWCriticalException("Falha ao montar MO para teste de UniqueConstraint: '${0}'.", new String[] { e.getMessage() }, e);
      }

      RFWVO dbvo = this.dataProvider.findUniqueMatch(voClass, mo, null);
      if (dbvo != null) {
        throw new RFWValidationException("J� existe um cadastro com os mesmos valores nos campos: ${fieldname}!", createPath(basepath, ann.fields()[0], null), voClass.getCanonicalName(), fieldcaptions);
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em uma classe com a RFWMetaAttributeRelation.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param basepath Caminho base at� chegar nesta valida��o
   */
  private void validateAttributeRelation(Class<? extends RFWVO> voClass, RFWVO vo, String basepath) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaAttributeRelation ann = voClass.getAnnotation(RFWMetaAttributeRelation.class);

    // Obtemos os valores dos dois atributos e executamos a compara��o
    Object value1 = null;
    Object value2 = null;
    try {
      value1 = RUReflex.getPropertyValue(vo, ann.attribute());
    } catch (Exception e) {
      throw new RFWCriticalException("BISValidator n�o pode recuperar o valor do attributo '${0}' da classe '${1}'.", new String[] { ann.attribute(), voClass.getCanonicalName() }, e);
    }
    try {
      value2 = RUReflex.getPropertyValue(vo, ann.attribute2());
    } catch (Exception e) {
      throw new RFWCriticalException("BISValidator n�o pode recuperar o valor do attributo '${0}' da classe '${1}'.", new String[] { ann.attribute2(), voClass.getCanonicalName() }, e);
    }

    // Com os dois valores na m�o, chamamos o m�todo para resolver a condi��o
    if (!evalCompareOperation(value1, value2, ann.operation())) {
      throw new RFWValidationException(ann.exceptioncode(), new String[] { "" + value1, "" + value2 });
    }
  }

  /**
   * Este m�todo � usado pelas valida��es que utiliza��o o {@link CompareOperation} para avaliar dois valores.
   *
   * @param value1 Primeiro valor da Compara��o
   * @param value2 Segundo valor da Compara��o
   * @param operation Opera��o de compara��o
   * @return true caso a opera��o/condi��o se satisfa�a, false caso contr�rio.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private boolean evalCompareOperation(Object value1, Object value2, CompareOperation operation) {
    boolean satisfied = false;
    switch (operation) {
      case EQUAL:
        satisfied |= value1 == null && value2 == null;
        satisfied |= value1 != null && value1.equals(value2);
        break;
      case LESSOREQUALTHAN:
        satisfied |= value1 == null || value2 == null || (value1 instanceof Comparable) && ((Comparable) value1).compareTo(value2) <= 0;
        break;
      case LESSTHAN:
        satisfied |= value1 == null || value2 == null || (value1 instanceof Comparable) && ((Comparable) value1).compareTo(value2) < 0;
        break;
      case MOREOREQUALTHAN:
        satisfied |= value1 == null || value2 == null || (value1 instanceof Comparable) && ((Comparable) value1).compareTo(value2) >= 0;
        break;
      case MORETHAN:
        satisfied |= value1 == null || value2 == null || ((value1 instanceof Comparable) && ((Comparable) value1).compareTo(value2) > 0);
        break;
      case NOTEQUAL:
        satisfied |= value1 == null ^ value2 == null;
        satisfied |= value1 != null && !value1.equals(value2);
        break;
    }
    return satisfied;
  }

  /**
   * Este m�todo executa as valida��es de depend�ncias com o objeto. Normalmente usada antes de excluir o objeto.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param VO Entidade sendo validada.
   * @param basepath Caminho base at� chegar nesta valida��o
   */
  private void validateUsedBy(Class<? extends RFWVO> voClass, Long id, String basepath) throws RFWException {
    if (this.dataProvider != null) {
      // Recuperamos a anota��o se suas defini��es
      RFWMetaUsedBy[] annlist = voClass.getAnnotationsByType(RFWMetaUsedBy.class);
      if (annlist != null) {
        for (int j = 0; j < annlist.length; j++) {
          final RFWMetaUsedBy ann = annlist[j];

          // Procuramos na classe dependente a anota��o do BISDepency cujo alvo seja esta classe
          RFWMetaDependency[] dependentTargetVOClass = ann.voClass().getAnnotationsByType(RFWMetaDependency.class);
          boolean found = false;
          if (dependentTargetVOClass != null) {
            for (int i = 0; i < dependentTargetVOClass.length; i++) {
              RFWMetaDependency dependentAnn = dependentTargetVOClass[i];
              if (dependentAnn.voClass().equals(voClass)) {
                found = true;
                // Se encontramos a annotationd e dependecy na outra classe, buscamos atrav�s do MO se alguma classe est� usando este objeto
                RFWMO mo = new RFWMO();
                mo.equal(dependentAnn.attribute() + ".id", id);

                // Busca no banco se temos
                List<Long> list = this.dataProvider.findIDs(ann.voClass(), mo, null);
                if (list != null && list.size() > 0) {
                  throw new RFWValidationException("O cadastro est� em uso no momento e n�o pode ser excl�ido!", new String[] { "" + list.get(0) }, createPath(basepath, "id", null), ann.voClass().getCanonicalName(), null);
                }
              }
            }
          }
          if (!found) {
            throw new RFWCriticalException("Imposs�vel encontrar a annotation RFWMetaDependency na classe '${0}' com depend�ncia para a classe '${1}'.", new String[] { ann.voClass().getCanonicalName(), voClass.getCanonicalName() });
          }
        }
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaRelationshipField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param forceRequired
   * @param forceRequiredFields
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void validateRelationshipField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, VALIDATION validation, RFWVO rootvo, String rootpath, List<RFWVO> newVOs, boolean forceRequired, String[] forceRequiredFields) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaRelationshipField ann = field.getAnnotation(RFWMetaRelationshipField.class);

    // Se for uma WEAK_ASSOCIATION n�o faz absolutamente nenhuma valida��o, nem de obrigatoriedade nem nada!
    if (ann.relationship() == RelationshipTypes.WEAK_ASSOCIATION) return;

    Object value;
    try {
      value = RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaRelationshipField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    if (value != null) {
      switch (ann.relationship()) {
        case WEAK_ASSOCIATION:
          // Nada para fazer, esse tipo de associa��o � como se n�o existisse para o RFWDAO.
          break;
        case ASSOCIATION:
        case MANY_TO_MANY:
          if (value instanceof RFWVO) {
            // Se � uma associa��o verificamos se o objeto associado � um RFWVO e se ele tem um id...
            if (((RFWVO) value).getId() == null || ((RFWVO) value).isInsertWithID()) { // Um objeto que tem ID mas tem a marca��o de inserir com ID = true � considerado um objeto que n�o est� no banco. Se ele j� foi persistido, deve ter a flag defina em false.
              // ...e se ele n�o est� na lista de objetos que ser�o inseridos por serem novos.
              if (!newVOs.contains(value)) {
                throw new RFWValidationException("Associa��o inv�lida! � esperado um objeto pr�-existente no atributo '${fieldname}'!", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
              }
            } else {
              if (this.dataProvider != null) {
                // Valida se o objeto j� est� no banco de dados...
                RFWVO dbvo = this.dataProvider.findByID((Class<? extends RFWVO>) value.getClass(), ((RFWVO) value).getId(), null);
                if (dbvo == null) {
                  throw new RFWCriticalException("O objeto associado n�o foi encontrado no banco de dados! Atributo: '${0}'", new String[] { field.getName() });
                }
              }
              // Valida unicidade do relacionamento
              if (ann.unique()) {
                checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
              }
            }
          } else if (value instanceof List) {
            List<?> list = (List) value;
            // Se temos uma lista de associa��es, verificamos se ela tem o tamanho m�nimo exigido
            if (ann.minSize() > -1 && list.size() < ann.minSize()) {
              throw new RFWValidationException("'${fieldname}' deve ter no m�nimo '${0}' relacionamento(s).", new String[] { "" + ann.minSize() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { ann.caption() });
            }
            // Valida os itens da lista
            for (Object listvo : list) {
              // valida se o objeto da lista n�o � nulo e se tem um ID
              if (listvo == null || ((RFWVO) listvo).getId() == null) {
                throw new RFWCriticalException("A associa��o, ou seu ID, do atributo '${0}' da classe '${1}' � nulo!", new String[] { field.getName(), voClass.getCanonicalName() });
              }
              // agora se cada existe no banco
              if (this.dataProvider != null) {
                final RFWVO dbvo = this.dataProvider.findByID((Class<? extends RFWVO>) listvo.getClass(), ((RFWVO) listvo).getId(), null);
                if (dbvo == null) {
                  throw new RFWCriticalException("'${fieldname}' cont�m uma associa��o com um objeto que n�o foi encontrado na base de dados: '${0}' / ID: '${1}'.", new String[] { listvo.getClass().getCanonicalName(), "" + ((RFWVO) listvo).getId() });
                }
              }
            }
            // Se o relacionamento for marcado como �nico, verificamos se cada uma das associa��es n�o est� em uso por outro objeto
            if (ann.unique()) {
              final ArrayList compareList = new ArrayList(list); // Duplicamos a lista para manipular e iterar sem prejudicar a lista principal
              for (Object assocVO : list) {
                if (assocVO instanceof RFWVO) {
                  // Busca um relacionamento duplicando dentro do mesmo objeto
                  compareList.remove(assocVO); // Remove este objeto para n�o conincidir com ele mesmo, e para j� ir diminuindo a lista de relacionamento, deixando cada for 1 item menor
                  for (Object dupVO : compareList) {
                    if (((RFWVO) dupVO).getId().equals(((RFWVO) assocVO).getId())) {
                      throw new RFWValidationException("'${fieldname}' duplicado!. N�o podem existir dois cadastros com o mesmo '${fieldname}'.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
                    }
                  }
                  if (this.dataProvider != null) {
                    // Busca algum outro relacionamento com este mesmo objeto no banco de dados.
                    RFWMO mo = new RFWMO();
                    mo.equal(field.getName() + ".id", ((RFWVO) assocVO).getId());
                    // Caso este objeto tenha um ID, garantimos que a busca n�o vai encontrar esse pr�prio objeto na busca, afinal em caso de update o relacionamento pode j� existir no banco
                    mo.notEqual("id", vo.getId());
                    List<Long> foundList = this.dataProvider.findIDs(voClass, mo, null);
                    if (foundList != null && foundList.size() > 0) {
                      throw new RFWValidationException("'${fieldname}' duplicado. N�o podem existir dois cadastros com o mesmo '${fieldname}'.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
                    }
                  }
                }
              }
            }
          } else if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            // Se temos um Map de associa��es, verificamos se ela tem o tamanho m�nimo exigido
            if (ann.minSize() > -1 && map.size() < ann.minSize()) {
              throw new RFWValidationException("'${fieldname}' deve ter no m�nimo '${0}' relacionamento(s).", new String[] { "" + ann.minSize() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { ann.caption() });
            }
            // Valida os itens da lista
            for (Object listvo : map.values()) {
              // valida se o objeto da lista n�o � nulo e se tem um ID
              if (listvo == null || ((RFWVO) listvo).getId() == null) {
                throw new RFWCriticalException("A associa��o, ou seu ID, do atributo '${0}' da classe '${1}' � nulo!", new String[] { field.getName(), voClass.getCanonicalName() });
              }
              // agora se cada existe no banco
              if (this.dataProvider != null) {
                final RFWVO dbvo = this.dataProvider.findByID((Class<? extends RFWVO>) listvo.getClass(), ((RFWVO) listvo).getId(), null);
                if (dbvo == null) {
                  throw new RFWCriticalException("'${fieldname}' cont�m uma associa��o com um objeto que n�o foi encontrado na base de dados: '${0}' / ID: '${1}'.", new String[] { listvo.getClass().getCanonicalName(), "" + ((RFWVO) listvo).getId() });
                }
              }
            }
            // Se o relacionamento for marcado como �nico, verificamos se cada uma das associa��es n�o est� em uso por outro objeto
            if (ann.unique()) {
              final ArrayList compareList = new ArrayList(map.values()); // Duplicamos a lista para manipular e iterar sem prejudicar a lista principal
              for (Object assocVO : map.values()) {
                if (assocVO instanceof RFWVO) {
                  // Busca um relacionamento duplicando dentro do mesmo objeto
                  compareList.remove(assocVO); // Remove este objeto para n�o conincidir com ele mesmo, e para j� ir diminuindo a lista de relacionamento, deixando cada for 1 item menor
                  for (Object dupVO : compareList) {
                    if (((RFWVO) dupVO).getId().equals(((RFWVO) assocVO).getId())) {
                      throw new RFWValidationException("'${fieldname}' duplicado!. N�o podem existir dois cadastros com o mesmo '${fieldname}'.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
                    }
                  }
                  // Busca algum outro relacionamento com este mesmo objeto no banco de dados.
                  if (this.dataProvider != null) {
                    RFWMO mo = new RFWMO();
                    mo.equal(field.getName() + ".id", ((RFWVO) assocVO).getId());
                    // Caso este objeto tenha um ID, garantimos que a busca n�o vai encontrar esse pr�prio objeto na busca, afinal em caso de update o relacionamento pode j� existir no banco
                    mo.notEqual("id", vo.getId());
                    List<Long> foundList = this.dataProvider.findIDs(voClass, mo, null);
                    if (foundList != null && foundList.size() > 0) {
                      throw new RFWValidationException("'${fieldname}' duplicado. N�o podem existir dois cadastros com o mesmo '${fieldname}'.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
                    }
                  }
                }
              }
            }
          } else {
            throw new RFWCriticalException("O BISValidator n�o suporta a BISMetaRelatioship '${3}' para o tipo '${0}', encontrada no atributo '${1}' da classe '${2}'.", new String[] { value.getClass().getCanonicalName(), field.getName(), voClass.getCanonicalName(), ann.relationship().toString() });
          }
          break;
        case COMPOSITION:
          // Faz a valida��o em cadeia
          if (value instanceof RFWVO) {
            validatePersist(((RFWVO) value).getClass(), (RFWVO) value, createPath(basepath, field.getName(), null), validation, vo, rootvo, createPath(rootpath, field.getName(), null), newVOs, forceRequiredFields);
          } else if (value instanceof List) {
            List<?> list = (List) value;
            // Se temos uma lista de associa��es, verificamos se ela tem o tamanho m�nimo exigido
            if (ann.minSize() > -1 && list.size() < ann.minSize()) {
              throw new RFWValidationException("'${fieldname}' deve ter no m�nimo '${0}' relacionamento(s).", new String[] { "" + ann.minSize() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { ann.caption() });
            }
            int count = 0;
            for (Iterator iterator = ((List) value).iterator(); iterator.hasNext();) {
              Object obj = iterator.next();
              if (obj instanceof RFWVO) {
                RFWVO childvo = (RFWVO) obj;
                validatePersist(childvo.getClass(), childvo, createPath(basepath, field.getName(), "" + count), validation, vo, rootvo, createPath(rootpath, field.getName(), "" + count), newVOs, forceRequiredFields);
              } else {
                throw new RFWCriticalException("O BISValidator n�o suporta a BISMetaRelationship '${3}' em uma List de '${0}'. Encontrada no atributo '${1}' da classe '${2}'.", new String[] { obj.getClass().getCanonicalName(), field.getName(), voClass.getCanonicalName(), ann.relationship().toString() });
              }
              count++;
            }
          } else if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            // Se temos um Map de associa��es, verificamos se ela tem o tamanho m�nimo exigido
            if (ann.minSize() > -1 && map.size() < ann.minSize()) {
              throw new RFWValidationException("'${fieldname}' deve ter no m�nimo '${0}' relacionamento(s).", new String[] { "" + ann.minSize() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { ann.caption() });
            }

            int count = 0;
            for (Object obj : ((Map) value).values()) {
              if (obj instanceof RFWVO) {
                RFWVO childvo = (RFWVO) obj;
                validatePersist(childvo.getClass(), childvo, createPath(basepath, field.getName(), "" + count), validation, vo, rootvo, createPath(rootpath, field.getName(), "" + count), newVOs, forceRequiredFields);
              } else {
                throw new RFWCriticalException("O BISValidator n�o suporta a BISMetaRelationship '${3}' em uma List de '${0}'. Encontrada no atributo '${1}' da classe '${2}'.", new String[] { obj.getClass().getCanonicalName(), field.getName(), voClass.getCanonicalName(), ann.relationship().toString() });
              }
              count++;
            }
          } else {
            throw new RFWCriticalException("O BISValidator n�o suporta a BISMetaRelatioship '${3}' para o tipo '${0}', encontrada no atributo '${1}' da classe '${2}'.", new String[] { value.getClass().getCanonicalName(), field.getName(), voClass.getCanonicalName(), ann.relationship().toString() });
          }
          break;
        case PARENT_ASSOCIATION:
          if (value instanceof RFWVO) {
            // Se � uma associa��o verificamos se o objeto associado � um RFWVO e se ele tem um id...
            if (((RFWVO) value).getId() == null || ((RFWVO) value).isInsertWithID()) {
              // ...e se ele n�o est� na lista de objetos que ser�o inseridos por serem novos.
              if (!newVOs.contains(value)) {
                throw new RFWValidationException("Associa��o inv�lida! � esperado um objeto pr�-existente no atributo '${fieldname}'!", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
              }
            } else {
              // Valida se o objeto j� est� no banco de dados...
              if (this.dataProvider != null) {
                RFWVO dbvo = this.dataProvider.findByID((Class<? extends RFWVO>) value.getClass(), ((RFWVO) value).getId(), null);
                if (dbvo == null) {
                  throw new RFWCriticalException("O objeto associado n�o foi encontrado no banco de dados! Atributo: '${0}'", new String[] { field.getName() });
                }
              }
              // Valida unicidade do relacionamento
              if (ann.unique()) {
                checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
              }
            }
          } else {
            // Se n�o � do tipo RFWVO lan�amos exception, pois esse relacionamento s� deve ser utilizado para mapear um objeto pai, e obviamente n�o pode ser uma lista, hash ou qualquer outro tipo de conjunto
            throw new RFWCriticalException("O relacionamento PARENT_ASSOCIATION s� pode ser utilizado em relacionamentos de um RFWVO para indicar a classe pai. '${0}' no atributo '${1}'!", new String[] { voClass.getCanonicalName(), field.getName() });
          }
          break;
        case INNER_ASSOCIATION:
          if (value instanceof RFWVO) {
            // Se � uma inner associa��o verificamos se o objeto associado � um RFWVO e se ele tem um id...
            if (((RFWVO) value).getId() == null) {
              // ... N�o causamos erro aqui pq ele pode ser inserido junto com o objeto, mesmo que este objeto seja inserido no banco antes do INNER_ASSOCIATION, ele entra na lista de pend�ncias. Se no final ele ainda n�o estiver pronto para ser associado, o RFWDAO causar� erro posterior
            } else {
              // Valida se o objeto j� est� no banco de dados...
              if (this.dataProvider != null) {
                RFWVO dbvo = this.dataProvider.findByID((Class<? extends RFWVO>) value.getClass(), ((RFWVO) value).getId(), null);
                if (dbvo == null) {
                  throw new RFWCriticalException("O objeto associado n�o foi encontrado no banco de dados mesmo j� vindo com um ID definido! Atributo: '${0}'", new String[] { field.getName() });
                }
              }
              // Valida a unicidade do relacionamento
              if (ann.unique()) {
                checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
              }
            }
          } else {
            // Se n�o � do tipo RFWVO lan�amos exception, pois esse relacionamento s� deve ser utilizado para mapear um objeto pai, e obviamente n�o pode ser uma lista, hash ou qualquer outro tipo de conjunto
            throw new RFWCriticalException("O relacionamento PARENT_ASSOCIATION s� pode ser utilizado em relacionamentos de um RFWVO para indicar a classe pai. '${0}' no atributo '${1}'!", new String[] { voClass.getCanonicalName(), field.getName() });
          }
          break;
        case COMPOSITION_TREE:
          // Faz a valida��o em cadeia
          if (value instanceof RFWVO) {
            validatePersist(((RFWVO) value).getClass(), (RFWVO) value, createPath(basepath, field.getName(), null), validation, vo, rootvo, createPath(rootpath, field.getName(), null), newVOs, forceRequiredFields);
          } else if (value instanceof List) {
            List<?> list = (List) value;
            // Se temos uma lista de associa��es, verificamos se ela tem o tamanho m�nimo exigido
            if (ann.minSize() > -1 && list.size() < ann.minSize()) {
              throw new RFWValidationException("'${fieldname}' deve ter no m�nimo '${0}' relacionamento(s).", new String[] { "" + ann.minSize() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { ann.caption() });
            }
            int count = 0;
            for (Iterator iterator = ((List) value).iterator(); iterator.hasNext();) {
              Object obj = iterator.next();
              if (obj instanceof RFWVO) {
                RFWVO childvo = (RFWVO) obj;
                validatePersist(childvo.getClass(), childvo, createPath(basepath, field.getName(), "" + count), validation, vo, rootvo, createPath(rootpath, field.getName(), "" + count), newVOs, forceRequiredFields);
              } else {
                throw new RFWCriticalException("O BISValidator n�o suporta a BISMetaRelationship '${3}' em uma List de '${0}'. Encontrada no atributo '${1}' da classe '${2}'.", new String[] { obj.getClass().getCanonicalName(), field.getName(), voClass.getCanonicalName(), ann.relationship().toString() });
              }
              count++;
            }
          } else if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            // Se temos um Map de associa��es, verificamos se ela tem o tamanho m�nimo exigido
            if (ann.minSize() > -1 && map.size() < ann.minSize()) {
              throw new RFWValidationException("'${fieldname}' deve ter no m�nimo '${0}' relacionamento(s).", new String[] { "" + ann.minSize() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { ann.caption() });
            }

            int count = 0;
            for (Object obj : ((Map) value).values()) {
              if (obj instanceof RFWVO) {
                RFWVO childvo = (RFWVO) obj;
                validatePersist(childvo.getClass(), childvo, createPath(basepath, field.getName(), "" + count), validation, vo, rootvo, createPath(rootpath, field.getName(), "" + count), newVOs, forceRequiredFields);
              } else {
                throw new RFWCriticalException("O BISValidator n�o suporta a BISMetaRelationship '${3}' em uma List de '${0}'. Encontrada no atributo '${1}' da classe '${2}'.", new String[] { obj.getClass().getCanonicalName(), field.getName(), voClass.getCanonicalName(), ann.relationship().toString() });
              }
              count++;
            }
          } else {
            throw new RFWCriticalException("O BISValidator n�o suporta a BISMetaRelatioship '${3}' para o tipo '${0}', encontrada no atributo '${1}' da classe '${2}'.", new String[] { value.getClass().getCanonicalName(), field.getName(), voClass.getCanonicalName(), ann.relationship().toString() });
          }
          break;
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaStringCNPJField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateStringCNPJField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaStringCNPJField ann = field.getAnnotation(RFWMetaStringCNPJField.class);
    String value;
    try {
      value = (String) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaStringCNPJField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    // Valida o dado se preenchido
    if (value != null) {
      RUDV.validateCNPJ(value);
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaStringCPFField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateStringCPFField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaStringCPFField ann = field.getAnnotation(RFWMetaStringCPFField.class);
    String value;
    try {
      value = (String) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaStringCPFField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    // Valida o dado se preenchido
    if (value != null) {
      RUDV.validateCPF(value);
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaStringCPFField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateStringCPFOrCNPJField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaStringCPFOrCNPJField ann = field.getAnnotation(RFWMetaStringCPFOrCNPJField.class);
    String value;
    try {
      value = (String) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaStringCPFOrCNPJField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    // Valida o dado se preenchido
    if (value != null) {
      RUDV.validateCPFOrCNPJ(value);
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaStringEmailField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootVO
   * @param rootpath
   * @param forceRequired
   */
  private void validateStringEmailField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootVO, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaStringEmailField ann = field.getAnnotation(RFWMetaStringEmailField.class);
    String value;
    try {
      value = (String) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaStringEmailField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootVO.getClass(), basepath, field.getName()) });
    }
    // Valida o tamanho m�ximo
    if (ann.maxLength() > 0 && value != null && value.length() > ann.maxLength()) {
      throw new RFWValidationException("O valor '${0}' � muito grande. Deve ter no m�ximo '${1}' caracteres.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { value, "" + ann.maxLength(), getAttributeFullCaption(rootVO.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootVO, rootpath);
    }
    // Valida o dado se preenchido
    if (value != null) {
      if (ann.useRFC822()) {
        RUMail.validateMailAddress(value);
      } else {
        RUMail.validateMailAddress(value);
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaStringIEField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateStringIEField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaStringIEField ann = field.getAnnotation(RFWMetaStringIEField.class);
    String value;
    try {
      value = (String) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaStringField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    // Valida o dado se preenchido
    if (value != null) {
      // Verificase temos uma UF Definida
      if (!"".equals(ann.uf())) {
        RUDV.validateIE(value, ann.uf());
      } else if (!"".equals(ann.uffield())) {
        // Verifica se conseguimos um valor pela UF Atribuida
        final Object uf = RUReflex.getPropertyValue(vo, ann.uffield());
        if (uf != null) {
          if (uf instanceof String) {
            RUDV.validateIE(value, (String) uf);
          } else {
            throw new RFWCriticalException("O tipo do campo indicado como sendo a UF na BISMetaIEField no campo '${0}' da classe '${1}' n�o � suportado!");
          }
        } else {
          RUDV.validateIE(value);
        }
      } else {
        RUDV.validateIE(value);
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaStringCEPField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateStringCEPField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaStringCEPField ann = field.getAnnotation(RFWMetaStringCEPField.class);
    String value;
    try {
      value = (String) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaStringField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    // Valida o dado se preenchido
    if (value != null) {
      RFWCEPDataFormatter.getInstance().validate(value, RFW.getLocale());
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaStringPhoneField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateStringPhoneField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaStringPhoneField ann = field.getAnnotation(RFWMetaStringPhoneField.class);
    String value;
    try {
      value = (String) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaStringField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    // Valida o dado se preenchido
    if (value != null) {
      if (RFWPhoneDataFormatter.getPhoneType(value) == PhoneType.UNKNOW && !ann.acceptUnknowFormats()) throw new RFWValidationException("O N�mero '${0}' de Telefone Inv�lido!", new String[] { value }, createPath(basepath, field.getName(), null));
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaDoubleField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateDoubleField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaDoubleField ann = field.getAnnotation(RFWMetaDoubleField.class);
    Double value;
    try {
      value = (Double) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaDoubleField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    if (value != null) {
      // Valida max value
      if (ann.maxValue() < value) {
        throw new RFWValidationException("'${fieldname}' valor maior que o permitido! O maior valor aceito � ${0}.", new String[] { "" + ann.maxValue() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida minvalue
      if (ann.minValue() > value) {
        throw new RFWValidationException("'${fieldname}' valor menor que o permitido! O menor valor aceito � ${0}.", new String[] { "" + ann.minValue() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaFloatField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateFloatField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaFloatField ann = field.getAnnotation(RFWMetaFloatField.class);
    Float value;
    try {
      value = (Float) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaFloatField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    if (value != null) {
      // Valida max value
      if (ann.maxValue() < value) {
        throw new RFWValidationException("'${fieldname}' valor maior que o permitido! O maior valor aceito � ${0}.", new String[] { "" + ann.maxValue() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida minvalue
      if (ann.minValue() > value) {
        throw new RFWValidationException("'${fieldname}' valor menor que o permitido! O menor valor aceito � ${0}.", new String[] { "" + ann.minValue() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaIntegerField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateIntegerField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaIntegerField ann = field.getAnnotation(RFWMetaIntegerField.class);
    Integer value;
    try {
      value = (Integer) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaIntegerField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    if (value != null) {
      // Valida max value
      if (ann.maxvalue() < value) {
        throw new RFWValidationException("'${fieldname}' valor maior que o permitido! O maior valor aceito � ${0}.", new String[] { "" + ann.maxvalue() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida minvalue
      if (ann.minvalue() > value) {
        throw new RFWValidationException("'${fieldname}' valor menor que o permitido! O menor valor aceito � ${0}.", new String[] { "" + ann.maxvalue() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaIntegerField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateLongField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaLongField ann = field.getAnnotation(RFWMetaLongField.class);
    Long value;
    try {
      value = (Long) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaLongField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    if (value != null) {
      // Valida max value
      if (ann.maxvalue() < value) {
        throw new RFWValidationException("'${fieldname}' valor maior que o permitido! O maior valor aceito � ${0}.", new String[] { "" + ann.maxvalue() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida minvalue
      if (ann.minvalue() > value) {
        throw new RFWValidationException("'${fieldname}' valor menor que o permitido! O menor valor aceito � ${0}.", new String[] { "" + ann.maxvalue() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaBigDecimalField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateBigDecimalField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaBigDecimalField ann = field.getAnnotation(RFWMetaBigDecimalField.class);
    BigDecimal value;
    try {
      value = (BigDecimal) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaBigDecimalField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    if (value != null) {
      final int minScale = (ann.scale() == -1 ? 0 : ann.scale());
      final int maxScale = (ann.scaleMax() == -1 ? minScale : ann.scaleMax());
      if (maxScale < minScale) throw new RFWCriticalException("A precis�o m�xima n�o pode ser menor que a precis�o m�nima para o atributo '${0}' da classe '${1}'.", new String[] { field.getName(), vo.getClass().getCanonicalName() });

      // MaxValue
      BigDecimal max = null;
      if (!"".equals(ann.maxValue())) max = new BigDecimal(ann.maxValue());
      // Valida o Scale do valor m�ximo
      if (max != null && max.scale() > minScale && max.scale() > maxScale) {
        throw new RFWCriticalException("No campo '${0}' da classe '${1}', a defini��o de maxFloatValue tem mais casas do que a precis�o definida pela propriedade scale!", new String[] { field.getName(), voClass.getCanonicalName() });
      }
      if (max != null && max.compareTo(value) < 0) {
        throw new RFWValidationException("'${fieldname}' valor maior que o permitido! O maior valor aceito � ${0}.", new String[] { max.toString() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }

      // MinValue
      BigDecimal min = null;
      if (!"".equals(ann.minValue())) min = new BigDecimal(ann.minValue());
      // Valida o Scale do valor m�nimo
      if (min != null && min.scale() > minScale && min.scale() > maxScale) {
        throw new RFWCriticalException("No campo '${0}' da classe '${1}', a defini��o de minFloatValue tem mais casas do que a precis�o definida pela propriedade scale!", new String[] { field.getName(), voClass.getCanonicalName() });
      }
      if (min != null && min.compareTo(value) > 0) {
        throw new RFWValidationException("'${fieldname}' valor menor que o permitido! O menor valor aceito � ${0}.", new String[] { min.toString() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida o Scale (Precis�o) - Se definido
      if (value.scale() < minScale) {
        throw new RFWValidationException("'${fieldname}' deve ter uma precis�o de ${0} casas decimais.", new String[] { "" + minScale }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida o Scale M�ximo (Precis�o) - Se definido
      if (value.scale() > maxScale) {
        throw new RFWValidationException("'${fieldname}' deve ter uma precis�o m�xima de ${0} casas decimais.", new String[] { "" + maxScale }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida o valor absoluto - Mas permite o valor zero
      if (ann.absolute() && value.abs().compareTo(value) != 0 && value.compareTo(BigDecimal.ZERO) != 0) {
        throw new RFWValidationException("'${fieldname}' deve ter um valor positivo! Valores negativos n�o s�o aceitos!", null, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaBigDecimalCurrencyField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateBigDecimalCurrencyField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaBigDecimalCurrencyField ann = field.getAnnotation(RFWMetaBigDecimalCurrencyField.class);
    BigDecimal value;
    try {
      value = (BigDecimal) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaBigDecimalCurrencyField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    if (value != null) {
      final int minScale = (ann.scale() == -1 ? 0 : ann.scale());
      final int maxScale = (ann.scaleMax() == -1 ? minScale : ann.scaleMax());
      if (maxScale < minScale) throw new RFWCriticalException("A precis�o m�xima n�o pode ser menor que a precis�o m�nima para o atributo '${0}' da classe '${1}'.", new String[] { field.getName(), vo.getClass().getCanonicalName() });

      // MaxValue
      BigDecimal max = null;
      if (!"".equals(ann.maxValue())) max = new BigDecimal(ann.maxValue());
      // Valida o Scale do valor m�ximo
      if (max != null && max.scale() > minScale && max.scale() > maxScale) {
        throw new RFWCriticalException("No campo '${0}' da classe '${1}', a defini��o de maxFloatValue tem mais casas do que a precis�o definida pela propriedade scale!", new String[] { field.getName(), voClass.getCanonicalName() });
      }
      if (max != null && max.compareTo(value) < 0) {
        throw new RFWValidationException("'${fieldname}' valor maior que o permitido! O maior valor aceito � ${0}.", new String[] { max.toString() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }

      // MinValue
      BigDecimal min = null;
      if (!"".equals(ann.minValue())) min = new BigDecimal(ann.minValue());
      // Valida o Scale do valor m�nimo
      if (min != null && min.scale() > minScale && min.scale() > maxScale) {
        throw new RFWCriticalException("No campo '${0}' da classe '${1}', a defini��o de minFloatValue tem mais casas do que a precis�o definida pela propriedade scale!", new String[] { field.getName(), voClass.getCanonicalName() });
      }
      if (min != null && min.compareTo(value) > 0) {
        throw new RFWValidationException("'${fieldname}' valor menor que o permitido! O menor valor aceito � ${0}.", new String[] { min.toString() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida o Scale (Precis�o) - Se definido
      if (value.scale() < minScale) {
        throw new RFWValidationException("'${fieldname}' deve ter uma precis�o de ${0} casas decimais.", new String[] { "" + minScale }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida o Scale M�ximo (Precis�o) - Se definido
      if (value.scale() > maxScale) {
        throw new RFWValidationException("'${fieldname}' deve ter uma precis�o m�xima de ${0} casas decimais.", new String[] { "" + maxScale }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida o valor absoluto - Mas permite o valor zero
      if (ann.absolute() && value.abs().compareTo(value) != 0 && value.compareTo(BigDecimal.ZERO) != 0) {
        throw new RFWValidationException("'${fieldname}' deve ter um valor positivo! Valores negativos n�o s�o aceitos!", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaBigDecimalPercentageField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateBigDecimalPercentageField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaBigDecimalPercentageField ann = field.getAnnotation(RFWMetaBigDecimalPercentageField.class);
    BigDecimal value;
    try {
      value = (BigDecimal) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaBigDecimalPercentageField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
    if (value != null) {
      final int minScale = (ann.scale() == -1 ? 0 : ann.scale());
      final int maxScale = (ann.scaleMax() == -1 ? minScale : ann.scaleMax());
      if (maxScale < minScale) throw new RFWCriticalException("A precis�o m�xima n�o pode ser menor que a precis�o m�nima para o atributo '${0}' da classe '${1}'.", new String[] { field.getName(), vo.getClass().getCanonicalName() });

      // MaxValue
      BigDecimal max = null;
      if (!"".equals(ann.maxValue())) max = new BigDecimal(ann.maxValue());
      // Valida o Scale do valor m�ximo
      if (max != null && max.scale() > minScale && max.scale() > maxScale) {
        throw new RFWCriticalException("No campo '${0}' da classe '${1}', a defini��o de maxFloatValue tem mais casas do que a precis�o definida pela propriedade scale!", new String[] { field.getName(), voClass.getCanonicalName() });
      }
      if (max != null && max.compareTo(value) < 0) {
        throw new RFWValidationException("'${fieldname}' valor maior que o permitido! O maior valor aceito � ${0}.", new String[] { max.toString() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }

      // MinValue
      BigDecimal min = null;
      if (!"".equals(ann.minValue())) min = new BigDecimal(ann.minValue());
      // Valida o Scale do valor m�nimo
      if (min != null && min.scale() > minScale && min.scale() > maxScale) {
        throw new RFWCriticalException("No campo '${0}' da classe '${1}', a defini��o de minFloatValue tem mais casas do que a precis�o definida pela propriedade scale!", new String[] { field.getName(), voClass.getCanonicalName() });
      }
      if (min != null && min.compareTo(value) > 0) {
        throw new RFWValidationException("'${fieldname}' valor menor que o permitido! O menor valor aceito � ${0}.", new String[] { min.toString() }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida o Scale (Precis�o) - Se definido
      if (value.scale() < minScale) {
        throw new RFWValidationException("'${fieldname}' deve ter uma precis�o de ${0} casas decimais.", new String[] { "" + minScale }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida o Scale M�ximo (Precis�o) - Se definido
      if (value.scale() > maxScale) {
        throw new RFWValidationException("'${fieldname}' deve ter uma precis�o m�xima de ${0} casas decimais.", new String[] { "" + maxScale }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
      // Valida o valor absoluto - Mas permite o valor zero
      if (ann.absolute() && value.abs().compareTo(value) != 0 && value.compareTo(BigDecimal.ZERO) != 0) {
        throw new RFWValidationException("'${fieldname}' deve ter um valor positivo! Valores negativos n�o s�o aceitos!", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaEnumField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateEnumField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaEnumField ann = field.getAnnotation(RFWMetaEnumField.class);
    Enum<?> value = null;
    try {
      value = (Enum<?>) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaEnumField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation {@link RFWMetaCollectionField}.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateCollectionField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaCollectionField ann = field.getAnnotation(RFWMetaCollectionField.class);
    Object value = null;
    try {
      value = RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), ann.getClass().getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    if (value != null) {
      // Avaliamos o tipo do atributo
      if (value instanceof Map<?, ?>) {
        if (ann.minSize() > ((Map<?, ?>) value).size()) {
          throw new RFWValidationException("O atributo '${0}' deve conter no m�nimo '${1}' elemento.", new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()), "" + ann.minSize() }, createPath(basepath, field.getName(), null));
        }
        for (Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
          if (entry.getKey() instanceof String) {
            if (ann.maxLenghtKey() > 0 && entry.getKey() != null && ((String) entry.getKey()).length() > ann.maxLenghtKey()) {
              // Lan�ado como Critico pq o usu�rio provavelmente n�o define como chave, deve faltar limitador em algum ponto do sistema que valide a chave.
              throw new RFWCriticalException("A chave '${2}' do Map no atributo '${0}' da classe '${1}' � maior do que o limite definido de '${3}'.", new String[] { createPath(basepath, field.getName(), null), rootvo.getClass().getCanonicalName(), entry.getKey().toString(), "" + ann.maxLenghtKey() });
            }
          }
          if (entry.getValue() instanceof String) {
            if (ann.maxLenght() > 0 && entry.getValue() != null && ((String) entry.getValue()).length() > ann.maxLenght()) {
              throw new RFWValidationException("O valor '${0}' � muito grande. Deve ter no m�ximo '${1}' caracteres.", new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()), "" + ann.maxLenght() }, createPath(basepath, field.getName(), null));
            }
          }
        }
      } else if (value instanceof List<?>) {
        if (ann.minSize() > ((List<?>) value).size()) {
          throw new RFWValidationException("O atributo '${0}' deve conter no m�nimo '${1}' elemento.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()), "" + ann.minSize() });
        }
        for (Object item : ((List<?>) value)) {
          if (item instanceof String) {
            if (ann.maxLenght() > 0 && item != null && ((String) item).length() > ann.maxLenght()) {
              throw new RFWValidationException("O valor '${0}' � muito grande. Deve ter no m�ximo '${1}' caracteres.", new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()), "" + ann.maxLenght() }, createPath(basepath, field.getName(), null));
            }
          }
        }
      } else if (value instanceof HashSet<?>) {
        if (ann.minSize() > ((HashSet<?>) value).size()) {
          throw new RFWValidationException("O atributo '${0}' deve conter no m�nimo '${1}' elemento.", new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()), "" + ann.minSize() }, createPath(basepath, field.getName(), null));
        }
        for (Object item : ((HashSet<?>) value)) {
          if (item instanceof String) {
            if (ann.maxLenght() > 0 && item != null && ((String) item).length() > ann.maxLenght()) {
              throw new RFWValidationException("O valor '${0}' � muito grande. Deve ter no m�ximo '${1}' caracteres.", new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()), "" + ann.maxLenght() }, createPath(basepath, field.getName(), null));
            }
          }
        }
      } else {
        throw new RFWCriticalException("O atributo '${0}' da classe '${1}' marcado com a RFWMetaCollectionField n�o tem um tipo de Collection Suportado pelo BISValidator. Atualmente os tipos suportador s�o Map e List.", new String[] { field.getName(), rootvo.getClass().getCanonicalName() });
      }
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaBooleanField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateBooleanField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaBooleanField ann = field.getAnnotation(RFWMetaBooleanField.class);
    Boolean value;
    try {
      value = (Boolean) RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaBooleanField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && value == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }
    // Valida unicidade
    if (ann.unique()) {
      checkUnique(value, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }
  }

  /**
   * Este m�todo executa as valida��es necess�rias em um field atribuido com a meta-annotation RFWMetaDateField.
   *
   * @param voClass Classe da Entidade/VO sendo validada.
   * @param vo Entidade sendo validada.
   * @param field com a anota��o.
   * @param basepath Caminho base at� este atributo, caso a valida��o esteja ocorrendo cascata.
   * @param rootvo
   * @param rootpath
   * @param forceRequired
   */
  private void validateDateField(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired) throws RFWException {
    // Recuperamos a anota��o se suas defini��es
    final RFWMetaDateField ann = field.getAnnotation(RFWMetaDateField.class);
    Object obj;
    try {
      obj = RUReflex.getPropertyValue(vo, field.getName());
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("BISMetaField '${1}' usado em um campo n�o compat�vel no '${2} da '${0}'.", new String[] { voClass.getCanonicalName(), RFWMetaDateField.class.getName(), field.getName() }, e);
    }
    // Valida obrigatoriedade
    if ((forceRequired || ann.required()) && obj == null) {
      throw new RFWValidationException("'${fieldname}' � obrigat�rio.", createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
    }

    // Valida unicidade
    if (ann.unique()) {
      checkUnique(obj, voClass, field.getName(), vo, basepath, ann.caption(), rootvo, rootpath);
    }

    // Demais Valida��o conforme a classe de Data utilizada
    if (obj instanceof Date) {
      validateDateField_Date(voClass, vo, field, basepath, rootvo, rootpath, forceRequired, (Date) obj, ann);
    } else if (obj instanceof LocalDate) {
      validateDateField_LocalDate(voClass, vo, field, basepath, rootvo, rootpath, forceRequired, (LocalDate) obj, ann);
    } else if (obj instanceof LocalDateTime) {
      validateDateField_LocalDateTime(voClass, vo, field, basepath, rootvo, rootpath, forceRequired, (LocalDateTime) obj, ann);
    } else if (obj instanceof LocalTime) {
      validateDateField_LocalTime(voClass, vo, field, basepath, rootvo, rootpath, forceRequired, (LocalTime) obj, ann);
    }
  }

  public void validateDateField_LocalDate(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired, LocalDate value, RFWMetaDateField ann) throws RFWException {
    if (value != null) {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMddHHmmssSSSS");

      // Valida data m�nima
      if (ann.minValue().length() > 0) {
        LocalDate minDate;
        try {
          minDate = LocalDate.parse(ann.minValue(), dtf);
        } catch (Exception e) {
          throw new RFWCriticalException("Data inv�lida encontrada na BISMetaDateAnnotation da classe '${0}' no atributo '${1}'.", new String[] { voClass.getCanonicalName(), field.getName() }, e);
        }
        if (minDate.compareTo(value) < 0) {
          throw new RFWValidationException("A Data de '${fieldname}' deve ser maior ou igual � '${0}'.", new String[] { SimpleDateFormat.getDateTimeInstance().format(minDate) }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
        }
      }
      // Valida data m�xima
      if (ann.maxValue().length() > 0) {
        LocalDate maxDate;
        try {
          maxDate = LocalDate.parse(ann.maxValue(), dtf);
        } catch (Exception e) {
          throw new RFWCriticalException("Data inv�lida encontrada na BISMetaDateAnnotation da classe '${0}' no atributo '${1}'.", new String[] { voClass.getCanonicalName(), field.getName() }, e);
        }
        if (maxDate.compareTo(value) > 0) {
          throw new RFWValidationException("A Data de '${fieldname}' deve ser menor ou igual � '${0}'.", new String[] { SimpleDateFormat.getDateTimeInstance().format(maxDate) }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
        }
      }
    }
  }

  public void validateDateField_LocalDateTime(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired, LocalDateTime value, RFWMetaDateField ann) throws RFWException {
    if (value != null) {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMddHHmmssSSSS");

      // Valida data m�nima
      if (ann.minValue().length() > 0) {
        LocalDateTime minDate;
        try {
          minDate = LocalDateTime.parse(ann.minValue(), dtf);
        } catch (Exception e) {
          throw new RFWCriticalException("Data inv�lida encontrada na BISMetaDateAnnotation da classe '${0}' no atributo '${1}'.", new String[] { voClass.getCanonicalName(), field.getName() }, e);
        }
        if (minDate.compareTo(value) < 0) {
          throw new RFWValidationException("A Data de '${fieldname}' deve ser maior ou igual � '${0}'.", new String[] { SimpleDateFormat.getDateTimeInstance().format(minDate) }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
        }
      }
      // Valida data m�xima
      if (ann.maxValue().length() > 0) {
        LocalDateTime maxDate;
        try {
          maxDate = LocalDateTime.parse(ann.maxValue(), dtf);
        } catch (Exception e) {
          throw new RFWCriticalException("Data inv�lida encontrada na BISMetaDateAnnotation da classe '${0}' no atributo '${1}'.", new String[] { voClass.getCanonicalName(), field.getName() }, e);
        }
        if (maxDate.compareTo(value) > 0) {
          throw new RFWValidationException("A Data de '${fieldname}' deve ser menor ou igual � '${0}'.", new String[] { SimpleDateFormat.getDateTimeInstance().format(maxDate) }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
        }
      }
    }
  }

  public void validateDateField_LocalTime(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired, LocalTime value, RFWMetaDateField ann) throws RFWException {
    if (value != null) {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMddHHmmssSSSS");

      // Valida data m�nima
      if (ann.minValue().length() > 0) {
        LocalTime minDate;
        try {
          minDate = LocalTime.parse(ann.minValue(), dtf);
        } catch (Exception e) {
          throw new RFWCriticalException("Data inv�lida encontrada na BISMetaDateAnnotation da classe '${0}' no atributo '${1}'.", new String[] { voClass.getCanonicalName(), field.getName() }, e);
        }
        if (minDate.compareTo(value) < 0) {
          throw new RFWValidationException("A Data de '${fieldname}' deve ser maior ou igual � '${0}'.", new String[] { SimpleDateFormat.getDateTimeInstance().format(minDate) }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
        }
      }
      // Valida data m�xima
      if (ann.maxValue().length() > 0) {
        LocalTime maxDate;
        try {
          maxDate = LocalTime.parse(ann.maxValue(), dtf);
        } catch (Exception e) {
          throw new RFWCriticalException("Data inv�lida encontrada na BISMetaDateAnnotation da classe '${0}' no atributo '${1}'.", new String[] { voClass.getCanonicalName(), field.getName() }, e);
        }
        if (maxDate.compareTo(value) > 0) {
          throw new RFWValidationException("A Data de '${fieldname}' deve ser menor ou igual � '${0}'.", new String[] { SimpleDateFormat.getDateTimeInstance().format(maxDate) }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
        }
      }
    }
  }

  public void validateDateField_Date(Class<? extends RFWVO> voClass, RFWVO vo, Field field, String basepath, RFWVO rootvo, String rootpath, boolean forceRequired, Date value, RFWMetaDateField ann) throws RFWException {
    if (value != null) {
      SimpleDateFormat sd = new SimpleDateFormat("uuuuMMddHHmmssSSSS");

      // Valida data m�nima
      if (ann.minValue().length() > 0) {
        Date minDate;
        try {
          minDate = sd.parse(ann.minValue());
        } catch (ParseException e) {
          throw new RFWCriticalException("Data inv�lida encontrada na BISMetaDateAnnotation da classe '${0}' no atributo '${1}'.", new String[] { voClass.getCanonicalName(), field.getName() }, e);
        }
        if (minDate.compareTo(value) < 0) {
          throw new RFWValidationException("A Data de '${fieldname}' deve ser maior ou igual � '${0}'.", new String[] { SimpleDateFormat.getDateTimeInstance().format(minDate) }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
        }
      }
      // Valida data m�xima
      if (ann.maxValue().length() > 0) {
        Date maxDate;
        try {
          maxDate = sd.parse(ann.maxValue());
        } catch (ParseException e) {
          throw new RFWCriticalException("Data inv�lida encontrada na BISMetaDateAnnotation da classe '${0}' no atributo '${1}'.", new String[] { voClass.getCanonicalName(), field.getName() }, e);
        }
        if (maxDate.compareTo(value) > 0) {
          throw new RFWValidationException("A Data de '${fieldname}' deve ser menor ou igual � '${0}'.", new String[] { SimpleDateFormat.getDateTimeInstance().format(maxDate) }, createPath(basepath, field.getName(), null), voClass.getCanonicalName(), new String[] { getAttributeFullCaption(rootvo.getClass(), basepath, field.getName()) });
        }
      }
    }
  }

  /**
   * Centraliza a valida��o de unicidade do valor no objeto.
   */
  private void checkUnique(Object value, Class<? extends RFWVO> voClass, String fieldname, RFWVO vo, String basepath, String fieldcaption, RFWVO rootvo, String rootpath) throws RFWException {
    if (value != null) {
      // Procura o conicid�ncia de valor pelo objeto raiz ao inv�s de s� o objeto atual. Assim evitamos o BUG 306.
      RFWMO mo = new RFWMO();
      mo.equal(RUReflex.getCleanPath(createPath(rootpath, fieldname, null)), value);
      if (rootvo.getId() != null) { // Se tem ID o objeto pode j� estar no banco, evitamos o objeto com mesmo ID para n�o valida unicidade contra o mesmo objeto
        mo.notEqual("id", rootvo.getId());
      }
      RFWVO foundvo = this.dataProvider.findUniqueMatch(rootvo.getClass(), mo, null);
      if (foundvo != null) {
        throw new RFWValidationException("'${fieldname}' duplicado. N�o podem existir dois cadastros com o mesmo '${fieldname}'.", createPath(basepath, fieldname, null), voClass.getCanonicalName(), new String[] { fieldcaption, "" + value });
      }
    }
  }

  /**
   * M�todo auxiliar usado para facilitar a cria��o do caminho dos fields.
   *
   * @param basepath Caminho base dos VOs anteriores, ou Null caso ainda esteja no VO raiz.
   * @param field field da classe com erro de valida��o.
   * @param iterableid identificador do item dentro da colection. Index em caso de listas ou key em caso de hashs.
   * @return Caminho completo para a propriedade.
   */
  private String createPath(String basepath, String fieldname, String iterableid) {
    if (basepath == null) basepath = "";
    basepath += (basepath.length() > 0 ? "." : "") + fieldname + (iterableid != null ? "[" + iterableid + "]" : "");
    return basepath;
  }

  /**
   * M�todo auxiliar usado para recuperar o "Caption Completo" do atributo para ser adicionado na msg de valida��o.
   *
   * @param voClass Classe do VO raiz para come�ar a busca
   * @param basepath Caminho base dos VOs anteriores, ou Null caso ainda esteja no VO raiz.
   * @param fieldname field da classe com erro de valida��o.
   * @return
   * @throws RFWException Lan�ado caso algum atributo no caminho n�o tenha a BISMetaAnnotation
   */
  private String getAttributeFullCaption(Class<? extends RFWVO> voClass, String basepath, String fieldname) throws RFWException {
    return RUReflex.getRFWMetaAnnotationFullCaption(voClass, createPath(basepath, fieldname, null));
  }

}
