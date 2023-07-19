package br.eng.rodrigogml.rfw.kernel.exceptions;

/**
 * Description: Classe de exce??es de erros de sistema. Erros que n?o s?o cr?ticos (como falha de desenvolvimento), mas tamb?m n?o s?o de valida??o de dados de entrada. Geralmente erros causados por situa??es de configura??o do ambiente, indiponibilidade de rede, etc.<br>
 *
 * @author Rodrigo Leit?o
 * @since 10.0.0 (11 de jul de 2018)
 */
public class RFWWarningException extends RFWException {

  private static final long serialVersionUID = 2905650541268655880L;

  /**
   * Cria uma nova Exception
   *
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWWarningException(Throwable ex) {
    super(ex);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C?digo da Exception para identifica??o. Este c?digo ? utilizado tamb?m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n?o encontrado no bundle o valor passado aqui ? utilizado.
   * @param params Par?metros que ser?o substitu?dos na mensagem do Bundle com o padr?o ${0}, ${1} ...
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWWarningException(String exceptionCode, String[] params, Throwable ex) {
    super(exceptionCode, params, ex);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C?digo da Exception para identifica??o. Este c?digo ? utilizado tamb?m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n?o encontrado no bundle o valor passado aqui ? utilizado.
   * @param params Par?metros que ser?o substitu?dos na mensagem do Bundle com o padr?o ${0}, ${1} ...
   */
  public RFWWarningException(String exceptionCode, String[] params) {
    super(exceptionCode, params);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C?digo da Exception para identifica??o. Este c?digo ? utilizado tamb?m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n?o encontrado no bundle o valor passado aqui ? utilizado.
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWWarningException(String exceptionCode, Throwable ex) {
    super(exceptionCode, ex);
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode C?digo da Exception para identifica??o. Este c?digo ? utilizado tamb?m para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando n?o encontrado no bundle o valor passado aqui ? utilizado.
   */
  public RFWWarningException(String exceptionCode) {
    super(exceptionCode);
  }
}