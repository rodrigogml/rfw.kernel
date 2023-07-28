package br.eng.rodrigogml.rfw.kernel.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilit�ria para conter m�todos de aux�lio do servi�o de e-mail.<BR>
 *
 * @author Rodrigo Leit�o
 * @since 4.2.0 (30/10/2011)
 */
public class RUMail {

  /**
   * Patter dos caracteres aceitos em um e-mail. Tanto na �rea de usu�rio quando de dom�nio.<br>
   * Esse pattern est� incompleto pois n�o aceita os "escape caracteres", por exemplo segundo as especifica��es um email pode ter uma @ como parte do nome do usu�rio deste que seja "escaped" com o caractere \@. Esses caracteres incomuns n�o est�o sendo tratados neste pattern.<Br>
   * <b>Aten��o:</n> N�o incluir neste patter os caracteres de sintaxe. Por exemplo, o e-mail pode ter o nome do usu�rio cercado por aspas, e o dom�nio em forma de ip se cercado por colchetes. Ex: "rodiro leitao"@[10.0.0.1]. Esses caracteres de "entorno" n�o devem ser considerados neste patter, mesmo que a " possa fazer parte do nome do usu�rio como um escaped caracter \", n�o estamos falando dos
   * escapade caracteres, apenas dos caracteres de entorno. Tamb�m n�o colocar nenhum tipo de defini��o de quantidade de repeti��o, deixar apenas a lista de caracteres v�lidos.
   */
  public static final String mailAcceptedChar = "[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~\\.]";

  /**
   * Construtor privado para classe est�tica.
   */
  private RUMail() {
  }

  /**
   * Valida se o endere�o � v�lido de acordo com a RFC822.<br>
   *
   * @param mail
   * @throws RFWException
   */
  public static void validateMailAddress(String mail) throws RFWException {
    if (mail == null) throw new RFWValidationException("RFW_000018");
    // try {
    // InternetAddress emailAddr = new InternetAddress(mail);
    // emailAddr.validate();
    // } catch (AddressException ex) {
    // throw new RFWValidationException("O endere�o de e-mail n�o � um endere�o v�lido.");
    // }
    // Implementa��o removendo a depend�ncia do javamail durante a migra��o para o RFW Kernel
    String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(mail);

    if (!matcher.matches()) {
      throw new RFWValidationException("O endere�o de e-mail n�o � um endere�o v�lido.");
    }
  }
}
