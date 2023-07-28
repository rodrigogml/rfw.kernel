package br.eng.rodrigogml.rfw.kernel.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilitária para conter métodos de auxílio do serviço de e-mail.<BR>
 *
 * @author Rodrigo Leitão
 * @since 4.2.0 (30/10/2011)
 */
public class RUMail {

  /**
   * Patter dos caracteres aceitos em um e-mail. Tanto na área de usuário quando de domínio.<br>
   * Esse pattern está incompleto pois não aceita os "escape caracteres", por exemplo segundo as especificações um email pode ter uma @ como parte do nome do usuário deste que seja "escaped" com o caractere \@. Esses caracteres incomuns não estão sendo tratados neste pattern.<Br>
   * <b>Atenção:</n> Não incluir neste patter os caracteres de sintaxe. Por exemplo, o e-mail pode ter o nome do usuário cercado por aspas, e o domínio em forma de ip se cercado por colchetes. Ex: "rodiro leitao"@[10.0.0.1]. Esses caracteres de "entorno" não devem ser considerados neste patter, mesmo que a " possa fazer parte do nome do usuário como um escaped caracter \", não estamos falando dos
   * escapade caracteres, apenas dos caracteres de entorno. Também não colocar nenhum tipo de definição de quantidade de repetição, deixar apenas a lista de caracteres válidos.
   */
  public static final String mailAcceptedChar = "[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~\\.]";

  /**
   * Construtor privado para classe estática.
   */
  private RUMail() {
  }

  /**
   * Valida se o endereço é válido de acordo com a RFC822.<br>
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
    // throw new RFWValidationException("O endereço de e-mail não é um endereço válido.");
    // }
    // Implementação removendo a dependência do javamail durante a migração para o RFW Kernel
    String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(mail);

    if (!matcher.matches()) {
      throw new RFWValidationException("O endereço de e-mail não é um endereço válido.");
    }
  }
}
