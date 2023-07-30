package br.eng.rodrigogml.rfw.kernel.exceptions;

import br.eng.rodrigogml.rfw.kernel.bundle.RFWBundle;
import br.eng.rodrigogml.rfw.kernel.utils.RUGenerators;

/**
 * Description: Classe de exceção principal do framework. Todas as demais classes de exceção devem derivar desta.<br>
 * Esta classe é abstrata propositalmente, para evitar que seja instanciada diretamente.
 *
 * @author Rodrigo Leitão
 * @since 10.0.0 (11 de jul de 2018)
 */
public abstract class RFWException extends Exception {

  private static final long serialVersionUID = -9076652594260619730L;

  /**
   * Identificador universal para cara exception gerada.<br>
   * Útil para módulos identificarem se é a mesma exception e evitar reprocessamento.
   */
  private final String uuid = RUGenerators.UUID_REGEXP;

  /**
   * Código de identificação do erro, ou mensagem de erro (não recomendado).
   */
  private String exceptionCode = null;

  /**
   * Parâmetros para substituir na mensagem de bundle ou para registro no Log.
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
   * @param exceptionCode Código da Exception para identificação. Este código é utilizado também para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando não encontrado no bundle o valor passado aqui é utilizado.
   */
  public RFWException(String exceptionCode) {
    this.exceptionCode = exceptionCode;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode Código da Exception para identificação. Este código é utilizado também para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando não encontrado no bundle o valor passado aqui é utilizado.
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWException(String exceptionCode, Throwable ex) {
    super(ex);
    this.exceptionCode = exceptionCode;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode Código da Exception para identificação. Este código é utilizado também para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando não encontrado no bundle o valor passado aqui é utilizado.
   * @param params Parâmetros que serão substituídos na mensagem do Bundle com o padrão ${0}, ${1} ...
   */
  public RFWException(String exceptionCode, String[] params) {
    this(exceptionCode);
    this.params = params;
  }

  /**
   * Cria uma nova Exception
   *
   * @param exceptionCode Código da Exception para identificação. Este código é utilizado também para resovler no arquivo de bundle. Alternativamente pode ser passada a mensagem de erro diretamente, pois quando não encontrado no bundle o valor passado aqui é utilizado.
   * @param params Parâmetros que serão substituídos na mensagem do Bundle com o padrão ${0}, ${1} ...
   * @param ex Exception causadora anteriore. Sempre que houver uma exception anterior ela deve ser passada aqui para que o dev tenha a pilha completa do problema.
   */
  public RFWException(String exceptionCode, String[] params, Throwable ex) {
    this(exceptionCode, ex);
    this.params = params;
  }

  /**
   * # código de identificação do erro, ou mensagem de erro (não recomendado).
   *
   * @return código de identificação do erro, ou mensagem de erro (não recomendado)
   */
  public String getExceptionCode() {
    return exceptionCode;
  }

  /**
   * # código de identificação do erro, ou mensagem de erro (não recomendado).
   *
   * @param exceptionCode código de identificação do erro, ou mensagem de erro (não recomendado)
   */
  protected void setExceptionCode(String exceptionCode) {
    this.exceptionCode = exceptionCode;
  }

  /**
   * # parâmetros para substituir na mensagem de bundle ou para registro no Log.
   *
   * @return parâmetros para substituir na mensagem de bundle ou para registro no Log
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
   * Sobrescreve este método para que seja exibido no console a mensagem de erro
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
