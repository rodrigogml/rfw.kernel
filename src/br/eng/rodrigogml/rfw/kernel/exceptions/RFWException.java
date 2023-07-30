package br.eng.rodrigogml.rfw.kernel.exceptions;

import br.eng.rodrigogml.rfw.kernel.bundle.RFWBundle;
import br.eng.rodrigogml.rfw.kernel.utils.RUGenerators;

/**
 * Description: Classe de exce��o principal do framework. Todas as demais classes de exce��o devem derivar desta.<br>
 * Esta classe � abstrata propositalmente, para evitar que seja instanciada diretamente.
 *
 * @author Rodrigo Leit�o
 * @since 10.0.0 (11 de jul de 2018)
 */
public abstract class RFWException extends Exception {

  private static final long serialVersionUID = -9076652594260619730L;

  /**
   * Identificador universal para cara exception gerada.<br>
   * �til para m�dulos identificarem se � a mesma exception e evitar reprocessamento.
   */
  private final String uuid = RUGenerators.UUID_REGEXP;

  /**
   * C�digo de identifica��o do erro, ou mensagem de erro (n�o recomendado).
   */
  private String exceptionCode = null;

  /**
   * Par�metros para substituir na mensagem de bundle ou para registro no Log.
   */
  private String[] params = null;

  /**
   * Cria uma nova Exception
   *
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWException(Throwable ex) {
    super(ex);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   */
  public RFWException(String exceptionCode) {
    this.exceptionCode = exceptionCode;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWException(String exceptionCode, Throwable ex) {
    super(ex);
    this.exceptionCode = exceptionCode;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   */
  public RFWException(String exceptionCode, String[] params) {
    this(exceptionCode);
    this.params = params;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWException(String exceptionCode, String[] params, Throwable ex) {
    this(exceptionCode, ex);
    this.params = params;
  }

  /**
   * # c�digo de identifica��o do erro, ou mensagem de erro (n�o recomendado).
   *
   * @return c�digo de identifica��o do erro, ou mensagem de erro (n�o recomendado)
   */
  public String getExceptionCode() {
    return exceptionCode;
  }

  /**
   * # c�digo de identifica��o do erro, ou mensagem de erro (n�o recomendado).
   *
   * @param exceptionCode c�digo de identifica��o do erro, ou mensagem de erro (n�o recomendado)
   */
  protected void setExceptionCode(String exceptionCode) {
    this.exceptionCode = exceptionCode;
  }

  /**
   * # par�metros para substituir na mensagem de bundle ou para registro no Log.
   *
   * @return par�metros para substituir na mensagem de bundle ou para registro no Log
   */
  public String[] getParams() {
    return params;
  }

  /**
   * # identificador universal para cara exception gerada.
   *
   * @return identificador universal para cara exception gerada
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Sobrescreve este m�todo para que seja exibido no console a mensagem de erro
   */
  @Override
  public String getMessage() {
    return RFWBundle.get(this);
  }

  @Override
  public String getLocalizedMessage() {
    return RFWBundle.get(this);
  }

}
