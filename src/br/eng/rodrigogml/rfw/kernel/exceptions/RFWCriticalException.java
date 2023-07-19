package br.eng.rodrigogml.rfw.kernel.exceptions;

/**
 * Description: Classe de exce��o de erros cr�ticos. Geralmente erros que nunca deveriam ocorrer no sistema e s� ocorrem por m� programa��o ou l�gica com falha.<br>
 *
 * @author Rodrigo Leit�o
 * @since 10.0.0 (11 de jul de 2018)
 */
public class RFWCriticalException extends RFWException {

  private static final long serialVersionUID = 6582412010912877668L;

  /**
   * Cria uma nova Exception
   */
  public RFWCriticalException() {
    super("Major Exception");
  }

  /**
   * Cria uma nova Exception
   *
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWCriticalException(Throwable ex) {
    super(ex);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWCriticalException(String exceptionCode, String[] params, Throwable ex) {
    super(exceptionCode, params, ex);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   */
  public RFWCriticalException(String exceptionCode, String[] params) {
    super(exceptionCode, params);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWCriticalException(String exceptionCode, Throwable ex) {
    super(exceptionCode, ex);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   */
  public RFWCriticalException(String exceptionCode) {
    super(exceptionCode);
  }
}
