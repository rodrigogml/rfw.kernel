package br.eng.rodrigogml.rfw.kernel.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.security.KeyStore;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe de teste da {@link RUCert}.<br>
 *
 * @author Rodrigo GML
 * @since 1.0.0 (24 de ago. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUCertTest {

  @Test
  public void t00_testA1CertificateLoad() throws RFWException, Exception {
    String filePath = RFW.getDevProperty("rfw.kernel.utils.rucert.a1certpath");
    String password = RFW.getDevProperty("rfw.kernel.utils.rucert.a1certpass");
    PreProcess.requiredNonNull(filePath);
    PreProcess.requiredNonNull(password);

    KeyStore keyStore = RUCert.loadKeyStoreA1Certificate(new File(filePath), password);

    assertEquals("Deveriamos encontrar 1 certificado dentro do KeyStore", 1, keyStore.size());

  }

}
