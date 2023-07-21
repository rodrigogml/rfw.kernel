package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

/**
 * Description: Esta classe � um padr�o de defini��o de um DataFormatter usado pelo RFWDeprec.<br>
 * Um DataFormatter � uma classe que opera normalmente entre o bean (VOs) e os componentes de exibi��o para o usu�rio (como GUI, relat�rios, etc.), fazendo o parser e a formata��o dos dados para que sejam armazenados no Bean corretamente, enquanto que s�o exibidos na tela de maneira amig�vel para o usu�rio.<br>
 * al�m disso, esta classe carrega outras defini��es uteis, como validador para campos, tamanhos m�ximos digit�veis, etc. <br>
 * <b>Sempre que poss�vel (quando n�o houver configura��es, como casas decimais, negativos, etc.) implemente o DataFormatter como singleton para evitar m�ltiplas instancias denecess�rias na mem�ria.</b>
 *
 * @author Rodrigo Leit�o
 * @since 4.1.0 (23/06/2011)
 */
public interface RFWDataFormatter<PRESENTATIONTYPE, VOTYPE> {

  /**
   * M�todo chamado sempre que precisar converter o dado do jeito como � gerenciado no sistema para ser apresentado para o usu�rio.<br>
   * <b>ATEN��O: </b>Para a apresenta��o, nuncca se retorna null! Para valores null, o valor formatado deve sempre ser "". Usualamente retornar null para exibi��o o usu�rio far� com que a palavra 'null' seja concatenada e exibida para o usu�rio final.
   *
   * @param value valor a ser formatado para a tela.
   * @param locale Localidade para ser usada na formata��o caso necessario.
   * @return Valor em formato amig�vel para o usu�rio.
   */
  public abstract PRESENTATIONTYPE toPresentation(VOTYPE value, Locale locale) throws RFWException;

  /**
   * Converte uma informa��o dada pelo usu�rio (ou mesmo dada pelo M�todo toPresentation) para o objeto como � gerenciado no VO.
   *
   * @param formattedvalue valor formatado (ou n�o) como veio da tela, digitado pelo usu�rio. Pode precisar de valida��o, ou mesmo ser inv�lido.
   * @param locale Localidade para ser usada na formata��o caso necessario.
   * @return Valor para ser colocado no VO.
   * @throws RFWException Exce��o a ser lan�ada quando n�o for poss�vel fazer o parser do valor. Em caso de exce��o o valor ser� anulado no bean.
   */
  public abstract VOTYPE toVO(PRESENTATIONTYPE formattedvalue, Locale locale) throws RFWException;

  /**
   * O M�todo de valida��o deve validar se o conte�do pode ser recebido no M�todo toVO() e ser salvo com sucesso, ou seja, ser convertido do valor do usu�rio para o valor como deve ser salvo no VO.<br>
   * Esta valida��o nada tem a ver com a valida��o do VO, apenas se o valor recebido pode ser convertido e salvo no VO, se � um valor v�lido o valor formatado recebido.
   */
  public abstract void validate(Object value, Locale locale) throws RFWException;

  /**
   * Define o tamanho m�ximo formatado para o dado.<br>
   * Normalmente a informa��o formatada tende a ser maior do que o dado salvo no VO, essa informa��o tende a informar qual o tamanho m�ximo que a informa��o formatada pode ter. Seja para limitar o que o usu�rio poder� digitar, ou o tamanho que o dado formatado ocupar� em um relat�rio ou outro tipo de midia.
   *
   * @return inteiro que determina a quantidade de d�gitos no campo.
   */
  public abstract int getMaxLenght();

  /**
   * Este M�todo tem a fun��o de "reformatar" o conte�do do campo digitado pelo usu�rio e retornar o valor formatado para a pr�pria apresenta��o novamente.<br>
   * A implementa��o padr�o deste M�todo joga o conte�do recebido da apresenta��o apra o M�todo {@link #toVO(Object, Locale)} e depois joga novamente para o M�todo {@link #toPresentation(Object, Locale)}. Pois realizando as duas convers�es teremos o conte�do formatado com sucesso para a apresenta��o. Mas pode ser substituido por uma implementa��o mais eficiente dependendo da implementa��o.
   *
   * @param content conte�do que o usu�rio entrou.
   * @param locale Locale do usu�rio para interpreta��o correta dos formatos utilizados pelo mesmo.
   * @return Valor j� formatado, utilizando o pr�prio locale do usu�rio. Conforme implementa��o da Interface.
   * @throws RFWException Lan�ado em caso de erro de convers�o ou o conte�do entrado pelo usu�rio n�o seja v�lido. Na implementa��o padr�o, retorna as exceptions que possam ocorrer nos M�todos {@link #toVO(Object, Locale)} ou {@link #toPresentation(Object, Locale)}
   */
  public default PRESENTATIONTYPE reformatPresentationContent(PRESENTATIONTYPE content, Locale locale) throws RFWException {
    // Convertemos para o VO, j� que o M�todo de Presentation s� � preparado apra receber um dado v�lidado conforme esperado do VO.
    this.validate(content, locale);
    final VOTYPE voValue = this.toVO(content, locale);
    // Reconvertemos para a PRESENTATION
    return this.toPresentation(voValue, locale);
  }
}
