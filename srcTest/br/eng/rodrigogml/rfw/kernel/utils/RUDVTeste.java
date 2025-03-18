package br.eng.rodrigogml.rfw.kernel.utils;

import static br.eng.rodrigogml.rfw.kernel.utils.RUDV.calcDVCNPJ;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDV.calcDVCPF;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDV.validateCNPJ;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDV.validateCPF;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDV.validateCPFOrCNPJ;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDV.validateUF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe de teste dos métodos de {@link RUDV}.<br>
 *
 * @author Rodrigo Leitão
 * @since (21 de fev. de 2025)
 */
/**
 *
 */
/**
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUDVTeste {

  // 100 CNPJs válidos
  private static final String[] VALID_CNPJs = {
      "01461665000165", "01461665000165", "03623748000139", "45755238000165", "57494130000145",
      "06774025000129", "48460745000160", "01461665000165", "06774025000129", "48460745000160",
      "07652353000115", "07652353000115", "01461665000165", "06774025000129", "88441555000110",
      "01461665000165", "06774025000129", "01461665000165", "88441555000110", "01461665000165",
      "01461665000165", "01461665000165", "88441555000110", "06774025000129", "88441555000110",
      "44893410001407", "01461665000165", "08326260000163", "06774025000129", "88441555000110",
      "46634069000178", "46634499000190", "46634085000160", "01461665000165", "06774025000129",
      "46634069000178", "46634085000160", "01461665000165", "06774025000129", "01461665000165",
      "01461665000165", "66069154000148", "06774025000129", "01461665000165", "03623748000139",
      "01461665000165", "01461665000165", "06774025000129", "01461665000165", "94261534000115",
      "01461665000165", "06774025000129", "01461665000165", "61971545000130", "06774025000129",
      "01461665000165", "06774025000129", "01461665000165", "01461665000165", "01461665000165",
      "85129518000182", "04506487000130", "04506487000130", "06774025000129", "00346953000106",
      "01461665000165", "61971545000130", "06774025000129", "44599041000111", "04506487000130",
      "01461665000165", "61971545000130", "04506487000130", "06774025000129", "01461665000165",
      "04506487000130", "06774025000129", "04235259000172", "01461665000165", "06774025000129",
      "01461665000165", "10588201000105", "01461665000165", "15794062000190", "10588201000105",
      "17220753000105", "83011247001021", "01461665000165", "61971545000130", "02786436000345",
      "83011247002346", "83011247001021", "83011247002346", "17220753000105", "01461665000165",
      "14706189000147", "02786436000345", "01461665000165", "14706189000147", "67107391000119"
  };

  // 100 CPFs válidos
  private static final String[] VALID_CPFs = {
      "34219411810", "03313104657", "02166067808", "04951079801", "09697114846",
      "36911261899", "30812959809", "36178560800", "39920675806", "86245600049",
      "32178969801", "28468232866", "87132940830", "62830155815", "31458025853",
      "06770970694", "39896679800", "37979737806", "38311598827", "50319019853",
      "03361916852", "89767136053", "35916021836", "00311679269", "10223424870",
      "29782180882", "34228064825", "22436815879", "31877947857", "02501579852",
      "25278930829", "46634770115", "11937125807", "32062761600", "03455962858",
      "03675122891", "12310943622", "11804079626", "02839230593", "28449545889",
      "34880517810", "22095156889", "30952038889", "36132609806", "17887582857",
      "16835066857", "03313104657", "29004633863", "81001380959", "03945541670",
      "04951079801", "04951079801", "32062761600", "82779805820", "36884105801",
      "10208775889", "22609645830", "06523417808", "36911618800", "77304080825",
      "24661497880", "09232083809", "71103902849", "62291777653", "60215631900",
      "28257161896", "23329423862", "40549659897", "32673430890", "03313104657",
      "33437731823", "01221790803", "07970922856", "33602957810", "02815661861",
      "03725724806", "05738614801", "02815661861", "07896989867", "13773420854",
      "77501578834", "12033305880", "14062339862", "38137177809", "13804957846",
      "13767145820", "12053725800", "03945541670", "36408356893", "26532781894",
      "03256392300", "26795327833", "72129948804", "45926140978", "32729830820",
      "02889422640", "28509042870", "15469268870", "03675122891", "05266878860"
  };

  // 100 CNPJs inválidos (com um número alterado aleatoriamente)
  private static final String[] INVALID_CNPJs = {
      "01461165000165", "01461165000165", "03623778000139", "45755238000765", "57494630000145",
      "06774025000429", "48860745000160", "01561665000165", "06474025000129", "48460045000160",
      "07652354000115", "07652353000118", "01461665008165", "06774025700129", "88441555000810",
      "06461665000165", "06774025000159", "01441665000165", "88411555000110", "01461665006165",
      "01431665000165", "01461665001165", "88448555000110", "06774025100129", "88441555300110",
      "44893410001404", "01461666000165", "08326270000163", "06774025000199", "88441555004110",
      "46634079000178", "46634459000190", "46934085000160", "01461665000164", "09774025000129",
      "46634099000178", "46634085000190", "01561665000165", "06784025000129", "01461665800165",
      "01461663000165", "66019154000148", "06774025005129", "01431665000165", "03623740000139",
      "01401665000165", "01431665000165", "06774025000149", "01461665000115", "94264534000115",
      "01961665000165", "06774025000128", "01461665500165", "61971545060130", "06774025001129",
      "01461665080165", "06774015000129", "11461665000165", "01461665000166", "01461665040165",
      "85126518000182", "04506487000133", "04506483000130", "06734025000129", "00346353000106",
      "01461668000165", "61971545009130", "06774025000529", "47599041000111", "04506987000130",
      "41461665000165", "61671545000130", "04506482000130", "06714025000129", "01468665000165",
      "04506483000130", "06774025010129", "04235259000174", "08461665000165", "06774026000129",
      "01461665001165", "10578201000105", "01464665000165", "15794962000190", "10588204000105",
      "17220553000105", "83011247001051", "01461667000165", "61971505000130", "02786486000345",
      "83011247005346", "83011247701021", "83011247005346", "17220753005105", "01461465000165",
      "54706189000147", "02786836000345", "01461665000765", "14706189000447", "67107391000117"
  };

  // 100 CPFs inválidos (com um número alterado aleatoriamente)
  private static final String[] INVALID_CPFs = {
      "34719411810", "03313104656", "02066067808", "44951079801", "09497114846",
      "36919261899", "30812959849", "36128560800", "39910675806", "86245800049",
      "32078969801", "28428232866", "87132930830", "62820155815", "31458025833",
      "06770971694", "39896673800", "37970737806", "38317598827", "50319019813",
      "03362916852", "89737136053", "38916021836", "30311679269", "10223427870",
      "29782150882", "14228064825", "22436815839", "32877947857", "02501579882",
      "25270930829", "46674770115", "11937725807", "32062761000", "04455962858",
      "03675122191", "18310943622", "11804059626", "02839270593", "28449545869",
      "34880517880", "22095156899", "30952038869", "36132649806", "17887582157",
      "16835066807", "04313104657", "29004633063", "81001380559", "03945541170",
      "04951079811", "02951079801", "52062761600", "82771805820", "36884105101",
      "10508775889", "22009645830", "06583417808", "36910618800", "77004080825",
      "24668497880", "09232086809", "71103902149", "66291777653", "69215631900",
      "28227161896", "23329823862", "40449659897", "32673430897", "53313104657",
      "33437931823", "41221790803", "07970922816", "33602657810", "02815661461",
      "03725824806", "95738614801", "02815261861", "07896989897", "13773420814",
      "77101578834", "12033305800", "14022339862", "38127177809", "13804957346",
      "13767345820", "16053725800", "03945571670", "38408356893", "26332781894",
      "03256372300", "26794327833", "72129048804", "45926140988", "32721830820",
      "02889422140", "28502042870", "15469264870", "03675122191", "05266878820"
  };

  private static final String[] VALID_IE_MG = {
      "4603791450091" // Esta IE de MG é do CNPJ: 07.599.349/0001-30, confirmada na SEFAZ e dava erro no código atual de validação de IE de MG. Foi colocada aqui apra garantir que o novo código, quando corrigido a valida.
  };

  /**
   * Testa se {@code validateCNPJ} aceita corretamente CNPJs válidos.
   */
  @Test
  public void t00_validateIE_validValues() {
    for (String cnpj : VALID_IE_MG) {
      try {
        RUDV.validateIE(cnpj);
      } catch (Exception e) {
        fail("Falha ao validar um CNPJ válido: " + cnpj + " - Exceção: " + e.getMessage());
      }
    }
  }

  /**
   * Testa se {@code validateCNPJ} aceita corretamente CNPJs válidos.
   */
  @Test
  public void t00_validateCNPJ_validValues() {
    for (String cnpj : VALID_CNPJs) {
      try {
        validateCNPJ(cnpj);
      } catch (Exception e) {
        fail("Falha ao validar um CNPJ válido: " + cnpj + " - Exceção: " + e.getMessage());
      }
    }
  }

  /**
   * Testa se {@code validateCNPJ} rejeita corretamente CNPJs inválidos.
   */
  @Test
  public void t01_validateCNPJ_invalidValues() {
    for (String cnpj : INVALID_CNPJs) {
      assertThrows(RFWValidationException.class, () -> validateCNPJ(cnpj));
    }
  }

  /**
   * Testa se {@code calcDVCNPJ} calcula corretamente os dígitos verificadores de CNPJs válidos.
   *
   * @throws RFWException
   */
  @Test
  public void t00_calcDVCNPJ_validValues() throws RFWException {
    for (String cnpj : VALID_CNPJs) {
      String baseCNPJ = cnpj.substring(0, 12);
      String expectedDV = cnpj.substring(12);
      assertEquals("Erro ao calcular DV para: " + cnpj, expectedDV, calcDVCNPJ(baseCNPJ));
    }
  }

  /**
   * Testa se {@code calcDVCNPJ} rejeita entradas inválidas.
   *
   * @throws RFWException
   */
  @Test
  public void t01_calcDVCNPJ_invalidValues() throws RFWException {
    for (String cnpj : INVALID_CNPJs) {
      String baseCNPJ = cnpj.substring(0, 12);
      String expectedDV = cnpj.substring(12);
      assertNotEquals("Erro ao calcular DV para: " + cnpj, expectedDV, calcDVCNPJ(baseCNPJ));
    }
  }

  /**
   * Testa se {@code calcDVCPF} calcula corretamente os dígitos verificadores de CPFs válidos.
   *
   * @throws RFWException
   */
  @Test
  public void t00_calcDVCPF_validValues() throws RFWException {
    for (String cpf : VALID_CPFs) {
      String baseCPF = cpf.substring(0, 9);
      String expectedDV = cpf.substring(9);
      assertEquals("Erro ao calcular DV para: " + cpf, expectedDV, calcDVCPF(baseCPF));
    }
  }

  /**
   * Testa se {@code calcDVCPF} rejeita entradas inválidas.
   *
   * @throws RFWException
   */
  @Test
  public void t01_calcDVCPF_invalidValues() throws RFWException {
    for (String cpf : INVALID_CPFs) {
      String baseCPF = cpf.substring(0, 9);
      String expectedDV = cpf.substring(9);
      assertNotEquals("Erro ao calcular DV para: " + cpf, expectedDV, calcDVCPF(baseCPF));
    }
  }

  /**
   * Testa se {@code validateCPF} aceita corretamente CPFs válidos.
   */
  @Test
  public void t00_validateCPF_validValues() {
    for (String cpf : VALID_CPFs) {
      try {
        validateCPF(cpf);
      } catch (Exception e) {
        fail("Falha ao validar um CPF válido: " + cpf + " - Exceção: " + e.getMessage());
      }
    }
  }

  /**
   * Testa se {@code validateCPF} rejeita corretamente CPFs inválidos.
   */
  @Test
  public void t01_validateCPF_invalidValues() {
    for (String cpf : INVALID_CPFs) {
      assertThrows(RFWValidationException.class, () -> validateCPF(cpf));
    }
  }

  /**
   * Testa se {@code validateCPFOrCNPJ} aceita corretamente CPFs e CNPJs válidos.
   */
  @Test
  public void t00_validateCPFOrCNPJ_validValues() {
    for (String number : VALID_CPFs) {
      try {
        validateCPFOrCNPJ(number);
      } catch (Exception e) {
        fail("Falha ao validar um CPF válido: " + number + " - Exceção: " + e.getMessage());
      }
    }

    for (String number : VALID_CNPJs) {
      try {
        validateCPFOrCNPJ(number);
      } catch (Exception e) {
        fail("Falha ao validar um CNPJ válido: " + number + " - Exceção: " + e.getMessage());
      }
    }
  }

  /**
   * Testa se {@code validateCPFOrCNPJ} rejeita corretamente CPFs e CNPJs inválidos.
   */
  @Test
  public void t01_validateCPFOrCNPJ_invalidValues() {
    for (String number : INVALID_CPFs) {
      assertThrows(RFWValidationException.class, () -> validateCPFOrCNPJ(number));
    }

    for (String number : INVALID_CNPJs) {
      assertThrows(RFWValidationException.class, () -> validateCPFOrCNPJ(number));
    }
  }

  /**
   * Testa se {@code validateUF} aceita corretamente UFs válidas do Brasil.
   */
  @Test
  public void t00_validateUF_validValues() {
    String[] validUFs = {
        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
        "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SE", "SP", "TO"
    };

    for (String uf : validUFs) {
      try {
        validateUF(uf);
        validateUF(uf.toLowerCase()); // Testa ignorando case
      } catch (Exception e) {
        fail("Falha ao validar UF válida: " + uf + " - Exceção: " + e.getMessage());
      }
    }
  }

  /**
   * Testa se {@code validateUF} rejeita corretamente UFs inválidas.
   */
  @Test
  public void t01_validateUF_invalidValues() {
    String[] invalidUFs = {
        "XX", "ZZ", "AB", "ACR", "P", "123", "SP1", "RJX", "Minas", "br", "RJ RJ", "",
        "São Paulo", "GOI", "MG!", "@#", "aa", "sp1", "MG-", "to!"
    };

    for (String uf : invalidUFs) {
      assertThrows(RFWValidationException.class, () -> validateUF(uf));
    }

    assertThrows(RFWValidationException.class, () -> validateUF(null)); // Testa UF nula
  }
}
