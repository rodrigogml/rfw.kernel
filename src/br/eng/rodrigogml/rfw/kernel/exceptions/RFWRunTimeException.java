package br.eng.rodrigogml.rfw.kernel.exceptions;

/**
 * Description: Classe de exce��es de tempo de execu��o. Devem ser evitadas ao m�ximo se substituindo, por exemplo, por exce��es de valida��o sempre que poss�vel.<br>
 *
 * @author Rodrigo Leit�o
 * @since 4.0 (25/01/2011)
 */
public class RFWRunTimeException extends RuntimeException {

  private static final long serialVersionUID = -1610492052849402597L;

  private final RFWException rfwException;
  private final String[] params;

  /**
   * Cria uma nova Exception
   *
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWRunTimeException(RFWException ex) {
    super(ex);
    this.rfwException = ex;
    this.params = null;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   */
  public RFWRunTimeException(String exceptionCode) {
    super(exceptionCode);
    this.rfwException = null;
    this.params = null;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   */
  public RFWRunTimeException(String exceptionCode, Throwable cause) {
    super(exceptionCode, cause);
    this.rfwException = null;
    this.params = null;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   */
  public RFWRunTimeException(String exceptionCode, String[] params) {
    super(exceptionCode);
    this.rfwException = null;
    this.params = params;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C�digo da Exception para identifica��o. Este c�digo � utilizado tamb�m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n�o encontrado no bundle o valor passado aqui � utilizado.
   * @param params Par�metros que ser�o substitu�dos na mensagem do Bundle com o padr�o ${0}, ${1} ...
   */
  public RFWRunTimeException(String exceptionCode, String[] params, Throwable cause) {
    super(exceptionCode, cause);
    this.rfwException = null;
    this.params = params;
  }

  public String[] getParams() {
    return params;
  }

  public RFWException getRFWException() {
    return rfwException;
  }

}
