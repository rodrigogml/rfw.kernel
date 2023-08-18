package br.eng.rodrigogml.rfw.kernel.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

import br.eng.rodrigogml.rfw.kernel.utils.RUArray;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Classe de exce��o de dados inv�lidos. Lan�ado sempre que algum dados for submetido a alguma valida��o e falhar, interrompendo o fluxo normal.<br>
 *
 * @author Rodrigo Leit�o
 * @since 1.0
 */
public class RFWValidationException extends RFWException {

  private static final long serialVersionUID = 1092994413574267711L;

  /**
   * Armazena o caminho para o atributo que deu erro. Quando o atributo est� dentro de outro objeto que n�o o principal que foi validado, ou dentro de uma lista, collection, etc.
   */
  private String fieldPath = null;

  /**
   * Classe do objeto que gerou esta valida��o.
   */
  private String className = null;

  /**
   * Nome do atributo que o problema de valida��o foi encontrado.
   */
  private String[] fieldName = null;

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   */
  public RFWValidationException(String exceptionCode) {
    super(exceptionCode);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWValidationException(String exceptionCode, Throwable ex) {
    super(exceptionCode, ex);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   */
  public RFWValidationException(String exceptionCode, String[] params) {
    super(exceptionCode, params);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWValidationException(String exceptionCode, String[] params, Throwable ex) {
    super(exceptionCode, params, ex);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param fieldPath caminho para o field do VO que falhou na valida��o.
   */
  public RFWValidationException(String exceptionCode, String fieldPath) {
    super(exceptionCode);
    this.fieldPath = fieldPath;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   * @param fieldPath caminho para o field do VO que falhou na valida��o.
   */
  public RFWValidationException(String exceptionCode, Throwable ex, String fieldPath) {
    super(exceptionCode, ex);
    this.fieldPath = fieldPath;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   * @param fieldPath caminho para o field do VO que falhou na valida��o.
   */
  public RFWValidationException(String exceptionCode, String[] params, String fieldPath) {
    super(exceptionCode, params);
    this.fieldPath = fieldPath;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWValidationException(String exceptionCode, String[] params, Throwable ex, String fieldPath) {
    super(exceptionCode, params, ex);
    this.fieldPath = fieldPath;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   * @param fieldPath caminho para o field do VO que falhou na valida��o.
   * @param className Nome da Classe descendente do {@link RFWVO} que falhou na valida��o.
   * @param fieldName Nome dos campos que falharam na valida��o.
   */
  public RFWValidationException(String exceptionCode, String[] params, String fieldPath, String className, String[] fieldName) {
    super(exceptionCode, params);
    this.className = className;
    this.fieldName = fieldName;
    this.fieldPath = fieldPath;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   * @param fieldPath caminho para o field do VO que falhou na valida��o.
   * @param className Nome da Classe descendente do {@link RFWVO} que falhou na valida��o.
   * @param fieldName Nome dos campos que falharam na valida��o.
   */
  public RFWValidationException(String exceptionCode, String[] params, Throwable ex, String fieldPath, String className, String[] fieldName) {
    super(exceptionCode, params, ex);
    this.className = className;
    this.fieldName = fieldName;
    this.fieldPath = fieldPath;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param fieldPath caminho para o field do VO que falhou na valida��o.
   * @param className Nome da Classe descendente do {@link RFWVO} que falhou na valida��o.
   * @param fieldName Nome dos campos que falharam na valida��o.
   */
  public RFWValidationException(String exceptionCode, String fieldPath, String className, String[] fieldName) {
    super(exceptionCode);
    this.className = className;
    this.fieldName = fieldName;
    this.fieldPath = fieldPath;
  }

  public String getFieldPath() {
    return fieldPath;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String[] getFieldName() {
    return fieldName;
  }

  public void setFieldPath(String fieldPath) {
    this.fieldPath = fieldPath;
  }

  @Override
  public void printStackTrace(PrintStream s) {
    if (this.fieldName != null) s.println("<fieldName>" + RUArray.concatArrayIntoString(this.fieldName, "|") + "</fieldName>");
    if (this.fieldPath != null) s.println("<fieldPath>" + this.fieldPath + "</fieldPath>");
    super.printStackTrace(s);
  }

  @Override
  public void printStackTrace(PrintWriter s) {
    if (this.fieldName != null) s.println("<fieldName>" + RUArray.concatArrayIntoString(this.fieldName, "|") + "</fieldName>");
    if (this.fieldPath != null) s.println("<fieldPath>" + this.fieldPath + "</fieldPath>");
    super.printStackTrace(s);
  }

}
